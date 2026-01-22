package com.example.organization.service.iface;


import com.example.organization.dto.OrganizationOnboardingDTO;
import com.example.organization.dto.SpocOrganizationResponseDTO;
import com.example.organization.util.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface OrganizationService {


//    ApiResponse save(OrganizationOnboardingDTO dto, MultipartFile spocAuthLetter, MultipartFile establishmentLetter);
ApiResponse save(
        OrganizationOnboardingDTO dto,
        Map<String, List<MultipartFile>> documents
);


    ApiResponse approveOrRejectOrg(String status,Long id);

    ApiResponse getAllOrganizations();




    ApiResponse<Page<SpocOrganizationResponseDTO>> getOrganizationsBySpocEmail(String email, int page, int size);
    ApiResponse getOrganizationDetailsById(Long id);

    ApiResponse getDashboardDetails(String spocEmail);


    ApiResponse getAllOrganizationApprovalDetails();

    ApiResponse getOrgCategoryandidByOrgid(String orgId);

    ApiResponse getRecentOrganizationBySpocEmail(String spocEmail);
}
