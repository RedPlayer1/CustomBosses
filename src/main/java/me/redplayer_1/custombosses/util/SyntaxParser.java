package me.redplayer_1.custombosses.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for replacing string representations of variable names with their appropriate values
 */
public class SyntaxParser {
    private final HashMap<String, String> vars = new HashMap<>();

    public SyntaxParser(String[] placeholders, String[] values) {
        for (int i = 0; i < placeholders.length; i++) {
            try {
                vars.put(placeholders[i], values[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                vars.put(placeholders[i], null);
            }
        }
    }

    public SyntaxParser(String... placeholders) {
        for (String str : placeholders) {
            vars.put(str, null);
        }
    }

    public void setVar(String key, String value) {
        vars.put(key, value);
    }

    /**
     * Parses the input with the stored placeholders and values
     *
     * @param input the string to parse
     * @return the resulting string
     */
    public String parse(String input) {
        for (Map.Entry<String, String> i : vars.entrySet()) {
            input = input.replace(i.getKey(), i.getValue());
        }
        return input;
    }

    /**
     * Parses the input with the provided values using predefined placeholders
     */
    public String parse(String input, String... values) {
        int index = 0;
        for (String placeholder : vars.keySet()) {
            if (values[index] == null) {
                input = input.replace(placeholder, "");
            } else {
                input = input.replace(placeholder, values[index]);
            }
            index++;
        }
        return input;
    }
}
