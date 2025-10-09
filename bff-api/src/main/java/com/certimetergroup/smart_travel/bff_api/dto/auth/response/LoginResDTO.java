package com.certimetergroup.smart_travel.bff_api.dto.auth.response;

import com.certimetergroup.smart_travel.bff_api.dto.user.response.UserNoPwdResDTO;

public record LoginResDTO(UserNoPwdResDTO user, String accessToken, String refreshToken) {

}
