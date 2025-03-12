package com.pda.piggyBank_module.domain;

import com.pda.piggyBank_module.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Pig")
@NoArgsConstructor
@Setter
@Getter
public class Piggy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", length = 20)
    private Long userId;

    @Column(name = "mileage")
    private int mileage;

    @Column(name = "last_mileage_date")
    private LocalDate lastMileageDate;

    public Piggy(Long userId, int mileage) {
        this.userId = userId;
        this.mileage = mileage;
    }

    public void addMileage(int amount) {
        this.mileage += amount;
    }
}
