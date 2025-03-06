package com.pda.piggyBank_module.repository;

import com.pda.piggyBank_module.domain.Piggy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MileageRepository extends JpaRepository<Piggy, String> {
    Optional<Piggy> findMileageByUserId(String userId);
}
