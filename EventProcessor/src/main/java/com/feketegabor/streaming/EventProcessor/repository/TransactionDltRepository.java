package com.feketegabor.streaming.EventProcessor.repository;

import com.feketegabor.streaming.EventProcessor.repository.model.DeadLetterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionDltRepository extends JpaRepository<DeadLetterEntity, UUID> {
}
