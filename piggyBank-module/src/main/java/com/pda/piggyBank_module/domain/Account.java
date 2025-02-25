package com.pda.piggyBank_module.domain;

import com.pda.piggyBank_module.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 20, unique = true)
    private String account;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<OwnStock> ownStocks;
}
