package com.example.planowestapp1;

public class FriendEntry {
    private String displayName;
    private String nickname;

    public FriendEntry(String displayName, String nickname) {
        this.displayName = displayName;
        this.nickname = nickname;
    }

    public String getFriendName() {
        return displayName;
    }

    public String getNickname() {
        return nickname;
    }
}
