package com.example.organization.service.impl;

import com.example.organization.dto.LoginRequestDTO;
import com.example.organization.dto.TrustedUserDTO;
import com.example.organization.dto.TrustedUserResponseDTO;
import com.example.organization.model.PasswordResetTokenEntity;
import com.example.organization.model.TrustedUsersEntity;
import com.example.organization.repository.PasswordResetTokenRepository;
import com.example.organization.repository.TrustedUsersRepository;
import com.example.organization.repository.SpocRepository;
import com.example.organization.service.iface.TrustedUserService;
import com.example.organization.util.ApiResponse;
import com.example.organization.util.AppUtil;
import com.example.organization.util.EmailService;
import com.example.organization.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TrustedUserServiceImpl implements TrustedUserService {
    private static final String CLASS = "TrustedUserServiceImpl";
    private static final Logger logger = LoggerFactory.getLogger(TrustedUserServiceImpl.class);

    private final SpocRepository spocRepository;

    private final TrustedUsersRepository trustedUsersRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${reset.password.expiry.minutes}")
    private long resetPasswordExpiryMinutes;

    @Value("${portal.url}")
    private String portalUrl;

    @Value("${portal.name}")
    private String portalName;


    public TrustedUserServiceImpl(

            SpocRepository spocRepository,

            TrustedUsersRepository trustedUsersRepository,
            EmailService emailService,PasswordEncoder passwordEncoder,PasswordResetTokenRepository passwordResetTokenRepository) {


        this.spocRepository = spocRepository;

        this.trustedUsersRepository = trustedUsersRepository;
        this.emailService=emailService;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }


    @Value("${reset.password.url}")
    private String resetPasswordUrl;



    @Override
    public ApiResponse  login(LoginRequestDTO dto) {
        logger.info("{} login  :::" ,CLASS);

        try {

            TrustedUsersEntity auth = trustedUsersRepository.findByEmail(dto.getEmail());

            if (auth == null) {
                return new ApiResponse<>(false, "Invalid email", null);
            }

            boolean matches = passwordEncoder.matches(dto.getPassword(), auth.getPassword()
            );

            if (!matches) {
                return new ApiResponse<>(false, "Invalid password", null);
            }

            return new ApiResponse (true, "Login successful", null);
        }catch (Exception e){
            e.printStackTrace();
            return new ApiResponse(false,"Something went wrong",null);

        }
    }

    @Override
    public ApiResponse forgotPassword(String email) {
        logger.info("{} forgot password  :::" ,CLASS);
        try {

            TrustedUsersEntity auth = trustedUsersRepository.findByEmail(email);

            if (auth == null) {
                return new ApiResponse(true, "If email exists, reset link has been sent", null);
            }

            String token = AppUtil.generateToken();

            PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity();
            resetToken.setToken(token);
            resetToken.setEmail(email);

            resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(resetPasswordExpiryMinutes).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            passwordResetTokenRepository.save(resetToken);

            String resetLink = resetPasswordUrl + "?token=" + token;

            String body =
                    "<html>" +
                            "<body style='font-family: Arial, Helvetica, sans-serif; font-size: 14px; color: #000;'>" +

                            "<p>Dear User,</p>" +

                            "<p>We received a request to reset your password.</p>" +

                            "<p>" +
                            "Click the link below to reset your password:<br/>" +
                            "<a href='" + resetLink + "' target='_blank'>" + resetLink + "</a>" +
                            "</p>" +

                            "<p>" +
                            "This link expires in <strong>" + resetPasswordExpiryMinutes + " minutes</strong>." +
                            "</p>" +

                            "<p>If you did not request this, please ignore this email.</p>" +

                            "<p>Regards,<br/>" +
                            "Admin</p>" +



                            "<p style='font-size: 10px; font-style: italic; color: gray;'>" +
                            "* This is an automated email from <strong>" + portalName + "</strong>. " +
                            "Please contact the administrator if you have any questions regarding this email." +
                            "</p>" +


                            "</body>" +
                            "</html>";


            emailService.sendEmail(email, body,"Password Reset");



            return new ApiResponse(true, "Password reset link sent to email", null);
        }catch (Exception e){
            e.printStackTrace();
            return new ApiResponse(false,"Something went wrong",null);
        }
    }


    @Override
    public ApiResponse resetPassword(String token, String newPassword) {
        logger.info("{} reset password  :::" ,CLASS);
        try {

            PasswordResetTokenEntity resetToken = passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));

            if (token == null || newPassword == null || newPassword.isEmpty() || token.isEmpty()) {
                return new ApiResponse(false, "Token or New Password cannot be null", null);
            }

            if (resetToken.getUsed())
                return new ApiResponse(false, "Token already used", null);

            LocalDateTime expiryTime = LocalDateTime.parse(resetToken.getExpiryTime());

            if (expiryTime.isBefore(LocalDateTime.now())) {
                return new ApiResponse(false, "Token expired", null);
            }
            String  email= resetToken.getEmail();
            TrustedUsersEntity authEntity = trustedUsersRepository.findByEmail(email);
            if(authEntity == null){
                return new ApiResponse(false,"No user with this email",null);
            }

            authEntity.setPassword(passwordEncoder.encode(newPassword));

            trustedUsersRepository.save(authEntity);

            resetToken.setUsed(true);
            passwordResetTokenRepository.save(resetToken);

            return new ApiResponse(true, "Password reset successful", null);
        }catch (Exception e){
            e.printStackTrace();
            return new ApiResponse(false,"Something went wrong",null);
        }

    }

    @Override
    public String validateResetToken(String token) {
        logger.info("{} validate reset  :::" ,CLASS);
        try {

            PasswordResetTokenEntity resetToken = passwordResetTokenRepository.findByToken(token).orElse(null);

            if (resetToken == null) {
                return "INVALID";
            }

            if (Boolean.TRUE.equals(resetToken.getUsed())) {
                return "USED";
            }

            LocalDateTime expiryTime = LocalDateTime.parse(resetToken.getExpiryTime());

            if (expiryTime.isBefore(LocalDateTime.now())) {
                return "EXPIRED";
            }

            return "VALID";

        } catch (Exception e) {
            return "INVALID";
        }
    }

    @Override
    @Transactional
    public ApiResponse changePassword(String email, String currentPassword, String newPassword) {
        logger.info("{} change password  :::" ,CLASS);
        try {

            TrustedUsersEntity user = trustedUsersRepository.findByEmail(email);

            if (user == null) {
                return new ApiResponse(false, "User not found", null);
            }

            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return new ApiResponse(false, "Current password is incorrect", null);
            }


            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                return new ApiResponse(false, "New password must be different", null);
            }


            user.setPassword(passwordEncoder.encode(newPassword));
            trustedUsersRepository.save(user);

            return new ApiResponse(true, "Password changed successfully", null);
        }catch (Exception e){
            e.printStackTrace();
            return new ApiResponse(false,"Something went wrong",null);
        }

}



    public ApiResponse saveTrustedUser(TrustedUserDTO trustedUserDTO) {
        logger.info("{} save trust user :::" ,CLASS);
        try{
            TrustedUsersEntity trustedUsersEntity = trustedUsersRepository.findByEmail(trustedUserDTO.getEmail());
            if(trustedUsersEntity!=null){
                return new ApiResponse(false,"This user is already added as Trusted User",null);
            }

            TrustedUsersEntity trustedUsers = new TrustedUsersEntity();
            trustedUsers.setName(trustedUserDTO.getName());
            trustedUsers.setEmail(trustedUserDTO.getEmail());
            trustedUsers.setMobileNumber(trustedUserDTO.getMobileNumber());
            String password = PasswordUtil.generateRandomPassword();
            trustedUsers.setPassword(passwordEncoder.encode(password));
            trustedUsers.setCreatedOn(AppUtil.getDate());
            trustedUsers.setUpdatedOn(AppUtil.getDate());




            String body =
                    "<html>" +
                            "<body style='font-family: Arial, Helvetica, sans-serif; font-size: 14px; color: #000;'>" +

                            "<p>Dear " + trustedUserDTO.getName() + ",</p>" +

                            "<p>You can log in to the <strong>" + portalName + "</strong> using the credentials below:</p>" +

                            "<p>" +
                            "<strong>Username:</strong> " + trustedUserDTO.getEmail() + "<br/>" +
                            "<strong>Password:</strong> " + password +
                            "</p>" +

                            "<p>" +
                            "<strong>Link:</strong><br/>" +
                            "<a href='" + portalUrl + "' target='_blank'>" + portalUrl + "</a>" +
                            "</p>" +

                            "<p>For security reasons, please change your password after logging in.</p>" +

                            "<p>Regards,<br/>" +
                            "Admin</p>" +

                            "<p style='font-size: 10px; font-style: italic; color: gray;'>" +
                            "* This is an automated email from <strong>" + portalName + "</strong>. " +
                            "Please contact the administrator if you have any questions regarding this email." +
                            "</p>" +

                            "</body>" +
                            "</html>";



            emailService.sendEmail(trustedUserDTO.getEmail(),  body,"Login Credentials for " + portalName);


            trustedUsersRepository.save(trustedUsers);
            return new ApiResponse(true,"Trusted user saved successfully",null);


        }catch (Exception e){
            e.printStackTrace();
            return new ApiResponse(false,"Something went wrong",null);
        }
    }

    @Override
    public ApiResponse getAllTrustedUsers() {
        logger.info("{} get all trusted users  :::" ,CLASS);
        try{


            List<TrustedUserResponseDTO> users =
                    trustedUsersRepository.findAll()
                            .stream()
                            .map(user -> {
                                TrustedUserResponseDTO dto = new TrustedUserResponseDTO();
                                dto.setId(user.getId());
                                dto.setName(user.getName());
                                dto.setEmail(user.getEmail());
                                dto.setMobileNumber(user.getMobileNumber());
                                dto.setCreatedOn(user.getCreatedOn());
                                dto.setUpdatedOn(user.getUpdatedOn());
                                return dto;
                            })
                            .toList();

            return new ApiResponse(true,"Trusted user saved successfully",trustedUsersRepository.findAll());

        }catch (Exception e){
            e.printStackTrace();
            return new ApiResponse(false,"Something went wrong",null);
        }
    }





}
