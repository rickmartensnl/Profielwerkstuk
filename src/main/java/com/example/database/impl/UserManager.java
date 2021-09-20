package com.example.database.impl;

import com.example.database.Model;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    @Getter private static UserManager userManager;

    public Map<UUID, User> userMap;

    public UserManager() {
        userManager = this;
        userMap = new HashMap<>();
    }

    public User getOrLoadUser(UUID uuid) {
        if (userMap.get(uuid) != null) {
            userMap.get(uuid).setCached(true);
            return userMap.get(uuid);
        }

        User user = new User(uuid);
        userMap.put(uuid, user);

        return user;
    }

    public static class User implements Model {

        @Getter private final UUID uuid;

        @Getter @Setter private boolean cached;

        public User(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public String getJsonObject() {
            return "{\"token\":\"" + uuid + "\",\"chached\":" + isCached() + "}";
        }

    }

}
