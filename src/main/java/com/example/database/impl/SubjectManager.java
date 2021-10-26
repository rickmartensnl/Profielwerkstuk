package com.example.database.impl;

import com.example.database.Model;
import com.example.exceptions.DatabaseOfflineException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import io.sentry.Sentry;
import lombok.Getter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class SubjectManager {

    @Getter private static SubjectManager subjectManager;

    public SubjectManager() {
        subjectManager = this;
    }

    public Subject getSubject(UUID uuid) throws DatabaseOfflineException {
        try {
            return new Subject(uuid);
        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }
    }

    public ArrayList<Subject> getAllSubjects() throws DatabaseOfflineException {
        ArrayList<Subject> subjects = new ArrayList<>();

        subjects.add(getSubject(UUID.fromString("ed244d8c-1c87-11ec-9621-0242ac130002")));
        subjects.add(getSubject(UUID.fromString("ed244d8c-1c87-11ec-9621-5552ac130002")));
        subjects.add(getSubject(UUID.fromString("ed244d8c-1c87-11ec-9621-8882ac130002")));
        subjects.add(getSubject(UUID.fromString("ed244d8c-1c87-11ec-9621-2222ac130002")));

        return subjects;
    }

    public class Subject implements Model {

        @Expose @Getter private final UUID uuid;
        @Expose @Getter private String name;
        @Expose @Getter private int flags;
        @Expose @Getter private UserManager.User creator;

        public Subject(UUID uuid) throws SQLException, DatabaseOfflineException {
            this.uuid = uuid;
            this.name = "Wiskunde B";
            this.flags = 1;
            this.creator = UserManager.getUserManager().getUser(UUID.fromString("ed244d8c-1c87-11ec-9621-0242ac130002"));
        }

        @Override
        public String getJsonObject() {
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            return gson.toJson(this);
        }

    }

}
