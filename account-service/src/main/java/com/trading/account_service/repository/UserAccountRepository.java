package com.trading.account_service.repository;

import com.trading.account_service.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link UserAccount} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations 
 * backed by Spring Data JPA and HikariCP connection pooling.
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
}
