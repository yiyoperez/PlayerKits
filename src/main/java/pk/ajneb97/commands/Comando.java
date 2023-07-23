package pk.ajneb97.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.managers.InventarioEditar;
import pk.ajneb97.managers.InventarioManager;
import pk.ajneb97.managers.InventarioPreview;
import pk.ajneb97.managers.JugadorManager;
import pk.ajneb97.managers.KitManager;
import pk.ajneb97.utils.Checks;
import pk.ajneb97.utils.MessageUtils;
import pk.ajneb97.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Comando implements CommandExecutor, TabCompleter {

    private String prefix;
    private final FileConfiguration kits;
    private final FileConfiguration config;
    private final FileConfiguration messages;
    private final PlayerKits plugin;


    public Comando(PlayerKits plugin) {
        this.plugin = plugin;
        this.kits = plugin.getKits();
        this.config = plugin.getConfig();
        this.messages = plugin.getMessages();
        this.prefix = messages.getString("prefix");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length > 0) {

            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "reload":
                    plugin.reloadConfig();
                    plugin.reloadKits();
                    plugin.reloadPlayerDataSaveTask();
                    sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("configReload")));
                    break;
                case "give":
                    give(sender, args, prefix, config, kits);
                    break;
                case "open":
                    open(sender, args, prefix, config, kits);
                    break;
                case "reset":
                    reset(sender, args, prefix, config, kits);
                    break;
                default:
                    helpArgument(sender);
            }
            return false;
        }

        final Player player = (Player) sender;

        // Open inventory if empty command.
        if (args.length <= 0) {
            //Abrir inventario
            if (!Checks.checkTodo(plugin, player)) {
                return true;
            }
            InventarioManager.abrirInventarioMain(config, plugin, player, 1);
            return false;
        }

        //TODO: This option gets stuck player is an admin, it doesn't let him use subcommands.
        if (quickClaimMethod(player, args)) return true;

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "open":
                openArgument(sender, player, args);
                break;
            case "create":
                createArgument(player, args);
                break;
            case "delete":
                deleteArgument(player, args);
                break;
            case "edit":
                editArgument(player, args);
                break;
            case "list":
                listArgument(player);
                break;
            case "claim":
                claimArgument(player, args);
                break;
            case "preview":
                previewArgument(player, args);
                break;
            case "give":
                giveArgument(sender, player, args);
                break;
            case "reset":
                resetArgument(sender, player, args);
                break;
            case "reload":
                reloadArgument(player);
                break;
            default:
                helpArgument(player);
                break;
        }
        return true;
    }

    private boolean quickClaimMethod(Player player, String[] args) {
        if (config.getBoolean("claim_kit_short_command")) {
            // /kit <kit>
            String kit = getKit(kits, args[0]);
            if (kit == null) {
                player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[0])));
            }

            if (kits.contains("Kits." + kit + ".slot") || player.isOp() || player.hasPermission("playerkits.admin")) {
                KitManager.claimKit(player, kit, plugin, true, false, false);
            }
            return true;
        }
        return false;
    }

    private void helpArgument(CommandSender sender) {
        if (sender.isOp() || sender.hasPermission("playerkits.admin")) {
            for (String helpMessage : messages.getStringList("command-help-message")) {
                sender.sendMessage(MessageUtils.getMensajeColor(helpMessage));
            }
        } else {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
        }
    }

    private void previewArgument(Player player, String[] args) {
        if (args.length >= 2) {
            String kit = getKit(kits, args[1]);
            if (kit != null) {
                if (kits.contains("Kits." + kit + ".slot") || player.isOp() || player.hasPermission("playerkits.admin")) {
                    boolean hasPermission = true;
                    if (kits.contains("Kits." + kit + ".permission")) {
                        String permission = kits.getString("Kits." + kit + ".permission");
                        if (!player.isOp() && !player.hasPermission(permission)) {
                            hasPermission = false;
                        }
                    }
                    boolean permissionCheck = config.getBoolean("preview_inventory_requires_permission");
                    if (permissionCheck && !hasPermission) {
                        player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("cantPreviewError")));
                        return;
                    }

                    InventarioPreview.abrirInventarioPreview(plugin, player, kit, 1);
                } else {
                    player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
                }
            } else {
                player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            }
        } else {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandPreviewError")));
        }
    }

    private void giveArgument(CommandSender sender, Player player, String[] args) {
        if (player.isOp() || player.hasPermission("playerkits.admin")) {
            give(sender, args, prefix, config, kits);
        } else {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
        }
    }

    private void editArgument(Player player, String[] args) {
        if (player.isOp() || player.hasPermission("playerkits.admin")) {
            if (args.length >= 2) {
                String kit = getKit(kits, args[1]);
                if (kit != null) {
                    InventarioEditar.crearInventario(player, kit, plugin);
                } else {
                    player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
                }
            } else {
                player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandEditError")));
            }
        } else {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
        }
    }

    private void listArgument(Player player) {
        if (player.isOp() || player.hasPermission("playerkits.admin") || player.hasPermission("playerkits.list")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandList")));
            int c = 1;
            JugadorManager jManager = plugin.getJugadorManager();
            if (kits.contains("Kits")) {
                for (String key : kits.getConfigurationSection("Kits").getKeys(false)) {
                    if (kits.contains("Kits." + key + ".slot") || player.isOp() || player.hasPermission("playerkits.admin")) {
                        if (kits.contains("Kits." + key + ".permission") && !player.hasPermission(kits.getString("Kits." + key + ".permission"))) {
                            player.sendMessage(MessageUtils.getMensajeColor(messages.getString("commandListKitNoPermissions").replace("%number%", c + "").replace("%kit%", key)));
                        } else {
                            if (kits.contains("Kits." + key + ".one_time") && kits.getString("Kits." + key + ".one_time").equals("true") && jManager.isOneTime(player, key)) {
                                player.sendMessage(MessageUtils.getMensajeColor(messages.getString("commandListKitOneTime").replace("%number%", c + "").replace("%kit%", key)));
                            } else {
                                boolean cooldownReady = true;
                                if (kits.contains("Kits." + key + ".cooldown")) {
                                    String cooldown = Utils.getCooldown(key, player, kits, config, jManager);
                                    if (!cooldown.equals("ready")) {
                                        cooldownReady = false;
                                        player.sendMessage(MessageUtils.getMensajeColor(messages.getString("commandListKitInCooldown").replace("%number%", c + "").replace("%kit%", key).replace("%time%", cooldown)));
                                    }
                                }
                                if (cooldownReady) {
                                    player.sendMessage(MessageUtils.getMensajeColor(messages.getString("commandListKit").replace("%number%", c + "").replace("%kit%", key)));
                                }
                            }
                        }
                        c++;
                    }

                }
            }
        } else {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
        }
    }

    private void claimArgument(Player player, String[] args) {
        if (args.length >= 2) {
            String kit = getKit(kits, args[1]);
            if (kit != null) {
                if (kits.contains("Kits." + kit + ".slot") || player.isOp() || player.hasPermission("playerkits.admin")) {
                    KitManager.claimKit(player, kit, plugin, true, false, false);
                } else {
                    player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
                }
            } else {
                player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            }
        } else {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandClaimError")));
        }
    }

    private void openArgument(CommandSender sender, Player player, String[] args) {
        if (!player.isOp() && !player.hasPermission("playerkits.admin")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
            return;
        }

        open(sender, args, prefix, config, kits);
    }

    public void open(CommandSender sender, String[] args, String prefix, FileConfiguration config, FileConfiguration kits) {
        if (args.length >= 2) {
            Player player = Bukkit.getPlayer(args[1]);
            if (!Checks.checkTodo(plugin, sender)) {
                return;
            }
            if (player != null) {
                int pag = 1;
                if (args.length >= 3) {
                    try {
                        pag = Integer.valueOf(args[2]);
                        int pagsTotales = InventarioManager.getPaginasTotales(kits);
                        if (pag > pagsTotales) {
                            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("errorPage")));
                            return;
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("errorPage")));
                        return;
                    }
                }
                InventarioManager.abrirInventarioMain(config, plugin, player, pag);
                sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitInventoryOpen").replace("%player%", args[1])));
            } else {
                sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("playerNotOnline").replace("%player%", args[1])));
            }
        } else {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandOpenError")));
        }
    }

    private void createArgument(Player player, String[] args) {
        if (player.isOp() || player.hasPermission("playerkits.admin")) {
            if (args.length >= 2) {
                String kit = getKit(kits, args[1]);
                if (kit == null) {
                    if (KitManager.save(args[1], kits, config, player)) {
                        plugin.saveKits();
                        player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitCreated").replace("%name%", args[1])));
                    } else {
                        player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("inventoryEmpty")));
                    }
                } else {
                    player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitAlreadyExists").replace("%name%", kit)));
                }
            } else {
                player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandCreateError")));
            }
        } else {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
        }
    }

    private void deleteArgument(Player player, String[] args) {
        if (player.isOp() || player.hasPermission("playerkits.admin")) {
            if (args.length >= 2) {
                String kit = getKit(kits, args[1]);
                if (kit != null) {
                    kits.set("Kits." + kit, null);
                    plugin.saveKits();
                    player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitRemoved").replace("%name%", kit)));
                } else {
                    player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
                }
            } else {
                player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandDeleteError")));
            }
        } else {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
        }
    }

    public void give(CommandSender sender, String[] args, String prefix, FileConfiguration config, FileConfiguration kits) {
        if (args.length >= 3) {
            String kit = getKit(kits, args[1]);
            if (kit != null) {
                Player player = Bukkit.getPlayer(args[2]);
                if (player != null) {
                    KitManager.claimKit(player, kit, plugin, true, true, false);
                    sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitGive").replace("%player%", args[2]).replace("%kit%", kit)));
                } else {
                    sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("playerNotOnline").replace("%player%", args[2])));
                }
            } else {
                sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            }
        } else {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandGiveError")));
        }
    }

    private void resetArgument(CommandSender sender, Player player, String[] args) {
        if (player.isOp() || player.hasPermission("playerkits.admin")) {
            reset(sender, args, prefix, config, kits);
        } else {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
        }
    }

    public void reset(CommandSender sender, String[] args, String prefix, FileConfiguration config, FileConfiguration kits) {
        // /kits reset <kit> <player>
        if (args.length < 3) {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandResetError")));
            return;
        }

        String nombreJugador = args[2];
        String kit = getKit(kits, args[1]);
        if (kit == null) {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            return;
        }

        if (plugin.getJugadorManager().reiniciarKit(nombreJugador, kit)) {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitResetCorrect").replace("%kit%", args[1]).replace("%player%", nombreJugador)));
        } else {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitResetFail").replace("%kit%", args[1]).replace("%player%", nombreJugador)));
        }
    }

    private void reloadArgument(Player player) {
        if (!player.isOp() && !player.hasPermission("playerkits.admin")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
            return;
        }

        plugin.reloadConfig();
        plugin.reloadKits();
        plugin.reloadPlayerDataSaveTask();
        player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("configReload")));
    }

    public String getKit(FileConfiguration kits, String kitName) {
        ConfigurationSection section = kits.getConfigurationSection("Kits");

        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (key.equalsIgnoreCase(kitName)) {
                    return key;
                }
            }
        }

        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<String>();

        if (args.length == 1) {
            //Mostrar todos los comandos
            List<String> commands = new ArrayList<String>();
            commands.add("preview");
            if (config.getBoolean("claim_kit_short_command")) {
                String argKit = args[0];
                for (String key : kits.getConfigurationSection("Kits").getKeys(false)) {
                    if (kits.contains("Kits." + key + ".slot") || sender.isOp() || sender.hasPermission("playerkits.admin")) {
                        if (argKit.toLowerCase().isEmpty() || key.toLowerCase().startsWith(argKit.toLowerCase())) {
                            completions.add(key);
                        }
                    }
                }
            } else {
                commands.add("claim");
            }

            for (String c : commands) {
                if (args[0].isEmpty() || c.startsWith(args[0].toLowerCase())) {
                    completions.add(c);
                }
            }
        } else {
            if ((args[0].equalsIgnoreCase("claim") || args[0].equalsIgnoreCase("preview")) && args.length == 2) {
                String argKit = args[1];
                for (String key : kits.getConfigurationSection("Kits").getKeys(false)) {
                    if (argKit.toLowerCase().isEmpty() || key.toLowerCase().startsWith(argKit.toLowerCase())) {
                        if (kits.contains("Kits." + key + ".slot") || sender.isOp() || sender.hasPermission("playerkits.admin")) {
                            completions.add(key);
                        }
                    }
                }
            }
        }


        if (sender.isOp() || sender.hasPermission("playerkits.admin") || sender.hasPermission("playerkits.list")) {
            if (args.length == 1) {
                String c = "list";
                if (args[0].isEmpty() || c.startsWith(args[0].toLowerCase())) {
                    completions.add(c);
                }
            }
        }
        if (sender.isOp() || sender.hasPermission("playerkits.admin")) {
            if (args.length == 1) {
                //Mostrar todos los comandos
                List<String> commands = new ArrayList<String>();
                commands.add("open");
                commands.add("create");
                commands.add("delete");
                commands.add("edit");
                commands.add("give");
                commands.add("reset");
                commands.add("reload");
                for (String c : commands) {
                    if (args[0].isEmpty() || c.startsWith(args[0].toLowerCase())) {
                        completions.add(c);
                    }
                }
            } else {
                if ((args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("reset")) && args.length == 2) {
                    String argKit = args[1];
                    for (String key : kits.getConfigurationSection("Kits").getKeys(false)) {
                        if (argKit.toLowerCase().isEmpty() || key.toLowerCase().startsWith(argKit.toLowerCase())) {
                            completions.add(key);
                        }
                    }
                }
            }
        }

        if (completions.isEmpty()) {
            return null;
        }


        return completions;
    }
}
