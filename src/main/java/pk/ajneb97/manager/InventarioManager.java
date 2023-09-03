package pk.ajneb97.manager;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
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
import pk.ajneb97.model.CurrentPlayerInventory;
import pk.ajneb97.model.PlayerData;
import pk.ajneb97.model.PlayerKit;
import pk.ajneb97.util.Cooldown;
import pk.ajneb97.util.ItemStackBuilder;
import pk.ajneb97.util.MessageHandler;
import pk.ajneb97.util.MessageUtils;
import pk.ajneb97.util.Utils;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
public class InventarioManager {

    private final PlayerKits plugin;
    private int taskID;

    public InventarioManager(PlayerKits plugin) {
        this.plugin = plugin;
    }

    public static int getCurrentPages(FileConfiguration kitsConfig) {
        //Should return the max amount of pages from kits file.
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
        Inventory inv = Bukkit.createInventory(null, size, MessageUtils.translateColor(getInventoryPageName(config, pagina)));
        jugador.openInventory(inv);
        InventarioManager invM = new InventarioManager(plugin);
        plugin.agregarInventarioJugador(new CurrentPlayerInventory(jugador, pagina, invM, "main"));

        invM.updateInventory(jugador, pagina);
    }

    public static String getInventoryPageName(FileConfiguration config, int page) {
        String defaultPage = config.getString("inventory.pages_names.1");
        for (String key : config.getConfigurationSection("inventory.pages_names").getKeys(false)) {
            if (key.equals(String.valueOf(page))) {
                return config.getString("inventory.pages_names." + key);
            }
        }
        return defaultPage;
    }

    public int getTaskID() {
        return this.taskID;
    }

    // TODO: Globalize task.
    public void updateInventory(final Player player, final int pagina) {
        BukkitScheduler sh = Bukkit.getServer().getScheduler();
        taskID = sh.scheduleSyncRepeatingTask(plugin, () -> {
            if (!update(player, pagina)) {
                Bukkit.getScheduler().cancelTask(taskID);
            }
        }, 0L, 20L);
    }

    protected boolean update(Player player, int pagina) {
        FileConfiguration config = plugin.getConfig();

        String pathInventory = MessageUtils.translateColor(getInventoryPageName(config, pagina));
        String pathInventoryM = ChatColor.stripColor(pathInventory);

        Inventory inv = player.getOpenInventory().getTopInventory();
        if (inv == null || !ChatColor.stripColor(player.getOpenInventory().getTitle()).equals(pathInventoryM)) {
            return false;
        }

        updateMainItems(player, pagina, inv);
        updateKitsItems(player, pagina, inv);

        return true;
    }

    private void updateMainItems(Player player, int pagina, Inventory inv) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration configKits = plugin.getKits();
        MessageHandler messageHandler = plugin.getMessageHandler();

        ConfigurationSection invSection = config.getConfigurationSection("inventory.items");

        if (invSection.getKeys(false).isEmpty()) return;

        for (String key : invSection.getKeys(false)) {
            int slot = Integer.parseInt(key);

            ItemStack item = new ItemStackBuilder()
                    .from(Utils.getItem(invSection.getString(key + ".id")))
                    .amount(1)
                    .name(messageHandler.intercept(player, invSection.getString(key + ".name")))
                    .lore(invSection.getStringList(key + ".lore"))
                    .setCustomModelData(invSection.getInt(key + ".custom_model_data"))
                    .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

            if (invSection.contains(key + ".skulldata")) {
                String skulldata = invSection.getString(key + ".skulldata");
                item = Utils.setSkull(item, skulldata);
            }

            int paginasTotales = getCurrentPages(configKits);
            if (invSection.contains(key + ".type")) {
                if (invSection.getString(key + ".type").equals("previous_page")) {
                    if (pagina == 1) {
                        continue;
                    }
                } else if (invSection.getString(key + ".type").equals("next_page")) {
                    if (paginasTotales <= pagina) {
                        continue;
                    }
                }
            }
            inv.setItem(slot, item);
        }
    }

    private void updateKitsItems(Player player, int pagina, Inventory inv) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();
        FileConfiguration configKits = plugin.getKits();
        PlayerManager playerManager = plugin.getPlayerManager();

        ConfigurationSection kitsSection = configKits.getConfigurationSection("Kits");
        if (kitsSection.getKeys(false).isEmpty()) return;

        for (String key : kitsSection.getKeys(false)) {
            // Ignore if kit item doesn't have a valid slot.
            if (!configKits.contains("Kits." + key + ".slot")) continue;

            int slot = configKits.getInt("Kits." + key + ".slot");
            int page = 1;
            if (configKits.contains("Kits." + key + ".page")) {
                page = configKits.getInt("Kits." + key + ".page");
            }
            // Ignore if item doesn't belong in the page-
            if (page != pagina) continue;

            if (configKits.contains("Kits." + key + ".permission") && !player.hasPermission(configKits.getString("Kits." + key + ".permission"))) {
                if (config.getBoolean("hide_kits_with_permissions")) {
                    continue;
                }
            }
            PlayerData playerData = playerManager.getOrCreatePlayer(player);
            PlayerKit playerKit = playerData.getPlayerKit(key);

            if (configKits.contains("Kits." + key + ".permission") && !player.hasPermission(configKits.getString("Kits." + key + ".permission"))
                    && configKits.contains("Kits." + key + ".noPermissionsItem")) {
                ItemStack item = createBaseItem("Kits." + key + ".noPermissionsItem");

                inv.setItem(slot, item);
            } else if (configKits.contains("Kits." + key + ".one_time_buy") && configKits.getBoolean("Kits." + key + ".one_time_buy") && !playerKit.isBought()
                    && configKits.contains("Kits." + key + ".noBuyItem")) {
                //TODO: also show its own item when kit is one-time-buy instead of nobuy item.
                ItemStack item = createBaseItem("Kits." + key + ".noBuyItem");
                inv.setItem(slot, item);
            } else {
                if (configKits.contains("Kits." + key + ".display_item")) {
                    ItemStack item = createBaseItem("Kits." + key);
                    ItemMeta meta = item.getItemMeta();
                    if (configKits.contains("Kits." + key + ".one_time") && configKits.getBoolean("Kits." + key + ".one_time")
                            && playerKit.isOneTime()) {
                        List<String> lore = messages.getStringList("kitOneTimeLore");
                        lore.replaceAll(MessageUtils::translateColor);
                        meta.setLore(lore);
                    } else {
                        if (configKits.contains("Kits." + key + ".cooldown")) {
                            //TODO
                            if (playerData.hasCooldown(key)) {
                                List<String> lore = messages.getStringList("kitInCooldownLore");
                                Cooldown cooldown = playerData.getCooldown(key);
                                lore.replaceAll(s -> MessageUtils.translateColor(s.replace("%time%", cooldown.getTimeLeftRoundedSeconds())));
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

    public ItemStack createBaseItem(String path) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration configKits = plugin.getKits();
        MessageHandler messageHandler = plugin.getMessageHandler();

        ConfigurationSection section = configKits.getConfigurationSection(path);

        ItemStackBuilder builder = new ItemStackBuilder()
                .from(Utils.getItem(section.getString("display_item")))
                .name(section.getString("display_name"))
                .lore(section.getStringList("display_lore"));

        // TODO: Detect if kit can be buy or/and preview.
        if (config.getBoolean("add_buy_lore_automatically")) {
            builder.addLore(messageHandler.getRawStringList("purchase.lore"));
        }
        if (config.getBoolean("add_preview_lore_automatically")) {
            builder.addLore(messageHandler.getRawStringList("preview.lore"));
        }

        if (config.getBoolean("display_item_glowing")) {
            builder.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }
        builder.addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        if (section.contains("display_item_skulldata")) {
            String skullData = configKits.getString(path + ".display_item_skulldata");
            return Utils.setSkull(builder, skullData);
        }

        return builder;
    }
}
