package com.pda.community_module.domain;

import com.pda.community_module.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "post_count")

public class PostCount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    @Builder.Default
    private Long likeCount=0L;

    @Column(nullable = false)
    @Builder.Default
    private Long commentCount=0L;
}
