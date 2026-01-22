package com.example.organization.repository;

import com.example.organization.model.OrganisationCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganisationCategoryRepo
        extends JpaRepository<OrganisationCategories, Integer> {

    Optional<OrganisationCategories> findByCategoryName(String categoryName);
}