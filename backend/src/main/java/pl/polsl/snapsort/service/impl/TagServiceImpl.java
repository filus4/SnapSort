package pl.polsl.snapsort.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.polsl.snapsort.repository.TagRepository;
import pl.polsl.snapsort.service.TagService;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
}