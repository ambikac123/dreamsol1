package com.dreamsol.services;

import com.dreamsol.dto.PermissionRequestDto;
import com.dreamsol.securities.JwtRequest;
import com.dreamsol.securities.JwtResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface SecurityService
{
    ResponseEntity<JwtResponse> login(JwtRequest request);
    ResponseEntity<?> logout();
}
