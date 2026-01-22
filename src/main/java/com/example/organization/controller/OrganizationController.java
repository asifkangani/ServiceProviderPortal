package com.example.organization.controller;

import com.example.organization.dto.OrganizationOnboardingDTO;
import com.example.organization.model.MetaDocumentEntity;
import com.example.organization.repository.MetaDocumentRepository;
import com.example.organization.service.iface.OrganizationService;
import com.example.organization.service.iface.SoftwareService;
import com.example.organization.util.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class OrganizationController {

    private static final String CLASS = "OrganizationController";
    private static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    @Value("${portal.url}")
    private String portalUrl;

    @Autowired
    private Validator validator;

    private final OrganizationService organizationService;
    private final SoftwareService softwareService;
    private final MetaDocumentRepository metaDocumentRepository;
    public OrganizationController(OrganizationService establishmentService, SoftwareService softwareService, MetaDocumentRepository metaDocumentRepository) {
        this.organizationService = establishmentService;
        this.softwareService = softwareService;
        this.metaDocumentRepository =  metaDocumentRepository;
    }

//    @PostMapping(
//            value = "/save",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    public ApiResponse save(
//            @Valid @RequestPart("data") OrganizationOnboardingDTO dto,
//            @RequestPart("spocAuthLetter") MultipartFile spocAuthLetter,
//            @RequestPart("organizationLetter") MultipartFile organizationLetter) {
//        logger.info("{} save org details ",CLASS);
//        return organizationService.save(dto, spocAuthLetter, organizationLetter);
//    }
@PostMapping(
        value = "/save",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public ApiResponse save(
        @Valid @RequestPart("data") OrganizationOnboardingDTO dto,
        MultipartHttpServletRequest request
) {
    return organizationService.save(dto, request.getMultiFileMap());
}

    @GetMapping("/meta-documents")
    public ApiResponse getAllMetaDocuments() {
        try {
            List<MetaDocumentEntity> list = metaDocumentRepository.findAll();

            return new ApiResponse(true, "Fetched successfully", list);

        } catch (Exception e) {
            return new ApiResponse(false, "Failed to fetch meta documents", e.getMessage());
        }
    }

    @GetMapping("/recent/by-spoc")
    public ApiResponse getRecentOrgBySpoc(
            @RequestParam String email) {
        return organizationService.getRecentOrganizationBySpocEmail(email);
    }


}