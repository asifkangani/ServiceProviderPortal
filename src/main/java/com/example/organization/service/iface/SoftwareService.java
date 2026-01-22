package com.example.organization.service.iface;

import com.example.organization.dto.SoftwareWithLicenseDTO;
import com.example.organization.dto.UploadSofwareDTO;
import com.example.organization.util.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SoftwareService {

    ApiResponse uploadSoftware(UploadSofwareDTO dto, MultipartFile softwareZip);

    ApiResponse publishOrUnpublishSoftware(Long softwareId, String action);

    ApiResponse getAllSoftwares();

    List<SoftwareWithLicenseDTO> getSoftwareLicenseCards(Long orgDetailsId);

    ResponseEntity<Resource> downloadSoftware(Long softwareId);

    ApiResponse getSoftwareNameWithValues();
}
