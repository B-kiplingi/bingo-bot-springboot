package com.example.bingospring.service;

import com.example.bingospring.model.Server;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ServerService {
    Server getOrCreateServer(long serverId);
    Server getServer(long serverId);
    String getPoolChannel(long guildId);
    void save(Server server);
}
