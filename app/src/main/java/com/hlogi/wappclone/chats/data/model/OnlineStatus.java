package com.hlogi.wappclone.chats.data.model;

public class OnlineStatus {

    private String number;
    private Boolean online;
    private Long last_seen;
    private Boolean typing;
    private Boolean recording;

    public OnlineStatus() {
    }

    public OnlineStatus(String number, Boolean online, Long last_seen, Boolean typing, Boolean recording) {
        this.number = number;
        this.online = online;
        this.last_seen = last_seen;
        this.typing = typing;
        this.recording = recording;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Long getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(Long last_seen) {
        this.last_seen = last_seen;
    }

    public Boolean getTyping() {
        return typing;
    }

    public void setTyping(Boolean typing) {
        this.typing = typing;
    }

    public Boolean getRecording() {
        return recording;
    }

    public void setRecording(Boolean recording) {
        this.recording = recording;
    }

    @Override
    public String toString() {
        return "OnlineStatus{" +
                "number='" + number + '\'' +
                ", online=" + online +
                ", last_seen=" + last_seen +
                ", typing=" + typing +
                ", recording=" + recording +
                '}';
    }

}
