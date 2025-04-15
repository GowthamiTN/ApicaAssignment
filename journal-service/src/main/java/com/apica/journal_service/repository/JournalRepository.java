package com.apica.journal_service.repository;

import com.apica.journal_service.entity.JournalEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalRepository extends JpaRepository<JournalEvent, Long> {
     List<JournalEvent> findByUsername(String username);
}

