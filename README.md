# Custom Bosses
A Paper plugin that adds various different mini-bosses to your server.

## Usage
#### Spawning a Boss
As a player
```
/bossEntity <TYPE> spawn
```
As console
```
/bossEntity <TYPE> spawn <x> <y> <z>
```

#### Despawning a Boss
As a player or console
```
/bossEntity <TYPE> despawn
```

## Builtin Boss Types
See [boss_config.yml](/src/main/resources/boss_config.yml)
- BASIC_ZOMBIE
- BASIC_HUSK
- MINER_ZOMBIE
- BOB

## ToDo
(in no particular order)
- [x] Customizable Bosses (via a yaml file)
- [ ] User defined abilities
- [ ] PlaceholderAPI integration (for statistics)
- [ ] Boss levels (scale health, damage, resistance, etc)
- [ ] Custom items (armor & weapons)
- [ ] New enchantments
- [ ] More Abilities
- [ ] PlaceholderAPI hook (bossEntity kills/spawns)
- [ ] Boss spawn eggs
- [ ] Wiki (for bossEntity & ability showcase, config documentation)
