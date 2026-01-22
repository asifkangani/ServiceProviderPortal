package com.example.organization.repository;

import com.example.organization.model.AuditorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuditorRepository extends JpaRepository<AuditorEntity, Long> {
    Optional<AuditorEntity> findByOrgDetailsId(Long orgDetailsId);
}
