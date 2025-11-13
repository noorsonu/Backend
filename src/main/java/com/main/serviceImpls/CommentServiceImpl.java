package com.main.serviceImpls;

import com.main.entities.Comment;
import com.main.entities.Post;
import com.main.entities.UserEntity;
import com.main.repositories.CommentRepository;
import com.main.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment addComment(Post post, UserEntity user, String content) {
        Comment c = new Comment();
        c.setPost(post);
        c.setUser(user);
        c.setContent(content);
        return commentRepository.save(c);
    }

    @Override
    public Comment updateComment(Long commentId, UserEntity currentUser, boolean isAdmin, String content) {
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!isAdmin && !c.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can edit only your own comments");
        }
        if (content != null && !content.isBlank()) {
            c.setContent(content);
        }
        return commentRepository.save(c);
    }

    @Override
    public void deleteComment(Long commentId, UserEntity currentUser, boolean isAdmin) {
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!isAdmin && !c.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can delete only your own comments");
        }
        commentRepository.delete(c);
    }

    @Override
    public List<Comment> listByPost(Long postId) {
        return commentRepository.findByPost_Id(postId);
    }

    @Override
    public List<Comment> listByUserId(Long userId) {
        return commentRepository.findByUser_Id(userId);
    }

    @Override
    public List<Comment> listByUserName(String name) {
        return commentRepository.findByUser_NameIgnoreCase(name);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        commentRepository.deleteByUser_Id(userId);
    }
}
