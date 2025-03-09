package com.pda.piggyBank_module.repository;

import com.pda.piggyBank_module.domain.Piggy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PiggyRepository extends JpaRepository<Piggy, Long> {
    Optional<Piggy> findByUserId(Long userId);
    Optional<Piggy> findMileageByUserId(Long Id);
}