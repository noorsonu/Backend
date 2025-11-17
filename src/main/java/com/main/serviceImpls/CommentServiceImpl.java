package com.main.serviceImpls;

import com.main.dtos.CommentResponseDto;
import com.main.dtos.UserPublicDto;
import com.main.entities.Comment;
import com.main.entities.Post;
import com.main.entities.UserEntity;
import com.main.repositories.CommentRepository;
import com.main.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    private CommentResponseDto convertToDto(Comment comment) {
        UserPublicDto userPublicDto = new UserPublicDto(comment.getUser().getId(), comment.getUser().getName(), comment.getUser().getEmail());
        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                userPublicDto,
                comment.getPost().getId()
        );
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(Post post, UserEntity user, String content) {
        if (user.isBlocked()) {
            throw new IllegalArgumentException("Blocked users cannot add comments.");
        }
        Comment c = new Comment();
        c.setPost(post);
        c.setUser(user);
        c.setContent(content);
        Comment savedComment = commentRepository.save(c);
        return convertToDto(savedComment);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long commentId, UserEntity currentUser, boolean isAdmin, String content) {
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!isAdmin && !c.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can edit only your own comments");
        }
        if (content != null && !content.isBlank()) {
            c.setContent(content);
        }
        Comment updatedComment = commentRepository.save(c);
        return convertToDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, UserEntity currentUser, boolean isAdmin) {
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!isAdmin && !c.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can delete only your own comments");
        }
        commentRepository.delete(c);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> listByPost(Long postId) {
        return commentRepository.findByPost_Id(postId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> listByUserId(Long userId) {
        return commentRepository.findByUser_Id(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> listByUserName(String name) {
        return commentRepository.findByUser_NameIgnoreCase(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAllByUserId(Long userId) {
        commentRepository.deleteByUser_Id(userId);
    }
}
