Provo (1.1!)
=====

Provo is a plugin with a bunch of features for Bukkit I found were missing from other plugins so I compiled them myself.

Some examples of the features integrated now:

* Inventory sorting
* Mailing
* Reminders
* Measurement
* /math
* /recipe
* /unenchant

For the future:

* Sign editing
* Additional redstone blocks
* Admin chat
* Unenchantment
* Notepad
* Inventory storage via a single command
* Planning tools

And many more!

I've created a [public Trello board](https://trello.com/b/Nt3fcCmn/provo) for organization purposes.

Right now the only person working on the project is me.

Stay tuned for the 1.0 release!

Compilation
-----------

This is the first project i've used with maven.  Christ.

Configuration & Permissions
-------------

Right now for project maintenance purposes I'm keeping this information in [the permissions card](https://trello.com/c/VmGCUTRD) and [the configurations card](https://trello.com/c/m8IoStvd) respectively.


About the code
=====

Inventory Sorting
-----------------

Sorting is inspired by the InventoryTweaks mod and the late ConvenientInventory mod, both client side mods that function brilliantly in singleplayer but not too well in multiplayer due to lag issues.  I'll try to mimick these well as they're great functions.

Sorting is done via the `/sort` command.  Inventories of players, single chests, and double chests can be sorted.  Code is centralized in the `com.jakeconley.provo.functions.sorting` package. Sorting is a flexible and powerful feature that is highly configurable:

The sorting engine works by being provided a class and then going through rule-by-rule and matching items to the assigned slots.  See [the sorting information card](https://trello.com/c/DlvMIFIN) for more information.

Meta scripts
------------

Meta code generation scripts are found in the meta_scripts directory, open them for more info.

They're not too special, not used in the plugin itself, just that since java lacks a preprocessor I drew them up to generate code I was otherwise too lazy to.

Figured I might as well include them in the repo.
