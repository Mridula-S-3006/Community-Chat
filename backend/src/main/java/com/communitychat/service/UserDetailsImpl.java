package com.communitychat.service;

import com.communitychat.model.entity.User;

public class UserDetailsImpl {
    private Long id;
    private String username;

    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
