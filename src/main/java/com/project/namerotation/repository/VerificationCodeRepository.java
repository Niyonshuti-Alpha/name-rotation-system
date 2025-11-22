package com.project.namerotation.repository;

import com.project.namerotationsystem.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    
    Optional<VerificationCode> findByEmailAndCodeAndUsedFalseAndExpiresAtAfter(
        String email, String code, LocalDateTime now);
    
    Optional<VerificationCode> findByEmailAndCodeAndUsedTrue(String email, String code);
    
    void deleteByEmail(String email);
    
    @Modifying
    @Query("DELETE FROM VerificationCode v WHERE v.expiresAt < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);
}