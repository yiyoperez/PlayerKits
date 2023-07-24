package pk.ajneb97.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.managers.InventarioEditar;
import pk.ajneb97.managers.InventarioManager;
import pk.ajneb97.managers.InventoryPreview;
import pk.ajneb97.managers.JugadorManager;
import pk.ajneb97.managers.KitManager;
import pk.ajneb97.utils.Checks;
import pk.ajneb97.utils.MessageUtils;
import pk.ajneb97.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class Comando implements CommandExecutor, TabCompleter {

    private final String prefix;
    private final PlayerKits plugin;


    public Comando(PlayerKits plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getMessages().getString("prefix");
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();

        if (!(sender instanceof Player) && args.length > 0) {

            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "reload":
                    plugin.reloadConfig();
                    plugin.reloadKits();
                    plugin.reloadMessages();
                    plugin.reloadPlayerDataSaveTask();
                    sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("configReload")));
                    break;
                case "give":
                    give(sender, args);
                    break;
                case "open":
                    open(sender, args);
                    break;
                case "reset":
                    reset(sender, args);
                    break;
                default:
                    helpArgument(sender);
            }
            return true;
        }

        final Player player = (Player) sender;
        // Open inventory if empty command.
        if (args.length == 0) {
            //Verify main inventory items.

            if (Checks.mainInventoryContainsBadItems(plugin, player)) return true;

            // Finally open inventory to player.
            InventarioManager.abrirInventarioMain(config, plugin, player, 1);
            return true;
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
        FileConfiguration kits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();

        if (config.getBoolean("claim_kit_short_command")) {
            String kit = getKit(kits, args[0]);
            if (kit == null) {
                if (player.hasPermission("playerkits.bypass.short.claim")) {
                    return false;
                }
                player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[0])));
                return true;
            }

            if (kits.contains("Kits." + kit + ".slot") || player.isOp() || player.hasPermission("playerkits.admin")) {
                KitManager.claimKit(player, kit, plugin, true, false, false);
            }
            return true;
        }
        return false;
    }

    private void helpArgument(CommandSender sender) {
        FileConfiguration messages = plugin.getMessages();
        for (String helpMessage : messages.getStringList("command-help-message")) {
            sender.sendMessage(MessageUtils.getMensajeColor(helpMessage));
        }
    }

    private void openArgument(CommandSender sender, Player player, String[] args) {
        FileConfiguration messages = plugin.getMessages();
        if (!player.isOp() && !player.hasPermission("playerkits.open")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
            return;
        }

        open(sender, args);
    }

    public void open(CommandSender sender, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();
        if (args.length < 2) {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandOpenError")));
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);

        // If main inventory contains bad items return.
        if (Checks.mainInventoryContainsBadItems(plugin, player)) return;

        if (player == null) {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("playerNotOnline").replace("%player%", args[1])));
            return;
        }

        int pag = 1;
        if (args.length >= 3) {
            try {
                pag = Integer.parseInt(args[2]);
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
    }

    private void createArgument(Player player, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();
        if (!player.isOp() && !player.hasPermission("playerkits.create")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandCreateError")));
            return;
        }

        String kit = getKit(kits, args[1]);
        if (kit != null) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitAlreadyExists").replace("%name%", kit)));
            return;
        }

        if (KitManager.save(args[1], kits, config, player)) {
            plugin.saveKits();
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitCreated").replace("%name%", args[1])));
        } else {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("inventoryEmpty")));
        }
    }

    private void deleteArgument(Player player, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration messages = plugin.getMessages();
        if (!player.isOp() && !player.hasPermission("playerkits.delete")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandDeleteError")));
            return;
        }

        String kit = getKit(kits, args[1]);
        if (kit == null) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            return;
        }

        kits.set("Kits." + kit, null);
        plugin.saveKits();
        player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitRemoved").replace("%name%", kit)));
    }

    private void editArgument(Player player, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration messages = plugin.getMessages();
        if (!player.isOp() && !player.hasPermission("playerkits.edit")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandEditError")));
            return;
        }

        String kit = getKit(kits, args[1]);
        if (kit == null) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            return;
        }

        InventarioEditar.crearInventario(player, kit, plugin);
    }

    private void listArgument(Player player) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();
        if (!player.isOp() && !player.hasPermission("playerkits.admin") && !player.hasPermission("playerkits.list")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
            return;
        }

        player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandList")));
        JugadorManager jManager = plugin.getJugadorManager();

        // There is no kits available.
        if (!kits.contains("Kits")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandListEmpty")));
            return;
        }

        StringJoiner list = new StringJoiner(messages.getString("commandListKitDisplayFormat", "&r, "));
        for (String key : kits.getConfigurationSection("Kits").getKeys(false)) {
            if (kits.contains("Kits." + key + ".slot") || player.isOp() || player.hasPermission("playerkits.list")) {
                if (kits.contains("Kits." + key + ".permission") && !player.hasPermission(kits.getString("Kits." + key + ".permission"))) {
                    list.add(messages.getString("commandListKitNoPermissions").replace("%kit%", key));
                } else {
                    if (kits.contains("Kits." + key + ".one_time") && kits.getBoolean("Kits." + key + ".one_time") && jManager.isOneTime(player, key)) {
                        list.add(messages.getString("commandListKitOneTime").replace("%kit%", key));
                    } else {
                        boolean cooldownReady = true;
                        if (kits.contains("Kits." + key + ".cooldown")) {
                            String cooldown = Utils.getCooldown(key, player, kits, config, jManager);
                            if (!cooldown.equals("ready")) {
                                cooldownReady = false;
                                list.add(messages.getString("commandListKitInCooldown").replace("%kit%", key).replace("%time%", cooldown));
                            }
                        }
                        if (cooldownReady) {
                            list.add(messages.getString("commandListKit").replace("%kit%", key));
                        }
                    }
                }
            }
        }

        player.sendMessage(MessageUtils.getMensajeColor(String.valueOf(list)));
    }

    private void claimArgument(Player player, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration messages = plugin.getMessages();
        if (args.length < 2) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandClaimError")));
            return;
        }

        String kit = getKit(kits, args[1]);
        if (kit == null) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            return;
        }

        if (kits.contains("Kits." + kit + ".slot") || player.isOp() || player.hasPermission("playerkits.claim")) {
            KitManager.claimKit(player, kit, plugin, true, false, false);
        } else {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
        }
    }

    private void previewArgument(Player player, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();
        if (args.length < 2) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandPreviewError")));
            return;
        }

        String kit = getKit(kits, args[1]);
        if (kit == null) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            return;
        }

        if (kits.contains("Kits." + kit + ".slot") || player.isOp() || player.hasPermission("playerkits.preview")) {
            boolean permissionCheck = config.getBoolean("preview_inventory_requires_permission");
            if (permissionCheck) {
                boolean hasPermission = true;
                if (kits.contains("Kits." + kit + ".permission")) {
                    String permission = kits.getString("Kits." + kit + ".permission");
                    if (!player.isOp() && !player.hasPermission(permission)) {
                        hasPermission = false;
                    }
                }

                if (!hasPermission) {
                    player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("cantPreviewError")));
                    return;
                }
            }

            InventoryPreview.abrirInventarioPreview(plugin, player, kit, 1);
        }
    }

    private void giveArgument(CommandSender sender, Player player, String[] args) {
        FileConfiguration messages = plugin.getMessages();
        if (!player.isOp() && !player.hasPermission("playerkits.give")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
            return;
        }

        give(sender, args);
    }

    public void give(CommandSender sender, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration messages = plugin.getMessages();
        if (args.length < 3) {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandGiveError")));
            return;
        }
        String kit = getKit(kits, args[1]);
        if (kit == null) {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            return;
        }

        Player player = Bukkit.getPlayer(args[2]);
        if (player != null) {
            KitManager.claimKit(player, kit, plugin, true, true, false);
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitGive").replace("%player%", args[2]).replace("%kit%", kit)));
        } else {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("playerNotOnline").replace("%player%", args[2])));
        }
    }

    private void resetArgument(CommandSender sender, Player player, String[] args) {
        FileConfiguration messages = plugin.getMessages();
        if (!player.isOp() && !player.hasPermission("playerkits.reset")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
            return;
        }

        reset(sender, args);
    }

    public void reset(CommandSender sender, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration messages = plugin.getMessages();
        // /kits reset <kit> <player>
        if (args.length < 3) {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandResetError")));
            return;
        }

        String name = args[2];
        String kit = getKit(kits, args[1]);
        if (kit == null) {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            return;
        }

        if (plugin.getJugadorManager().resetKit(name, kit)) {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitResetCorrect").replace("%kit%", args[1]).replace("%player%", name)));
        } else {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitResetFail").replace("%kit%", args[1]).replace("%player%", name)));
        }
    }

    private void reloadArgument(Player player) {
        FileConfiguration messages = plugin.getMessages();
        if (!player.isOp() && !player.hasPermission("playerkits.reload")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
            return;
        }

        plugin.reloadConfig();
        plugin.reloadMessages();
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
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();

        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            //Mostrar todos los comandos
            List<String> commands = new ArrayList<>();
            commands.add("preview");
            if (!config.getBoolean("claim_kit_short_command")) {
                commands.add("claim");
            } else {
                String argKit = args[0];
                for (String key : kits.getConfigurationSection("Kits").getKeys(false)) {
                    if (kits.contains("Kits." + key + ".slot") || sender.isOp() || sender.hasPermission("playerkits.admin")) {
                        if (argKit.toLowerCase().isEmpty() || key.toLowerCase().startsWith(argKit.toLowerCase())) {
                            completions.add(key);
                        }
                    }
                }
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
                List<String> commands = new ArrayList<>();
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
                    String argKit = args[1].toLowerCase();
                    for (String key : kits.getConfigurationSection("Kits").getKeys(false)) {
                        if (argKit.isEmpty() || key.toLowerCase().startsWith(argKit.toLowerCase())) {
                            completions.add(key);
                        }
                    }
                }
            }
        }

        if (completions.isEmpty()) {
            return Collections.emptyList();
        }


        return completions;
    }
}
