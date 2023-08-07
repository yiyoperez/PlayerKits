package pk.ajneb97.inventory;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.manager.KitManager;
import pk.ajneb97.model.KitModification;
import pk.ajneb97.util.TimeUtils;
import pk.ajneb97.util.Utils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
public class InventoryEdit {


    @SuppressWarnings("deprecation")
    public static void crearInventario(Player jugador, String kit, PlayerKits plugin) {
        FileConfiguration kits = plugin.getKits();
        Inventory inv = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', "&9Editing Kit"));

        ItemStack item = new ItemStack(Material.DROPPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lSlot"));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to define the position of the display"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7item of this kit in the Inventory."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        String slot = "none";
        if (kits.contains("Kits." + kit + ".slot")) {
            slot = kits.getString("Kits." + kit + ".slot");
        }
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Slot: &a" + slot));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(10, item);

        item = new ItemStack(Material.GHAST_TEAR);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lCooldown"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to define the cooldown of"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7the kit."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        int cooldown = 0;
        if (kits.contains("Kits." + kit + ".cooldown")) {
            cooldown = kits.getInt("Kits." + kit + ".cooldown");
        }
        //TODO CHECK THIS
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Cooldown: &a" + TimeUtils.millisToSeconds(cooldown * 1000L) + "(s)"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Cooldown: &a" + TimeUtils.millisToSeconds(System.currentTimeMillis() + cooldown * 1000L) + "(s)"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(11, item);

        item = new ItemStack(Material.REDSTONE_BLOCK);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lPermission"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to define the permission of"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7the kit."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        String permission = "none";
        if (kits.contains("Kits." + kit + ".permission")) {
            permission = kits.getString("Kits." + kit + ".permission");
        }
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Permission: &a" + permission));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(12, item);

        String firstJoin = "false";
        if (kits.contains("Kits." + kit + ".first_join")) {
            firstJoin = kits.getString("Kits." + kit + ".first_join");
        }
        if (firstJoin.equals("true")) {
            if (!Utils.isLegacy()) {
                item = new ItemStack(Material.LIME_DYE);
            } else {
                item = new ItemStack(Material.valueOf("INK_SACK"), 1, (short) 10);
            }
        } else {
            if (!Utils.isLegacy()) {
                item = new ItemStack(Material.GRAY_DYE);
            } else {
                item = new ItemStack(Material.valueOf("INK_SACK"), 1, (short) 8);
            }
        }
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lFirst Join"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to define if players should"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7receive this kit when they join for"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7the first time."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Status: &a" + firstJoin));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(19, item);


        String oneTime = "false";
        if (kits.contains("Kits." + kit + ".one_time")) {
            oneTime = kits.getString("Kits." + kit + ".one_time");
        }
        if (oneTime.equals("true")) {
            if (!Utils.isLegacy()) {
                item = new ItemStack(Material.LIME_DYE);
            } else {
                item = new ItemStack(Material.valueOf("INK_SACK"), 1, (short) 10);
            }
        } else {
            if (!Utils.isLegacy()) {
                item = new ItemStack(Material.GRAY_DYE);
            } else {
                item = new ItemStack(Material.valueOf("INK_SACK"), 1, (short) 8);
            }
        }
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lOne Time"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to define if players should"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7claim this kit just one time."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Status: &a" + oneTime));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(20, item);

        String autoArmor = "false";
        if (kits.contains("Kits." + kit + ".auto_armor")) {
            autoArmor = kits.getString("Kits." + kit + ".auto_armor");
        }
        if (autoArmor.equals("true")) {
            if (!Utils.isLegacy()) {
                item = new ItemStack(Material.LIME_DYE);
            } else {
                item = new ItemStack(Material.valueOf("INK_SACK"), 1, (short) 10);
            }
        } else {
            if (!Utils.isLegacy()) {
                item = new ItemStack(Material.GRAY_DYE);
            } else {
                item = new ItemStack(Material.valueOf("INK_SACK"), 1, (short) 8);
            }
        }
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lAuto Armor"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to set if kit armor should"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7be equipped automatically when"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7claiming the kit."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Status: &a" + autoArmor));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(28, item);

        item = new ItemStack(Material.BEACON);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lNo Buy Item"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to edit the kit display item when"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7player has not buyed it."));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(41, item);

        item = new ItemStack(Material.BARRIER);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lNo Permission Item"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to edit the kit display item when"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7player doesn't have permissions to claim"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7the kit."));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(40, item);

        item = new ItemStack(Material.IRON_SWORD);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lDisplay Item"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to edit the kit display item."));
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        inv.setItem(39, item);

        List<String> items = new ArrayList<>();
        FileConfiguration config = plugin.getConfig();
        if (kits.contains("Kits." + kit + ".Items")) {
            for (String n : kits.getConfigurationSection("Kits." + kit + ".Items").getKeys(false)) {
                String path = "Kits." + kit + ".Items." + n;
                ItemStack itemN = KitManager.getItem(kits, path, jugador);
                items.add("x" + itemN.getAmount() + " " + itemN.getType());
            }
        }

        item = new ItemStack(Material.DIAMOND);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lKit Items"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to edit the kit items."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Items:"));
        for (String s : items) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8- &a") + s);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(14, item);

        List<String> comandos = new ArrayList<>();
        if (kits.contains("Kits." + kit + ".Commands")) {
            comandos = kits.getStringList("Kits." + kit + ".Commands");
        }
        item = new ItemStack(Material.IRON_INGOT);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lKit Commands"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to edit which commands should"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7the kit execute to the player when"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7receiving it."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Commands:"));
        if (comandos.isEmpty()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&cNONE"));
        } else {
            for (String comando : comandos) {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&8- &a") + comando);
            }
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(15, item);

        item = new ItemStack(Material.PAPER);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lPrice"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to define the price of"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7the kit."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        String price = "none";
        if (kits.contains("Kits." + kit + ".price")) {
            price = kits.getString("Kits." + kit + ".price");
        }
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Price: &a" + price));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(23, item);

        item = new ItemStack(Material.ENDER_PEARL);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lPage"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to define the page of the of"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7this kit in the Inventory."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        String page = "1";
        if (kits.contains("Kits." + kit + ".page")) {
            page = kits.getString("Kits." + kit + ".page");
        }
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Page: &a" + page));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(16, item);

        String oneTimeBuy = "false";
        if (kits.contains("Kits." + kit + ".one_time_buy")) {
            oneTimeBuy = kits.getString("Kits." + kit + ".one_time_buy");
        }
        if (oneTimeBuy.equals("true")) {
            if (!Utils.isLegacy()) {
                item = new ItemStack(Material.LIME_DYE);
            } else {
                item = new ItemStack(Material.valueOf("INK_SACK"), 1, (short) 10);
            }
        } else {
            if (!Utils.isLegacy()) {
                item = new ItemStack(Material.GRAY_DYE);
            } else {
                item = new ItemStack(Material.valueOf("INK_SACK"), 1, (short) 8);
            }
        }
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lOne Time Buy"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to set if the kit should be"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7buyed just one time. This option"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7requires a price for the kit."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Status: &a" + oneTimeBuy));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(21, item);

        jugador.openInventory(inv);

        plugin.setKitEditando(new KitModification(jugador, kit, ""));
    }

    @SuppressWarnings("deprecation")
    public static void crearInventarioItems(Player jugador, String kit, PlayerKits plugin) {
        FileConfiguration kits = plugin.getKits();
//		FileConfiguration config = plugin.getConfig();
//		int slots = Integer.valueOf(config.getString("Config.previewInventorySize"));
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', "&9Editing Kit Items"));

        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7Go Back"));
        item.setItemMeta(meta);
        inv.setItem(45, item);

        item = new ItemStack(Material.EMERALD);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aSave Items"));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7If you made any changes in this inventory"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7it is very important to click this item"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7before closing it or going back."));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(53, item);

        if (!Utils.isLegacy()) {
            item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        } else {
            item = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 8);
        }
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " "));
        item.setItemMeta(meta);
        for (int i = 46; i <= 52; i++) {
            inv.setItem(i, item);
        }

        if (!Bukkit.getVersion().contains("1.8")) {
            item = new ItemStack(Material.BOOK);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aInformation"));
            lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7If you want to set an item on the offhand"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7just right click it."));
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(49, item);
        }


        int slot = 0;
        if (kits.contains("Kits." + kit + ".Items")) {
            for (String n : kits.getConfigurationSection("Kits." + kit + ".Items").getKeys(false)) {
                String path = "Kits." + kit + ".Items." + n;
                item = KitManager.getItem(kits, path, jugador);

                List<String> loreOffhand = new ArrayList<>();
                ItemMeta metaNuevo = item.getItemMeta();
                if (!Bukkit.getVersion().contains("1.8") && kits.contains(path + ".offhand") && kits.getString(path + ".offhand").equals("true")) {
                    loreOffhand.add(ChatColor.translateAlternateColorCodes('&', " "));
                    loreOffhand.add(ChatColor.translateAlternateColorCodes('&', "&8[&cRight Click to remove from OFFHAND&8]"));
                }
                if (!loreOffhand.isEmpty()) {
                    if (metaNuevo.hasLore()) {
                        List<String> loreNuevo = meta.getLore();
                        loreNuevo.addAll(loreOffhand);
                        metaNuevo.setLore(loreNuevo);
                    } else {
                        metaNuevo.setLore(loreOffhand);
                    }
                    item.setItemMeta(metaNuevo);
                }

                if (kits.contains(path + ".preview_slot")) {
                    inv.setItem(kits.getInt(path + ".preview_slot"), item);
                } else {
                    inv.setItem(slot, item);
                    slot++;
                }
            }
        }


        jugador.openInventory(inv);

        plugin.setKitEditando(new KitModification(jugador, kit, ""));
    }

    @SuppressWarnings("deprecation")
    public static void crearInventarioDisplayItem(Player jugador, String kit, PlayerKits plugin, String tipoDisplay) {
        //El tipoDisplay puede ser: normal o nopermission
        FileConfiguration kits = plugin.getKits();
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&9Editing Display Item"));

        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7Go Back"));
        item.setItemMeta(meta);
        inv.setItem(18, item);

        if (!Utils.isLegacy()) {
            item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        } else {
            item = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 8);
        }
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " "));
        item.setItemMeta(meta);
        for (int i = 19; i <= 26; i++) {
            inv.setItem(i, item);
        }
        for (int i = 0; i <= 8; i++) {
            inv.setItem(i, item);
        }
        inv.setItem(9, item);
        inv.setItem(13, item);
        inv.setItem(17, item);

        if (!Utils.isLegacy()) {
            item = new ItemStack(Material.PLAYER_HEAD);
        } else {
            item = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7Place item here &6>>"));
        item.setItemMeta(meta);
        item = Utils.setSkull(item, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19");
        inv.setItem(10, item);

        if (!Utils.isLegacy()) {
            item = new ItemStack(Material.PLAYER_HEAD);
        } else {
            item = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6<< &7Place item here"));
        item.setItemMeta(meta);
        item = Utils.setSkull(item, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==");
        inv.setItem(12, item);

        String name = "none";
        if (tipoDisplay.equals("normal")) {
            if (kits.contains("Kits." + kit + ".display_name")) {
                name = kits.getString("Kits." + kit + ".display_name");
            }
        } else {
            if (kits.contains("Kits." + kit + "." + tipoDisplay + ".display_name")) {
                name = kits.getString("Kits." + kit + "." + tipoDisplay + ".display_name");
            } else {
                if (kits.contains("Kits." + kit + ".display_name")) {
                    name = kits.getString("Kits." + kit + ".display_name");
                }
            }
        }
        item = new ItemStack(Material.NAME_TAG);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lDisplay Name"));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to define the display item name."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Name: &a" + name));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(14, item);

        List<String> displayLore = new ArrayList<>();
        if (tipoDisplay.equals("normal")) {
            if (kits.contains("Kits." + kit + ".display_lore")) {
                displayLore = kits.getStringList("Kits." + kit + ".display_lore");
            }
        } else {
            if (kits.contains("Kits." + kit + "." + tipoDisplay + ".display_lore")) {
                displayLore = kits.getStringList("Kits." + kit + "." + tipoDisplay + ".display_lore");
            } else {
                if (kits.contains("Kits." + kit + ".display_lore")) {
                    displayLore = kits.getStringList("Kits." + kit + ".display_lore");
                }
            }
        }
        item = new ItemStack(Material.PAPER);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lDisplay Lore"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to define the display item lore."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Lore:"));
        if (displayLore.isEmpty()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&cNONE"));
        } else {
            for (String s : displayLore) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(15, item);

        String glowing = "false";
        if (tipoDisplay.equals("normal")) {
            if (kits.contains("Kits." + kit + ".display_item_glowing")) {
                glowing = kits.getString("Kits." + kit + ".display_item_glowing");
            }
        } else {
            if (kits.contains("Kits." + kit + "." + tipoDisplay + ".display_item_glowing")) {
                glowing = kits.getString("Kits." + kit + "." + tipoDisplay + ".display_item_glowing");
            }
        }
        if (glowing.equals("true")) {
            if (!Utils.isLegacy()) {
                item = new ItemStack(Material.LIME_DYE);
            } else {
                item = new ItemStack(Material.valueOf("INK_SACK"), 1, (short) 10);
            }
        } else {
            if (!Utils.isLegacy()) {
                item = new ItemStack(Material.GRAY_DYE);
            } else {
                item = new ItemStack(Material.valueOf("INK_SACK"), 1, (short) 8);
            }
        }
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eSet &6&lDisplay Item Glowing"));
        lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to define if the display item"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7should display an enchantment."));
        lore.add(ChatColor.translateAlternateColorCodes('&', ""));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current Status: &a" + glowing));
        meta.setLore(lore);
        if (glowing.equals("true")) {
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        inv.setItem(16, item);

        ItemStack displayItem = null;
        if (tipoDisplay.equals("normal")) {
            if (kits.contains("Kits." + kit + ".display_item")) {
                displayItem = Utils.getDisplayItem(kits, "Kits." + kit);
            }
        } else {
            if (kits.contains("Kits." + kit + "." + tipoDisplay + ".display_item")) {
                displayItem = Utils.getDisplayItem(kits, "Kits." + kit + "." + tipoDisplay);
            }
        }

        if (displayItem != null) {
            meta = displayItem.getItemMeta();
            meta.setDisplayName(null);
            meta.setLore(null);
            displayItem.setItemMeta(meta);
            inv.setItem(11, displayItem);
        }

        jugador.openInventory(inv);

        plugin.setKitEditando(new KitModification(jugador, kit, tipoDisplay));
    }

    @SuppressWarnings("deprecation")
    public static void crearInventarioDisplayItemLore(Player jugador, String kit, PlayerKits plugin, String tipoDisplay) {
        FileConfiguration kits = plugin.getKits();
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', "&9Editing Display Item Lore"));

        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7Go Back"));
        item.setItemMeta(meta);
        inv.setItem(45, item);

        item = new ItemStack(Material.EMERALD_BLOCK);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aAdd new Lore Line"));
        item.setItemMeta(meta);
        inv.setItem(53, item);

        if (!Utils.isLegacy()) {
            item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        } else {
            item = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 8);
        }
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " "));
        item.setItemMeta(meta);
        for (int i = 46; i <= 52; i++) {
            inv.setItem(i, item);
        }

        List<String> lore = new ArrayList<>();
        if (tipoDisplay.equals("normal")) {
            if (kits.contains("Kits." + kit + ".display_lore")) {
                lore = kits.getStringList("Kits." + kit + ".display_lore");
            }
        } else {
            if (kits.contains("Kits." + kit + "." + tipoDisplay + ".display_lore")) {
                lore = kits.getStringList("Kits." + kit + "." + tipoDisplay + ".display_lore");
            }
        }
        for (int i = 0; i < lore.size(); i++) {
            item = new ItemStack(Material.PAPER, 1);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9Line &e#" + (i + 1)));
            List<String> lore2 = new ArrayList<>();
            lore2.add(ChatColor.translateAlternateColorCodes('&', "&7") + lore.get(i));
            lore2.add(ChatColor.translateAlternateColorCodes('&', ""));
            lore2.add(ChatColor.translateAlternateColorCodes('&', "&8[&cRight Click to remove&8]"));
            meta.setLore(lore2);
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }

        jugador.openInventory(inv);

        plugin.setKitEditando(new KitModification(jugador, kit, tipoDisplay));
    }

    @SuppressWarnings("deprecation")
    public static void crearInventarioComandos(Player jugador, String kit, PlayerKits plugin) {
        FileConfiguration kits = plugin.getKits();
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', "&9Editing Kit Commands"));

        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7Go Back"));
        item.setItemMeta(meta);
        inv.setItem(45, item);

        item = new ItemStack(Material.EMERALD_BLOCK);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aAdd new Command"));
        item.setItemMeta(meta);
        inv.setItem(53, item);

        if (!Utils.isLegacy()) {
            item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        } else {
            item = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 8);
        }
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " "));
        item.setItemMeta(meta);
        for (int i = 46; i <= 52; i++) {
            inv.setItem(i, item);
        }

        List<String> comandos = new ArrayList<>();
        if (kits.contains("Kits." + kit + ".Commands")) {
            comandos = kits.getStringList("Kits." + kit + ".Commands");
        }
        for (int i = 0; i < comandos.size(); i++) {
            item = new ItemStack(Material.PAPER, 1);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9Command &e#" + (i + 1)));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7") + comandos.get(i));
            lore.add(ChatColor.translateAlternateColorCodes('&', ""));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8[&cRight Click to remove&8]"));
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }

        jugador.openInventory(inv);

        plugin.setKitEditando(new KitModification(jugador, kit, ""));
    }

}
