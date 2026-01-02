package com.c4.hero.domain.evaluation.controller;

import com.c4.hero.common.response.CustomResponse;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.evaluation.dto.dashboard.DashBoardResponseDTO;
import com.c4.hero.domain.evaluation.dto.employee.EmployeeResponseDTO;
import com.c4.hero.domain.evaluation.dto.evaluation.EvaluationRequestDTO;
import com.c4.hero.domain.evaluation.dto.evaluation.EvaluationResponseDTO;
import com.c4.hero.domain.evaluation.dto.form.EvaluationFormRequestDTO;
import com.c4.hero.domain.evaluation.dto.form.EvaluationFormResponseDTO;
import com.c4.hero.domain.evaluation.dto.form.EvaluationFormUpdateDTO;
import com.c4.hero.domain.evaluation.dto.guide.EvaluationGuideRequestDTO;
import com.c4.hero.domain.evaluation.dto.guide.EvaluationGuideResponseDTO;
import com.c4.hero.domain.evaluation.dto.guide.EvaluationGuideUpdateDTO;
import com.c4.hero.domain.evaluation.dto.response.EmployeeEvaluationListResponseDTO;
import com.c4.hero.domain.evaluation.dto.template.EvaluationTemplateRequestDTO;
import com.c4.hero.domain.evaluation.dto.template.EvaluationTemplateResponseDTO;
import com.c4.hero.domain.evaluation.dto.template.EvaluationTemplateUpdateDTO;
import com.c4.hero.domain.evaluation.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationController
 * Description: 평가 관련 컨트롤러 로직 처리
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
public class EvaluationController {


    /** 평가 관련 서비스 의존성 주입 */
    private final EvaluationService evaluationService;

    /**
     * 평가 템플릿 전체 조회
     *
     * @return result PageResponse<EvaluationTemplateResponseDTO>
     *     전체 평가 템플릿 데이터를 응답함
     */
    @Operation(
            summary = "평가 템플릿 전체 조회",
            description = "페이징 처리된 전체 평가 템플릿 목록을 조회한다."
    )
    @GetMapping("/evaluation-template/all")
    public ResponseEntity<PageResponse<EvaluationTemplateResponseDTO>> selectAllTemplate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<EvaluationTemplateResponseDTO> result = evaluationService.selectAllTemplate(page, size);
        return ResponseEntity.ok(
                result
        );
    }


    /**
     * 평가 템플릿 template_id로 조회
     *
     * @param id Integer
     *      평가 템플릿 키(template_id)를 파라미터로 받음.
     * @return result EvaluationTemplateResponseDTO
     *     평가 템플릿 테이블의 pk로 특정 평가 템플릿 데이터를 응답함
     */
    @Operation(
            summary = "평가 템플릿 단건 조회",
            description = "template_id로 특정 평가 템플릿을 조회한다."
    )
    @GetMapping("/evaluation-template/{id}")
    public ResponseEntity<EvaluationTemplateResponseDTO> selectTemplate(@PathVariable Integer id){

        EvaluationTemplateResponseDTO result = evaluationService.selectTemplate(id);

        return ResponseEntity.ok(result);
    }


    /**
     * 평가 템플릿 생성
     *
     * @param evaluationTemplateDTO EvaluationTemplateRequestDTO
     *      평가 템플릿 생성 데이터를 파라미터로 받음.
     * @return id Integer
     *     생성된 평가 템플릿 테이블의 pk를 응답함
     */
    @Operation(
            summary = "평가 템플릿 생성",
            description = "새로운 평가 템플릿을 생성한다."
    )
    @PostMapping("/evaluation-template")
    public ResponseEntity<Integer> createTemplate(@RequestBody EvaluationTemplateRequestDTO evaluationTemplateDTO){

        Integer id = evaluationService.createTemplate(evaluationTemplateDTO);

        return ResponseEntity.ok(id);

    }


    /**
     * 평가 템플릿 수정
     *
     * @param evaluationTemplateDTO EvaluationTemplateUpdateDTO
     *      평가 템플릿 수정 데이터를 파라미터로 받음.
     * @return updatedId Integer
     *     수정된 평가 템플릿 테이블의 pk를 응답함.
     */
    @Operation(
            summary = "평가 템플릿 수정",
            description = "기존 평가 템플릿 정보를 수정한다."
    )
    @PutMapping("/evaluation-template")
    public ResponseEntity<Integer> updateTemplate(@RequestBody EvaluationTemplateUpdateDTO evaluationTemplateDTO){

        Integer updatedId = evaluationService.updateTemplate(evaluationTemplateDTO);

        return ResponseEntity.ok(updatedId);
    }


    /**
     * 평가 템플릿 template_id로 조회 후, 삭제
     *
     * @param id Integer
     *      삭제할 평가 템플릿의 키(template_id)를 클라이언트로 부터 요청
     * @return Void
     *     평가 템플릿 삭제 후 반환하는 값은 없음
     */
    @Operation(
            summary = "평가 템플릿 삭제",
            description = "template_id로 평가 템플릿을 삭제한다."
    )
    @DeleteMapping("/evaluation-template/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Integer id){
        evaluationService.deleteTemplate(id);

        return ResponseEntity.ok().build();
    }

    /**
     * 평가 가이드 전체 조회
     *
     * @return result PageResponse<EvaluationGuideResponseDTO>
     *     전체 평가 가이드 데이터를 반환
     */
    @Operation(
            summary = "평가 가이드 전체 조회",
            description = "페이징 처리된 전체 평가 가이드 목록을 조회한다."
    )
    @GetMapping("/evaluation-guide/all")
    public ResponseEntity<PageResponse<EvaluationGuideResponseDTO>> selectAllGuide(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<EvaluationGuideResponseDTO> result = evaluationService.selectAllGuide(page, size);

        return ResponseEntity.ok(result);
    }

    /**
     * 평가 가이드 전체 조회(평가 생성에 사용)
     *
     * @return result List<EvaluationGuideResponseDTO>
     *     전체 평가 가이드 데이터를 반환
     */
    @Operation(
            summary = "평가 가이드 전체 조회 (평가 생성에 사용)",
            description = "평가 생성 시 사용하기 위한 전체 평가 가이드 목록을 조회한다."
    )
    @GetMapping("/evaluation-guide/all2")
    public ResponseEntity<List<EvaluationGuideResponseDTO>> selectAllGuide2(){

        List<EvaluationGuideResponseDTO> result = evaluationService.selectAllGuide2();

        return ResponseEntity.ok(result);
    }

    /**
     * 평가 가이드 evaluation_guide_id로 조회
     *
     * @param id Integer
     *      클라이언트로 부터 evaluation_guide_id를 요청함
     * @return result EvaluationGuideResponseDTO
     *     조회된 평가 가이드 데이터를 반환
     */
    @Operation(
            summary = "평가 가이드 단건 조회",
            description = "evaluation_guide_id로 특정 평가 가이드를 조회한다."
    )
    @GetMapping("/evaluation-guide/{id}")
    public ResponseEntity<EvaluationGuideResponseDTO> selectGuide(@PathVariable Integer id){

        EvaluationGuideResponseDTO result = evaluationService.selectGuide(id);

        return ResponseEntity.ok(result);
    }


    /**
     * 평가 가이드 생성
     *
     * @param evaluationGuideRequestDTO EvaluationGuideRequestDTO
     *      클라이언트로 부터 받아온 평가 가이드 데이터
     * @return id Integer
     *     생성된 평가 가이드 테이블 pk 값 반환
     */
    @Operation(
            summary = "평가 가이드 생성",
            description = "새로운 평가 가이드를 생성한다."
    )
    @PostMapping("/evaluation-guide")
    public ResponseEntity<Integer> createGuide(@RequestBody EvaluationGuideRequestDTO evaluationGuideRequestDTO){

        Integer id = evaluationService.createGuide(evaluationGuideRequestDTO);

        return ResponseEntity.ok(id);
    }

    /**
     * 평가 가이드 수정
     *
     * @param evaluationGuideUpdateDTO EvaluationGuideUpdateDTO
     *      평가 가이드 수정 데이터를 파라미터로 받음.
     * @return updatedId Integer
     *     수정된 평가 가이드 테이블의 pk를 응답함.
     */
    @Operation(
            summary = "평가 가이드 수정",
            description = "기존 평가 가이드를 수정한다."
    )
    @PutMapping("/evaluation-guide")
    public ResponseEntity<Integer> updateGuide(@RequestBody EvaluationGuideUpdateDTO evaluationGuideUpdateDTO){

        Integer updateId = evaluationService.updateGuide(evaluationGuideUpdateDTO);

        return ResponseEntity.ok(updateId);
    }

    /**
     * 평가 가이드 evaluation_guide_id로 조회 후, 삭제
     *
     * @param id Integer
     *      삭제할 평가 가이드의 키(evaluation_guide_id)를 클라이언트로 부터 요청
     * @return Void
     *     평가 가이드 삭제 후 반환하는 값은 없음
     */
    @Operation(
            summary = "평가 가이드 삭제",
            description = "evaluation_guide_id로 평가 가이드를 삭제한다."
    )
    @DeleteMapping("/evaluation-guide/{id}")
    public ResponseEntity<Void> deleteGuide(@PathVariable Integer id){
        evaluationService.deleteGuide(id);

        return ResponseEntity.ok().build();
    }

    /**
     * 피평가자들 department_id로 조회
     *
     * @param id Integer
     *      삭제할 평가 가이드의 키(evaluation_guide_id)를 클라이언트로 부터 요청
     * @return result List<EmployeeResponseDTO>
     *     평가 가이드 삭제 후 반환하는 값은 없음
     */
    @Operation(
            summary = "부서별 피평가자 조회",
            description = "department_id로 해당 부서의 피평가자 목록을 조회한다."
    )
    @GetMapping("/evaluation/employee/{id}")
    public ResponseEntity<List<EmployeeResponseDTO>> selectEmployeeByDepartmentId(@PathVariable Integer id){

        List<EmployeeResponseDTO> result = evaluationService.selectEmployeeByDepartmentId(id);

        return ResponseEntity.ok(result);
    }


    /**
     * 평가 전체 조회
     *
     * @return result List<EvaluationResponseDTO>
     *     전체 평가 데이터를 응답함
     */
    @Operation(
            summary = "평가 전체 조회",
            description = "전체 평가 목록을 페이징 조회한다."
    )
    @GetMapping("/evaluation/all")
    public ResponseEntity<PageResponse<EvaluationResponseDTO>> selectAllEvaluation(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<EvaluationResponseDTO> result =
                evaluationService.selectAllEvaluation(page, size);

        return ResponseEntity.ok(result);
    }

    /**
     * 평가 evaluation_id로 조회
     *
     * @return result EvaluationResponseDTO
     *     evaluation_id로 조회한 평가 데이터를 응답함
     */
    @Operation(
            summary = "평가 단건 조회",
            description = "evaluation_id로 특정 평가를 조회한다."
    )
    @GetMapping("/evaluation/{id}")
    public ResponseEntity<EvaluationResponseDTO> selectEvaluation(@PathVariable Integer id){

        EvaluationResponseDTO result = evaluationService.selectEvaluation(id);

        return ResponseEntity.ok(result);
    }



    /**
     * 평가 생성
     *
     * @param evaluationRequestDTO EvaluationRequestDTO
     *      클라이언트로 부터 받아온 생성할 평가 데이터
     * @return id Integer
     *     생성된 평가 pk 값 반환
     */
    @Operation(
            summary = "평가 생성",
            description = "평가 템플릿을 기반으로 평가를 생성한다."
    )
    @PostMapping("/evaluation")
    public ResponseEntity<Integer> createEvaluation(@RequestBody EvaluationRequestDTO evaluationRequestDTO){

        Integer id = evaluationService.createEvaluation(evaluationRequestDTO);

        return ResponseEntity.ok(id);
    }

    /**
     * 평가 템플릿 evaluation_id로 조회 후, 삭제
     *
     * @param id Integer
     *      삭제할 평가 키(evaluation_id)를 클라이언트로 부터 요청
     * @return Void
     *     평가 삭제 후 반환하는 값은 없음
     */
    @Operation(
            summary = "평가 삭제",
            description = "evaluation_id로 평가를 삭제한다."
    )
    @DeleteMapping("/evaluation/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Integer id) {

        evaluationService.deleteEvaluation(id);

        return ResponseEntity.ok().build();
    }

    /**
     * 평가서 전체 조회
     *
     * @return result List<EvaluationFormResponseDTO>
     *     전체 평가서 데이터를 응답함
     */
    @Operation(
            summary = "평가서 전체 조회",
            description = "전체 평가서 목록을 조회한다."
    )
    @GetMapping("/evaluation-form/all")
    public ResponseEntity<List<EvaluationFormResponseDTO>> selectAllForm(){

        List<EvaluationFormResponseDTO> result = evaluationService.selectAllForm();

        return ResponseEntity.ok(result);
    }

    /**
     * 평가서 form_id로 조회 조회
     * @param id Integer
     *      평가서
     * @return result List<EvaluationFormResponseDTO>
     *     전체 평가서 데이터를 응답함
     */
    @Operation(
            summary = "평가서 단건 조회",
            description = "evaluation_id와 employee_id로 평가서를 조회한다."
    )
    @GetMapping("/evaluation-form/{evaluationId}/{employeeId}")
    public ResponseEntity<EvaluationFormResponseDTO> selectForm(@PathVariable Integer evaluationId, @PathVariable Integer employeeId){

        EvaluationFormResponseDTO result = evaluationService.selectForm(evaluationId, employeeId);

        return ResponseEntity.ok(result);
    }

    /**
     * 평가서 생성
     *
     * @param evaluationFormRequestDTO EvaluationFormRequestDTO
     *      평가서 생성 데이터를 파라미터로 받음.
     * @return id Integer
     *     생성된 평가서 테이블의 pk를 응답함
     */
    @Operation(
            summary = "평가서 생성",
            description = "피평가자에 대한 평가서를 생성한다."
    )
    @PostMapping("/evaluation-form")
    public ResponseEntity<Integer> createForm(@RequestBody EvaluationFormRequestDTO evaluationFormRequestDTO){

         Integer id = evaluationService.createForm(evaluationFormRequestDTO);

         return ResponseEntity.ok(id);
    }

    /**
     * 평가서 수정
     *
     * @param updateDTO EvaluationFormUpdateDTO
     *      평가서 수정 데이터를 파라미터로 받음.
     * @return updatedId Integer
     *     수정된 평가서 테이블의 pk를 응답함.
     */
    @Operation(
            summary = "평가서 수정",
            description = "평가서를 수정한다."
    )
    @PutMapping("/evaluation-form")
    public ResponseEntity<Integer> updateForm(@RequestBody EvaluationFormUpdateDTO updateDTO) {

        Integer updatedId = evaluationService.updateForm(updateDTO);

        return ResponseEntity.ok(updatedId);
    }


    /**
     * 평가서 채점
     *
     * @param updateDTO EvaluationFormUpdateDTO
     *      평가서 채점 데이터를 파라미터로 받음.
     * @return updatedId Integer
     *     채점된 평가서 테이블의 pk를 응답함.
     */
    @Operation(
            summary = "평가서 채점",
            description = "평가서를 채점 처리한다."
    )
    @PutMapping("/evaluation-form/grading")
    public ResponseEntity<Integer> gradingForm(@RequestBody EvaluationFormUpdateDTO updateDTO) {

        Integer updatedId = evaluationService.gradingForm(updateDTO);

        return ResponseEntity.ok(updatedId);
    }

    /**
     * 전체 대시보드 데이터 조회
     *
     * @return result List<DashBoardResponseDTO>
     *     전체 대시보드 데이터를 응답함.
     */
    @Operation(
            summary = "대시보드 데이터 전체 조회",
            description = "전체 부서의 대시보드 데이터를 조회한다."
    )
    @GetMapping("/dashboard/all")
    public ResponseEntity<List<DashBoardResponseDTO>> selectAllDashBoard(){

        List<DashBoardResponseDTO> result = evaluationService.selectAllDashBoard();

        return ResponseEntity.ok(result);
    }

    /**
     * 대시보드 데이터 department_id로 조회
     *
     * @param id Integer
     *     부서 ID를 요청함
     *
     * @return result List<DashBoardResponseDTO>
     *     부서 ID로 조회된 대시보드 데이터를 응답함.
     */
    @Operation(
            summary = "부서별 대시보드 데이터 조회",
            description = "department_id로 특정 부서의 대시보드 데이터를 조회한다."
    )
    @GetMapping("/dashboard/{id}")
    public ResponseEntity<List<DashBoardResponseDTO>> selectDashBoard(@PathVariable Integer id){

        List<DashBoardResponseDTO> result = evaluationService.selectDashBoard(id);

        return ResponseEntity.ok(result);
    }

    /**
     * 특정 직원의 평가 결과 목록 조회
     *
     * @param employeeId 직원 ID
     * @return 평가 결과 목록
     */
    @Operation(
            summary = "직원별 평가 결과 목록 조회",
            description = "특정 직원의 평가 결과 목록(evaluationId, 평가명, 등급, 생성일)을 조회한다."
    )
    @GetMapping("/evaluation-form/list/{employeeId}")
    public ResponseEntity<CustomResponse<List<EmployeeEvaluationListResponseDTO>>> getEmployeeEvaluationList(@PathVariable Integer employeeId) {
        List<EmployeeEvaluationListResponseDTO> result = evaluationService.getEmployeeEvaluationList(employeeId);
        return ResponseEntity.ok(CustomResponse.success(result));
    }
}
