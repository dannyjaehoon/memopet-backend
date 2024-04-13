package com.memopet.memopet.domain.pet.service;

import com.memopet.memopet.domain.pet.dto.*;
import com.memopet.memopet.domain.pet.entity.*;
import com.memopet.memopet.domain.pet.repository.CommentRepository;
import com.memopet.memopet.domain.pet.repository.MemoryImageRepository;
import com.memopet.memopet.domain.pet.repository.MemoryRepository;
import com.memopet.memopet.domain.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PetRepository petRepository;
    private final MemoryRepository memoryRepository;
    private final MemoryImageRepository memoryImageRepository;
    private final NotificationService notificationService;


    @Transactional(readOnly = false)
    public CommentDeleteResponseDto deleteComment(CommentDeleteRequestDto commentDeleteRequestDto) {

        CommentDeleteResponseDto commentDeleteResponseDto = CommentDeleteResponseDto.builder().decCode('1').build();

        // comment_id로 넘겨받은것에 삭제 날짜
        Optional<Comment> comment = commentRepository.findById(commentDeleteRequestDto.getCommentId());
        Comment comment1 = comment.get();
        comment1.updateDeleteDate(LocalDateTime.now());


        return commentDeleteResponseDto;
    }

    public CommentsResponseDto findMemoryCommentsByPetId(CommentsRequestDto commentsRequestDto) {
        Optional<Pet> pet = petRepository.findById(commentsRequestDto.getPetId());

        PageRequest pageRequest = PageRequest.of(commentsRequestDto.getCurrentPage()-1, commentsRequestDto.getDataCounts());
        if(!pet.isPresent()) return CommentsResponseDto.builder().build();

        Page<Comment> page = commentRepository.findMemoryCommentsByCommenterId(pet.get().getId(), pageRequest);
        List<Comment> comments = page.getContent();

        if(comments.size() == 0) return CommentsResponseDto.builder().build();

        List<Long> memoryIds = new ArrayList<>();

        for (Comment comment : comments) {
            System.out.println("memory id : " + comment.getMemory().getId());
            memoryIds.add(comment.getMemory().getId());
        }

        List<Memory> memories = memoryRepository.findByMemoryIds(memoryIds);
        HashMap<Long, MemoryImage> memoryInfoHashMap = new HashMap<>();
        List<CommentResponseDto> memoriesContent = new ArrayList<>();

        for(Memory m : memories) {
            Optional<MemoryImage> memoryImage = memoryImageRepository.findOneById(m.getId());
            memoryInfoHashMap.put(m.getId(), memoryImage.isPresent() ? memoryImage.get() : null);
        }

        for (Comment c : comments) {
            memoriesContent.add(CommentResponseDto.builder()
                    .memoryId(c.getMemory().getId())
                    .commentId(c.getId())
                    .commentCreatedDate(c.getCreatedDate())
                    .comment(c.getComment())
                    .memoryImageUrl1(memoryInfoHashMap.getOrDefault(c.getMemory().getId(), null) != null ? memoryInfoHashMap.getOrDefault(c.getMemory().getId(), null).getUrl() : null )
                    .memoryImageUrlId1(memoryInfoHashMap.getOrDefault(c.getMemory().getId(), null) != null ? memoryInfoHashMap.getOrDefault(c.getMemory().getId(), null).getId() : null)
                    .build());
        }

        CommentsResponseDto commentsResponseDto = CommentsResponseDto.builder().totalPages(page.getTotalPages()).currentPage(page.getNumber()+1).dataCounts(page.getContent().size()).memoryResponseDto(memoriesContent).build();

        return commentsResponseDto;
    }

    @Transactional(readOnly = false)
    public CommentPostResponseDto PostAComment(CommentPostRequestDto commentPostRequestDto ) {
        CommentPostResponseDto commentDeleteResponseDto;

        Long parentCommentId = commentPostRequestDto.getParentCommentId();
        Long memoryId = commentPostRequestDto.getMemoryId();
        long commenterId = commentPostRequestDto.getCommenterId();
        int depth = commentPostRequestDto.getDepth();
        String commentContext = commentPostRequestDto.getComment();
        int commentGroup = commentPostRequestDto.getCommentGroup();
        Long petId = commentPostRequestDto.getPetId();

        Optional<Pet> pet = petRepository.findById(petId);
        Optional<Memory> memory = null;

        if(!pet.isPresent()) return CommentPostResponseDto.builder().decCode('0').errorMsg("프로필 ID로 조회된 데이터가 없습니다.").build();

        if(commentGroup == 1) { // 따뜻한 한마디 등록
            // 댓글 그룹이 1일때는 펫 id가 필수
            if(petId == null) return CommentPostResponseDto.builder().decCode('0').errorMsg("댓글 그룹이 따듯한 한마디일때 펫 id 필수입니다.").build();

            // 뎁스가 2일때는 부모댓글 id가 필수
            if(depth == 2 && parentCommentId == null) return CommentPostResponseDto.builder().decCode('0').errorMsg("대댓글 등록일때 부모 댓글 id 필수입니다.").build();

        } else { // 추억댓글 등록
            // 댓글 그룹이 2일때는 메모리 id가 필수
            if(memoryId == null) return CommentPostResponseDto.builder().decCode('0').errorMsg("댓글 그룹이 추억댓글일때 메모리 id 필수입니다.").build();

            memory = memoryRepository.findById(memoryId);

            // 뎁스가 2일때는 부모댓글 id가 필수
            if(depth == 2 && parentCommentId == null) return CommentPostResponseDto.builder().decCode('0').errorMsg("대댓글 등록일때 부모 댓글 id 필수입니다.").build();
            if(commentGroup == 2) if(!memory.isPresent()) return CommentPostResponseDto.builder().decCode('0').errorMsg("추억 ID로 조회된 데이터가 없습니다.").build();
        }

        // 알림 달기
        // 댓글
        if(depth == 1) {
            if(commentGroup == 1) notificationService.saveNotificationInfo(NotificationType.WARM_COMMENT_ALARM  ,pet.get(), commenterId);
            if(commentGroup == 2) notificationService.saveNotificationInfo(NotificationType.MEMORY_COMMENT_ALARM,pet.get(), commenterId);
        } else {
            // 대댓글
            if(commentGroup == 1) {
                notificationService.saveNotificationInfo(NotificationType.WARM_COMMENT_ALARM, pet.get(), commenterId);
                // 프로필 소유자와 댓글자가 다르면 프로필 소유자도 메시지를 보낸다
                if(commentPostRequestDto.getPetOwnId() != pet.get().getId()) {
                    Optional<Pet> pet1 = petRepository.findById(commentPostRequestDto.getPetOwnId());
                    notificationService.saveNotificationInfo(NotificationType.WARM_ALARM, pet1.get(), commenterId);
                }

            } else if(commentGroup == 2) {
                notificationService.saveNotificationInfo(NotificationType.MEMORY_COMMENT_ALARM, pet.get(), commenterId);
                // 추억 소유자와 댓글자가 다르면 추억 소유자도 메시지를 보낸다
                if(memory.get().getPet().getId() != pet.get().getId()) notificationService.saveNotificationInfo(NotificationType.MEMORY_ALARM, memory.get().getPet(), commenterId);
            }
        }

        Comment comment = Comment.builder()
                .parentCommentId(parentCommentId)
                .memory(commentGroup == 2 ? memory.get() : null)
                .commenterId(commenterId)
                .depth(depth)
                .pet(pet.get())
                .comment(commentContext)
                .commentGroup(commentGroup == 1 ? CommentGroup.LAST_WORD : CommentGroup.MEMORY_COMMENT)
                .build();

        commentRepository.save(comment);

        commentDeleteResponseDto = CommentPostResponseDto.builder().decCode('1').errorMsg("댓글 정상 등록되었습니다.").build();

        return commentDeleteResponseDto;
    }

    @Transactional(readOnly = false)
    public WarmCommentUpdateResponseDto updateWarmComment(WarmCommentUpdateRequestDto warmCommentUpdateRequestDto) {

        Optional<Comment> commentOptional = commentRepository.findById(warmCommentUpdateRequestDto.getCommentId());
        WarmCommentUpdateResponseDto warmCommentUpdateResponseDto;
        if(!commentOptional.isPresent()) return WarmCommentUpdateResponseDto.builder().decCode('0').errorMsg("해당 comment id로 조회한 데이터가 없습니다.").build();
        Comment comment = commentOptional.get();

        comment.updateComment(warmCommentUpdateRequestDto.getComment());
        warmCommentUpdateResponseDto = WarmCommentUpdateResponseDto.builder().decCode('1').build();

        return warmCommentUpdateResponseDto;
    }

    public PetAndMemoryCommentsResponseDto findComments(PetAndMemoryCommentsRequestDto petAndMemoryCommentsRequestDto) {
        PageRequest pageRequest = PageRequest.of(petAndMemoryCommentsRequestDto.getCurrentPage()-1, petAndMemoryCommentsRequestDto.getDataCounts());
        CommentGroup commentGroup = petAndMemoryCommentsRequestDto.getCommentGroup() == 1 ? CommentGroup.LAST_WORD : CommentGroup.MEMORY_COMMENT;
        int depth = petAndMemoryCommentsRequestDto.getDepth();

        Page<Comment> page = null;
        if(petAndMemoryCommentsRequestDto.getCommentGroup() == 1) { // 1: 따뜻한 한마디
            Optional<Pet> pet = petRepository.findById(petAndMemoryCommentsRequestDto.getPetId());
            page = commentRepository.findCommentsByPetId(pet.get(), commentGroup, depth, pageRequest);

        } else if(petAndMemoryCommentsRequestDto.getCommentGroup() == 2) { // 2: 추억댓글
            Optional<Memory> memoryOptional = memoryRepository.findById(petAndMemoryCommentsRequestDto.getMemoryId());
            page = commentRepository.findCommentsByMemoryId(memoryOptional.get(),commentGroup, depth, pageRequest);
        }

        List<Comment> comments = page.getContent();
        List<Long> commentIds = new ArrayList<>(); // depth 2 의 부모 댓글 조회를 위한 댓글 id
        HashSet<Long> petIds = new HashSet<>();
        HashMap<Long, Pet> petInfo = new HashMap<>();

        List<PetAndMemoryCommentResponseDto> PetAndMemoryCommentResponseDtos = new ArrayList<>();
        for (Comment comment : comments) {
            petIds.add(comment.getCommenterId());
            commentIds.add(comment.getId());
        }

        // 펫 정보 조회
        List<Pet> pets = petRepository.findByIds(petIds);
        // 펫 정보 저장
        for(Pet pet : pets) {
            petInfo.put(pet.getId(), pet);
        }
        HashMap<Long, Integer> replyInfo = null;

        if(depth == 1) {
            // 대댓글 수 조회
            List<ReplyPerCommentDto> replyCount = commentRepository.getReplyCount(commentIds);
            replyInfo = new HashMap<>();
            for(ReplyPerCommentDto replyPerCommentDto : replyCount) {
                replyInfo.put(replyPerCommentDto.getCommentId(), replyPerCommentDto.getReply());
            }
        }

        // 댓글 생성
        for (Comment comment : comments) {
            PetAndMemoryCommentResponseDtos.add(PetAndMemoryCommentResponseDto.builder()
                            .petId(petInfo.get(comment.getCommenterId()).getId())
                            .petName(petInfo.get(comment.getCommenterId()).getPetName())
                            .petProfileUrl(petInfo.get(comment.getCommenterId()).getPetProfileUrl())
                            .commenterId(comment.getCommenterId())
                            .comment(comment.getComment())
                            .commentId(comment.getId())
                            .commentCreatedDate(comment.getCreatedDate())
                            .replyCount(depth == 1 ? replyInfo.getOrDefault(comment.getId(),0): null)
                    .build());
        }

        PetAndMemoryCommentsResponseDto petAndMemoryCommentsResponseDto = PetAndMemoryCommentsResponseDto.builder().totalPages(page.getTotalPages()).currentPage(page.getNumber()+1).dataCounts(page.getContent().size()).petAndMemoryCommentResponseDtos(PetAndMemoryCommentResponseDtos).build();
        return petAndMemoryCommentsResponseDto;
    }

    public PetAndMemoryCommentsResponseDto findReply(PetAndMemoryCommentsRequestDto petAndMemoryCommentsRequestDto) {
        PageRequest pageRequest = PageRequest.of(petAndMemoryCommentsRequestDto.getCurrentPage()-1, petAndMemoryCommentsRequestDto.getDataCounts());
        CommentGroup commentGroup = petAndMemoryCommentsRequestDto.getCommentGroup() == 1 ? CommentGroup.LAST_WORD : CommentGroup.MEMORY_COMMENT;
        int depth = petAndMemoryCommentsRequestDto.getDepth();

        // 댓글 id로 대댓글 찾기
        Page<Comment> page = commentRepository.findByParentCommentId(petAndMemoryCommentsRequestDto.getCommentId(), pageRequest);

        List<Comment> comments = page.getContent();

        List<PetAndMemoryCommentResponseDto> PetAndMemoryCommentResponseDtos = new ArrayList<>();
        HashMap<Long, Pet> petInfo = new HashMap<>();
        HashSet<Long> petIds = new HashSet<>();

        for (Comment comment : comments) {
            petIds.add(comment.getCommenterId());
        }

        // 펫 정보 조회
        List<Pet> pets = petRepository.findByIds(petIds);
        // 펫 정보 저장
        for(Pet pet : pets) {
            petInfo.put(pet.getId(), pet);
        }

        // 댓글 생성
        for (Comment comment : comments) {
            PetAndMemoryCommentResponseDtos.add(PetAndMemoryCommentResponseDto.builder()
                    .petId(petInfo.get(comment.getCommenterId()).getId())
                    .petName(petInfo.get(comment.getCommenterId()).getPetName())
                    .petProfileUrl(petInfo.get(comment.getCommenterId()).getPetProfileUrl())
                    .commenterId(comment.getCommenterId())
                    .comment(comment.getComment())
                    .commentId(comment.getId())
                    .commentCreatedDate(comment.getCreatedDate())
                    .replyCount(0)
                    .build());
        }

        PetAndMemoryCommentsResponseDto petAndMemoryCommentsResponseDto = PetAndMemoryCommentsResponseDto.builder().totalPages(page.getTotalPages()).currentPage(page.getNumber()+1).dataCounts(page.getContent().size()).petAndMemoryCommentResponseDtos(PetAndMemoryCommentResponseDtos).build();
        return petAndMemoryCommentsResponseDto;
    }
}

