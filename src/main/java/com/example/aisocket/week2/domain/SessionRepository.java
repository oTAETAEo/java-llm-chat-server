package com.example.aisocket.week2.domain;

import java.util.List;

public interface SessionRepository {

    void save(String roomId, UserSession session);

    List<UserSession> findByRoomId(String roomId);

    void remove(String roomId, UserSession session);

}
