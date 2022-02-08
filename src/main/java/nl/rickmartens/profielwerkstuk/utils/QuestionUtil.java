/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

package nl.rickmartens.profielwerkstuk.utils;

import nl.rickmartens.profielwerkstuk.ProfielwerkstukServerLauncher;
import nl.rickmartens.profielwerkstuk.database.impl.QuestionManager;
import com.google.gson.internal.LinkedTreeMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static double calculateAnswer(String calc) throws ScriptException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");

        Pattern pattern = Pattern.compile("(?<=\\{).*?(?=})");

        Matcher matcher = pattern.matcher(calc);
        if (matcher.find()) {
            String group0 = matcher.group(0);

            if (group0.toLowerCase().startsWith("$asin[")) {
                String asin = group0.replaceFirst("\\$asin\\[", "");
                asin = asin.substring(0, asin.length() - 1);

                if (asin.contains("$")) {
                    String other = asin.substring(asin.indexOf("$"));
                    other = other.substring(0, other.indexOf("]") + 1);

                    if (other.toLowerCase().startsWith("$sin[")) {
                        String sin = other.replaceFirst("\\$sin\\[", "");
                        sin = sin.substring(0, sin.length() - 1);

                        Double sinans = Math.toRadians(Double.parseDouble(sin));
                        sinans = Math.sin(sinans);

                        asin = asin.replaceFirst("\\$sin\\[" + sin + "]", String.valueOf(sinans));
                    }

                    Double toAsin = (Double) engine.eval(asin);
                    Double answer = Math.asin(toAsin);
                    answer = Math.toDegrees(answer);

                    calc = calc.replaceFirst("\\{[^\"]+}|\\\\S+", String.valueOf(answer));
                }
            } else if (group0.toLowerCase().startsWith("$atan[")) {
                String atan = group0.replaceFirst("\\$atan\\[", "");
                atan = atan.substring(0, atan.length() - 1);
    
                if (atan.contains("$")) {
                    String other = atan.substring(atan.indexOf("$"));
                    other = other.substring(0, other.indexOf("]") + 1);
        
                    if (other.toLowerCase().startsWith("$sin[")) {
                        String sin = other.replaceFirst("\\$sin\\[", "");
                        sin = sin.substring(0, sin.length() - 1);
        
                        Double sinans = Math.toRadians(Double.parseDouble(sin));
                        sinans = Math.sin(sinans);
        
                        atan = atan.replaceFirst("\\$sin\\[" + sin + "]", String.valueOf(sinans));
                    } else if (other.toLowerCase().startsWith("$cos[")) {
                        String cos = other.replaceFirst("\\$cos\\[", "");
                        cos = cos.substring(0, cos.length() - 1);
    
                        Double cosans = Math.toRadians(Double.parseDouble(cos));
                        cosans = Math.cos(cosans);
    
                        atan = atan.replaceFirst("\\$cos\\[" + cos + "]", String.valueOf(cosans));
                    }
                    
                    if (atan.contains("$")) {
                        other = atan.substring(atan.indexOf("$"));
                        other = other.substring(0, other.indexOf("]") + 1);
                        
                        if (other.toLowerCase().startsWith("$sin[")) {
                            String sin = other.replaceFirst("\\$sin\\[", "");
                            sin = sin.substring(0, sin.length() - 1);
        
                            Double sinans = Math.toRadians(Double.parseDouble(sin));
                            sinans = Math.sin(sinans);
        
                            atan = atan.replaceFirst("\\$sin\\[" + sin + "]", String.valueOf(sinans));
                        } else if (other.toLowerCase().startsWith("$cos[")) {
                            String cos = other.replaceFirst("\\$cos\\[", "");
                            cos = cos.substring(0, cos.length() - 1);
        
                            Double cosans = Math.toRadians(Double.parseDouble(cos));
                            cosans = Math.cos(cosans);
        
                            atan = atan.replaceFirst("\\$cos\\[" + cos + "]", String.valueOf(cosans));
                        }
                    }
    
                    Double toAtan = (Double) engine.eval(atan);
                    Double answer = Math.atan(toAtan);
                    answer = Math.toDegrees(answer);
        
                    calc = calc.replaceFirst("\\{[^\"]+}|\\\\S+", String.valueOf(answer));
                }
            }
        }

        double answer = Double.parseDouble(engine.eval(calc).toString());

        return Math.round(answer);
    }

}
