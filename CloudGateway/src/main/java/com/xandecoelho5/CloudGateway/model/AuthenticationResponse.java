package com.xandecoelho5.CloudGateway.model;

import lombok.Builder;

import java.util.Collection;

@Builder
public record AuthenticationResponse(String userId, String accessToken, String refreshToken,
                                     long expiresAt, Collection<String> authorityList) {
}
