package pk.ajneb97.managers;

import net.milkbowl.vault.economy.Economy;
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
import pk.ajneb97.inventory.PlayerInventory;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.utils.MessageUtils;
import pk.ajneb97.utils.Utils;

import java.util.List;

public class InventarioConfirmacionDinero implements Listener {

    private final PlayerKits plugin;

    public InventarioConfirmacionDinero(PlayerKits plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    public static void crearInventario(Player jugador, PlayerKits plugin, double dinero, String kit, int pagina) {
        FileConfiguration config = plugin.getConfig();
        Inventory inv = Bukkit.createInventory(null, 9, MessageUtils.getMensajeColor(config.getString("Messages.moneyInventoryName")));
        ItemStack item;
        if (!Utils.isLegacy()) {
            item = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
        } else {
            item = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 5);
        }
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getMensajeColor(config.getString("Messages.moneyInventoryYes")));
        item.setItemMeta(meta);
        inv.setItem(0, item);
        inv.setItem(1, item);
        inv.setItem(2, item);
        inv.setItem(3, item);

        if (!Utils.isLegacy()) {
            item = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        } else {
            item = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 14);
        }
        meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getMensajeColor(config.getString("Messages.moneyInventoryNo")));
        item.setItemMeta(meta);
        inv.setItem(5, item);
        inv.setItem(6, item);
        inv.setItem(7, item);
        inv.setItem(8, item);

        item = new ItemStack(Material.COAL_BLOCK, 1);
        meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getMensajeColor(config.getString("Messages.moneyInventoryConfirmationName")));
        List<String> lore = config.getStringList("Messages.moneyInventoryConfirmationLore");
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, MessageUtils.getMensajeColor(lore.get(i).replace("%price%", dinero + "")));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(4, item);

        jugador.openInventory(inv);

        plugin.agregarInventarioJugador(new PlayerInventory(jugador, pagina, null, "buying: " + kit));
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
            // HMM ?
            if (event.getSlotType() == null) {
                event.setCancelled(true);
                return;
            }

            String prefix = config.getString("Messages.prefix");
            int slot = event.getSlot();
            event.setCancelled(true);
            if (event.getClickedInventory().equals(jugador.getOpenInventory().getTopInventory())) {
                String tipoInventario = inv.getTipoInventario();
                if (tipoInventario.startsWith("buying")) {
                    if (slot >= 0 && slot <= 3) {
                        FileConfiguration configKits = plugin.getKits();
                        String kit = tipoInventario.replace("buying: ", "");
                        double price = configKits.getDouble("Kits." + kit + ".price");
                        Economy econ = plugin.getEconomy();
                        double balance = econ.getBalance(jugador);
                        if (balance < price) {
                            jugador.sendMessage(MessageUtils.getMensajeColor(prefix + config.getString("Messages.noMoneyError")
                                    .replace("%current_money%", balance + "").replace("%required_money%", price + "")));
                        } else {
                            KitManager.claimKit(jugador, kit, plugin, true, false, true);
                            int pag = inv.getPagina();
                            if (pag != -1) {
                                InventarioManager.abrirInventarioMain(config, plugin, jugador, pag);
                            } else {
                                jugador.closeInventory();
                            }

                        }
                    } else if (slot >= 5 && slot <= 8) {
                        int pag = inv.getPagina();
                        if (pag != -1) {
                            InventarioManager.abrirInventarioMain(config, plugin, jugador, pag);
                        } else {
                            jugador.closeInventory();
                        }
                    }
                }
            }
        }
    }
}
