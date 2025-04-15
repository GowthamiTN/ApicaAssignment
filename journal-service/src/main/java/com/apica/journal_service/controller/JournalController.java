package com.apica.journal_service.controller;

import com.apica.journal_service.entity.JournalEvent;
import com.apica.journal_service.repository.JournalRepository;
import com.apica.journal_service.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
public class JournalController {
    @Autowired
    JournalService service;
    @Autowired
    JournalRepository journalRepository;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<JournalEvent> getAll() {
        return service.getAllEvents();
    }
    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<JournalEvent>> getEventsByUser(@PathVariable String username) {
        List<JournalEvent> events = journalRepository.findByUsername(username);
        if (events.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(events);
    }

}

