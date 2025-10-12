package com.communitychat.model.dto;

import java.util.Set;

public class GroupDto {
    private Long id;
    private String name;
    private Long ownerId;
    private Set<Long> memberIds;

    public GroupDto() {}

    public GroupDto(Long id, String name, Long ownerId, Set<Long> memberIds) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.memberIds = memberIds;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public Set<Long> getMemberIds() { return memberIds; }
    public void setMemberIds(Set<Long> memberIds) { this.memberIds = memberIds; }
}
