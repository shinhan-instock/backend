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
    private long id;

    @Column(name = "user_id", length = 20)
    private Long userId;

    @Column(name = "mileage")
    private long mileage;

    public Piggy(Long userId, long mileage) {
        this.userId = userId;
        this.mileage = mileage;
    }

    public void addMileage(long amount) {
        this.mileage += amount;
    }
}
