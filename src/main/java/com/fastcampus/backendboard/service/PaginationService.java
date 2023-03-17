package com.fastcampus.backendboard.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;
@Service
public class PaginationService {
    private static final int BAR_LENGTH=5;

    public List<Integer> getPaginationBarNumbers(int current, int total){
        int start = Math.max(current-(BAR_LENGTH/2),0);
        int end   = Math.min(start+BAR_LENGTH,total);

        return IntStream.range(start,end).boxed().toList();
    }
    public int getBarLength(){return BAR_LENGTH;}
}
