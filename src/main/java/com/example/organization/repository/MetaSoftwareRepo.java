package com.example.organization.repository;

import com.example.organization.model.MetaSoftwareEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetaSoftwareRepo extends JpaRepository<MetaSoftwareEntity, Long> {
}
