package com.main.serviceImpls;

import com.main.dtos.UserPublicDto;
import com.main.entities.Like;
import com.main.entities.Post;
import com.main.entities.UserEntity;
import com.main.repositories.LikeRepository;
import com.main.repositories.PostRepository;
import com.main.services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Override
    @Transactional
    public void toggleLike(Long postId, UserEntity user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserPublicDto> getLikesForPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        return likeRepository.findByPost(post).stream()
                .map(like -> new UserPublicDto(like.getUser().getId(), like.getUser().getName(), like.getUser().getEmail()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPostLikedByUser(Long postId, UserEntity user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        return likeRepository.findByUserAndPost(user, post).isPresent();
    }
}
