package pk.ajneb97.managers;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.inventory.PlayerInventory;
import pk.ajneb97.utils.MessageUtils;

public class InventarioPreview implements Listener {

    private final PlayerKits plugin;

    private static FileConfiguration kits;
    private static FileConfiguration config;
    private static FileConfiguration messages;

    public InventarioPreview(PlayerKits plugin) {
        this.plugin = plugin;
        kits = plugin.getKits();
        config = plugin.getConfig();
        messages = plugin.getMessages();
    }

    public static void abrirInventarioPreview(PlayerKits plugin, Player jugador, String kit, int pagina) {
        int slots = config.getInt("preview-inventory.size");
        Inventory inv = Bukkit.createInventory(null, slots, MessageUtils.getMensajeColor(messages.getString("previewInventoryName")));
        if (config.getBoolean("preview-inventory.back-item")) {
            ItemStack item = new ItemStack(Material.ARROW);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(MessageUtils.getMensajeColor(messages.getString("backItemName")));
            item.setItemMeta(meta);
            inv.setItem(Integer.valueOf(config.getString("preview-inventory.back-item-slot")), item);
        }


        int slot = 0;
        if (!kits.contains("Kits." + kit + ".Items")) {
            //No tiene items, solo comandos?
            String prefix = messages.getString("prefix");
            jugador.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPreviewError")));
            return;
        }
        for (String n : kits.getConfigurationSection("Kits." + kit + ".Items").getKeys(false)) {
            String path = "Kits." + kit + ".Items." + n;
            ItemStack item = KitManager.getItem(kits, path, config, jugador);
            try {
                if (kits.contains(path + ".preview_slot")) {
                    inv.setItem(Integer.valueOf(kits.getString(path + ".preview_slot")), item);
                } else {
                    inv.setItem(slot, item);
                    slot++;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', PlayerKits.pluginPrefix + "&cThere is an error. Items for this kits are set on an invalid slot of the preview inventory. Change the &7previewInventorySize &coption in the config.yml file!"));
                return;
            }
        }

        jugador.openInventory(inv);

        plugin.agregarInventarioJugador(new PlayerInventory(jugador, pagina, null, "preview"));
    }

    @EventHandler
    public void clickInventario(InventoryClickEvent event) {
        FileConfiguration config = plugin.getConfig();
        Player jugador = (Player) event.getWhoClicked();
        PlayerInventory inv = plugin.getInventarioJugador(jugador.getName());
        if (inv != null) {
            if (event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }
            if ((event.getSlotType() == null)) {
                event.setCancelled(true);
                return;
            }

            int slot = event.getSlot();
            event.setCancelled(true);
            if (event.getClickedInventory().equals(jugador.getOpenInventory().getTopInventory())) {
                String tipoInventario = inv.getTipoInventario();
                if (tipoInventario.equals("preview")) {
                    int slotAClickear = Integer.valueOf(config.getInt("preview-inventory.back-item-slot"));
                    if (slot == slotAClickear && !event.getCurrentItem().getType().name().contains("AIR")) {
                        InventarioManager.abrirInventarioMain(config, plugin, jugador, inv.getPagina());
                    }
                }
            }
        }
    }
}
