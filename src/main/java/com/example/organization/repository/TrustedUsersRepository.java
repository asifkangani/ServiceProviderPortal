package com.example.organization.repository;


import com.example.organization.model.TrustedUsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrustedUsersRepository extends JpaRepository<TrustedUsersEntity, Long> {

    TrustedUsersEntity findByEmail(String email);
}

