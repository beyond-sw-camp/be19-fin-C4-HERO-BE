package com.c4.hero.domain.payroll.bankaccount.controller;

import com.c4.hero.domain.auth.security.CustomUserDetails;
import com.c4.hero.domain.auth.security.LoginOnly;
import com.c4.hero.domain.payroll.bankaccount.dto.BankAccountCreateRequestDTO;
import com.c4.hero.domain.payroll.bankaccount.dto.BankAccountDTO;
import com.c4.hero.domain.payroll.bankaccount.service.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <pre>
 * Class Name: BankAccountController
 * Description: 사원 계좌 관련 컨트롤러
 *
 * History
 * 2025/12/08 - 동근 최초 작성
 * 2025/12/19 - 동근 swagger 문서화 주석 추가
 * 2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.2
 */

@LoginOnly
@RestController
@RequestMapping("/api/me/payroll")
@RequiredArgsConstructor
@Tag(name = "급여 계좌 API", description = "사원 본인 급여 수령 계좌 CRUD 및 대표 계좌 설정 API")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    /*
    내 계좌 목록 조회
    @param principal 사용자 인증 정보
    @return 계좌 목록
     */
    @Operation(summary = "내 계좌 목록 조회", description = "사원 본인의 등록된 계좌 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BankAccountDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping("/bank-accounts")
    public ResponseEntity<List<BankAccountDTO>> getMyBankAccounts(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Integer employeeId = user.getEmployeeId();
        List<BankAccountDTO> accounts = bankAccountService.getMyBankAccounts(employeeId);
        return ResponseEntity.ok(accounts);
    }

     /*
     새 계좌 추가
     @param request 계좌 생성 요청 DTO
     @param principal 사용자 인증 정보
     @return 생성된 계좌 정보
      */
     @Operation(summary = "내 계좌 추가", description = "사원 본인의 새 계좌를 등록합니다.")
     @ApiResponses(value = {
             @ApiResponse(responseCode = "200", description = "등록 성공",
                     content = @Content(schema = @Schema(implementation = BankAccountDTO.class))),
             @ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음", content = @Content),
             @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
             @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
             @ApiResponse(responseCode = "409", description = "중복 계좌/정책 위반 등으로 등록 불가", content = @Content)
     })
    @PostMapping("/bank-accounts")
    public ResponseEntity<BankAccountDTO> createMyBankAccount(
            @RequestBody BankAccountCreateRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {Integer employeeId = user.getEmployeeId();
        BankAccountDTO created = bankAccountService.createMyBankAccount(employeeId, request);
        return ResponseEntity.ok(created);
    }

    /*
    급여 수령 계좌 설정
    @param bankAccountId 계좌 ID
    @param principal 사용자 인증 정보
    @return noContent() 204 상태코드 반환
     */
    @Operation(summary = "대표(급여 수령) 계좌 설정", description = "지정한 계좌를 대표(급여 수령) 계좌로 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "설정 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음", content = @Content)
    })
    @PutMapping("/bank-accounts/{bankAccountId}/primary")
    public ResponseEntity<Void> setPrimaryBankAccount(
            @PathVariable Integer bankAccountId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Integer employeeId = user.getEmployeeId();
        bankAccountService.setPrimaryBankAccount(employeeId, bankAccountId);
        return ResponseEntity.noContent().build();
    }


    /**
     * 계좌 정보 수정
     * @param bankAccountId 계좌 ID
     * @param request 수정할 계좌 정보
     */
    @Operation(summary = "내 계좌 정보 수정", description = "사원 본인의 계좌 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "수정 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "정책 위반/중복 등으로 수정 불가", content = @Content)
    })
    @PutMapping("/bank-accounts/{bankAccountId}")
    public ResponseEntity<Void> updateMyBankAccount(
            @PathVariable Integer bankAccountId,
            @RequestBody BankAccountCreateRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Integer employeeId = user.getEmployeeId();
        bankAccountService.updateMyBankAccount(employeeId, bankAccountId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 계좌 삭제
     * @param bankAccountId 계좌 ID
     */
    @Operation(summary = "내 계좌 삭제", description = "사원 본인의 계좌를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "지급 이력 존재 등으로 삭제 불가", content = @Content)
    })
    @DeleteMapping("/bank-accounts/{bankAccountId}")
    public ResponseEntity<Void> deleteMyBankAccount(
            @PathVariable Integer bankAccountId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Integer employeeId = user.getEmployeeId();
        bankAccountService.deleteMyBankAccount(employeeId, bankAccountId);
        return ResponseEntity.noContent().build();
    }
}
