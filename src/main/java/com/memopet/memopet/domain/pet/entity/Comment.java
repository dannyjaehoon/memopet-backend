package com.memopet.memopet.domain.pet.entity;

import com.memopet.memopet.global.common.entity.FirstCreatedEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends FirstCreatedEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memoryId") // 메모리에 담긴 글이면 값이 있음
    private Memory memory;

    //부모 코맨트
    private Long parentCommentId; // 자식이면 값이 있고 부모는 null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petId") // 따뜻한 한마디에 달린 글이면 값이 있음
    private Pet pet;

    //코맨트 단 반려동물(프로필) ID
    @Column(nullable = false)
    private Long commenterId; // 작성자 pet_id

    @Column(nullable = false, length = 150)
    private String comment;

    //댓글 깊이, 댓글:0, 대댓글:1
    @Column(nullable = false)
    private int depth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentGroup commentGroup;
    private LocalDateTime deletedDate;

    public void updateComment(String comment) {this.comment = comment;}
    public void updateDeleteDate(LocalDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }

}
