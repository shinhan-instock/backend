package com.pda.piggyBank_module.domain;

import com.pda.piggyBank_module.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    public Piggy(Long userId, int mileage) {
        this.userId = userId;
        this.mileage = mileage;
    }

    public void addMileage(int amount) {
        this.mileage += amount;
    }
}
