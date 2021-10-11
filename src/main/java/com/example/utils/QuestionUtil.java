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
                String[] minMax = splitValue[1].split("-");
                return MathUtil.randomInt(Integer.parseInt(minMax[0]), Integer.parseInt(minMax[1]));
            }
        }

        return null;
    }

}
