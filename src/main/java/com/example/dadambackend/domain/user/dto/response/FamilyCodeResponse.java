package com.example.dadambackend.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "가족 코드 응답")
public class FamilyCodeResponse {

    @Schema(description = "가족 초대 코드", example = "DADAM-ABC123")
    private String familyCode;
}
