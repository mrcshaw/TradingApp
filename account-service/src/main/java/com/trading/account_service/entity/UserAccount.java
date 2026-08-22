package com.trading.account_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Represents a User Account in the system.
 * Maps to the "user_accounts" table in the database.
 */
@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String accountId;
    
    private String username;
    private String email;
    private boolean isActive;
    private LocalDateTime createdAt;
    
    // Getters and Setters

    /**
     * Gets the unique identifier for the account.
     * @return the UUID account ID.
     */
    public String getAccountId() { return accountId; }

    /**
     * Sets the unique identifier for the account.
     * @param accountId the UUID to set.
     */
    public void setAccountId(String accountId) { this.accountId = accountId; }

    /**
     * Gets the username associated with this account.
     * @return the username.
     */
    public String getUsername() { return username; }

    /**
     * Sets the username for this account.
     * @param username the username to set.
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Gets the email address associated with this account.
     * @return the email address.
     */
    public String getEmail() { return email; }

    /**
     * Sets the email address for this account.
     * @param email the email address to set.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Checks if the account is currently active.
     * @return true if active, false otherwise.
     */
    public boolean isActive() { return isActive; }

    /**
     * Sets the active status of the account.
     * @param active true to mark as active.
     */
    public void setActive(boolean active) { isActive = active; }

    /**
     * Gets the timestamp of when this account was created.
     * @return the creation timestamp.
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Sets the timestamp for when this account was created.
     * @param createdAt the timestamp to set.
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
