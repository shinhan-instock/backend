package com.pda.piggyBank_module.repository;

import com.pda.piggyBank_module.domain.Piggy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PiggyRepository extends JpaRepository<Piggy, Long> {
    Optional<Piggy> findByUserId(Long userId);
    Optional<Piggy> findMileageByUserId(Long Id);

    @Transactional
    @Modifying
    @Query("UPDATE Piggy p SET p.mileage = :mileage WHERE p.userId = :userId")
    void updateMileageByUserId(Long userId, Integer mileage);
}