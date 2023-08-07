package pk.ajneb97;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pk.ajneb97.api.ExpansionPlayerKits;
import pk.ajneb97.api.PlayerKitsAPI;
import pk.ajneb97.commands.MainCommand;
import pk.ajneb97.inventory.KitEditando;
import pk.ajneb97.inventory.CurrentPlayerInventory;
import pk.ajneb97.listeners.InventarioConfirmacionDinero;
import pk.ajneb97.listeners.InventarioEditar;
import pk.ajneb97.listeners.InventarioListener;
import pk.ajneb97.listeners.InventoryPreview;
import pk.ajneb97.listeners.PlayerListeners;
import pk.ajneb97.managers.KitManager;
import pk.ajneb97.managers.PlayerManager;
import pk.ajneb97.tasks.PlayerDataSaveTask;
import pk.ajneb97.utils.MessageHandler;
import pk.ajneb97.utils.PluginLogger;
import pk.ajneb97.utils.TimeUtils;
import pk.ajneb97.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public final class PlayerKits extends JavaPlugin {

    private File kitsFile;
    private File configFile;
    private File playersFile;
    private File messagesFile;
    private FileConfiguration kits;
    private FileConfiguration players;
    private FileConfiguration messages;

    private KitEditando kitEditando;
    RegisteredServiceProvider<Economy> rsp;
    private static Economy economy = null;
    private final ArrayList<CurrentPlayerInventory> playerInventories = new ArrayList<>();

    private KitManager kitManager;
    private PlayerManager playerManager;

    private MessageHandler messageHandler;

    public static final String pluginPrefix = ChatColor.translateAlternateColorCodes('&', "&8[&4PlayerKits&8] ");

    private PlayerDataSaveTask playerDataSaveTask;

    public void onEnable() {
        registerKits();
        registerConfig();
        registerMessages();
        registerPlayers();
        new TimeUtils().setStrings(getMessages());
        this.messageHandler = new MessageHandler(this);

        registerEvents();
        registerCommands();

        setupEconomy();
        populateConfigIfEmpty();

        this.kitManager = new KitManager(this);
        this.playerManager = new PlayerManager(this);

        PlayerKitsAPI api = new PlayerKitsAPI(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ExpansionPlayerKits(this).register();
        }

        reloadPlayerDataSaveTask();
        Bukkit.getConsoleSender().sendMessage(pluginPrefix + ChatColor.YELLOW + "Has been enabled! " + ChatColor.WHITE + "Version: " + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(pluginPrefix + ChatColor.YELLOW + "Thanks for using my plugin!  " + ChatColor.WHITE + "~Ajneb97");
    }

    public void onDisable() {
        playerManager.saveServerPlayerData();
        if (kits != null) {
            saveKits();
        }
        Bukkit.getConsoleSender().sendMessage(pluginPrefix + ChatColor.YELLOW + "Has been disabled! " + ChatColor.WHITE + "Version: " + getDescription().getVersion());
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void registerCommands() {
        this.getCommand("kit").setExecutor(new MainCommand(this));
    }

    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventarioListener(this), this);
        pm.registerEvents(new InventoryPreview(this), this);
        pm.registerEvents(new PlayerListeners(this), this);
        pm.registerEvents(new InventarioEditar(this), this);
        pm.registerEvents(new InventarioConfirmacionDinero(this), this);
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public void agregarInventarioJugador(CurrentPlayerInventory inv) {
        this.playerInventories.add(inv);
    }

    public CurrentPlayerInventory getInventarioJugador(String jugador) {
        for (CurrentPlayerInventory inv : playerInventories) {
            if (inv.getJugador().getName().equals(jugador)) {
                return inv;
            }
        }
        return null;
    }

    public void removerInventarioJugador(String jugador) {
        for (int i = 0; i < playerInventories.size(); i++) {
            if (playerInventories.get(i).getJugador().getName().equals(jugador)) {
                playerInventories.remove(i);
            }
        }
    }

    public void reloadPlayerDataSaveTask() {
        if (playerDataSaveTask != null) {
            playerDataSaveTask.end();
        }
        playerDataSaveTask = new PlayerDataSaveTask(this);
        playerDataSaveTask.start();
    }

    public void registerConfig() {
        this.configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }

    public void reloadMessages() {
        this.messagesFile = new File(this.getDataFolder(), "messages.yml");
        this.messages = YamlConfiguration.loadConfiguration(this.messagesFile);

        Reader defConfigStream = new InputStreamReader(this.getResource("messages.yml"), StandardCharsets.UTF_8);
        this.messages.setDefaults(YamlConfiguration.loadConfiguration(defConfigStream));
    }

    public void registerMessages() {
        this.messagesFile = new File(this.getDataFolder(), "messages.yml");
        if (!this.messagesFile.exists()) {
            this.getMessages().options().copyDefaults(true);
            saveMessages();
        }
    }

    public void saveMessages() {
        try {
            this.messages.save(this.messagesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileConfiguration getMessages() {
        if (this.messages == null) {
            reloadMessages();
        }
        return this.messages;
    }

    public void reloadKits() {
        if (this.kits == null) {
            this.kitsFile = new File(getDataFolder(), "kits.yml");
        }
        this.kits = YamlConfiguration.loadConfiguration(this.kitsFile);

        Reader defConfigStream = new InputStreamReader(this.getResource("kits.yml"), StandardCharsets.UTF_8);
        this.kits.setDefaults(YamlConfiguration.loadConfiguration(defConfigStream));
    }

    public void registerKits() {
        this.kitsFile = new File(this.getDataFolder(), "kits.yml");
        if (!this.kitsFile.exists()) {
            this.getKits().options().copyDefaults(true);
            saveKits();
        }
    }

    public void saveKits() {
        try {
            this.kits.save(this.kitsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getKits() {
        if (this.kits == null) {
            reloadKits();
        }
        return this.kits;
    }

    public void reloadPlayers() {
        if (this.players == null) {
            this.playersFile = new File(getDataFolder(), "players.yml");
        }
        this.players = YamlConfiguration.loadConfiguration(playersFile);

        Reader defConfigStream = new InputStreamReader(this.getResource("players.yml"), StandardCharsets.UTF_8);
        this.players.setDefaults(YamlConfiguration.loadConfiguration(defConfigStream));
    }

    public void registerPlayers() {
        this.playersFile = new File(this.getDataFolder(), "players.yml");
        if (!this.playersFile.exists()) {
            this.getPlayers().options().copyDefaults(true);
            savePlayers();
        }
    }

    public void savePlayers() {
        try {
            this.players.save(this.playersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getPlayers() {
        if (this.players == null) {
            reloadPlayers();
        }
        return this.players;
    }

    public void setKitEditando(KitEditando p) {
        this.kitEditando = p;
    }

    public void removerKitEditando() {
        this.kitEditando = null;
    }

    public KitEditando getKitEditando() {
        return this.kitEditando;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            PluginLogger.warn("Could NOT setup economy hook.");
            PluginLogger.warn("Vault was not found!");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            PluginLogger.warn("Not found any Registered Economy Service.");
            return false;
        }

        economy = rsp.getProvider();

        if (economy != null) {
            PluginLogger.info(economy.getName() + " has been set as economy service provider.");
        }

        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }


    public void populateConfigIfEmpty() {
        FileConfiguration config = getConfig();

        if (config.getConfigurationSection("sounds").getKeys(false).isEmpty()) {
            config.set("sounds.page_sound", Utils.isLegacy() ? "LAVA_POP;10;1" : "BLOCK_LAVA_POP;10;1");
            config.set("sounds.claim_sound", Utils.isLegacy() ? "NOTE_PLING;10;0.1" : "BLOCK_NOTE_BLOCK_PLING;10;0.1");
            config.set("sounds.error_sound", Utils.isLegacy() ? "LEVEL_UP;10;1.5" : "ENTITY_PLAYER_LEVELUP;10;1.5");
        }

        if (!config.getConfigurationSection("inventory.items").getKeys(false).isEmpty()) return;

        config.set("inventory.items.0.id", Utils.isLegacy() ? "STAINED_GLASS_PANE:15" : "BLACK_STAINED_GLASS_PANE");
        config.set("inventory.items.8.id", Utils.isLegacy() ? "STAINED_GLASS_PANE:15" : "BLACK_STAINED_GLASS_PANE");
        config.set("inventory.items.36.id", Utils.isLegacy() ? "STAINED_GLASS_PANE:15" : "BLACK_STAINED_GLASS_PANE");
        config.set("inventory.items.44.id", Utils.isLegacy() ? "STAINED_GLASS_PANE:15" : "BLACK_STAINED_GLASS_PANE");
        config.set("inventory.items.18.id", Utils.isLegacy() ? "SKULL_ITEM:3" : "PLAYER_HEAD");
        config.set("inventory.items.26.id", Utils.isLegacy() ? "SKULL_ITEM:3" : "PLAYER_HEAD");

        config.set("inventory.items.0.name", " ");
        config.set("inventory.items.8.name", " ");
        config.set("inventory.items.36.name", " ");
        config.set("inventory.items.44.name", " ");
        config.set("inventory.items.18.name", "&6Previous Page");
        config.set("inventory.items.18.skulldata", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==");
        config.set("inventory.items.18.type", "previous_page");
        config.set("inventory.items.26.name", "&6Next Page");
        config.set("inventory.items.26.skulldata", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19");
        config.set("inventory.items.26.type", "next_page");

        this.saveConfig();
    }
}
