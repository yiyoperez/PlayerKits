package pk.ajneb97.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.listeners.InventarioEditar;
import pk.ajneb97.listeners.InventoryPreview;
import pk.ajneb97.managers.InventarioManager;
import pk.ajneb97.managers.KitManager;
import pk.ajneb97.managers.PlayerManager;
import pk.ajneb97.models.PlayerData;
import pk.ajneb97.models.PlayerKit;
import pk.ajneb97.utils.Checks;
import pk.ajneb97.utils.Cooldown;
import pk.ajneb97.utils.MessageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final String prefix;
    private final PlayerKits plugin;


    public MainCommand(PlayerKits plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getMessages().getString("prefix");
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();

        if (!(sender instanceof Player)) {

            if (args.length == 0) {
                helpArgument(sender);
                return true;
            }

            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "reload":
                    plugin.reloadPlayerDataSaveTask();
                    plugin.reloadPlayers();
                    plugin.reloadConfig();
                    plugin.reloadKits();
                    plugin.reloadMessages();
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
            InventarioManager.openMainInventory(config, plugin, player, 1);
            return true;
        }

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
        KitManager kitManager = plugin.getKitManager();
        FileConfiguration messages = plugin.getMessages();

        if (config.getBoolean("claim_kit_short_command")) {
            if (!kitManager.existsKit(args[1])){
                if (player.hasPermission("playerkits.bypass.short.claim")) {
                    return false;
                }
                player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[0])));
                return true;
            }

            if (kits.contains("Kits." + args[1] + ".slot") || player.isOp() || player.hasPermission("playerkits.admin")) {
                kitManager.claimKit(player, args[1], true, false, false);
            }
            return true;
        }
        return false;
    }

    private void helpArgument(CommandSender sender) {
        FileConfiguration messages = plugin.getMessages();
        for (String helpMessage : messages.getStringList("command.help-message")) {
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
                int pagsTotales = InventarioManager.getCurrentPages(kits);
                if (pag > pagsTotales) {
                    sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("errorPage")));
                    return;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("errorPage")));
                return;
            }
        }

        InventarioManager.openMainInventory(config, plugin, player, pag);
        sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitInventoryOpen").replace("%player%", args[1])));
    }

    private void createArgument(Player player, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration messages = plugin.getMessages();
        if (!player.isOp() && !player.hasPermission("playerkits.create")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandCreateError")));
            return;
        }

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitAlreadyExists").replace("%name%", args[1])));
            return;
        }

        if (kitManager.save(player, args[1])) {
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

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            return;
        }

        //TODO: Create a method for this at manager.
        kits.set("Kits." + args[1], null);
        plugin.saveKits();
        player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitRemoved").replace("%name%", args[1])));
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

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            return;
        }

        InventarioEditar.crearInventario(player, args[1], plugin);
    }

    private void listArgument(Player player) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration messages = plugin.getMessages();
        if (!player.isOp() && !player.hasPermission("playerkits.admin") && !player.hasPermission("playerkits.list")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
            return;
        }

        player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("command.list.header")));

        // There is no kits available.
        if (!kits.contains("Kits")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("command.list.empty")));
            return;
        }

        PlayerManager playerManager = plugin.getPlayerManager();
        PlayerData playerData = playerManager.getOrCreatePlayer(player);

        StringJoiner list = new StringJoiner(messages.getString("command.list.format", "&r, "));
        for (String key : kits.getConfigurationSection("Kits").getKeys(false)) {
            PlayerKit playerKit = playerManager.getOrCreatePlayerKit(player, key);
            if (kits.contains("Kits." + key + ".slot") || player.isOp() || player.hasPermission("playerkits.list")) {
                if (kits.contains("Kits." + key + ".permission") && !player.hasPermission(kits.getString("Kits." + key + ".permission"))) {
                    list.add(messages.getString("command.list.cooldown").replace("%kit%", key));
                } else {
                    if (kits.contains("Kits." + key + ".one_time") && kits.getBoolean("Kits." + key + ".one_time") && playerKit.isOneTime()) {
                        list.add(messages.getString("command.list.one-time").replace("%kit%", key));
                    } else {
                        if (kits.contains("Kits." + key + ".cooldown") && playerData.hasCooldown(key)) {
                            Cooldown cooldown = playerData.getCooldown(key);
                            list.add(messages.getString("command.list.cooldown").replace("%kit%", key).replace("%time%", cooldown.getTimeLeftRoundedSeconds()));
                        } else {
                            list.add(messages.getString("command.list.available").replace("%kit%", key));
                        }
                    }
                }
            }
        }

        player.sendMessage(MessageUtils.getMensajeColor(list.toString()));
    }

    private void claimArgument(Player player, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration messages = plugin.getMessages();
        if (args.length < 2) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandClaimError")));
            return;
        }

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            return;
        }

        if (kits.contains("Kits." + args[1] + ".slot") || player.isOp() || player.hasPermission("playerkits.claim")) {
            kitManager.claimKit(player, args[1], true, false, false);
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

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            return;
        }

        if (kits.contains("Kits." + args[1] + ".slot") || player.isOp() || player.hasPermission("playerkits.preview")) {
            boolean permissionCheck = config.getBoolean("preview_inventory_requires_permission");
            if (permissionCheck) {
                boolean hasPermission = true;
                if (kits.contains("Kits." + args[1] + ".permission")) {
                    String permission = kits.getString("Kits." + args[1] + ".permission");
                    if (!player.isOp() && !player.hasPermission(permission)) {
                        hasPermission = false;
                    }
                }

                if (!hasPermission) {
                    player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("cantPreviewError")));
                    return;
                }
            }

            InventoryPreview.abrirInventarioPreview(plugin, player, args[1], 1);
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

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            return;
        }

        Player player = Bukkit.getPlayer(args[2]);
        if (player != null) {
            kitManager.claimKit(player, args[1], true, true, false);
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitGive").replace("%player%", args[2]).replace("%kit%", args[1])));
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
        if (args.length < 3) {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("commandResetError")));
            return;
        }

        String name = args[2];

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitDoesNotExists").replace("%name%", args[1])));
            return;
        }

        Player target = Bukkit.getPlayer(name);
        if (target == null) {

            return;
        }

        PlayerManager playerManager = plugin.getPlayerManager();
        PlayerData playerData = playerManager.getOrCreatePlayer(target);
        PlayerKit playerKit = playerManager.getOrCreatePlayerKit(target, args[1]);

        playerKit.setCooldown(-1);
        playerData.removeCooldown(args[1]);
        sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitResetCorrect").replace("%kit%", args[1]).replace("%player%", name)));

        //sender.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitResetFail").replace("%kit%", args[1]).replace("%player%", name)));
    }

    private void reloadArgument(Player player) {
        FileConfiguration messages = plugin.getMessages();
        if (!player.isOp() && !player.hasPermission("playerkits.reload")) {
            player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noPermissions")));
            return;
        }

        plugin.reloadPlayerDataSaveTask();
        plugin.reloadConfig();
        plugin.reloadMessages();
        plugin.reloadPlayers();
        plugin.reloadKits();

        player.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("configReload")));
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
