package me.redplayer_1.custombosses.util;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class LocationUtils {
    /**
     * Gets the closest player to that location
     *
     * @param loc            the location (must have a non-null world)
     * @param mustBeSurvival whether the player returned must be in gamemode survival
     * @param range          how close the player must be to the location. a range of -1 will disable this
     * @return the closest player, or null if there are no players in that world/range
     */
    public static @Nullable Player getClosestPlayer(Location loc, boolean mustBeSurvival, double range) {
        Player closest = null;
        for (Player p : loc.getNearbyPlayers(range, player -> player.getGameMode() == GameMode.SURVIVAL)) {
            if (closest == null) {
                closest = p;
            } else if (loc.distanceSquared(closest.getLocation()) > loc.distanceSquared(p.getLocation())) {
                closest = p;
            }
        }
        return closest;
    }
}
