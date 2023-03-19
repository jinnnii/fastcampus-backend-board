package com.fastcampus.backendboard.service;

import com.fastcampus.backendboard.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    public Object parseHashtagNames(String content) {
        return null;
    }

    public Object findHashtagsByNames(Set<String> expectedHashtagNames) {
        return null;
    }

    public void deleteHashtagWithoutArticles(Object any) {
    }

}
