package pk.ajneb97.listener;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.model.CurrentPlayerInventory;
import pk.ajneb97.inventory.InventoryPreview;
import pk.ajneb97.manager.InventarioManager;
import pk.ajneb97.manager.KitManager;
import pk.ajneb97.util.MessageUtils;

public class InventoryListener implements Listener {

    private final PlayerKits plugin;

    public InventoryListener(PlayerKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKitsInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        CurrentPlayerInventory inv = plugin.getInventarioJugador(player.getName());
        if (inv == null) return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            event.setCancelled(true);
            return;
        }

        //TODO: ...
        if (event.getSlotType() == null) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() != player.getOpenInventory().getTopInventory()) {
            return;
        }

        event.setCancelled(true);

        String tipoInventario = inv.getInventoryType();
        if (!tipoInventario.equals("main")) return;

        int slot = event.getSlot();
        int pagina = inv.getPage();
        FileConfiguration configKits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();

        KitManager kitManager = plugin.getKitManager();
        ConfigurationSection kitsSection = configKits.getConfigurationSection("Kits");
        if (!kitsSection.getKeys(false).isEmpty()) {
            for (String key : kitsSection.getKeys(false)) {
                if (configKits.contains("Kits." + key + ".slot")) {
                    if (slot == configKits.getInt("Kits." + key + ".slot")) {
                        int page = 1;
                        if (configKits.contains("Kits." + key + ".page")) {
                            page = configKits.getInt("Kits." + key + ".page");
                        }
                        if (page == pagina) {
                            if (event.getClick() == ClickType.RIGHT && config.getBoolean("preview-inventory.enabled")) {
                                //Comprobar si tiene permiso y si esta activada la opcion de requerir permiso
                                boolean hasPermission = true;
                                if (configKits.contains("Kits." + key + ".permission")) {
                                    String permission = configKits.getString("Kits." + key + ".permission");
                                    if (!player.isOp() && !player.hasPermission(permission)) {
                                        hasPermission = false;
                                    }
                                }
                                boolean permissionCheck = config.getBoolean("preview_inventory_requires_permission");
                                if (permissionCheck && !hasPermission) {
                                    String prefix = messages.getString("prefix");
                                    player.sendMessage(MessageUtils.translateColor(prefix + messages.getString("cantPreviewError")));
                                    return;
                                }

                                InventoryPreview.openInventory(plugin, player, key, inv.getPage());
                            } else {
                                kitManager.claimKit(player, key, true, false);
                            }
                            return;
                        }
                    }
                }
            }

        }

    }

    @EventHandler
    public void onMainInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        CurrentPlayerInventory inv = plugin.getInventarioJugador(player.getName());
        if (inv == null) return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            event.setCancelled(true);
            return;
        }

        //TODO: ...
        if (event.getSlotType() == null) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() != player.getOpenInventory().getTopInventory()) {
            return;
        }

        event.setCancelled(true);

        String tipoInventario = inv.getInventoryType();
        if (!tipoInventario.equals("main")) return;

        int slot = event.getSlot();
        int pagina = inv.getPage();
        FileConfiguration configKits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();
        int paginasTotales = InventarioManager.getCurrentPages(configKits);

        ConfigurationSection inventorySection = config.getConfigurationSection("inventory.items");
        if (!inventorySection.getKeys(false).isEmpty()) {
            for (String key : config.getConfigurationSection("inventory.items").getKeys(false)) {
                int slotNuevo = Integer.parseInt(key);
                if (slot == slotNuevo) {

                    if (config.contains("inventory.items." + key + ".type")) {
                        if (config.getString("inventory.items." + key + ".type").equals("previous_page")) {
                            if (pagina > 1) {
                                if (!config.getString("sounds.page_sound").equals("none")) {
                                    String[] separados = config.getString("sounds.page_sound").split(";");
                                    try {
                                        Sound sound = Sound.valueOf(separados[0]);
                                        player.playSound(player.getLocation(), sound, Float.parseFloat(separados[1]), Float.parseFloat(separados[2]));
                                    } catch (Exception ex) {
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PlayerKits.pluginPrefix + "&7Sound Name: &c" + separados[0] + " &7is not valid. Change the name of the sound corresponding to your Minecraft version."));
                                    }
                                }

                                InventarioManager.openMainInventory(config, plugin, player, pagina - 1);
                                return;
                            }
                        } else if (config.getString("inventory.items." + key + ".type").equals("next_page")) {
                            if (paginasTotales > pagina) {
                                if (!config.getString("sounds.page_sound").equals("none")) {
                                    String[] separados = config.getString("sounds.page_sound").split(";");
                                    try {
                                        Sound sound = Sound.valueOf(separados[0]);
                                        player.playSound(player.getLocation(), sound, Float.parseFloat(separados[1]), Float.parseFloat(separados[2]));
                                    } catch (Exception ex) {
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PlayerKits.pluginPrefix + "&7Sound Name: &c" + separados[0] + " &7is not valid. Change the name of the sound corresponding to your Minecraft version."));
                                    }
                                }
                                InventarioManager.openMainInventory(config, plugin, player, pagina + 1);
                                return;
                            }
                        }
                    }

                    if (config.contains("inventory.items." + key + ".command")) {
                        String comando = config.getString("inventory.items." + key + ".command");
                        CommandSender console = Bukkit.getServer().getConsoleSender();
                        String comandoAEnviar = comando.replaceAll("%player%", player.getName());
                        Bukkit.dispatchCommand(console, comandoAEnviar);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player jugador = (Player) event.getPlayer();
        CurrentPlayerInventory inv = plugin.getInventarioJugador(jugador.getName());
        if (inv != null && inv.getInventoryManager() != null) {
            Bukkit.getScheduler().cancelTask(inv.getInventoryManager().getTaskID());
        }

        plugin.removerInventarioJugador(jugador.getName());
    }
}
