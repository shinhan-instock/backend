package com.pda.community_module.domain;

import com.pda.community_module.domain.common.BaseEntity;
import com.pda.community_module.domain.mapping.PostLike;
import com.pda.community_module.domain.mapping.PostScrap;
import com.pda.community_module.domain.mapping.UserFollows;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String userId;

    @Column(nullable = false, length = 30)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 50)
    private String imageUrl;

    @Column(length = 255)
    private String introduction;


    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<UserFollows> followingList;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private List<UserFollows> followerList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WatchList> watchLists;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PostLike> likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PostScrap> scraps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<Sentiment> sentiments;
}
