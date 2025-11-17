package com.main.serviceImpls;

import com.main.dtos.TagDto;
import com.main.entities.Tag;
import com.main.repositories.TagRepository;
import com.main.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    @Transactional
    public TagDto createTag(TagDto tagDto) {
        if (tagRepository.existsByNameIgnoreCase(tagDto.getName())) {
            throw new IllegalArgumentException("Tag with name " + tagDto.getName() + " already exists.");
        }
        Tag tag = new Tag();
        tag.setName(tagDto.getName());
        Tag savedTag = tagRepository.save(tag);
        return new TagDto(savedTag.getId(), savedTag.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tag -> new TagDto(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new RuntimeException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }
}
