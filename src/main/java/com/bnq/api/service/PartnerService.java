package com.bnq.api.service;

import java.util.List;

import com.bnq.api.dto.PartnerDTO;

public interface PartnerService {
    List<PartnerDTO> getAllPartners();
    PartnerDTO createPartner(PartnerDTO partner);
    void deletePartner(Long id);
    boolean existsByAlias(String alias);
}