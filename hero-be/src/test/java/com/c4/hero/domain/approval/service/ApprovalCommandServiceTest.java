package com.c4.hero.domain.approval.service;

import com.c4.hero.common.s3.S3Service;
import com.c4.hero.domain.approval.dto.ApprovalLineDTO;
import com.c4.hero.domain.approval.dto.request.ApprovalActionRequestDTO;
import com.c4.hero.domain.approval.dto.request.ApprovalRequestDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalActionResponseDTO;
import com.c4.hero.domain.approval.entity.*;
import com.c4.hero.domain.approval.repository.*;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalCommandServiceTest {

    @InjectMocks
    private ApprovalCommandService approvalCommandService;

    @Mock
    private ApprovalDocumentRepository documentRepository;
    @Mock
    private ApprovalAttachmentRepository attachmentRepository;
    @Mock
    private ApprovalLineRepository lineRepository;
    @Mock
    private ApprovalReferenceRepository referenceRepository;
    @Mock
    private ApprovalBookmarkRepository bookmarkRepository;
    @Mock
    private ApprovalTemplateRepository templateRepository;
    @Mock
    private ApprovalSequenceRepository sequenceRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private S3Service s3Service;
    @Mock
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("즐겨찾기 토글 - 없을 때 추가")
    void toggleBookmark_Add() {
        // given
        Integer empId = 1;
        Integer templateId = 100;
        given(bookmarkRepository.findByEmpIdAndTemplateId(empId, templateId))
                .willReturn(Optional.empty());

        // when
        boolean result = approvalCommandService.toggleBookmark(empId, templateId);

        // then
        assertTrue(result);
        verify(bookmarkRepository, times(1)).save(any(ApprovalBookmark.class));
    }

    @Test
    @DisplayName("즐겨찾기 토글 - 있을 때 삭제")
    void toggleBookmark_Remove() {
        // given
        Integer empId = 1;
        Integer templateId = 100;
        ApprovalBookmark bookmark = ApprovalBookmark.builder().build();
        given(bookmarkRepository.findByEmpIdAndTemplateId(empId, templateId))
                .willReturn(Optional.of(bookmark));

        // when
        boolean result = approvalCommandService.toggleBookmark(empId, templateId);

        // then
        assertFalse(result);
        verify(bookmarkRepository, times(1)).delete(bookmark);
    }

    @Test
    @DisplayName("문서 생성 - 임시저장")
    void createDocument_Draft() {
        // given
        Integer empId = 1;
        ApprovalRequestDTO dto = new ApprovalRequestDTO();
        dto.setFormType("VACATION");
        dto.setTitle("휴가 신청");
        dto.setDetails("휴가 갑니다.");
        List<MultipartFile> files = new ArrayList<>();
        String status = "DRAFT";

        ApprovalTemplate template = ApprovalTemplate.builder().templateId(1).templateKey("VACATION").build();
        given(templateRepository.findByTemplateKey("VACATION")).willReturn(template);

        ApprovalDocument savedDoc = ApprovalDocument.builder().docId(10).docStatus("DRAFT").build();
        given(documentRepository.save(any(ApprovalDocument.class))).willReturn(savedDoc);

        // when
        Integer docId = approvalCommandService.createDocument(empId, dto, files, status);

        // then
        assertEquals(10, docId);
        verify(documentRepository, times(1)).save(any(ApprovalDocument.class));
    }

    @Test
    @DisplayName("결재 처리 - 승인 (다음 결재자가 있는 경우)")
    void processApproval_Approve_NextPending() {
        // given
        Integer empId = 2;
        ApprovalActionRequestDTO request = new ApprovalActionRequestDTO();
        request.setDocId(10);
        request.setLineId(20);
        request.setAction("APPROVE");

        ApprovalLine currentLine = ApprovalLine.builder()
                .lineId(20)
                .docId(10)
                .approverId(empId)
                .seq(2)
                .lineStatus("PENDING")
                .build();

        ApprovalDocument document = ApprovalDocument.builder()
                .docId(10)
                .docStatus("INPROGRESS")
                .templateId(1)
                .build();

        ApprovalTemplate template = ApprovalTemplate.builder().templateId(1).templateKey("VACATION").build();

        // Mocking repository responses
        given(lineRepository.findById(20)).willReturn(Optional.of(currentLine));
        given(documentRepository.findById(10)).willReturn(Optional.of(document));
        given(templateRepository.findByTemplateId(1)).willReturn(template);

        // Mocking all lines to simulate next approver exists
        ApprovalLine line1 = ApprovalLine.builder().seq(1).lineStatus("APPROVED").build();
        ApprovalLine line2 = currentLine; // seq 2
        ApprovalLine line3 = ApprovalLine.builder().seq(3).lineStatus("PENDING").approverId(3).build();

        given(lineRepository.findByDocIdOrderBySeqAsc(10)).willReturn(List.of(line1, line2, line3));

        // when
        ApprovalActionResponseDTO response = approvalCommandService.processApproval(request, empId);

        // then
        assertTrue(response.isSuccess());
        assertEquals("INPROGRESS", response.getDocStatus());
        assertEquals("APPROVED", currentLine.getLineStatus());
    }

    @Test
    @DisplayName("결재 처리 - 반려")
    void processApproval_Reject() {
        // given
        Integer empId = 2;
        ApprovalActionRequestDTO request = new ApprovalActionRequestDTO();
        request.setDocId(10);
        request.setLineId(20);
        request.setAction("REJECT");
        request.setComment("반려합니다.");

        ApprovalLine currentLine = ApprovalLine.builder()
                .lineId(20)
                .docId(10)
                .approverId(empId)
                .seq(2)
                .lineStatus("PENDING")
                .build();

        ApprovalDocument document = ApprovalDocument.builder()
                .docId(10)
                .docStatus("INPROGRESS")
                .templateId(1)
                .build();

        ApprovalTemplate template = ApprovalTemplate.builder().templateId(1).templateKey("VACATION").build();

        given(lineRepository.findById(20)).willReturn(Optional.of(currentLine));
        given(documentRepository.findById(10)).willReturn(Optional.of(document));
        given(templateRepository.findByTemplateId(1)).willReturn(template);

        // when
        ApprovalActionResponseDTO response = approvalCommandService.processApproval(request, empId);

        // then
        assertTrue(response.isSuccess());
        assertEquals("REJECTED", response.getDocStatus());
        assertEquals("REJECTED", currentLine.getLineStatus());
        assertEquals("반려합니다.", currentLine.getComment());
        assertEquals("REJECTED", document.getDocStatus());
    }
}