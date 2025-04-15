package com.bnq.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bnq.api.entity.Partner;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
    boolean existsByAlias(String alias);
}