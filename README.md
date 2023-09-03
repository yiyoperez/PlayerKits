# PlayerKits

Ajneb's plugin page
at [SpigotMC](https://www.spigotmc.org/resources/playerkits-fully-configurable-kits-1-8-1-17.75185/),
and [original source](https://github.com/Ajneb97/PlayerKits).

---

## Fork

My intention with this fork is NOT to maintain the plugin, it's just a version that I could use in my own testing server
environment.

I could probably accept some feature & pull requests, I'm also looking to fix errors so issues section is available.

---

### **DISCLAIMER**

**ℹ THIS PROJECT IS IN BEING RECODED, FEATURES, FUNCTIONS, PREVIOUS CONFIG MAY NOT WORK!**
<br>
**⚠ USE AT YOUR OWN WILL ⚠**
<br>

**ℹ THIS PROJECT IS IN BEING RECODED, FEATURES, FUNCTIONS, PREVIOUS CONFIG MAY NOT WORK!**
<br>
**⚠ USE AT YOUR OWN WILL ⚠**
<br>

---

![](assets/playerkits.png)

### What can I do with PlayerKits?

This plugin allows you to create a variety of kits and put them on a GUI Inventory.
<br>
Using <span style="color:red">/kit</span> players will be able to claim these kits if they have the correct permission.
<br>
Kits can also have a cooldown, so users can't claim them everytime.

What makes **<span style="color:yellow">Player</span><span style="color:light_gray">Kits</span>** special is its easy
way to create kits, just having the items on your inventory and executing a
command.
<br>
You can edit kits properties by using <span style="color:red">/kit edit</span> command.
<br>
Also, almost all item attributes will be stored in the kit, so they are given correctly.

<details>
<summary>Features</summary>

- ✓ GUI Inventory to claim Kits.
- ✓ Command to claim Kits.
- ✓ Configurable display item in GUI:​

  Item, name, lore.
  Different item when player doesn't have permission.
  Different item when player hasn't buyed the kit.
  Different lore when kit is in cooldown.

- ✓ Cooldown for kits.
- ✓ Permissions for kits.
- ✓ Price for kits.
- ✓ One Time Buy kits
- ✓ Execute commands when giving a kit.
- ✓ Customizable Kit Preview.
- ✓ Pages System.
- ✓ Edit Kits from Game.
- ✓ First join kit.
- ✓ One time use kit.
- ✓ 1.16 HEX Color support for kit items.
- ✓ Auto Armor Equip.
- ✓ Auto Offhand item Equip.
- ✓ Configurable GUI.
- ✓ MySQL support for player data.
- ✓ Kits will save Item Attributes.
    - Name, Lore.
    - %player% variable in name and lore.
    - PlaceholderAPI static variables in name and lore.
    - Enchantments.
    - Potion Effects.
    - Leather Armor Color.
    - Book Enchantments.
    - Written Books.
    - Fireworks Attributes.
    - Banner, Shields Attributes.
    - Skull Textures.
    - Item Flags.
    - Unbreakable Tag.
    - Attributes Modifiers.
    - NBT Tags.

- ✓ Works in 1.8+.
- ✓ Messages translation.

</details>

### How to Start

To create a new kit you just need to have some items on your inventory and then execute the command `/kit create <name>`.

To claim the kit you have two options.

1) Use the `/kit` command and click on the kit item inside the GUI.
2) Use the `/kit claim <name>` command.

> ℹ Or use `/kit <name>` if you have `claim_kit_short_command` option set to `true`


You can also edit the kit directly from Minecraft without touching the config, using the `/kit edit <kit>` command.

<img src="assets/editing.png" width="80%">

---

### More

WIKI is located **[HERE](https://github.com/yiyoperez/PlayerKits/wiki)**.  

  <details>
  <summary>Config options</summary>

  ```yaml
# This will define the time in seconds in which player kit data is saved automatically.
player_data_save_time: 300
# This will run command before giving items to player.
commands_before_items: false
# When true allow player to use /kit <name> to claim kits instead of /kit claim <name>
claim_kit_short_command: false
# Close inventory after claiming kit.
close_inventory_on_claim: false
# No idea if this is even working rn.
# Enabling this will fix some nbt issues especially with AdvancedEnchantments plugin. If you modify this option you must save your kits again.
nbt_alternative_data_save: false
# If true "purchase > lore" message will be added automatically to lore.
add_buy_lore_automatically: true
# Hides kits with permissions if player doesn't have them.
hide_kits_with_permissions: false
# Drop items to ground if inventory is full.
drop_items_if_full_inventory: false
# Close inventory when claiming kit if player doesn't have permissions.
close_inventory_no_permission: true
# If true "preview > lore" message will be added automatically to lore.
add_preview_lore_automatically: true
# If true players will need a permission to preview kit items.
preview_inventory_requires_permission: false

# Sounds Section, these are created when starting the server.
# Here you can define the sound name for specific events.
# Format: Sound;Volume;Pitch
# Please check the sounds for your version here https://docs.andre601.ch/Spigot-Sounds/sounds/
sounds: { }

# Preview Section
preview-inventory:
  # Enable preview inventory.
  enabled: true
  # Preview inventory size
  size: 54
  # Enable back item
  back-item: true
  # Back item slot
  back-item-slot: 45

# Database Section, Currently NOT supported.
mysql_database:
  enabled: false
  host: localhost
  port: 3306
  username: root
  password: root
  database: database

# Inventory Section.
inventory:
  # Inventory size
  size: 45
  # Inventory pages name. Format: page: "Title"
  # Here you need to define the title of the kits inventory per page.
  # If not set the plugin will NOT open the inventory page!
  pages_names:
    1: "&9Kits"
    2: "&bVIP Kits"
  # Default items will be created when starting the plugin.
  # Here you can modify the slot and properties of the next page and previous page items.
  # Just remember to leave the "type" intact.
  items: { }
  # You can modify the GUI Inventory as you like by adding items here. 
  # You can set command, skulldata, custom_model_data & PlaceholderAPI variables on the name and lore.
  # Example
  # '0':
  #   id: STONE
  #   name: "&8Stone"
  #   command: "tell %player% Hello!"
  #   custom_model_data: 69420
  #   lore: 
  #     - "&aYup this is an stone."

```

---

</details>
<details>
<summary>Kit options</summary>

```yaml
# These are options that you can add or modify in kits options.

# The position of the kit in the GUI Inventory.
# If you don't want to show the kit you can remove this option.
slot: 10

# The page of the inventory where the kit will show.
# If you want to show the item in the first page, you don't need to add this option.
page: 2

# Attributes of the item in the inventory. 

# Kit item.
# For Minecraft version use these link as references
# 1.8.8 https://helpch.at/docs/1.8.8/org/bukkit/Material.html
# 1.12.2 https://helpch.at/docs/1.12.2/org/bukkit/Material.html
# 1.13.2 https://helpch.at/docs/1.13.2/org/bukkit/Material.html
# 1.14.4 https://helpch.at/docs/1.14.4/org/bukkit/Material.html
# Latest (1.20.1) https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html
display_item: IRON_AXE
# Kit item display name.
# If you don't want to shown a display name leave it empty such as display_name: " "
display_name: "&6&lIron &aKit"
# Kit item display lore.
# You can remove this option if you don't want it.
display_lore:
  - '&eThis kit includes:'
  - '&8- &7x1 Iron Axe'
  - '&8- &7x1 Iron Pickaxe'
  - '&8- &7x1 Iron Sword'
  - ''
  - '&7Cooldown: &c3 hours'
  - ''
  - '&aClick to claim!'

# If is set to true, the display item will be enchanted.
display_item_glowing: true

# Cooldown of the kit in seconds.
# How much time the player will have to wait to claim this kit again.
cooldown: 10800

# You can add a price for the kit.
# For this option to work you need Vault and an Economy plugin suchs as Essentials, XConomy or any that hooks with vault. 
# Vault: https://dev.bukkit.org/projects/vault
# Essentials: https://essentialsx.net/downloads.html
# XConomy: https://www.spigotmc.org/resources/75669/
price: 5000

# If is set to true, players will only be able to buy this kit ONCE!
one_time_buy: true

# If this option is set to true, players will receive this kit when joining for the first time.
first_join: true

# If this option is set to true, players can claim this kit just one time.
one_time: true

# You can add a permission for the kit.
# You can remove this option if you don't want it.
permission: playerkits.kit.diamond

# Besides giving items in kits, you can execute commands from the console, remember to use %player% variable.
# You can remove this option if you don't want it.
Commands:
  - "bc &6%player% &ejust claimed a &aDIAMOND KIT&e!"

# If the player doesn't have the correct permission, this item will show in the inventory instead of the original one.
noPermissionsItem:
  display_item: BARRIER
  display_name: "&6&lDiamond &aKit"
  display_lore:
    - "&cYou don't have permissions to claim"
    - "&cthis kit."
    - ""
    - "&7You need: &bVIP&6+ &7rank."

# If the player hasn't buyed a kit with one_time_buy option, this item will show in the inventory instead of the original one.
noBuyItem:
  display_item: BARRIER
  display_name: "&6&lIron &aKit"
  display_lore:
    - '&eThis kit includes:'
    - '&8- &7x1 Iron Axe'
    - '&8- &7x1 Iron Pickaxe'
    - '&8- &7x1 Iron Sword'
    - ''
    - '&7Price: &$5000'
    - ''
    - '&aClick to buy!'
```

---

</details>

<details>
<summary>Translations</summary>

| Language            	 | Translator                                                                	 | File (pastebin)          	 |
|-----------------------|-----------------------------------------------------------------------------|----------------------------|
| Spanish             	 | Me (Sliide_)                                                             	  | [Click here]() 	           |
| Russian             	 | [@snr93]( https://www.spigotmc.org/members/snr93.130652/)                	  | [Click here]() 	           |
| Vietnamese          	 | [@ImCursedKiwi]( https://www.spigotmc.org/members/imcursedkiwi.1060814/) 	  | [Click here]() 	           |
| Simplified Chinese  	 | [@Lijinhong]( https://www.spigotmc.org/members/lijinhong.1218190/)       	  | [Click here]() 	           |
| Traditional Chinese 	 | [@Lijinhong]( https://www.spigotmc.org/members/lijinhong.1218190/)       	  | [Click here]() 	           |
| Rumanian            	 | [@Iepurooy]( https://www.spigotmc.org/members/iepurooy.1389071/)         	  | [Click here]() 	           |
| Polish              	 | [@Tomcio0203x]( https://www.spigotmc.org/members/tomcio0203x.1361713/)   	  | [Click here]() 	           |

---

</details>

<details>
<summary>Placeholders</summary>


**Local** placeholders.

> Time placeholders, they can be used in cooldown related messages.

| Placeholder 	    | Description 	                                             |
|------------------|-----------------------------------------------------------|
| %timer%    	     | Returns remaining cooldown as timer.             	        |
| %seconds%        | Returns remaining cooldown as seconds                     |
| %plainseconds%   | Returns remaining cooldown as plain seconds.              |
| %roundedseconds% | Returns remaining cooldown as rounded seconds.            |
| %timeleft%       | Returns remaining cooldown as timer then rounded seconds. |

---

**PlaceholderAPI** placeholders.

> ⚠ PlaceholderAPI is required to use them these variables.
>
> The plugin has the following placeholder format `%playerkits_<identifier>_<kit>%`

| Identifier 	  | Description 	                                                                                                                                                 |
|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| cooldown    	 | Returns kit cooldown string in plain seconds.             	                                                                                                   |
| waiting       | Returns yes or no if player is waiting/in cooldown for that kit.	<br/>Defined at PlaceholderAPI config.<br>Path (plugins/PlaceholderAPI/config.yml > boolean) |

---

</details>

<details>
<summary>Command and permissions</summary>

| Command 	             | Permission 	       | Description 	                                     |
|-----------------------|--------------------|---------------------------------------------------|
| /kit                  | none               | Open the Kits GUI.                                |
| /kit kit              | none               | Claim a kit. (if claim_kit_short_command enabled) |
| /kit open player      | playerkits.open    | Open the Kits GUI to a player.                    |
| /kit create kit       | playerkits.create  | Creates a new kit.                                |
| /kit delete kit       | playerkits.delete  | Removed a created kit.                            |
| /kit list             | playerkits.list    | Show all available kits.                          |
| /kit claim kit        | none               | Claim a kit.                                      |
| /kit preview kit      | playerkits.preview | Previews a kit.                                   |
| /kit edit kit         | playerkits.edit    | Open kit editor GUI.                              |
| /kit give kit player  | playerkits.give    | Gives a kit to a player.                          |
| /kit reset kit player | playerkits.reset   | Resets kit data to a player.                      |
| /kit reload           | playerkits.reload  | Reload the config.                                |
|                       |                    |                                                   |

> ℹ Command alias: kits

---

</details>

<details>
<summary>Videos</summary>

English by [@XDRGAMING_S4](https://www.spigotmc.org/members/xdrgaming_s4.992898/) **OUTDATED**

[<img src="https://i.ytimg.com/vi/dIrKREG8uy4/maxresdefault.jpg" width="40%">](https://www.youtube.com/watch?v=dIrKREG8uy4)

Spanish by [Ajneb97](https://www.spigotmc.org/resources/authors/ajneb97.43796/) **OUTDATED**

[<img src="https://i.ytimg.com/vi/7qt0swW0IF8/maxresdefault.jpg" width="40%">](https://www.youtube.com/watch?v=7qt0swW0IF8)

</details>