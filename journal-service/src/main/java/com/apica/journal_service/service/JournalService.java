package com.apica.journal_service.service;

import com.apica.journal_service.entity.JournalEvent;
import com.apica.journal_service.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JournalService {
    @Autowired
    JournalRepository repo;

    public void saveEvent(JournalEvent event) {
        repo.save(event);
    }

    public List<JournalEvent> getAllEvents() {
        return repo.findAll();
    }
}
