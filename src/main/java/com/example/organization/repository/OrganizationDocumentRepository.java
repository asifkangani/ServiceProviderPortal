package com.example.organization.repository;


import com.example.organization.model.OrganizationDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationDocumentRepository extends JpaRepository<OrganizationDocumentEntity, Long> {

    List<OrganizationDocumentEntity> findByOrgId(Long orgId);
}
