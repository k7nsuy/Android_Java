package com.example.chattingapp.model;

import org.w3c.dom.Comment;

import java.util.HashMap;
import java.util.Map;

public class Chat {

    public Map<String,Boolean> users = new HashMap<>(); // 채팅방의 유저들
    public Map<String,Comment> comments = new HashMap<>(); // 채팅방의 대화내용

    public static class Comment {
        public String uid;
        public String message;
        public Object timestmap;
        public Map<String,Object> readUsers = new HashMap<>();
    }
}
