package com.project.iob.common.controller;
 
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.iob.common.dto.CodeDto;
import com.project.iob.common.dto.CodeGroupsDto;
import com.project.iob.common.dto.EmailDto;
import com.project.iob.common.service.CommonService;
import com.project.iob.common.service.MailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
@Tag(name = "Common API", description = "파일업로드, 이메일 등 공통 API")
@Slf4j
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class CommonController {
 
    private final MailService mailService;
    private final CommonService commonService;
 
    @Operation(
	    summary = "메일 전송", 
	    description = "특정 파일명을 수신자로 하여 테스트 이메일을 전송합니다. 실제 구현에서는 파일명이 이메일 주소로 매핑되어야 합니다."
	)
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "204", description = "메일 전송 성공 (반환 데이터 없음)"),
	    @ApiResponse(responseCode = "404", description = "해당 이름을 찾을 수 없음 또는 이메일 주소로 매핑 실패"),
	    @ApiResponse(responseCode = "500", description = "메일 서버 통신 오류 또는 전송 권한 없음")
	})
    @PostMapping("/sendEmail/{email}")
    public ResponseEntity<Void> sendEmail(
    	@ParameterObject EmailDto emailDto 
    	) {
    	
    	mailService.sendEmail(emailDto);
        
    	// 2. 204 No Content 반환 (성공했지만 줄 데이터는 없음)
        return ResponseEntity.noContent().build(); 
	}
    
    @Operation(
	    summary = "그룹 코드 조회", 
	    description = "그룹 코드 목록을 조회"
	)
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "204", description = "코드 조회 성공"),
	    @ApiResponse(responseCode = "404", description = "해당 이름을 찾을 수 없음 또는 이메일 주소로 매핑 실패"),
	    @ApiResponse(responseCode = "500", description = "메일 서버 통신 오류 또는 전송 권한 없음")
	})
    @GetMapping("/groupcodeList/{groupcode}")
    public ResponseEntity<List<CodeGroupsDto>> getGroupCodeList(@Parameter(
            description = "codeGroups의 값을 가져옴", 
            required = true, 
            example = "CATEGORY"
        ) 
        @PathVariable(value = "groupcode") String groupCode) {
    	
		List<CodeGroupsDto> list = commonService.getCodeGroupList(groupCode);
        
    	// 2. 204 No Content 반환 (성공했지만 줄 데이터는 없음)
        return ResponseEntity.ok().body(list); 
	}
    
    @Operation(
	    summary = "코드 목록 조회", 
	    description = "코드 목록을 조회"
	)
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "204", description = "코드 조회 성공"),
	    @ApiResponse(responseCode = "404", description = "해당 이름을 찾을 수 없음 또는 이메일 주소로 매핑 실패"),
	    @ApiResponse(responseCode = "500", description = "메일 서버 통신 오류 또는 전송 권한 없음")
	})
    @GetMapping("/codelist")
    public ResponseEntity<List<CodeDto>> getCodeList() {
    	
		List<CodeDto> list = commonService.getCodeList();
        
    	// 2. 204 No Content 반환 (성공했지만 줄 데이터는 없음)
        return ResponseEntity.ok().body(list); 
	}
    
}