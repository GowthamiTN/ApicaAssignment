package com.apica.journal_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalEventDTO {
    private String action;
    private Long userId;
    private String username;
    private String role;
    private String timestamp;
}
