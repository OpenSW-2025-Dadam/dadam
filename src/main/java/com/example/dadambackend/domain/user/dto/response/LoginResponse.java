package com.example.dadambackend.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인/회원가입 응답")
public class LoginResponse {

    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;

    @Schema(description = "로그인된 사용자 정보")
    private UserProfileResponse user;
}
