package me.redplayer_1.custombosses.util;

public class MessageUtils {

    /**
     * Replaces any occurrences of the placeholder names with the string representation of the element at the same index in placeholderValues.
     * If the lengths of the two arrays are unequal, this method will fail silently and return the supplied message.
     * @param placeholderNames the names of the placeholders
     * @param placeholderValues the corresponding values of the placeholders
     * @param message the message to be formatted
     * @return the formatted message
     */
    public static String replacePlaceholders(String[] placeholderNames, Object[] placeholderValues, String message) {
        if (placeholderNames.length != placeholderValues.length) return message;
        String result = message;
        for (int i = 0; i < placeholderNames.length; i ++) {
            result = result.replace(placeholderNames[i], placeholderValues[i].toString());
        }
        return result;
    }
}
