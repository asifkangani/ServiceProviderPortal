package com.example.organization.controller;


import com.example.organization.dto.SoftwareWithLicenseDTO;
import com.example.organization.dto.SpocOrganizationResponseDTO;
import com.example.organization.repository.SpocRepository;
import com.example.organization.security.CustomUserDetails;
import com.example.organization.service.iface.OrganizationService;
import com.example.organization.service.iface.SoftwareService;
import com.example.organization.service.iface.TrustedUserService;
import com.example.organization.service.iface.WalletIface;
import com.example.organization.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.List;


@Controller
public class WebController {
    private static final String CLASS = "WebController";
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    TrustedUserService trustedUserService;

    @Value("${file.pdf.max-size-kb}")
    private long maxPdfSizeKb;

    @Value("${portal.url}")
    private String portalUrl;

    @Value("${portal.name}")
    private String portalName;


    @Value("${wallet.cert}")
    private boolean walletCert;

    @Value("${wallet.cert.payment}")
    private String walletCertPayment;

    @Value("${wallet.cert.Admin.approval}")
    private String walletCertAdminApproval;



    @Autowired
   OrganizationService organizationService;

   @Autowired
    SpocRepository spocRepository;

   @Autowired
    SoftwareService softwareService;

   @Autowired
    WalletIface walletIface;



//    @GetMapping("/")
//    public String showSignInPage(Model model,Authentication authentication) {
//        if (authentication != null
//                && authentication.isAuthenticated()
//                && !(authentication instanceof AnonymousAuthenticationToken)) {
//            logger.info("User {} already authenticated, redirecting to dashboard",
//                    authentication.getName());
//            return "redirect:/dashboard";
//        }
//        logger.info("{}  Displaying sign-in page", CLASS);
//        model.addAttribute("portalUrl", portalUrl);
//        model.addAttribute("portalName",portalName);
//        return "signin";
//    }

    @GetMapping("/")
    public String showLoginPage(Model model,Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            logger.info("User {} already authenticated, redirecting to dashboard",
                    authentication.getName());
            return "redirect:/dashboard";
        }
        logger.info("{}  Displaying sign-in page", CLASS);
        model.addAttribute("portalUrl", portalUrl);
        model.addAttribute("portalName",portalName);
        return "new_login";
    }



    @GetMapping("/forgot-password")
    public ModelAndView showForgotPage(Model model) {
        logger.info("{} forget password", CLASS);
        model.addAttribute("portalUrl", portalUrl);
        model.addAttribute("portalName",portalName);
        return new ModelAndView("forgot-password");
    }

    @GetMapping("/new-forget-password")
    public ModelAndView showNewForgotPage(Model model) {
        logger.info("{} forget password", CLASS);
        model.addAttribute("portalUrl", portalUrl);
        model.addAttribute("portalName",portalName);
        return new ModelAndView("new-forget-password");
    }





    @GetMapping("/organizations")
    public ModelAndView showOrganizations(
            Model model,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        CustomUserDetails user =
                (CustomUserDetails) authentication.getPrincipal();
        //uncomment for deploy in service(  ApiResponse res = syncDataFromAdmin(email);)
        ApiResponse<Page<SpocOrganizationResponseDTO>> response =
                organizationService.getOrganizationsBySpocEmail(
                        user.getEmail(), page, size);

        Page<SpocOrganizationResponseDTO> orgPage =
                (Page<SpocOrganizationResponseDTO>) response.getResult();

        model.addAttribute("organizations", orgPage.getContent());

        // ✅ EXTRACT VALUES (CRITICAL FOR GRAALVM)
        model.addAttribute("currentPage", orgPage.getNumber());
        model.addAttribute("totalPages", orgPage.getTotalPages());
        model.addAttribute("hasNext", orgPage.hasNext());
        model.addAttribute("hasPrevious", orgPage.hasPrevious());

        model.addAttribute("portalName", portalName);

        return new ModelAndView("organizations");
    }




    @GetMapping("/organization-details")
    public ModelAndView showOrganizationDetails(@RequestParam("id") Long organizationId, Model model,
            Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        boolean authorized = spocRepository.existsBySpocOfficalEmailAndOrgDetailsId(user.getEmail(), organizationId);
        logger.info(
                "{} get organization details request | orgId={} | spocEmail={}",
                CLASS, organizationId, user.getEmail()
        );
        if (!authorized) {
            logger.warn(
                    "{} unauthorized access attempt | orgId={} | spocEmail={}",
                    CLASS, organizationId, user.getEmail()
            );
            return new ModelAndView("redirect:/organizations?unauthorized=true");
        }

        ApiResponse response =  organizationService.getOrganizationDetailsById(organizationId);


        ApiResponse response1 =  walletIface.walletDetails(organizationId);


        model.addAttribute("org", response.getResult());
        model.addAttribute("walletDetails", response1.getResult());

        model.addAttribute("organization", response.getResult());

        model.addAttribute("portalUrl", portalUrl);
        model.addAttribute("walletCert", walletCert);
        model.addAttribute("walletCertPayment",walletCertPayment);
        model.addAttribute("walletCertAdminApproval",walletCertAdminApproval);

        return new ModelAndView("organization-details");
    }



    @GetMapping("/reset-password")
    public ModelAndView resetPasswordPage(@RequestParam String token, Model model) {

        String status = trustedUserService.validateResetToken(token);
        logger.info("{} reset password token validation result | status={}", CLASS, status);
        model.addAttribute("status", status);

        if ("VALID".equals(status)) {
            model.addAttribute("token", token);
        }
        model.addAttribute("portalUrl", portalUrl);
        model.addAttribute("portalName",portalName);

        return new ModelAndView("reset-password");
    }


    @GetMapping("/available-softwares/{orgId}")
    public String showAvailableSoftwares(@PathVariable Long orgId, Model model) {

        List<SoftwareWithLicenseDTO> softwares = softwareService.getSoftwareLicenseCards(orgId);
        logger.info("{} get available softwares for orgId {}",CLASS,orgId);
        model.addAttribute("softwares", softwares);
        model.addAttribute("noSoftwares", softwares == null || softwares.isEmpty());


        model.addAttribute("organizationId", orgId);
        model.addAttribute("portalUrl", portalUrl);

        return "available-softwares";
    }

    @GetMapping("/download/software/by/softwareId/{softwareId}")
    public ResponseEntity<Resource> downloadSoftware(@PathVariable Long softwareId) {
        logger.info("{} download software for softwareId {}",CLASS,softwareId);
        return softwareService.downloadSoftware(softwareId);
    }



    @GetMapping("/profile")
    public ModelAndView showProfilePage(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            logger.info(
                    "{} authenticated user profile accessed | email={}",
                    CLASS, user.getEmail()
            );
            model.addAttribute("userName", user.getFullName());
            model.addAttribute("userEmail", user.getEmail());
        }

        model.addAttribute("portalUrl", portalUrl);
        return new ModelAndView("profile");
    }


    @PostMapping("/profile/change-password")
    public String handleChangePassword(@RequestParam String currentPassword,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       Principal principal,
                                       HttpServletRequest request) {

        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/profile?error=passwordMismatch";
        }

        ApiResponse response =
                trustedUserService.changePassword(principal.getName(), currentPassword, newPassword);

        if (response.isSuccess()) {

            SecurityContextHolder.clearContext();
            request.getSession().invalidate();

            return "redirect:/?passwordChanged=true";
        }

        return "redirect:/profile?error=updateFailed";
    }



    @GetMapping("/dashboard")
    public ModelAndView showIndexPage(Model model, Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        //uncomment for deploy
        ApiResponse responseDto = organizationService.getDashboardDetails(user.getEmail());
        model.addAttribute("details",responseDto.getResult());

        ApiResponse recentOrgResponse = organizationService.getRecentOrganizationBySpocEmail(user.getEmail());
        if (recentOrgResponse != null && recentOrgResponse.isSuccess()) {
            model.addAttribute("recentOrg", recentOrgResponse.getResult());
        }
        return new ModelAndView("dashboard");
    }



    @GetMapping("/create-organization")
    public ModelAndView createOrganizations(Model model,Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        model.addAttribute("spocEmail",user.getEmail());
        model.addAttribute("spocName",user.getFullName());
        model.addAttribute("fileSize", maxPdfSizeKb);
        model.addAttribute("portalUrl", portalUrl);
        return new ModelAndView("create-organization");
    }





}
