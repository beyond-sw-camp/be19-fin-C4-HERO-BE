package com.c4.hero.domain.approval.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.common.s3.S3Service;
import com.c4.hero.domain.approval.dto.ApprovalLineDTO;
import com.c4.hero.domain.approval.dto.ApprovalReferenceDTO;
import com.c4.hero.domain.approval.dto.request.ApprovalActionRequestDTO;
import com.c4.hero.domain.approval.dto.request.ApprovalRequestDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalActionResponseDTO;
import com.c4.hero.domain.approval.entity.*;
import com.c4.hero.domain.approval.event.ApprovalCompletedEvent;
import com.c4.hero.domain.approval.event.ApprovalRejectedEvent;
import com.c4.hero.domain.approval.exception.*;
import com.c4.hero.domain.approval.repository.*;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.notification.event.approval.ApprovalNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * Class Name  : ApprovalCommandService
 * Description : 전자결재 커맨드 관련 서비스 로직 (CUD: 삽입/수정/삭제)
 *               CQRS 패턴 적용 - 조회는 ApprovalQueryService에서 처리
 *
 * 주요 기능
 *   - 즐겨찾기 토글
 *   - 문서 생성 (임시저장/상신)
 *   - 임시저장 문서 수정/상신
 *   - 결재 처리 (승인/반려)
 *   - 문서 회수/삭제
 *   - 도메인 이벤트 발행 (승인 완료/반려)
 *
 * History
 *   2025/12/25 (민철) CQRS 패턴 적용 및 작성화면 조회 메서드 로직 추가
 *   2025/12/26 (민철) 결재선/참조목록 저장 로직 추가 및 DTO 필드명 수정
 *   2025/12/28 (승건) 반려 이벤트 발행 로직 추가
 *   2025/12/31 (민철) 임시저장 문서 수정 및 상신 메서드 추가
 *   2026/01/01 (민철) S3 파일 업로드 방식으로 변경
 *   2026/01/02 (민철) 결재선이 1단계(기안자)일 경우 상신-승인 동시 처리

 *   2026/01/02 (민철) 문서번호 생성 동시성 처리 (비관적 락 적용)
 *   2026/01/02 (민철) 메서드 주석 개선
 *   2026/01/02 (혜원) 결재 알림 이벤트 발행 추가
 * </pre>
 *
 * @author 민철
 * @version 3.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalCommandService {

    private final ApprovalDocumentRepository documentRepository;
    private final ApprovalAttachmentRepository attachmentRepository;
    private final ApprovalLineRepository lineRepository;
    private final ApprovalReferenceRepository referenceRepository;
    private final ApprovalBookmarkRepository bookmarkRepository;
    private final ApprovalTemplateRepository templateRepository;
    private final ApprovalSequenceRepository sequenceRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final S3Service s3Service;
    private final EmployeeRepository employeeRepository;


    /**
     * 즐겨찾기 토글 (있으면 삭제, 없으면 추가)
     * <pre>
     * 사용자가 자주 사용하는 서식을 즐겨찾기로 등록/해제
     * </pre>
     *
     * @param empId      사원 ID
     * @param templateId 문서 템플릿 ID
     * @return 즐겨찾기 상태 (true: 등록됨, false: 해제됨)
     */
    @Transactional
    public boolean toggleBookmark(Integer empId, Integer templateId) {
        Optional<ApprovalBookmark> bookmark =
                bookmarkRepository.findByEmpIdAndTemplateId(empId, templateId);

        if (bookmark.isPresent()) {
            bookmarkRepository.delete(bookmark.get());
            return false;
        } else {
            ApprovalBookmark newBookmark = ApprovalBookmark.builder()
                    .empId(empId)
                    .templateId(templateId)
                    .build();
            bookmarkRepository.save(newBookmark);
            return true;
        }
    }


    /**
     * 문서 생성 (임시저장 or 상신)
     * <pre>
     * 처리 흐름:
     * 1. 문서 본문 저장
     * 2. 결재선 저장 (seq=1 기안자는 APPROVED, 나머지는 PENDING)
     * 3. 참조자 저장
     * 4. 첨부파일 S3 업로드 및 DB 저장
     * 5. 상신(INPROGRESS)인 경우 결재선 확인
     *    5-1. 결재선이 기안자(seq=1)만 있으면 자동 승인 처리
     *    5-2. 문서 번호 생성 및 승인 완료 이벤트 발행
     * </pre>
     *
     * @param employeeId 기안자 ID
     * @param dto        문서 생성 요청 DTO
     * @param files      첨부 파일 목록
     * @param status     문서 상태 (DRAFT: 임시저장 / INPROGRESS: 상신)
     * @return 생성된 문서 ID
     * @throws ApprovalFileUploadException S3 파일 업로드 실패 시
     * @throws BusinessException           문서번호 생성 실패 시
     */
    @Transactional
    public Integer createDocument(
            Integer employeeId,
            ApprovalRequestDTO dto,
            List<MultipartFile> files,
            String status
    ) {

        ApprovalDocument document = createApprovalDocument(employeeId, dto, status);
        ApprovalDocument savedDoc = documentRepository.save(document);

        if (dto.getLines() != null && !dto.getLines().isEmpty()) {
            saveApprovalLines(savedDoc.getDocId(), dto.getLines());
        }

        if (dto.getReferences() != null && !dto.getReferences().isEmpty()) {
            saveReferences(savedDoc.getDocId(), dto.getReferences());
        }

        if (files != null && !files.isEmpty()) {
            saveFilesToS3(files, savedDoc);
        }

        if ("INPROGRESS".equals(status)) {
            List<ApprovalLine> lines = lineRepository.findByDocIdOrderBySeqAsc(savedDoc.getDocId());
            boolean onlyDrafter = lines.stream()
                    .allMatch(line -> line.getSeq() == 1);

            if (onlyDrafter) {
                savedDoc.complete();
                try {
                    String docNo = generateDocNo();
                    savedDoc.assignDocNo(docNo);
                } catch (BusinessException e) {
                    throw new ApprovalDocumentNumberFailedException(ErrorCode.DOC_NO_GENERATION_ERROR);
                }

                documentRepository.save(savedDoc);

                publishApprovalCompletedEvent(savedDoc);
            } else {
                ApprovalLine firstApprover = lines.stream()
                        .filter(line -> line.getSeq() == 2)
                        .findFirst()
                        .orElse(null);

                if (firstApprover != null) {
                    publishApprovalRequestEvent(savedDoc, firstApprover);
                }
            }
        }

        return savedDoc.getDocId();
    }


    /**
     * 문서 번호 생성 (Format: HERO-yyyy-00001)
     * <pre>
     * 동시성 제어:
     * - DB의 비관적 락(Pessimistic Lock)을 사용하여 동시 접근 제어
     * - SELECT ... FOR UPDATE 쿼리로 다른 트랜잭션의 접근 차단
     * - 트랜잭션 커밋 시점에 락이 해제됨
     *
     * 처리 흐름:
     * 1. 채번 키 생성 (예: HERO-2026)
     * 2. 비관적 락으로 시퀀스 조회 (없으면 새로 생성)
     * 3. 번호 증가 (메모리 상 변경)
     * 4. 변경 사항 저장 (트랜잭션 커밋 시 DB 반영 및 락 해제)
     * 5. 포맷팅하여 반환 (HERO-2026-00001)
     *
     * 주의사항:
     * - 이 메서드는 반드시 @Transactional 안에서 호출되어야 락이 유지됨
     * - 트랜잭션 외부에서 호출 시 락이 즉시 해제되어 동시성 문제 발생 가능
     * </pre>
     *
     * @return 생성된 문서 번호 (예: HERO-2026-00001)
     */
    private String generateDocNo() {
        String currentYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        String seqType = "HERO-" + currentYear;

        ApprovalSequence sequence = sequenceRepository.findBySeqTypeWithLock(seqType)
                .orElseGet(() -> ApprovalSequence.builder()
                        .seqType(seqType)
                        .currentVal(0L)
                        .build());

        sequence.increment();

        sequenceRepository.save(sequence);

        return seqType + "-" + String.format("%05d", sequence.getCurrentVal());
    }

    /**
     * ApprovalDocument Entity 생성
     *
     * @param employeeId 기안자 ID
     * @param dto        문서 요청 DTO
     * @param status     문서 상태
     * @return 생성된 문서 엔티티
     */
    private ApprovalDocument createApprovalDocument(
            Integer employeeId,
            ApprovalRequestDTO dto,
            String status
    ) {
        ApprovalTemplate templateEntity = templateRepository.findByTemplateKey(dto.getFormType());

        return ApprovalDocument.builder()
                .templateId(templateEntity.getTemplateId())
                .drafterId(employeeId)
                .title(dto.getTitle())
                .details(dto.getDetails())
                .docStatus(status)
                .build();
    }


    /**
     * 결재선 저장 (기안자 자동 승인)
     * <pre>
     * 저장 로직:
     * - seq=1 (기안자): APPROVED 상태로 저장, processDate 자동 설정
     * - seq>1 (결재자들): PENDING 상태로 저장
     *
     * 이유: 기안자는 문서를 작성하는 순간 승인한 것으로 간주
     * </pre>
     *
     * @param docId 문서 ID
     * @param lines 결재선 DTO 목록
     */
    private void saveApprovalLines(Integer docId, List<ApprovalLineDTO> lines) {
        for (ApprovalLineDTO lineDTO : lines) {
            String initialStatus = (lineDTO.getSeq() == 1) ? "APPROVED" : "PENDING";

            ApprovalLine.ApprovalLineBuilder builder = ApprovalLine.builder()
                    .docId(docId)
                    .approverId(lineDTO.getApproverId())
                    .seq(lineDTO.getSeq())
                    .lineStatus(initialStatus);

            if (lineDTO.getSeq() == 1) {
                builder.processDate(LocalDateTime.now());
            }

            ApprovalLine line = builder.build();
            lineRepository.save(line);

        }
    }


    /**
     * 참조자 저장
     * <pre>
     * 결재 프로세스에는 참여하지 않지만 문서 완료 시 알림을 받을 직원 목록 저장
     * </pre>
     *
     * @param docId      문서 ID
     * @param references 참조자 DTO 목록
     */
    private void saveReferences(Integer docId, List<ApprovalReferenceDTO> references) {
        for (ApprovalReferenceDTO refDTO : references) {
            ApprovalReference reference = ApprovalReference.builder()
                    .docId(docId)
                    .empId(refDTO.getReferencerId())
                    .build();

            referenceRepository.save(reference);
        }
    }


    /**
     * 첨부파일을 S3에 업로드하고 DB에 저장
     * <pre>
     * 처리 흐름:
     * 1. S3에 파일 업로드 (S3Service 호출)
     * 2. 반환된 S3 Key를 DB에 저장
     * 3. 원본 파일명, 파일 크기 등 메타데이터도 함께 저장
     * </pre>
     *
     * @param files    업로드할 파일 목록
     * @param document 문서 엔티티
     * @throws ApprovalFileUploadException S3 파일 업로드 실패 시
     */
    private void saveFilesToS3(List<MultipartFile> files, ApprovalDocument document) {
        for (MultipartFile file : files) {
            try {
                String s3Key = s3Service.uploadFile(file, "approval");

                ApprovalAttachment attachment = ApprovalAttachment.builder()
                        .document(document)
                        .originName(file.getOriginalFilename())
                        .savePath(s3Key)
                        .fileSize(file.getSize())
                        .build();

                attachmentRepository.save(attachment);

            } catch (BusinessException e) {
                throw new ApprovalFileUploadException(ErrorCode.FILE_UPLOAD_ERROR);
            }
        }
    }


    /**
     * 임시저장 문서 수정
     * <pre>
     * 검증:
     * - 문서 상태가 DRAFT인지 확인
     * - 작성자 본인인지 확인
     *
     * 처리 흐름:
     * 1. 문서 본문 업데이트
     * 2. 기존 결재선 삭제 후 재생성
     * 3. 기존 참조자 삭제 후 재생성
     * 4. 기존 첨부파일 삭제 (S3 및 DB)
     * 5. 새 파일 업로드 (S3)
     * </pre>
     *
     * @param employeeId 사원 ID
     * @param docId      문서 ID
     * @param dto        수정할 내용
     * @param files      새 첨부파일 목록
     * @return 수정된 문서 ID
     * @throws ApprovalDocumentNotFoundException 문서를 찾을 수 없는 경우
     * @throws InvalidApprovalStateException     문서 상태가 DRAFT가 아닌 경우
     * @throws ApprovalLineAuthorityException    작성자 본인이 아닌 경우
     * @throws ApprovalFileDeleteException       S3 파일 삭제 실패 시
     * @throws ApprovalFileUploadException       S3 파일 업로드 실패 시
     */
    @Transactional
    public Integer updateDraftDocument(
            Integer employeeId,
            Integer docId,
            ApprovalRequestDTO dto,
            List<MultipartFile> files
    ) {

        ApprovalDocument document = documentRepository.findById(docId)
                .orElseThrow(() -> new ApprovalDocumentNotFoundException(docId));

        if (!"DRAFT".equals(document.getDocStatus())) {
            throw new InvalidApprovalStateException(ErrorCode.DOCUMENT_NOT_DRAFT);
        }

        if (!document.getDrafterId().equals(employeeId)) {
            throw new ApprovalLineAuthorityException(ErrorCode.NOT_THE_DRAFTER);
        }

        document.updateTitle(dto.getTitle());
        document.updateDetails(dto.getDetails());

        lineRepository.deleteByDocId(docId);
        if (dto.getLines() != null && !dto.getLines().isEmpty()) {
            saveApprovalLines(docId, dto.getLines());
        }

        referenceRepository.deleteByDocId(docId);
        if (dto.getReferences() != null && !dto.getReferences().isEmpty()) {
            saveReferences(docId, dto.getReferences());
        }

        deleteAttachments(docId);

        if (files != null && !files.isEmpty()) {
            saveFilesToS3(files, document);
        }

        return docId;
    }

    /**
     * 문서의 모든 첨부파일 삭제 (S3 및 DB)
     * <pre>
     * 처리 흐름:
     * 1. DB에서 첨부파일 목록 조회
     * 2. 각 파일에 대해 S3에서 삭제
     * 3. DB에서 첨부파일 레코드 삭제
     * </pre>
     *
     * @param docId 문서 ID
     * @throws ApprovalFileDeleteException S3 파일 삭제 실패 시
     */
    private void deleteAttachments(Integer docId) {
        List<ApprovalAttachment> existingFiles = attachmentRepository.findByDocumentDocId(docId);

        for (ApprovalAttachment attachment : existingFiles) {
            try {
                s3Service.deleteFile(attachment.getSavePath());
            } catch (BusinessException e) {
                throw new ApprovalFileDeleteException(ErrorCode.FILE_DELETE_ERROR);
            }
        }
        attachmentRepository.deleteByDocumentDocId(docId);
    }

    /* ========================================== */
    /* 임시저장 문서 상신 */
    /* ========================================== */

    /**
     * 임시저장 문서를 상신 처리
     * <pre>
     * 검증:
     * - 문서 상태가 DRAFT인지 확인
     * - 작성자 본인인지 확인
     *
     * 처리 흐름:
     * 1. 문서 본문 업데이트
     * 2. 기존 결재선 삭제 후 재생성
     * 3. 기존 참조자 삭제 후 재생성
     * 4. 기존 첨부파일 삭제 후 새 파일 업로드
     * 5. 결재선 확인
     *    - 결재선이 1단계(기안)만 있으면 자동 승인 처리
     *    - 2단계 이상이면 INPROGRESS 상태로 변경
     *
     * 자동 승인 조건:
     * - 모든 결재선의 seq가 1인 경우 (기안자만 있는 경우)
     * - 문서 번호 생성 및 승인 완료 이벤트 발행
     * </pre>
     *
     * @param employeeId 사원 ID
     * @param docId      문서 ID
     * @param dto        수정할 내용
     * @param files      새 첨부파일 목록
     * @return 상신된 문서 ID
     * @throws ApprovalDocumentNotFoundException  문서를 찾을 수 없는 경우
     * @throws InvalidApprovalStateException      문서 상태가 DRAFT가 아닌 경우
     * @throws ApprovalDocumentAuthorityException 작성자 본인이 아닌 경우
     * @throws ApprovalFileDeleteException        S3 파일 삭제 실패 시
     * @throws ApprovalFileUploadException        S3 파일 업로드 실패 시
     */
    @Transactional
    public Integer submitDraftDocument(
            Integer employeeId,
            Integer docId,
            ApprovalRequestDTO dto,
            List<MultipartFile> files
    ) {

        ApprovalDocument document = documentRepository.findById(docId)
                .orElseThrow(() -> new ApprovalDocumentNotFoundException(docId));

        if (!"DRAFT".equals(document.getDocStatus())) {
            throw new InvalidApprovalStateException(ErrorCode.DOCUMENT_NOT_DRAFT);
        }

        if (!document.getDrafterId().equals(employeeId)) {
            throw new ApprovalDocumentAuthorityException(ErrorCode.NOT_THE_DRAFTER);
        }

        document.updateTitle(dto.getTitle());
        document.updateDetails(dto.getDetails());

        lineRepository.deleteByDocId(docId);
        if (dto.getLines() != null && !dto.getLines().isEmpty()) {
            saveApprovalLines(docId, dto.getLines());
        }

        referenceRepository.deleteByDocId(docId);
        if (dto.getReferences() != null && !dto.getReferences().isEmpty()) {
            saveReferences(docId, dto.getReferences());
        }

        deleteAttachments(docId);

        if (files != null && !files.isEmpty()) {
            saveFilesToS3(files, document);
        }

        List<ApprovalLine> lines = lineRepository.findByDocIdOrderBySeqAsc(docId);
        boolean onlyDrafter = lines.stream()
                .allMatch(line -> line.getSeq() == 1);

        if (onlyDrafter) {
            document.complete();
            String docNo = generateDocNo();
            document.assignDocNo(docNo);
            documentRepository.save(document);

            publishApprovalCompletedEvent(document);
        } else {
            ApprovalLine firstApprover = lines.stream()
                    .filter(line -> line.getSeq() == 2)
                    .findFirst()
                    .orElse(null);

            if (firstApprover != null) {
                publishApprovalRequestEvent(document, firstApprover);
            }

            document.changeStatus("INPROGRESS");
            documentRepository.save(document);
        }

        return docId;
    }


    /**
     * 결재 처리 (승인/반려)
     * <pre>
     * 검증:
     * - 유효한 액션인지 확인 (APPROVE / REJECT)
     * - 결재자 본인인지 확인
     * - 결재선 상태가 PENDING인지 확인
     * - 문서 상태가 INPROGRESS인지 확인
     *
     * 승인 처리 흐름:
     * 1. 결재선 상태를 APPROVED로 변경
     * 2. 모든 결재선이 승인되었는지 확인
     *    - 모두 승인: 문서를 APPROVED로 변경, 승인 완료 이벤트 발행
     *    - 대기중 있음: 문서를 INPROGRESS 유지
     *
     * 반려 처리 흐름:
     * 1. 결재선 상태를 REJECTED로 변경, 반려 사유 저장
     * 2. 문서를 REJECTED로 변경
     * 3. 반려 이벤트 발행
     * </pre>
     *
     * @param request    결재 처리 요청 (docId, lineId, action, comment)
     * @param employeeId 결재자 ID
     * @return 처리 결과 (성공 여부, 메시지, 문서 상태, 문서 번호)
     * @throws InvalidApprovalStateException     유효하지 않은 액션이거나 반려 사유 누락 시
     * @throws ApprovalLineNotFoundException     결재선을 찾을 수 없는 경우
     * @throws ApprovalLineAuthorityException    결재자 본인이 아닌 경우
     * @throws InvalidApprovalStateException     결재선 상태가 PENDING이 아닌 경우 (이미 처리됨)
     * @throws ApprovalDocumentNotFoundException 문서를 찾을 수 없는 경우
     * @throws InvalidApprovalStateException     문서 상태가 INPROGRESS가 아닌 경우
     */
    @Transactional
    public ApprovalActionResponseDTO processApproval(
            ApprovalActionRequestDTO request,
            Integer employeeId
    ) {
        validateApprovalAction(request);

        ApprovalLine line = lineRepository.findById(request.getLineId())
                .orElseThrow(() -> new ApprovalLineNotFoundException(ErrorCode.LINE_NOT_FOUND));

        if (!line.getApproverId().equals(employeeId)) {
            throw new ApprovalLineAuthorityException(ErrorCode.NOT_THE_APPROVER);
        }

        if (!"PENDING".equals(line.getLineStatus())) {
            throw new InvalidApprovalStateException(ErrorCode.ALREADY_PROCESSED_APPROVAL);
        }

        ApprovalDocument document = documentRepository.findById(request.getDocId())
                .orElseThrow(() -> new ApprovalDocumentNotFoundException(request.getDocId()));

        if (!"INPROGRESS".equals(document.getDocStatus())) {
            throw new InvalidApprovalStateException(ErrorCode.DOCUMENT_NOT_IN_PROGRESS);
        }

        if ("REJECT".equals(request.getAction())) {
            line.reject(request.getComment());
            document.reject();

            publishApprovalRejectedEvent(document, request.getComment());

            publishApprovalRejectedNotificationEvent(document, request.getComment(), employeeId);

            return ApprovalActionResponseDTO.builder()
                    .success(true)
                    .message("반려 처리 완료")
                    .docStatus("REJECTED")
                    .build();
        } else {
            line.approve();

            List<ApprovalLine> allLines = lineRepository.findByDocIdOrderBySeqAsc(request.getDocId());
            boolean allApproved = allLines.stream()
                    .filter(l -> l.getSeq() > 0)
                    .allMatch(l -> "APPROVED".equals(l.getLineStatus()));

            if (allApproved) {
                document.complete();

                if (document.getDocNo() == null || document.getDocNo().isEmpty()) {
                    String docNo = generateDocNo();
                    document.assignDocNo(docNo);
                }

                publishApprovalCompletedEvent(document);

                publishApprovalCompletedNotificationEvent(document, employeeId);

                return ApprovalActionResponseDTO.builder()
                        .success(true)
                        .message("최종 승인 완료")
                        .docStatus("APPROVED")
                        .docNo(document.getDocNo())
                        .build();
            } else {

                ApprovalLine nextApprover = allLines.stream()
                        .filter(l -> "PENDING".equals(l.getLineStatus()))
                        .findFirst()
                        .orElse(null);

                if (nextApprover != null) {
                    publishApprovalRequestEvent(document, nextApprover);
                }

                document.changeStatus("INPROGRESS");

                return ApprovalActionResponseDTO.builder()
                        .success(true)
                        .message("승인 처리 완료")
                        .docStatus("INPROGRESS")
                        .build();
            }
        }
    }

    /**
     * 결재 완료 이벤트 발행
     * <pre>
     * 다른 도메인(휴가, 근태 등)에서 이 이벤트를 수신하여 후속 처리 진행
     * </pre>
     *
     * @param document 승인 완료된 문서
     */
    private void publishApprovalCompletedEvent(ApprovalDocument document) {
        ApprovalTemplate template = templateRepository.findByTemplateId(document.getTemplateId());
        ApprovalCompletedEvent event = new ApprovalCompletedEvent(
                document.getDocId(),
                template.getTemplateKey(),
                document.getDetails(),
                document.getDrafterId(),
                document.getTitle()
        );

        eventPublisher.publishEvent(event);
    }

    /**
     * 결재 반려 이벤트 발행
     * <pre>
     * 알림 발송 등의 후속 처리를 위한 이벤트 발행
     * </pre>
     *
     * @param document 반려된 문서
     * @param comment  반려 사유
     */
    private void publishApprovalRejectedEvent(ApprovalDocument document, String comment) {
        ApprovalTemplate template = templateRepository.findByTemplateId(document.getTemplateId());
        ApprovalRejectedEvent event = new ApprovalRejectedEvent(
                document.getDocId(),
                template.getTemplateKey(),
                document.getDetails(),
                document.getDrafterId(),
                comment
        );

        eventPublisher.publishEvent(event);
    }

    /**
     * 결재 액션 유효성 검증
     * <pre>
     * 검증 항목:
     * - 액션이 APPROVE 또는 REJECT인지 확인
     * - REJECT인 경우 반려 사유가 있는지 확인
     * </pre>
     *
     * @param request 결재 처리 요청
     * @throws InvalidApprovalStateException 유효하지 않은 액션이거나 반려 사유 누락 시
     */
    private void validateApprovalAction(ApprovalActionRequestDTO request) {
        if (!"APPROVE".equals(request.getAction()) && !"REJECT".equals(request.getAction())) {
            throw new InvalidApprovalStateException(ErrorCode.INVALID_ACTION);
        }

        if ("REJECT".equals(request.getAction()) &&
                (request.getComment() == null || request.getComment().trim().isEmpty())) {
            throw new InvalidApprovalStateException(ErrorCode.MISSING_REJECTION_COMMENT);
        }
    }


    /**
     * 결재 대기 중인 문서 회수
     * <pre>
     * 용도: 상신한 문서를 다시 임시저장 상태로 되돌림
     * 조건: INPROGRESS 문서만 회수 가능
     *
     * 처리: INPROGRESS → DRAFT 상태 변경
     * </pre>
     *
     * @param docId 문서 ID
     * @return 성공 메시지
     * @throws ApprovalDocumentNotFoundException 문서를 찾을 수 없는 경우
     * @throws InvalidApprovalStateException     문서 상태가 INPROGRESS가 아닌 경우
     */
    @Transactional
    public String cancelDocument(Integer docId) {
        ApprovalDocument document = documentRepository.findById(docId)
                .orElseThrow(() -> new ApprovalDocumentNotFoundException(docId));

        if (!"INPROGRESS".equals(document.getDocStatus())) {
            throw new InvalidApprovalStateException(ErrorCode.DOCUMENT_NOT_IN_PROGRESS);
        }

        document.changeStatus("DRAFT");
        documentRepository.save(document);

        publishApprovalRecalledEvent(document);

        return "회수가 완료되었습니다.";

    }

    /**
     * 임시저장 문서 삭제
     * <pre>
     * 처리 흐름:
     * 1. 첨부파일 삭제 (S3 및 DB)
     * 2. 결재선 삭제
     * 3. 참조자 삭제
     * 4. 문서 삭제
     *
     * 주의: 연관된 모든 데이터를 완전히 삭제하며 복구 불가능
     * </pre>
     *
     * @param docId 문서 ID
     * @return 성공 메시지
     * @throws ApprovalFileDeleteException S3 파일 삭제 실패 시
     */
    @Transactional
    public String deleteDocument(Integer docId) {
        try {
            deleteAttachments(docId);

            lineRepository.deleteByDocId(docId);

            referenceRepository.deleteByDocId(docId);

            documentRepository.deleteById(docId);

        } catch (BusinessException ex) {
            throw new ApprovalFileDeleteException(ErrorCode.FILE_DELETE_ERROR);
        }

        return "삭제를 성공하였습니다.";
    }
    /* ========================================== */
    /* 신규 추가: 알림 헬퍼 메서드 */
    /* ========================================== */

    /**
     * 기안자 이름 조회
     * <pre>
     * 알림 메시지에 포함될 기안자 이름을 조회
     * 조회 실패 시 "알 수 없음" 반환
     * </pre>
     *
     * @param employeeId 사원 ID
     * @return 사원 이름 (조회 실패 시 "")
     */
    private String getDrafterName(Integer employeeId) {
        return employeeRepository.findById(employeeId)
                .map(emp -> emp.getEmployeeName())
                .orElse("");
    }

    /**
     * 결재 요청 알림 이벤트 발행 (결재자에게)
     * <pre>
     * 호출 시점:
     * - 문서 상신 시 다음 결재자에게
     * - 중간 승인 후 다음 결재자에게
     *
     * 수신자: 결재 대기중인 다음 결재자
     * 알림 내용: "OOO님의 'XXX' 문서 결재 요청이 도착했습니다."
     * </pre>
     *
     * @param document 결재 문서
     * @param approver 결재 대기중인 결재선 (다음 결재자)
     */
    private void publishApprovalRequestEvent(ApprovalDocument document, ApprovalLine approver) {
        ApprovalTemplate template = templateRepository.findByTemplateId(document.getTemplateId());

        ApprovalNotificationEvent.ApprovalRequestEvent event =
                ApprovalNotificationEvent.ApprovalRequestEvent.builder()
                        .docId(document.getDocId())
                        .templateKey(template.getTemplateKey())
                        .title(document.getTitle())
                        .drafterId(document.getDrafterId())
                        .drafterName(getDrafterName(document.getDrafterId()))
                        .approverId(approver.getApproverId())
                        .seq(approver.getSeq())
                        .requestedAt(LocalDateTime.now())
                        .build();

        eventPublisher.publishEvent(event);
    }

    /**
     * 결재 반려 알림 이벤트 발행 (기안자에게)
     * <pre>
     * 호출 시점: 결재자가 문서를 반려했을 때
     *
     * 수신자: 문서 기안자
     * 알림 내용: "'XXX' 문서가 반려되었습니다. 사유: OOO"
     * </pre>
     *
     * @param document   반려된 문서
     * @param comment    반려 사유
     * @param rejecterId 반려자 ID
     */
    private void publishApprovalRejectedNotificationEvent(ApprovalDocument document, String comment, Integer rejecterId) {
        ApprovalTemplate template = templateRepository.findByTemplateId(document.getTemplateId());

        ApprovalNotificationEvent.ApprovalRejectedEvent event =
                ApprovalNotificationEvent.ApprovalRejectedEvent.builder()
                        .docId(document.getDocId())
                        .templateKey(template.getTemplateKey())
                        .title(document.getTitle())
                        .drafterId(document.getDrafterId())
                        .rejecterId(rejecterId)
                        .rejecterName(getDrafterName(rejecterId))
                        .comment(comment)
                        .rejectedAt(LocalDateTime.now())
                        .build();

        eventPublisher.publishEvent(event);
    }

    /**
     * 최종 승인 완료 알림 이벤트 발행 (기안자에게)
     * <pre>
     * 호출 시점: 모든 결재선이 승인 완료되었을 때
     *
     * 수신자: 문서 기안자
     * 알림 내용: "'XXX' 문서가 최종 승인되었습니다."
     * </pre>
     *
     * @param document        최종 승인된 문서
     * @param finalApproverId 최종 승인자 ID
     */
    private void publishApprovalCompletedNotificationEvent(ApprovalDocument document, Integer finalApproverId) {
        ApprovalTemplate template = templateRepository.findByTemplateId(document.getTemplateId());

        ApprovalNotificationEvent.ApprovalCompletedEvent event =
                ApprovalNotificationEvent.ApprovalCompletedEvent.builder()
                        .docId(document.getDocId())
                        .templateKey(template.getTemplateKey())
                        .title(document.getTitle())
                        .drafterId(document.getDrafterId())
                        .approverId(finalApproverId)
                        .approverName(getDrafterName(finalApproverId))
                        .completedAt(LocalDateTime.now())
                        .build();

        eventPublisher.publishEvent(event);
    }

    /**
     * 회수 완료 알림 이벤트 발행 (기안자에게)
     * <pre>
     * 호출 시점: 기안자가 진행중인 문서를 회수했을 때
     *
     * 수신자: 문서 기안자
     * 알림 내용: "'XXX' 문서가 성공적으로 회수되었습니다."
     * </pre>
     *
     * @param document 회수된 문서
     */
    private void publishApprovalRecalledEvent(ApprovalDocument document) {
        ApprovalTemplate template = templateRepository.findByTemplateId(document.getTemplateId());

        ApprovalNotificationEvent.ApprovalRecalledEvent event =
                ApprovalNotificationEvent.ApprovalRecalledEvent.builder()
                        .docId(document.getDocId())
                        .templateKey(template.getTemplateKey())
                        .title(document.getTitle())
                        .drafterId(document.getDrafterId())
                        .recalledAt(LocalDateTime.now())
                        .build();

        eventPublisher.publishEvent(event);
    }

    /**
     * 결재 대기 독촉 알림 이벤트 발행 (결재자에게)
     * <pre>
     * 호출 시점:
     * - 스케줄러가 매일 오전 9시 자동 실행
     * - N일 이상 대기중인 문서의 결재자에게 발송
     *
     * 수신자: 결재 대기중인 결재자
     * 알림 내용: "OOO님의 'XXX' 문서가 N일째 결재 대기 중입니다."
     *
     * 사용처: ApprovalReminderScheduler에서 호출
     * </pre>
     *
     * @param document    대기중인 문서
     * @param approver    대기중인 결재자
     * @param waitingDays 대기 일수
     */
    public void publishApprovalReminderEvent(ApprovalDocument document, ApprovalLine approver, int waitingDays) {
        ApprovalTemplate template = templateRepository.findByTemplateId(document.getTemplateId());

        ApprovalNotificationEvent.ApprovalReminderEvent event =
                ApprovalNotificationEvent.ApprovalReminderEvent.builder()
                        .docId(document.getDocId())
                        .templateKey(template.getTemplateKey())
                        .title(document.getTitle())
                        .drafterId(document.getDrafterId())
                        .drafterName(getDrafterName(document.getDrafterId()))
                        .approverId(approver.getApproverId())
                        .waitingDays(waitingDays)
                        .requestedAt(document.getCreatedAt())
                        .build();

        eventPublisher.publishEvent(event);
    }
}