package com.example.bingospring.service;

import ch.qos.logback.classic.pattern.Util;
import com.example.bingospring.model.Round;
import com.example.bingospring.model.Server;
import com.example.bingospring.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ServerServiceImpl implements ServerService {
    private final ServerRepository serverRepository;

    @Autowired
    public ServerServiceImpl(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @Override
    public Server getOrCreateServer(long serverId) {
        return serverRepository.findById(serverId)
                .orElseGet(() -> {
                    Server server = new Server(serverId);
                    server.setActive(false);
                    return serverRepository.save(server);
                });
    }

    @Override
    public Server getServer(long serverId) {
        return serverRepository.findById(serverId).orElseGet(() -> null);
    }

    @Override
    public String getPoolChannel(long serverId) {
        return serverRepository.findById(serverId).get().getPoolChannel();
    }

    @Override
    public void save(Server server) {
        serverRepository.save(server);
    }
}