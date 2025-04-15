package com.bnq.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bnq.api.dto.PartnerDTO;
import com.bnq.api.mapper.PartnerMapper;
import com.bnq.api.repository.PartnerRepository;
import com.bnq.api.service.PartnerService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PartnerDTO> getAllPartners() {
        return partnerRepository.findAll().stream()
            .map(partnerMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PartnerDTO createPartner(PartnerDTO partnerDTO) {
        if (existsByAlias(partnerDTO.getAlias())) {
            throw new RuntimeException("Partner with this alias already exists");
        }
        return partnerMapper.toDTO(
            partnerRepository.save(partnerMapper.toEntity(partnerDTO))
        );
    }

    @Override
    @Transactional
    public void deletePartner(Long id) {
        if (!partnerRepository.existsById(id)) {
            throw new RuntimeException("Partner not found");
        }
        partnerRepository.deleteById(id);
    }

    @Override
    public boolean existsByAlias(String alias) {
        return partnerRepository.existsByAlias(alias);
    }
}