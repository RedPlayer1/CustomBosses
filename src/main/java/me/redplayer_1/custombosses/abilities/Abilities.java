package me.redplayer_1.custombosses.abilities;

import me.redplayer_1.custombosses.abilities.impl.FireRingAbility;

public enum Abilities {
    FIREBALL(new FireRingAbility());

    public final BossAbility ability;

    Abilities(BossAbility ability) {
        this.ability = ability;
    }
}
