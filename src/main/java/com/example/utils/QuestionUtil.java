package com.example.utils;

import com.example.database.impl.QuestionManager;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
        } else if (questionVariable.getValue() instanceof ArrayList) {
            ArrayList<String> value = new ArrayList<>();
            ((ArrayList<?>) questionVariable.getValue()).forEach(val -> {
                value.add((String) val);
            });

            return value.get(MathUtil.randomInt(value.size()));
        }

        return null;
    }

    public static Object generateFromQuestionFormat(QuestionManager.QuestionVariable questionVariable, QuestionManager.QuestionVariable depends) {
        if (questionVariable.getDepends() == null) {
            return generateFromQuestionFormat(questionVariable);
        }

        if (questionVariable.getValue() instanceof LinkedTreeMap) {
            HashMap<String, String> value = new HashMap<>();
            ((LinkedTreeMap<?, ?>) questionVariable.getValue()).forEach((val, val2) -> {
                value.put((String) val, (String) val2);
            });
            String[] splitValue = value.get(depends.getTheValue()).split("\\.");
            if (splitValue[0].equals("random")) {
                String[] minMax = splitValue[1].split("-");
                return MathUtil.randomInt(Integer.parseInt(minMax[0]), Integer.parseInt(minMax[1]));
            }
        }

        return null;
    }

}
