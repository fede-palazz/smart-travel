package com.certimetergroup.smart_travel.bff_api.dto.auth.response;

public record RefreshTokensResDTO(
    String accessToken,
    String refreshToken
) {

}
