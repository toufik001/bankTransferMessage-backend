package com.bnq.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bnq.api.dto.PartnerDTO;
import com.bnq.api.service.PartnerService;

import java.util.List;

@RestController
@RequestMapping("/partners")
@RequiredArgsConstructor
@Tag(name = "Partners", description = "Partner management APIs")
public class PartnerController {

    private final PartnerService partnerService;

    @GetMapping
    @Operation(summary = "Get all partners")
    public List<PartnerDTO> getPartners() {
        return partnerService.getAllPartners();
    }

    @PostMapping
    @Operation(summary = "Create a new partner")
    public ResponseEntity<PartnerDTO> createPartner(@Valid @RequestBody PartnerDTO partner) {
        return ResponseEntity.ok(partnerService.createPartner(partner));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a partner")
    public ResponseEntity<Void> deletePartner(@PathVariable Long id) {
        partnerService.deletePartner(id);
        return ResponseEntity.ok().build();
    }
}