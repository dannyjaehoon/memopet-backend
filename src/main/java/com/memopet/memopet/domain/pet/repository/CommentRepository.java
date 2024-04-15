package com.memopet.memopet.domain.pet.repository;

import com.memopet.memopet.domain.pet.dto.PetCommentResponseDto;
import com.memopet.memopet.domain.pet.dto.ReplyPerCommentDto;
import com.memopet.memopet.domain.pet.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    // 작성자 pet_id 로 조회 하기 - 즉 작성자의 댓글 조회
    @Query(value = "select * from comment where commenter_id = ?1 and deleted_date IS NULL and comment_group = 'MEMORY_COMMENT' and depth=1 order by created_date desc", nativeQuery = true)
    Page<Comment> findMemoryCommentsByCommenterId(Long petId, Pageable pageable);

    // 프로필의 댓글조회
    //@Query("select new com.memopet.memopet.domain.pet.dto.PetCommentResponseDto(p.id, p.petName, p.petProfileUrl, c.id, c.comment, c.createdDate) from Comment c join c.pet p where c.pet = :petId and c.deletedDate IS NULL and c.commentGroup = :commentGroup and c.depth=1")
    //Page<PetCommentResponseDto> findCommentsByPetId(@Param("petId") Pet pet, @Param("commentGroup") CommentGroup commentGroup, Pageable pageable);
    @Query("select c from Comment c where c.pet = :petId and c.commentGroup = :commentGroup and c.depth=:depth and c.deletedDate IS NULL")
    Page<Comment> findCommentsByPetId(@Param("petId") Pet pet, @Param("commentGroup") CommentGroup commentGroup, @Param("depth") int depth, Pageable pageable);

    @Query("select c from Comment c where c.pet in :petIds and c.deletedDate IS NULL")
    List<Comment> findCommentsByPetIds(@Param("petIds") List<Pet> pets);

    @Query("select c from Comment c where c.memory = :memoryId and c.commentGroup = :commentGroup and c.depth=:depth and c.deletedDate IS NULL")
    Page<Comment> findCommentsByMemoryId(@Param("memoryId") Memory memoryId, @Param("commentGroup") CommentGroup commentGroup, @Param("depth") int depth , PageRequest pageRequest);

    @Query(value="select parent_comment_id AS commentId, COUNT(parent_comment_id) AS reply from comment where parent_comment_id in ?1 and deleted_date IS NULL group by parent_comment_id", nativeQuery = true)
    List<ReplyPerCommentDto> getReplyCount(List<Long> commentIds);

    @Query("select c from Comment c where c.parentCommentId = :parentCommentId and c.deletedDate IS NULL")
    Page<Comment> findByParentCommentId(@Param("parentCommentId") Long parentCommentId, PageRequest pageRequest);

    List<Comment> findByMemory(Memory memory);
}
