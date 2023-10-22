package me.redplayer_1.custombosses.abilities;

import me.redplayer_1.custombosses.abilities.impl.FireballAbility;

public enum Abilities {
    FIREBALL(new FireballAbility());

    public final BossAbility ability;

    Abilities(BossAbility ability) {
        this.ability = ability;
    }
}
