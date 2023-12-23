package me.redplayer_1.custombosses.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageUtils {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Replaces any occurrences of the placeholder names with the string representation of the element at the same index in placeholderValues.
     * If the lengths of the two arrays are unequal, this method will fail silently and return the supplied message.
     *
     * @param placeholderNames  the names of the placeholders
     * @param placeholderValues the corresponding values of the placeholders
     * @param message           the message to be formatted
     * @return the formatted message
     */
    public static String replacePlaceholders(String[] placeholderNames, Object[] placeholderValues, String message) {
        if (placeholderNames.length != placeholderValues.length) return message;
        String result = message;
        for (int i = 0; i < placeholderNames.length; i++) {
            result = result.replace(placeholderNames[i], placeholderValues[i].toString());
        }
        return result;
    }

    /**
     * Converts a String with MiniMessage syntax to a Component.
     */
    public static Component mmsgToComponent(String miniMessage) {
        return MINI_MESSAGE.deserialize(miniMessage);
    }

    /**
     * Converts a Component of a MiniMessage to a String with Minecraft color codes.
     */
    public static String mmsgToString(Component miniMessage) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(miniMessage);
    }
}
