package com.pda.piggyBank_module.domain.common;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Pig")
@NoArgsConstructor
@Setter
@Getter
public class Piggy extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id")
    private long id;

    @Column(name = "user_id", length = 20)
    private String userId;

    @Column(name = "mileage")
    private long mileage;


}
