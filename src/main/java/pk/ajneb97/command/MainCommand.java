package pk.ajneb97.command;

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
import pk.ajneb97.inventory.InventoryEdit;
import pk.ajneb97.inventory.InventoryPreview;
import pk.ajneb97.manager.InventarioManager;
import pk.ajneb97.manager.KitManager;
import pk.ajneb97.manager.PlayerManager;
import pk.ajneb97.model.PlayerData;
import pk.ajneb97.model.PlayerKit;
import pk.ajneb97.util.Checks;
import pk.ajneb97.util.Cooldown;
import pk.ajneb97.util.MessageHandler;
import pk.ajneb97.util.MessageUtils;
import pk.ajneb97.util.Placeholder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final PlayerKits plugin;
    private final MessageHandler messageHandler;

    public MainCommand(PlayerKits plugin) {
        this.plugin = plugin;
        this.messageHandler = plugin.getMessageHandler();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
                    messageHandler.sendMessage(sender, "configReload");
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

            FileConfiguration config = plugin.getConfig();

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

        if (config.getBoolean("claim_kit_short_command")) {
            if (!kitManager.existsKit(args[0])) {
                if (player.hasPermission("playerkits.bypass.short.claim")) {
                    return false;
                }
                messageHandler.sendMessage(player, "kitDoestNotExists", new Placeholder("%name%", args[0]));
                return true;
            }

            if (kits.contains("Kits." + args[0] + ".slot") || player.isOp() || player.hasPermission("playerkits.admin")) {
                kitManager.claimKit(player, args[0], true, false);
            }
            return true;
        }
        return false;
    }

    private void helpArgument(CommandSender sender) {
        messageHandler.sendListMessage(sender, "command.help-message");
    }

    private void openArgument(CommandSender sender, Player player, String[] args) {
        if (!player.isOp() && !player.hasPermission("playerkits.open")) {
            messageHandler.sendMessage(sender, "noPermissions");
            return;
        }

        open(sender, args);
    }

    public void open(CommandSender sender, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();

        if (args.length < 2) {
            messageHandler.sendMessage(sender, "kit.open.usage");
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            messageHandler.sendMessage(sender, "playerNotOnline", new Placeholder("%player%", args[2]));
            return;
        }

        // If main inventory contains bad items return.
        if (Checks.mainInventoryContainsBadItems(plugin, player)) return;

        int pag = 1;
        if (args.length >= 3) {
            try {
                pag = Integer.parseInt(args[2]);
                int totalPages = InventarioManager.getCurrentPages(kits);
                if (pag > totalPages) {
                    messageHandler.sendMessage(sender, "invalid-page");
                    return;
                }
            } catch (NumberFormatException e) {
                messageHandler.sendMessage(sender, "invalid-page");
                return;
            }
        }

        InventarioManager.openMainInventory(config, plugin, player, pag);
        messageHandler.sendMessage(player, "kit.open.success", new Placeholder("%player%", args[1]));
    }

    private void createArgument(Player player, String[] args) {
        if (!player.isOp() && !player.hasPermission("playerkits.create")) {
            messageHandler.sendMessage(player, "noPermissions");
            return;
        }

        if (args.length < 2) {
            messageHandler.sendMessage(player, "kit.create.usage");
            return;
        }

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            messageHandler.sendMessage(player, "kit.error.alreadyExists", new Placeholder("%name%", args[1]));
            return;
        }

        if (kitManager.save(player, args[1])) {
            plugin.saveKits();
            messageHandler.sendMessage(player, "kit.create.success", new Placeholder("%name%", args[1]));
        } else {
            messageHandler.sendMessage(player, "inventoryEmpty");
        }
    }

    private void deleteArgument(Player player, String[] args) {
        FileConfiguration kits = plugin.getKits();

        if (!player.isOp() && !player.hasPermission("playerkits.delete")) {
            messageHandler.sendMessage(player, "noPermissions");
            return;
        }

        if (args.length < 2) {
            messageHandler.sendMessage(player, "kit.delete.usage");
            return;
        }

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            messageHandler.sendMessage(player, "kit.error.notFound", new Placeholder("%name%", args[1]));
            return;
        }

        //TODO: Create a method for this at manager.
        kits.set("Kits." + args[1], null);
        plugin.saveKits();
        messageHandler.sendMessage(player, "kit.delete.success", new Placeholder("%name%", args[1]));
    }

    private void editArgument(Player player, String[] args) {
        if (!player.isOp() && !player.hasPermission("playerkits.edit")) {
            messageHandler.sendMessage(player, "noPermissions");
            return;
        }

        if (args.length < 2) {
            messageHandler.sendMessage(player, "kit.edit.usage");
            return;
        }

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            messageHandler.sendMessage(player, "kit.error.notFound", new Placeholder("%name%", args[1]));
            return;
        }

        InventoryEdit.crearInventario(player, args[1], plugin);
    }

    private void listArgument(Player player) {
        FileConfiguration kits = plugin.getKits();

        if (!player.isOp() || !player.hasPermission("playerkits.list")) {
            messageHandler.sendMessage(player, "noPermissions");
            return;
        }

        messageHandler.sendMessage(player, "command.list.header");

        // There is no kits available.
        if (!kits.contains("Kits")) {
            messageHandler.sendMessage(player, "command.list.empty");
            return;
        }

        PlayerManager playerManager = plugin.getPlayerManager();
        PlayerData playerData = playerManager.getOrCreatePlayer(player);

        StringJoiner list = new StringJoiner(messageHandler.getRawMessage("command.list.format"));
        for (String key : kits.getConfigurationSection("Kits").getKeys(false)) {
            PlayerKit playerKit = playerManager.getOrCreatePlayerKit(player, key);
            if (kits.contains("Kits." + key + ".slot") || player.isOp() || player.hasPermission("playerkits.list")) {
                if (kits.contains("Kits." + key + ".permission") && !player.hasPermission(kits.getString("Kits." + key + ".permission"))) {
                    list.add(messageHandler.getRawMessage("command.list.no-permission", new Placeholder("%kit%", key)));
                } else {
                    if (kits.contains("Kits." + key + ".one_time") && kits.getBoolean("Kits." + key + ".one_time") && playerKit.isOneTime()) {
                        list.add(messageHandler.getRawMessage("command.list.one-time", new Placeholder("%kit%", key)));
                    } else {
                        if (kits.contains("Kits." + key + ".cooldown") && playerData.hasCooldown(key)) {
                            Cooldown cooldown = playerData.getCooldown(key);

                            List<Placeholder> placeholderList = new ArrayList<>();
                            placeholderList.add(new Placeholder("%kit%", key));
                            placeholderList.add(new Placeholder("%timeleft%", cooldown.getTimeLeft()));
                            placeholderList.add(new Placeholder("%timer%", cooldown.getTimeLeftTimer()));
                            placeholderList.add(new Placeholder("%seconds%", cooldown.getTimeLeftSeconds()));
                            placeholderList.add(new Placeholder("%plainseconds%", cooldown.getTimeLeftPlainSeconds()));
                            placeholderList.add(new Placeholder("%roundedseconds%", cooldown.getTimeLeftRoundedSeconds()));

                            list.add(messageHandler.getRawMessage("command.list.cooldown", placeholderList));
                        } else {
                            list.add(messageHandler.getRawMessage("command.list.available", new Placeholder("%kit%", key)));
                        }
                    }
                }
            }
        }

        player.sendMessage(MessageUtils.getMensajeColor(list.toString()));
    }

    private void claimArgument(Player player, String[] args) {
        if (args.length < 2) {
            messageHandler.sendMessage(player, "kit.claim.usage");
            return;
        }

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            messageHandler.sendMessage(player, "kit.error.notFound", new Placeholder("%name%", args[1]));
            return;
        }

        kitManager.claimKit(player, args[1], true, false);
    }

    private void previewArgument(Player player, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();

        if (args.length < 2) {
            messageHandler.sendMessage(player, "kit.preview.usage");
            return;
        }

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            messageHandler.sendMessage(player, "kit.error.notFound", new Placeholder("%name%", args[1]));
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
                    messageHandler.sendMessage(player, "kit.error.cantPreview");
                    return;
                }
            }

            InventoryPreview.openInventory(plugin, player, args[1], 1);
        }
    }

    private void giveArgument(CommandSender sender, Player player, String[] args) {
        if (!player.isOp() && !player.hasPermission("playerkits.give")) {
            messageHandler.sendMessage(sender, "noPermissions");
            return;
        }

        give(sender, args);
    }

    public void give(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messageHandler.sendMessage(sender, "kit.give.usage");
            return;
        }

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            messageHandler.sendMessage(sender, "kit.error.notFound", new Placeholder("%name%", args[1]));
            return;
        }

        Player player = Bukkit.getPlayer(args[2]);
        if (player == null) {
            messageHandler.sendMessage(sender, "playerNotOnline", new Placeholder("%player%", args[2]));
            return;
        }

        kitManager.claimKit(player, args[1], true, true);
        messageHandler.sendMessage(sender,
                "kit.give.success",
                new Placeholder("%player%", args[2]),
                new Placeholder("%kit%", args[1]));
    }

    private void resetArgument(CommandSender sender, Player player, String[] args) {
        if (!player.isOp() && !player.hasPermission("playerkits.reset")) {
            messageHandler.sendMessage(sender, "noPermission");
            return;
        }

        reset(sender, args);
    }

    public void reset(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messageHandler.sendMessage(sender, "kit.reset.usage");
            return;
        }

        String name = args[2];

        KitManager kitManager = plugin.getKitManager();
        if (!kitManager.existsKit(args[1])) {
            messageHandler.sendMessage(sender, "kit.error.notFound", new Placeholder("%name%", args[1]));
            return;
        }

        Player target = Bukkit.getPlayer(name);
        if (target == null) {
            messageHandler.sendMessage(sender, "playerNotOnline", new Placeholder("%player%", name));
            return;
        }

        PlayerManager playerManager = plugin.getPlayerManager();
        PlayerData playerData = playerManager.getOrCreatePlayer(target);
        PlayerKit playerKit = playerManager.getOrCreatePlayerKit(target, args[1]);

        playerKit.setOneTime(false);
        playerKit.setCooldown(-1);
        playerData.removeCooldown(args[1]);
        messageHandler.sendMessage(sender,
                "kit.reset.success",
                new Placeholder("%kit%", args[1]),
                new Placeholder("%player%", name));
    }

    private void reloadArgument(Player player) {
        if (!player.isOp() && !player.hasPermission("playerkits.reload")) {
            messageHandler.sendMessage(player, "noPermissions");
            return;
        }

        plugin.reloadPlayerDataSaveTask();
        plugin.reloadConfig();
        plugin.reloadMessages();
        plugin.reloadPlayers();
        plugin.reloadKits();

        messageHandler.sendMessage(player, "configReload");
    }

    //TODO: This.
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        FileConfiguration kits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();

        String commandArg = args[0].toLowerCase();
        List<String> completions = new ArrayList<>();
        ConfigurationSection kitsSection = kits.getConfigurationSection("Kits");
        if (config.getBoolean("claim_kit_short_command") && args.length == 1) {

            if (kitsSection.getKeys(false).isEmpty()) {
                return Collections.emptyList();
            }

            suggestKits(kitsSection, commandArg, completions);

            return completions;
        }

        Map<String, int[]> argumentMap = new HashMap<>();
        argumentMap.put("open", new int[]{1});
        argumentMap.put("list", new int[]{1});
        argumentMap.put("create", new int[]{1});
        argumentMap.put("reload", new int[]{1});
        argumentMap.put("edit", new int[]{1, 2});
        argumentMap.put("give", new int[]{1, 2});
        argumentMap.put("reset", new int[]{1, 2});
        argumentMap.put("delete", new int[]{1, 2});
        argumentMap.put("preview", new int[]{1, 2});
        argumentMap.put("claim", new int[]{1, 2});

        // Adds basic commands.
        Set<String> suggestions = argumentMap.keySet();
        suggestions.forEach(suggestion -> {

            if (sender.hasPermission("playerkits." + suggestion)) {

                for (int i : argumentMap.get(suggestion)) {
                    if (i == args.length) {
                        Set<String> commandSelector = new HashSet<>();
                        commandSelector.add("edit");
                        commandSelector.add("give");
                        commandSelector.add("claim");
                        commandSelector.add("reset");
                        commandSelector.add("delete");
                        commandSelector.add("preview");

                        if (commandSelector.contains(commandArg)) {
                            String kitArg = args[1].toLowerCase();

                            if (kitsSection != null) {
                                suggestKits(kitsSection, kitArg, completions);
                            }
                        } else {
                            if (commandArg.isEmpty() || suggestion.startsWith(commandArg.toLowerCase())) {
                                completions.add(suggestion);
                            }
                        }
                    }
                }
            }
        });

        if (completions.isEmpty()) {
            return Collections.emptyList();
        }

        return completions;
    }

    private void suggestKits(ConfigurationSection kits, String kitArg, List<String> completions) {
        for (String key : kits.getKeys(false)) {
            if (kitArg.isEmpty() || key.toLowerCase().startsWith(kitArg.toLowerCase())) {
                completions.add(key);
            }
        }
    }
}
