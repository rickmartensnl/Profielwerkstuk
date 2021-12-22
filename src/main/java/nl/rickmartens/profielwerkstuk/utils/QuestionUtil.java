package nl.rickmartens.profielwerkstuk.utils;

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

    public static int calculateAnswer(String calc) throws ScriptException {
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
            }
        }

        double answer = Double.parseDouble(engine.eval(calc).toString());

        return (int) Math.round(answer);
    }

}
