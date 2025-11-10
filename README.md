# Conditional Effects
Apply potion effects to players based on pre-defined conditions.

## Setup
- Add Conditional Effects jar into your plugins folder
- Restart your server
- Edit effects.yml to your liking
- Type /conditionaleffects reload

## Commands:
- **/conditionaleffects help:** Show the help message
- **/conditionaleffects reload:** Reload the plugin
- **/conditionaleffects give &lt;player&gt; &lt;effectId&gt;:** Give a conditional effect to a player
- **/conditionaleffects check &lt;player&gt; &lt;effectId&gt;:** Manually check a conditional effect on a player

## Permissions:
- **conditionaleffects.reload:** Allows the user to reload the plugin
- **conditonaleffects.give:** Allows the user to give conditional effects to players
- **conditionaleffects.check:** Allows the user to manually check conditional effects on players

## Effects
```yaml
hub-effects:
  conditions-check-interval-ticks: 20
  effects:
  - SPEED, 1
  - JUMP_BOOST, 1
  conditions:
    worlds:
    - hub
```
This example effect will provide speed and jump boost effects in the hub world.

### Settings
- **conditions-check-interval-ticks:** The number of ticks between each condition check.
- **effects:** The potion effects that will be applied when the conditions are met. In the format &lt;EffectType&gt;, \[Amplifier], \[Duration], \[HideParticles], \[ShowIcon].
- **conditions:** The conditions that must be met in order for the effects to applied. All conditions must be met for the effects to be applied. See the [Conditions](#condiions) section below.

### Conditions
- **worlds:** Worlds that the effects will be applied in.
- **environments:** Environments that the effects will be applied in (e.g. NETHER, OVERWORLD).
- **biomes:** Biomes that the effects will be applied in.
- **gamemodes:** Gamemodes that the effects will be applied in.
- **has-permission:** Effects will only be applied to players with all of these permissions.
- **missing-permission:** Effects will only be applied to players who don't have any of these permissions.
- **min-x:** Minimum X coordinate where the effects will be applied.
- **max-x:** Maximum X coordinate where the effects will be applied.
- **min-y:** Minimum Y coordinate where the effects will be applied.
- **max-y:** Maximum Y coordinate where the effects will be applied.
- **min-z:** Minimum Z coordinate where the effects will be applied.
- **max-z:** Maximum Z coordinate where the effects will be applied.
- **in-water:** Effects will only be applied to player in water.
- **not-in-water:** Effects will only be applied to players not in water.