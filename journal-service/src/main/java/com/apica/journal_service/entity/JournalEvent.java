package com.apica.journal_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JournalEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String action;
    private Long userId;
    private String username;
    private String role;
    private String timestamp;
}
