# Custom Bosses
A Paper plugin that adds various different mini-bosses to your server.

## Usage
#### Spawning a Boss
As a player
```
/boss <TYPE> spawn
```
As console
```
/boss <TYPE> spawn <x> <y> <z>
```

#### Despawning a Boss
As a player or console
```
/boss <TYPE> despawn
```

## Boss Types
See [the BossType enum](/src/main/java/me/redplayer_1/custombosses/boss/BossType.java)
- BASIC_ZOMBIE
- BASIC_HUSK
- MINER_ZOMBIE
- BOB

## ToDo
(in no particular order)
- [ ] Customizable Bosses (via a yaml file)
- [ ] Boss levels (scale health, damage, resistance, etc)
- [ ] Custom items (armor & weapons)
- [ ] New enchantments
- [ ] More Abilities
- [ ] PlaceholderAPI hook (boss kills/spawns)
- [ ] Boss spawn eggs
- [ ] Wiki (for boss & ability showcase, config documentation)
