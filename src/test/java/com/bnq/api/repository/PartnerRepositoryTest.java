package com.bnq.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.bnq.api.entity.Direction;
import com.bnq.api.entity.Partner;
import com.bnq.api.entity.ProcessedFlowType;
import com.bnq.api.repository.PartnerRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PartnerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PartnerRepository partnerRepository;

    @Test
    void save_ShouldPersistPartner() {
        // Given
        Partner partner = new Partner();
        partner.setAlias("test-partner");
        partner.setType("TEST");
        partner.setDirection(Direction.INBOUND);
        partner.setApplication("TestApp");
        partner.setProcessedFlowType(ProcessedFlowType.MESSAGE);
        partner.setDescription("Test Partner");

        // When
        Partner savedPartner = partnerRepository.save(partner);
        entityManager.flush();

        // Then
        Partner foundPartner = entityManager.find(Partner.class, savedPartner.getId());
        assertNotNull(foundPartner);
        assertEquals(partner.getAlias(), foundPartner.getAlias());
        assertEquals(partner.getType(), foundPartner.getType());
        assertEquals(partner.getDirection(), foundPartner.getDirection());
    }

    @Test
    void existsByAlias_WhenExists_ShouldReturnTrue() {
        // Given
        Partner partner = new Partner();
        partner.setAlias("existing-partner");
        partner.setType("TEST");
        partner.setDirection(Direction.INBOUND);
        partner.setDescription("Test Partner");
        entityManager.persist(partner);
        entityManager.flush();

        // When
        boolean exists = partnerRepository.existsByAlias("existing-partner");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByAlias_WhenNotExists_ShouldReturnFalse() {
        // When
        boolean exists = partnerRepository.existsByAlias("non-existing-partner");

        // Then
        assertFalse(exists);
    }

    @Test
    void findById_WhenExists_ShouldReturnPartner() {
        // Given
        Partner partner = new Partner();
        partner.setAlias("test-partner");
        partner.setType("TEST");
        partner.setDirection(Direction.INBOUND);
        partner.setDescription("Test Partner");
        Partner savedPartner = entityManager.persist(partner);
        entityManager.flush();

        // When
        Partner foundPartner = partnerRepository.findById(savedPartner.getId()).orElse(null);

        // Then
        assertNotNull(foundPartner);
        assertEquals(partner.getAlias(), foundPartner.getAlias());
        assertEquals(partner.getType(), foundPartner.getType());
    }

    @Test
    void deleteById_ShouldRemovePartner() {
        // Given
        Partner partner = new Partner();
        partner.setAlias("partner-to-delete");
        partner.setType("TEST");
        partner.setDirection(Direction.INBOUND);
        partner.setDescription("Test Partner");
        Partner savedPartner = entityManager.persist(partner);
        entityManager.flush();

        // When
        partnerRepository.deleteById(savedPartner.getId());
        entityManager.flush();

        // Then
        Partner foundPartner = entityManager.find(Partner.class, savedPartner.getId());
        assertNull(foundPartner);
    }
}