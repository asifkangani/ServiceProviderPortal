package com.example.organization.service.iface;

import com.example.organization.util.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface LicenseService {

    ApiResponse applyLicense(Long orgId);

    ResponseEntity<Resource> downloadLicense(String ouid,String type);






}
