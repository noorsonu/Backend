package com.main.services;

import com.main.dtos.TagDto;
import java.util.List;

public interface TagService {
    TagDto createTag(TagDto tagDto);
    List<TagDto> getAllTags();
    void deleteTag(Long id);
}
