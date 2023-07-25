package pk.ajneb97.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitScheduler;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.inventory.PlayerInventory;
import pk.ajneb97.utils.MessageUtils;
import pk.ajneb97.utils.Utils;

import java.util.List;

public class InventarioManager {

    private int taskID;
    private final PlayerKits plugin;

    public InventarioManager(PlayerKits plugin) {
        this.plugin = plugin;
    }

    public int getTaskID() {
        return this.taskID;
    }

    public void actualizarInventario(final Player player, final int pagina) {
        BukkitScheduler sh = Bukkit.getServer().getScheduler();
        taskID = sh.scheduleSyncRepeatingTask(plugin, () -> {
            if (!update(player, pagina)) {
                Bukkit.getScheduler().cancelTask(taskID);
            }
        }, 0L, 20L);
    }

    protected boolean update(Player jugador, int pagina) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();
        FileConfiguration configKits = plugin.getKits();

        String pathInventory = MessageUtils.getMensajeColor(getInventoryPageName(config, pagina));
        String pathInventoryM = ChatColor.stripColor(pathInventory);
        Inventory inv = jugador.getOpenInventory().getTopInventory();
        int paginasTotales = getCurrentPages(configKits);
        if (inv == null || !ChatColor.stripColor(jugador.getOpenInventory().getTitle()).equals(pathInventoryM)) {
            return false;
        }

        if (config.contains("Inventory")) {
            for (String key : config.getConfigurationSection("Inventory").getKeys(false)) {
                int slot = Integer.parseInt(key);

                ItemStack item = Utils.getItem(config.getString("Inventory." + key + ".id"), 1, "");
                ItemMeta meta = item.getItemMeta();
                if (config.contains("Inventory." + key + ".name")) {
                    String name = config.getString("Inventory." + key + ".name");
                    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                        name = PlaceholderAPI.setPlaceholders(jugador, name);
                    }
                    meta.setDisplayName(MessageUtils.getMensajeColor(name));
                }
                if (config.contains("Inventory." + key + ".lore")) {
                    List<String> lore = config.getStringList("Inventory." + key + ".lore");
                    for (int i = 0; i < lore.size(); i++) {
                        String linea = lore.get(i);
                        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                            linea = PlaceholderAPI.setPlaceholders(jugador, linea);
                        }
                        lore.set(i, MessageUtils.getMensajeColor(linea));
                    }
                    meta.setLore(lore);
                }
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
                if (Utils.isNew()) {
                    if (config.contains("Inventory." + key + ".custom_model_data")) {
                        int customModelData = config.getInt("Inventory." + key + ".custom_model_data");
                        meta.setCustomModelData(customModelData);
                    }
                }
                item.setItemMeta(meta);

                if (config.contains("Inventory." + key + ".skulldata")) {
                    String[] skulldata = config.getString("Inventory." + key + ".skulldata").split(";");
                    item = Utils.setSkull(item, skulldata[0], skulldata[1]);
                }

                if (config.contains("Inventory." + key + ".type")) {
                    if (config.getString("Inventory." + key + ".type").equals("previous_page")) {
                        if (pagina == 1) {
                            continue;
                        }
                    } else if (config.getString("Inventory." + key + ".type").equals("next_page")) {
                        if (paginasTotales <= pagina) {
                            continue;
                        }
                    }
                }
                inv.setItem(slot, item);
            }
        }

        JugadorManager manager = plugin.getJugadorManager();
        if (configKits.contains("Kits")) {
            for (String key : configKits.getConfigurationSection("Kits").getKeys(false)) {
                if (configKits.contains("Kits." + key + ".slot")) {
                    int slot = configKits.getInt("Kits." + key + ".slot");
                    int page = 1;
                    if (configKits.contains("Kits." + key + ".page")) {
                        page = configKits.getInt("Kits." + key + ".page");
                    }
                    if (page == pagina) {
                        if (configKits.contains("Kits." + key + ".permission") && !jugador.hasPermission(configKits.getString("Kits." + key + ".permission"))) {
                            if (config.getBoolean("hide_kits_with_permissions")) {
                                continue;
                            }
                        }
                        if (configKits.contains("Kits." + key + ".permission") && !jugador.hasPermission(configKits.getString("Kits." + key + ".permission"))
                                && configKits.contains("Kits." + key + ".noPermissionsItem")) {
                            ItemStack item = crearItemBase("Kits." + key + ".noPermissionsItem", key, configKits);
                            inv.setItem(slot, item);
                        } else if (configKits.contains("Kits." + key + ".one_time_buy") && configKits.getString("Kits." + key + ".one_time_buy").equals("true") && !manager.isBuyed(jugador, key)
                                && configKits.contains("Kits." + key + ".noBuyItem")) {
                            ItemStack item = crearItemBase("Kits." + key + ".noBuyItem", key, configKits);
                            inv.setItem(slot, item);
                        } else {
                            if (configKits.contains("Kits." + key + ".display_item")) {
                                ItemStack item = crearItemBase("Kits." + key, key, configKits);
                                ItemMeta meta = item.getItemMeta();
                                if (configKits.contains("Kits." + key + ".one_time") && configKits.getString("Kits." + key + ".one_time").equals("true")
                                        && manager.isOneTime(jugador, key)) {
                                    List<String> lore = messages.getStringList("kitOneTimeLore");
                                    lore.replaceAll(MessageUtils::getMensajeColor);
                                    meta.setLore(lore);
                                } else {
                                    if (configKits.contains("Kits." + key + ".cooldown")) {
                                        String cooldown = Utils.getCooldown(key, jugador, configKits, config, manager);
                                        if (!cooldown.equals("ready")) {
                                            List<String> lore = messages.getStringList("kitInCooldownLore");
                                            lore.replaceAll(s -> MessageUtils.getMensajeColor(s.replace("%time%", cooldown)));
                                            meta.setLore(lore);
                                        }
                                    }
                                }
                                item.setItemMeta(meta);

                                if (configKits.contains("Kits." + key + ".display_item_leathercolor")) {
                                    LeatherArmorMeta meta2 = (LeatherArmorMeta) meta;
                                    int color = configKits.getInt("Kits." + key + ".display_item_leathercolor");
                                    meta2.setColor(Color.fromRGB(color));
                                    item.setItemMeta(meta2);
                                }

                                inv.setItem(slot, item);
                            }

                        }
                    }

                }
            }
        }
        return true;
    }

    public static int getCurrentPages(FileConfiguration kitsConfig) {
        //Deberia retornar la pagina maxima desde el archivo de kits
        int paginaMaxima = 1;
        if (kitsConfig.contains("Kits")) {
            for (String key : kitsConfig.getConfigurationSection("Kits").getKeys(false)) {
                if (kitsConfig.contains("Kits." + key + ".page")) {
                    int paginaActual = kitsConfig.getInt("Kits." + key + ".page");
                    if (paginaActual > paginaMaxima) {
                        paginaMaxima = paginaActual;
                    }
                }
            }
        }
        return paginaMaxima;
    }

    public static void openMainInventory(FileConfiguration config, PlayerKits plugin, Player jugador, int pagina) {
        int size = config.getInt("inventory.size");
        Inventory inv = Bukkit.createInventory(null, size, MessageUtils.getMensajeColor(getInventoryPageName(config, pagina)));
        jugador.openInventory(inv);
        InventarioManager invM = new InventarioManager(plugin);
        plugin.agregarInventarioJugador(new PlayerInventory(jugador, pagina, invM, "main"));

        invM.actualizarInventario(jugador, pagina);
    }

    public ItemStack crearItemBase(String path, String kit, FileConfiguration configKits) {
        //paths:
        // Kits.kit.noPermissionsItem
        // Kits.kit
        // Kits.kit.noBuyItem
        ItemStack item = Utils.getItem(configKits.getString(path + ".display_item"), 1, "");
        ItemMeta meta = item.getItemMeta();
        if (configKits.contains(path + ".display_name")) {
            meta.setDisplayName(MessageUtils.getMensajeColor(configKits.getString(path + ".display_name")));
        } else {
            if (configKits.contains("Kits." + kit + ".display_name")) {
                meta.setDisplayName(MessageUtils.getMensajeColor(configKits.getString("Kits." + kit + ".display_name")));
            }

        }
        if (configKits.contains(path + ".display_lore")) {
            List<String> lore = configKits.getStringList(path + ".display_lore");
            lore.replaceAll(MessageUtils::getMensajeColor);
            meta.setLore(lore);
        } else {
            if (configKits.contains("Kits." + kit + ".display_lore")) {
                List<String> lore = configKits.getStringList("Kits." + kit + ".display_lore");
                lore.replaceAll(MessageUtils::getMensajeColor);
                meta.setLore(lore);
            }

        }
        if (configKits.contains(path + ".display_item_glowing") && configKits.getString(path + ".display_item_glowing").equals("true")) {
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        if (configKits.contains(path + ".display_item_custom_model_data")) {
            int customModelData = configKits.getInt(path + ".display_item_custom_model_data");
            meta.setCustomModelData(customModelData);
        }
        item.setItemMeta(meta);
        if (configKits.contains(path + ".display_item_skulldata")) {
            String[] skulldata = configKits.getString(path + ".display_item_skulldata").split(";");
            item = Utils.setSkull(item, skulldata[0], skulldata[1]);
        }

//		else {
//			if(configKits.contains("Kits."+kit+".display_item_skulldata")) {
//				String[] skulldata = configKits.getString("Kits."+kit+".display_item_skulldata").split(";");
//				item = Utilidades.setSkull(item, skulldata[0], skulldata[1]);
//			}
//		}
        return item;
    }

    public static String getInventoryPageName(FileConfiguration config, int page) {
        String defaultPage = config.getString("inventory_pages_names.1");
        for (String key : config.getConfigurationSection("inventory_pages_names").getKeys(false)) {
            if (key.equals(String.valueOf(page))) {
                return config.getString("inventory_pages_names." + key);
            }
        }
        return defaultPage;
    }
}
