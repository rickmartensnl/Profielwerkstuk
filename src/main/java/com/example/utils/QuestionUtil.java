package com.example.utils;

import com.example.database.impl.QuestionManager;

public class QuestionUtil {

    public static Object generateFromQuestionFormat(QuestionManager.QuestionVariable questionVariable) {
        if (questionVariable.getDepends() != null) {
            return null;
        }

        if (questionVariable.getValue() instanceof String) {
            String[] splitValue = ((String) questionVariable.getValue()).split("\\.");
            if (splitValue[0].equals("random")) {
                return "Lol";
            }
        }

        return null;
    }

}
