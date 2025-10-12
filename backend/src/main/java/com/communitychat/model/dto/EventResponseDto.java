package com.communitychat.model.dto;

public class EventResponseDto {
    private Long id;
    private Long eventId;
    private Long userId;
    private String response;

    public EventResponseDto() {}

    public EventResponseDto(Long id, Long eventId, Long userId, String response) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.response = response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
}
