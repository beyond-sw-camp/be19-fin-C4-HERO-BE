package com.c4.hero.domain.evaluation.controller;

import com.c4.hero.domain.evaluation.dto.EvaluationTemplateRequestDTO;
import com.c4.hero.domain.evaluation.dto.EvaluationTemplateResponseDTO;
import com.c4.hero.domain.evaluation.dto.EvaluationTemplateUpdateDTO;
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
@RequestMapping("/api/public/")
@RequiredArgsConstructor
public class EvaluationController {


    /** 평가 관련 서비스 의존성 주입 */
    private final EvaluationService evaluationService;

    /**
     * 평가 템플릿 전체 조회
     *
     * @return result List<EvaluationTemplateResponseDTO>
     *     전체 평가 템플릿 데이터를 응답함
     */
    @GetMapping("/evaluation-template/selectall")
    public ResponseEntity<List<EvaluationTemplateResponseDTO>> selectAllTemplate(){

        List<EvaluationTemplateResponseDTO> result = evaluationService.selectAllTemplate();

        return ResponseEntity.ok(result);
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


}
