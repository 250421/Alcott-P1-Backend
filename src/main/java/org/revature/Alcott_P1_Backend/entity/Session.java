package org.revature.Alcott_P1_Backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "SPRING_SESSION")
public class Session {

    public Session(String sessionId, Account account, LocalDateTime createdAt) {
        this.sessionId = sessionId;
        this.account = account;
        this.createdAt = createdAt;
        this.expiresAt = createdAt.plusHours(1); // Set expiration time to 1 hour from creation
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

}
