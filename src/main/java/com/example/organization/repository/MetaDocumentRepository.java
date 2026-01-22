package com.example.organization.repository;


import com.example.organization.model.MetaDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;



public interface MetaDocumentRepository extends JpaRepository<MetaDocumentEntity, Long> {

}