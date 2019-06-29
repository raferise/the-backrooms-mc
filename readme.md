# The Backrooms
Made with Jitse's NPCLib (https://github.com/JitseB/NPCLib)

## What it does
This plugin overwrites the nether's chunk generation to create the backrooms. While in the backrooms...
* Spooky noises play
* Blocks placed and broken undo when you look away
* Falling through the floor teleports you to above the ceiling
* Walking on the ceiling removes the blocks below you (which reappear when you look away)
* HE COMES (uses Jitse's NPCLib)

This plugin also affects the overworld. Walking backwards into a wall atleast 3x3 and 2 blocks deep causes the backrooms to be cloned to the overworld, teleporting you to the full backrooms a few moments after entering the room.

When the plugin boots, you will need to run `brsetup` from the console. This does the following to setup the backrooms:
* Deletes the world_nether folder
* Deletes DIM-1 in the world folder (so Bukkit doesn't believe that it's a singleplayer-format world
* Writes `TheBackrooms` to `worlds.world_nether.generator` in `bukkit.yml`