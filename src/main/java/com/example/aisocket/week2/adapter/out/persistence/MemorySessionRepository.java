package com.example.aisocket.week2.adapter.out.persistence;

import com.example.aisocket.week2.domain.SessionRepository;
import com.example.aisocket.week2.domain.UserSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MemorySessionRepository implements SessionRepository {

    private final ConcurrentHashMap<String, List<UserSession>> roomSessionMap = new ConcurrentHashMap<>();

    public void save(String roomId, UserSession session) {
        roomSessionMap.computeIfAbsent(roomId, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(session);
    }

    public List<UserSession> findByRoomId(String roomId) {
        List<UserSession> sessions = roomSessionMap.get(roomId);

        if(sessions == null){
            return Collections.emptyList();
        }

        return new ArrayList<>(sessions);
    }

    public void remove(String roomId, UserSession session) {
        List<UserSession> sessions = roomSessionMap.get(roomId);

        if (sessions != null) {
            sessions.remove(session);
            session.close();

            if (sessions.isEmpty()) {
                roomSessionMap.remove(roomId);
            }
        }
    }

}