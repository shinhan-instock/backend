package com.pda.community_module.service;

import org.springframework.stereotype.Service;

@Service
public class PostHistoryServiceImpl implements PostHistoryService {
    @Override
    public void addHistory(Long postId, String status) {
        System.out.println("PostHistory updated for postId: " + postId + " with status: " + status);
    }
}
