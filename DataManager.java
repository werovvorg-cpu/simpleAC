package me.server.anticheat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager {
    private final Map<UUID, PlayerData> map = new ConcurrentHashMap<>();

    public PlayerData create(UUID id) { return map.computeIfAbsent(id, k -> new PlayerData()); }
    public PlayerData get(UUID id) { return map.get(id); }
    public void remove(UUID id) { map.remove(id); }
}
