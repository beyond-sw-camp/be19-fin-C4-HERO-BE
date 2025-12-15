package com.c4.hero.domain.evaluation.controller;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.evaluation.dto.*;
import com.c4.hero.domain.evaluation.service.EvaluationService;
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
    @GetMapping("/evaluation-template/selectall")
//    public ResponseEntity<List<EvaluationTemplateResponseDTO>> selectAllTemplate(){
//
//        List<EvaluationTemplateResponseDTO> result = evaluationService.selectAllTemplate();
//
//        return ResponseEntity.ok(result);
//    }
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
    @GetMapping("/evaluation-template/select/{id}")
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
    @PostMapping("/evaluation-template/create")
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
    @PutMapping("/evaluation-template/update")
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
    @DeleteMapping("/evaluation-template/delete/{id}")
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
    @GetMapping("/evaluation-guide/selectall")
//    public ResponseEntity<List<EvaluationGuideResponseDTO>> selectAllGuide(){
//
//        List<EvaluationGuideResponseDTO> result = evaluationService.selectAllGuide();
//
//        return ResponseEntity.ok(result);
//    }
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
    @GetMapping("/evaluation-guide/selectall2")
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
    @GetMapping("/evaluation-guide/select/{id}")
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
    @PostMapping("/evaluation-guide/create")
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
    @PutMapping("/evaluation-guide/update")
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
    @DeleteMapping("/evaluation-guide/delete/{id}")
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
    @GetMapping("/evaluation/select/employee/{id}")
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
    @GetMapping("/evaluation/selectall")
//    public ResponseEntity<List<EvaluationResponseDTO>> selectAllEvaluation(){
//
//        List<EvaluationResponseDTO> result = evaluationService.selectAllEvaluation();
//
//        return ResponseEntity.ok(result);
//    }
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
    @GetMapping("/evaluation/select/{id}")
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
    @PostMapping("/evaluation/create")
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
    @DeleteMapping("/evaluation/delete/{id}")
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
    @GetMapping("/evaluation-form/selectall")
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
    @GetMapping("/evaluation-form/select/{id}")
    public ResponseEntity<EvaluationFormResponseDTO> selectForm(@PathVariable Integer id){

        EvaluationFormResponseDTO result = evaluationService.selectForm(id);

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
    @PostMapping("/evaluation-form/create")
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
    @PutMapping("/evaluation-form/update")
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
    @PutMapping("/evaluation-form/grading")
    public ResponseEntity<Integer> gradingForm(@RequestBody EvaluationFormUpdateDTO updateDTO) {

        Integer updatedId = evaluationService.gradingForm(updateDTO);

        return ResponseEntity.ok(updatedId);
    }
}
