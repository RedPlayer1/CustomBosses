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

## Permissions
```custombosses.commands.boss``` - root command for the /boss command
```custombosses.commands.boss.spawn``` - ability to spawn bosses with /boss
```custombosses.commands.boss.kill``` - ability to kill all bosses of a certain type with /boss

## ToDo
(in no particular order)
- [x] Customizable Bosses (via a yaml file)
- [ ] User defined abilities
- [x] PlaceholderAPI integration (for statistics)
- [ ] Boss levels (scale health, damage, resistance, etc)
- [ ] Custom items (armor & weapons)
- [ ] New enchantments
- [ ] More Abilities
- [ ] Boss spawn eggs
- [x] Wiki (for bossEntity & ability showcase, config documentation)
