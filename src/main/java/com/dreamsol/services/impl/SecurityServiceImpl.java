package com.dreamsol.services.impl;

import com.dreamsol.dto.EndpointResponseDto;
import com.dreamsol.entities.Endpoint;
import com.dreamsol.entities.RefreshToken;
import com.dreamsol.helpers.EndpointMappingsHelper;
import com.dreamsol.repositories.EndpointRepository;
import com.dreamsol.response.ApiResponse;
import com.dreamsol.securities.JwtHelper;
import com.dreamsol.securities.LoginRequest;
import com.dreamsol.securities.LoginResponse;
import com.dreamsol.services.RefreshTokenService;
import com.dreamsol.services.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class SecurityServiceImpl implements SecurityService
{
    private AuthenticationManager authenticationManager;
    private JwtHelper jwtHelper;
    private RefreshTokenService refreshTokenService;
    private EndpointRepository endpointRepository;
    private EndpointMappingsHelper endpointMappingsHelper;
    private Authentication authenticateUsernameAndPassword(String username, String password)
    {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,password);
        try{
            return authenticationManager.authenticate(authentication);
        }catch (BadCredentialsException e)
        {
            throw new BadCredentialsException(" Invalid username or password !");
        }
    }
    @Override
    public ResponseEntity<?> login(LoginRequest request)
    {
        try {
            SecurityContextHolder.clearContext();
            Authentication authentication = authenticateUsernameAndPassword(request.getUsername(), request.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtHelper.generateToken(userDetails);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
            LoginResponse loginResponse = LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getRefreshToken())
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Login Error: " + e.getMessage(), false));
        }
    }

    @Override
    public ResponseEntity<?> getAllEndpoints()
    {
        List<EndpointResponseDto> endpointResponseDtoList = endpointRepository.findAll()
                .stream()
                .map((endpointMappings -> new EndpointResponseDto(endpointMappings.getEndPointKey(), endpointMappings.getEndPointLink())))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(endpointResponseDtoList);
    }

    @Override
    public ResponseEntity<?> updateEndpoints()
    {
        int count = 0;
        Map<String,String> endpointMap = endpointMappingsHelper.getEndpointMap();
        for(Map.Entry<String,String> endpoint : endpointMap.entrySet())
        {
            Endpoint endpointMappings = new Endpoint(endpoint.getKey(),endpoint.getValue());
            endpointRepository.save(endpointMappings);
            count++;
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(count+" endpoints updated!",true));
    }
}
