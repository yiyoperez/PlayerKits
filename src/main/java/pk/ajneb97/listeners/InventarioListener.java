package pk.ajneb97.listeners;

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
import pk.ajneb97.inventory.PlayerInventory;
import pk.ajneb97.managers.InventarioManager;
import pk.ajneb97.managers.KitManager;
import pk.ajneb97.utils.MessageUtils;

public class InventarioListener implements Listener {

    private final PlayerKits plugin;

    public InventarioListener(PlayerKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerInventory inv = plugin.getInventarioJugador(player.getName());
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

        String tipoInventario = inv.getTipoInventario();
        if (!tipoInventario.equals("main")) return;

        int slot = event.getSlot();
        int pagina = inv.getPagina();
        FileConfiguration configKits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();
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
                                    player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("cantPreviewError")));
                                    return;
                                }

                                InventoryPreview.abrirInventarioPreview(plugin, player, key, inv.getPagina());
                            } else {
                                kitManager.claimKit(player, key, true, false, false);
                            }
                            return;
                        }
                    }
                }
            }

        }

    }

    @EventHandler
    public void alCerrar(InventoryCloseEvent event) {
        Player jugador = (Player) event.getPlayer();
        PlayerInventory inv = plugin.getInventarioJugador(jugador.getName());
        if (inv != null && inv.getInventarioManager() != null) {
            Bukkit.getScheduler().cancelTask(inv.getInventarioManager().getTaskID());
        }

        //remover
        plugin.removerInventarioJugador(jugador.getName());
    }
}
