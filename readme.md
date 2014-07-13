Provo
=====

Provo is a plugin with a bunch of features for Bukkit I found were missing from other plugins so I compiled them myself.

Some examples of planned features for now:

* Inventory sorting
* Sign editing
* Additional redstone blocks
* Mailing
* Distance measuring and planning tools
* /math
* Notepad

And many more!

I've created a [public Trello board](https://trello.com/b/Nt3fcCmn/provo) for organization purposes.

Right now the only person working on the project is me.

Stay tuned for the 1.0 release!

Compilation
-----------

This is the first project i've used with maven.  Christ.

Configuration
-------------

Stay tuned.


About the code
=====

Inventory Sorting
-----------------

Sorting is inspired by the InventoryTweaks mod and the late ConvenientInventory mod, both client side mods that function brilliantly in singleplayer but not too well in multiplayer due to lag issues.  I'll try to mimick these well as they're great functions.

Sorting is done via the `/sort` command.  Inventories of players, single chests, and double chests can be sorted.  Code is centralized in the `com.jakeconley.provo.functions.sorting` package. Sorting is a flexible and powerful feature that is highly configurable:

Each player has their own set of preferences stored in their YAML file in `plugins/Provo/sorting/player_preferences/<UUID>.yml`.  Their preferences can contain a bunch of "classes" (`PreferencesClass`) which is basically a named set of "rules" (`PreferencesRule`) to sort the inventory by.  Each `PreferencesRule` consists of two parts, a target area in the inventory and a list of items that can go in that area.

Each rule has its own priority, items with a higher priority setting will be sorted last and therefore are less likely to be overwritten in case of an overlap of the target area of the rules in the class.

The sorting engine goes through the inventory once for each rule, starting at the rule with the lowest priority setting, finds which blocks match the rules and moves them accordingly.

The idea is that one can do `/sort <classname>` and click on the chest (or not if the player wants to sort their own inventory) and then have it sorted accordingly.  If no `<classname>` is provided, it just groups similar items together.

Also, `/sort` should automatically put armor in its respective slots.

(How will it account for all inventory space?  Cache beforehand or do a series of swaps or what)

Meta scripts
------------

Meta code generation scripts are found in the meta_scripts directory, open them for more info.

They're not too special, not used in the plugin itself, just that since java lacks a preprocessor I drew them up to generate code I was otherwise too lazy to.

Figured I might as well include them in the repo.
