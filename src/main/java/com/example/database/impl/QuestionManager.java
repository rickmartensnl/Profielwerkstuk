package com.example.database.impl;

import com.example.database.Model;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class QuestionManager {

    @Getter private static QuestionManager questionManager;

    public QuestionManager() {
        questionManager = this;
    }

    public Question getQuestion(UUID uuid) {
        return new Question(uuid);
    }

    public static class Question implements Model {

        @Expose @Getter private final UUID uuid;
        @Getter @Setter private String question;
        @Getter @Setter private String information;
        @Getter @Setter private int flags;

        public Question(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public String getJsonObject() {
            return null;
        }

    }

    public static class QuestionAnswer {

        @Getter private final Type type;
        @Getter @Setter private String calculation;

        public QuestionAnswer(Type type, String calculation) {
            this.type = type;
            this.calculation = calculation;
        }

    }

    public enum Type {

        INT(Integer.class);

        @Getter private final Class clazz;

        Type(Class clazz) {
            this.clazz = clazz;
        }

    }

    public static class QuestionVariable {

        @Getter private final Type type;
        private String value;
        @Getter @Setter private String unit;
        @Getter @Setter private String theValue;

        public QuestionVariable(Type type) {
            this.type = type;
        }

    }

}
