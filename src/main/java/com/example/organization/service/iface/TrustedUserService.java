package com.example.organization.service.iface;

import com.example.organization.dto.LoginRequestDTO;
import com.example.organization.dto.TrustedUserDTO;
import com.example.organization.util.ApiResponse;

public interface TrustedUserService {



    ApiResponse login(LoginRequestDTO dto);

    ApiResponse  forgotPassword(String email);

    ApiResponse resetPassword(String token,String password);

    String validateResetToken(String token);

    ApiResponse changePassword(String email,String currentPassword,String newPassword);

    ApiResponse saveTrustedUser(TrustedUserDTO trustedUserDTO);
    ApiResponse getAllTrustedUsers();
}
