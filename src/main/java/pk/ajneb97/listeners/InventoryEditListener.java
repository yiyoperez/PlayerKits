package pk.ajneb97.listeners;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.inventory.InventoryEdit;
import pk.ajneb97.models.KitModification;
import pk.ajneb97.managers.KitManager;
import pk.ajneb97.utils.MessageHandler;
import pk.ajneb97.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class InventoryEditListener implements Listener {

    private final PlayerKits plugin;

    public InventoryEditListener(PlayerKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void clickInventarioItems(InventoryClickEvent event) {
        String pathInventory = ChatColor.translateAlternateColorCodes('&', "&9Editing Kit Items");
        String pathInventoryM = ChatColor.stripColor(pathInventory);

        if (ChatColor.stripColor(event.getView().getTitle()).equals(pathInventoryM)) {
            // Again?
            if (event.getSlotType() == null) {
                event.setCancelled(true);
                return;
            }

            final Player jugador = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            if (event.getClickedInventory() != null && event.getClickedInventory().equals(jugador.getOpenInventory().getTopInventory())) {
                final KitModification kitModification = plugin.getKitEditando();
                FileConfiguration kits = plugin.getKits();
                if (kitModification != null && kitModification.getPlayer().getName().equals(jugador.getName())) {
                    if (slot == 45) {
                        event.setCancelled(true);
                        InventoryEdit.crearInventario(jugador, kitModification.getKit(), plugin);
                    } else if (slot == 53) {
                        //Guardar items
                        event.setCancelled(true);
                        kits.set("Kits." + kitModification.getKit() + ".Items", null);
                        ItemStack[] contents = jugador.getOpenInventory().getTopInventory().getContents();
                        int c = 1;
                        FileConfiguration config = plugin.getConfig();
                        for (int i = 0; i < 44; i++) {
                            if (contents[i] != null && !contents[i].getType().equals(Material.AIR)) {
                                String path = "Kits." + kitModification.getKit() + ".Items." + c;
                                ItemStack contentsClone = contents[i].clone();
                                if (!Bukkit.getVersion().contains("1.8")) {
                                    ItemMeta meta = contentsClone.getItemMeta();
                                    List<String> lore = meta.getLore();
                                    if (lore != null && !lore.isEmpty()) {
                                        String ultimaLinea = ChatColor.stripColor(lore.get(lore.size() - 1));
                                        if (ultimaLinea.equals("[Right Click to remove from OFFHAND]")) {
                                            lore.remove(lore.size() - 1);
                                            lore.remove(lore.size() - 1);
                                            kits.set(path + ".offhand", true);
                                            if (lore.isEmpty()) {
                                                meta.setLore(null);
                                            } else {
                                                meta.setLore(lore);
                                            }
                                        }
                                        contentsClone.setItemMeta(meta);
                                    }
                                }

                                KitManager.saveItem(contentsClone, kits, path, config);
                                kits.set(path + ".preview_slot", i);
                                c++;
                            }
                        }
                        plugin.saveKits();
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aKit Items saved."));
                    } else if (slot >= 46 && slot <= 52) {
                        event.setCancelled(true);
                    } else if (event.getClick().equals(ClickType.RIGHT)) {
                        ItemStack item = event.getCurrentItem();
                        if (item != null && !item.getType().equals(Material.AIR)
                                && !Bukkit.getVersion().contains("1.8")) {
                            event.setCancelled(true);
                            ItemMeta meta = item.getItemMeta();
                            List<String> lore = meta.getLore();
                            if (lore != null) {
                                String ultimaLinea = ChatColor.stripColor(lore.get(lore.size() - 1));
                                if (ultimaLinea.equals("[Right Click to remove from OFFHAND]")) {
                                    lore.remove(lore.size() - 1);
                                    lore.remove(lore.size() - 1);
                                    if (lore.isEmpty()) {
                                        lore = null;
                                    }
                                } else {
                                    lore.add(ChatColor.translateAlternateColorCodes('&', " "));
                                    lore.add(ChatColor.translateAlternateColorCodes('&', "&8[&cRight Click to remove from OFFHAND&8]"));
                                }
                            } else {
                                lore = new ArrayList<>();
                                lore.add(ChatColor.translateAlternateColorCodes('&', " "));
                                lore.add(ChatColor.translateAlternateColorCodes('&', "&8[&cRight Click to remove from OFFHAND&8]"));
                            }
                            meta.setLore(lore);
                            item.setItemMeta(meta);
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void clickInventarioDisplayItem(InventoryClickEvent event) {
        String pathInventory = ChatColor.translateAlternateColorCodes('&', "&9Editing Display Item");
        String pathInventoryM = ChatColor.stripColor(pathInventory);

        if (ChatColor.stripColor(event.getView().getTitle()).equals(pathInventoryM)) {
            if (event.getCursor() == null) {
                event.setCancelled(true);
                return;
            }
            // Oh my.. whats this.
            if (event.getSlotType() == null) {
                event.setCancelled(true);
                return;
            }

            final Player jugador = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            if (event.getClickedInventory() != null && event.getClickedInventory().equals(jugador.getOpenInventory().getTopInventory())) {
                final KitModification kitModification = plugin.getKitEditando();
                FileConfiguration kits = plugin.getKits();
                if (slot != 11) {
                    event.setCancelled(true);
                    if (kitModification != null && kitModification.getPlayer().getName().equals(jugador.getName())) {
                        final String tipoDisplay = kitModification.getDisplayType();
                        if (slot == 18) {
                            guardarDisplayItem(event.getClickedInventory(), tipoDisplay, kits, kitModification.getKit());
                            InventoryEdit.crearInventario(jugador, kitModification.getKit(), plugin);
                        } else if (slot == 14) {
                            //set display name
                            jugador.closeInventory();
                            KitModification kit = new KitModification(jugador, kitModification.getKit(), tipoDisplay);
                            kit.setStep("display_name");
                            plugin.setKitEditando(kit);
                            jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aWrite the display name of the Kit."));
                            jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&7You can use color codes&8)"));
                        } else if (slot == 16) {
                            //set glowing
                            guardarDisplayItem(event.getClickedInventory(), tipoDisplay, kits, kitModification.getKit());
                            String path = "";
                            if (tipoDisplay.equals("normal")) {
                                path = "Kits." + kitModification.getKit() + ".display_item_glowing";
                                if (kits.contains("Kits." + kitModification.getKit() + ".display_item_glowing") && kits.getString("Kits." + kitModification.getKit() + ".display_item_glowing").equals("true")) {
                                    kits.set(path, false);
                                } else {
                                    kits.set(path, true);
                                }
                            } else {
                                path = "Kits." + kitModification.getKit() + "." + tipoDisplay + ".display_item_glowing";
                                if (kits.contains("Kits." + kitModification.getKit() + "." + tipoDisplay + ".display_item_glowing") && kits.getString("Kits." + kitModification.getKit() + "." + tipoDisplay + ".display_item_glowing").equals("true")) {
                                    kits.set(path, false);
                                } else {
                                    kits.set(path, true);
                                }
                            }

                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    InventoryEdit.crearInventarioDisplayItem(jugador, kitModification.getKit(), plugin, tipoDisplay);
                                }
                            }, 3L);
                        } else if (slot == 15) {
                            guardarDisplayItem(event.getClickedInventory(), tipoDisplay, kits, kitModification.getKit());
                            InventoryEdit.crearInventarioDisplayItemLore(jugador, kitModification.getKit(), plugin, tipoDisplay);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void clickInventarioDisplayLore(InventoryClickEvent event) {
        String pathInventory = ChatColor.translateAlternateColorCodes('&', "&9Editing Display Item Lore");
        String pathInventoryM = ChatColor.stripColor(pathInventory);

        if (ChatColor.stripColor(event.getView().getTitle()).equals(pathInventoryM)) {
            if (event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }
            // Im kinda giving up at this point.
            if (event.getSlotType() == null) {
                event.setCancelled(true);
                return;
            }
            final Player jugador = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            event.setCancelled(true);
            if (event.getClickedInventory().equals(jugador.getOpenInventory().getTopInventory())) {
                final KitModification kitModification = plugin.getKitEditando();
                FileConfiguration kits = plugin.getKits();
                if (kitModification != null && kitModification.getPlayer().getName().equals(jugador.getName())) {
                    final String tipoDisplay = kitModification.getDisplayType();
                    if (slot == 45) {
                        InventoryEdit.crearInventarioDisplayItem(jugador, kitModification.getKit(), plugin, tipoDisplay);
                    } else if (slot == 53) {
                        //Agregar lore
                        jugador.closeInventory();
                        KitModification kit = new KitModification(jugador, kitModification.getKit(), tipoDisplay);
                        kit.setStep("lore");
                        plugin.setKitEditando(kit);
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aWrite the lore line to add."));
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&7Write 'empty' to add an empty line&8)"));
                    } else if (slot >= 0 && slot <= 44 && event.getClick().equals(ClickType.RIGHT)) {
                        List<String> lore = new ArrayList<>();
                        if (tipoDisplay.equals("normal")) {
                            if (kits.contains("Kits." + kitModification.getKit() + ".display_lore")) {
                                lore = kits.getStringList("Kits." + kitModification.getKit() + ".display_lore");
                            }
                        } else {
                            if (kits.contains("Kits." + kitModification.getKit() + "." + tipoDisplay + ".display_lore")) {
                                lore = kits.getStringList("Kits." + kitModification.getKit() + "." + tipoDisplay + ".display_lore");
                            }
                        }
                        for (int i = 0; i < lore.size(); i++) {
                            if (i == slot) {
                                lore.remove(i);
                                break;
                            }
                        }
                        if (tipoDisplay.equals("normal")) {
                            kits.set("Kits." + kitModification.getKit() + ".display_lore", lore);
                        } else {
                            kits.set("Kits." + kitModification.getKit() + "." + tipoDisplay + ".display_lore", lore);
                        }

                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                InventoryEdit.crearInventarioDisplayItemLore(jugador, kitModification.getKit(), plugin, tipoDisplay);
                            }
                        }, 3L);
                    }
                }
            }
        }
    }

    @EventHandler
    public void clickInventarioComandos(InventoryClickEvent event) {
        String pathInventory = ChatColor.translateAlternateColorCodes('&', "&9Editing Kit Commands");
        String pathInventoryM = ChatColor.stripColor(pathInventory);

        if (ChatColor.stripColor(event.getView().getTitle()).equals(pathInventoryM)) {
            if (event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }
            // Don't even wanna keep comenting.
            if (event.getSlotType() == null) {
                event.setCancelled(true);
                return;
            }

            final Player jugador = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            event.setCancelled(true);
            if (event.getClickedInventory().equals(jugador.getOpenInventory().getTopInventory())) {
                final KitModification kitModification = plugin.getKitEditando();
                FileConfiguration kits = plugin.getKits();
                if (kitModification != null && kitModification.getPlayer().getName().equals(jugador.getName())) {
                    if (slot == 45) {
                        InventoryEdit.crearInventario(jugador, kitModification.getKit(), plugin);
                    } else if (slot == 53) {
                        //Agregar comando
                        jugador.closeInventory();
                        KitModification kit = new KitModification(jugador, kitModification.getKit(), "");
                        kit.setStep("comando");
                        plugin.setKitEditando(kit);
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aWrite the command to add."));
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&7This command will be executed from console&8)"));
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&7Player variable is: &e%player%&8)"));
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&7Write the command without the '/'&8)"));
                    } else if (slot >= 0 && slot <= 44 && event.getClick().equals(ClickType.RIGHT)) {
                        List<String> comandos = new ArrayList<>();
                        if (kits.contains("Kits." + kitModification.getKit() + ".Commands")) {
                            comandos = kits.getStringList("Kits." + kitModification.getKit() + ".Commands");
                        }
                        for (int i = 0; i < comandos.size(); i++) {
                            if (i == slot) {
                                comandos.remove(i);
                                break;
                            }
                        }
                        kits.set("Kits." + kitModification.getKit() + ".Commands", comandos);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                InventoryEdit.crearInventarioComandos(jugador, kitModification.getKit(), plugin);
                            }
                        }, 3L);
                    }
                }
            }
        }
    }

    @EventHandler
    public void cerrarInventarioDisplay(InventoryCloseEvent event) {
        String pathInventory = ChatColor.stripColor(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', "&9Editing Display Item")));
        Player jugador = (Player) event.getPlayer();
        KitModification kitModification = plugin.getKitEditando();
        if (kitModification != null && kitModification.getPlayer().getName().equals(jugador.getName())) {
            if (ChatColor.stripColor(event.getView().getTitle()).equals(pathInventory)) {
                guardarDisplayItem(event.getView().getTopInventory(), kitModification.getDisplayType(), plugin.getKits(), kitModification.getKit());
                plugin.removerKitEditando();
                plugin.saveKits();
            }
        }
    }

    //Guardar display item
    public static void guardarDisplayItem(Inventory inv, String tipoDisplay, FileConfiguration kits, String kit) {
        ItemStack item = inv.getItem(11);
        String path;
        if (tipoDisplay.equals("normal")) {
            path = "Kits." + kit;
        } else {
            path = "Kits." + kit + "." + tipoDisplay;
        }
        if (item == null) {
            kits.set(path + ".display_item", null);
            return;
        }

        Material id = item.getType();
        int datavalue = 0;
        if (Utils.isLegacy()) {
            if (id == Material.POTION) {
                datavalue = item.getDurability();
            } else {
                datavalue = item.getData().getData();
            }
        }

        if (datavalue != 0) {
            kits.set(path + ".display_item", item.getType() + ":" + datavalue);
        } else {
            kits.set(path + ".display_item", String.valueOf(item.getType()));
        }

        if (id.equals(Material.LEATHER_BOOTS) || id.equals(Material.LEATHER_CHESTPLATE)
                || id.equals(Material.LEATHER_HELMET) || id.equals(Material.LEATHER_LEGGINGS)) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            kits.set(path + ".display_item_leathercolor", String.valueOf(meta.getColor().asRGB()));
        }

        if (Utils.isNew()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasCustomModelData()) {
                kits.set(path + ".display_item_custom_model_data", meta.getCustomModelData());
            }
        }

        Utils.saveSkull(item, kits, path, true);
    }


    @EventHandler
    public void cerrarInventario(InventoryCloseEvent event) {
        String pathInventory = ChatColor.stripColor(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', "&9Editing Kit")));
        Player jugador = (Player) event.getPlayer();
        KitModification kitModification = plugin.getKitEditando();
        if (kitModification != null && kitModification.getPlayer().getName().equals(jugador.getName())) {
            if (ChatColor.stripColor(event.getView().getTitle()).startsWith(pathInventory)) {
                plugin.removerKitEditando();
                plugin.saveKits();
            }
        }
    }

    @EventHandler
    public void alSalir(PlayerQuitEvent event) {
        KitModification kit = plugin.getKitEditando();
        Player jugador = event.getPlayer();
        if (kit != null && kit.getPlayer().getName().equals(jugador.getName())) {
            plugin.removerKitEditando();
            plugin.saveKits();
        }
    }

    @EventHandler
    public void clickInventario(InventoryClickEvent event) {
        FileConfiguration config = plugin.getConfig();
        String pathInventory = ChatColor.translateAlternateColorCodes('&', "&9Editing Kit");
        String pathInventoryM = ChatColor.stripColor(pathInventory);

        if (ChatColor.stripColor(event.getView().getTitle()).equals(pathInventoryM)) {
            if (event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }
            // Todo: please check why those are here.
            if (event.getSlotType() == null) {
                event.setCancelled(true);
                return;
            }

            final Player jugador = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            event.setCancelled(true);
            if (event.getClickedInventory().equals(jugador.getOpenInventory().getTopInventory())) {
                final KitModification kitModification = plugin.getKitEditando();
                FileConfiguration kits = plugin.getKits();
                if (kitModification != null && kitModification.getPlayer().getName().equals(jugador.getName())) {
                    if (slot == 10) {
                        //set slot
                        jugador.closeInventory();
                        KitModification kit = new KitModification(jugador, kitModification.getKit(), "");
                        kit.setStep("slot");
                        plugin.setKitEditando(kit);
                        int max = config.getInt("inventory.size") - 1;
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aWrite the new slot of the Kit."));
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&7Use a number between 0 and " + max + "&8)"));
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&7Write 'none' to not show the kit&8)"));
                    } else if (slot == 11) {
                        //set cooldown
                        jugador.closeInventory();
                        KitModification kit = new KitModification(jugador, kitModification.getKit(), "");
                        kit.setStep("cooldown");
                        plugin.setKitEditando(kit);
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aWrite the cooldown of the Kit."));
                    } else if (slot == 12) {
                        //set permission
                        jugador.closeInventory();
                        KitModification kit = new KitModification(jugador, kitModification.getKit(), "");
                        kit.setStep("permission");
                        plugin.setKitEditando(kit);
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aWrite the permission of the Kit."));
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&7Write 'none' to not have a permission&8)"));
                    } else if (slot == 19) {
                        //set first join
                        if (kits.contains("Kits." + kitModification.getKit() + ".first_join") && kits.getString("Kits." + kitModification.getKit() + ".first_join").equals("true")) {
                            kits.set("Kits." + kitModification.getKit() + ".first_join", false);
                        } else {
                            kits.set("Kits." + kitModification.getKit() + ".first_join", true);
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                InventoryEdit.crearInventario(jugador, kitModification.getKit(), plugin);
                            }
                        }, 3L);
                    } else if (slot == 20) {
                        //set one time
                        if (kits.contains("Kits." + kitModification.getKit() + ".one_time") && kits.getString("Kits." + kitModification.getKit() + ".one_time").equals("true")) {
                            kits.set("Kits." + kitModification.getKit() + ".one_time", false);
                        } else {
                            kits.set("Kits." + kitModification.getKit() + ".one_time", true);
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                InventoryEdit.crearInventario(jugador, kitModification.getKit(), plugin);
                            }
                        }, 3L);
                    } else if (slot == 28) {
                        //set auto armor
                        if (kits.contains("Kits." + kitModification.getKit() + ".auto_armor") && kits.getString("Kits." + kitModification.getKit() + ".auto_armor").equals("true")) {
                            kits.set("Kits." + kitModification.getKit() + ".auto_armor", false);
                        } else {
                            kits.set("Kits." + kitModification.getKit() + ".auto_armor", true);
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                InventoryEdit.crearInventario(jugador, kitModification.getKit(), plugin);
                            }
                        }, 3L);
                    } else if (slot == 15) {
                        //set commands
                        InventoryEdit.crearInventarioComandos(jugador, kitModification.getKit(), plugin);
                    } else if (slot == 14) {
                        //set items
                        InventoryEdit.crearInventarioItems(jugador, kitModification.getKit(), plugin);
                    } else if (slot == 23) {
                        //set price
                        jugador.closeInventory();
                        KitModification kit = new KitModification(jugador, kitModification.getKit(), "");
                        kit.setStep("price");
                        plugin.setKitEditando(kit);
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aWrite the price of the Kit."));
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&7Write 'none' to not have a price&8)"));
                    } else if (slot == 16) {
                        //set page
                        jugador.closeInventory();
                        KitModification kit = new KitModification(jugador, kitModification.getKit(), "");
                        kit.setStep("page");
                        plugin.setKitEditando(kit);
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aWrite the new page of the Kit."));
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&7Use a number greater than 0&8)"));
                    } else if (slot == 21) {
                        //set one time buy
                        if (kits.contains("Kits." + kitModification.getKit() + ".one_time_buy") && kits.getString("Kits." + kitModification.getKit() + ".one_time_buy").equals("true")) {
                            kits.set("Kits." + kitModification.getKit() + ".one_time_buy", false);
                        } else {
                            kits.set("Kits." + kitModification.getKit() + ".one_time_buy", true);
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                InventoryEdit.crearInventario(jugador, kitModification.getKit(), plugin);
                            }
                        }, 3L);
                    } else if (slot == 40) {
                        InventoryEdit.crearInventarioDisplayItem(jugador, kitModification.getKit(), plugin, "noPermissionsItem");
                    } else if (slot == 39) {
                        InventoryEdit.crearInventarioDisplayItem(jugador, kitModification.getKit(), plugin, "normal");
                    } else if (slot == 41) {
                        InventoryEdit.crearInventarioDisplayItem(jugador, kitModification.getKit(), plugin, "noBuyItem");
                    }

                }

            }
        }
    }

    @EventHandler
    public void capturarChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final KitModification kit = plugin.getKitEditando();

        if (kit == null || !kit.getPlayer().getName().equals(player.getName())) {
            return;
        }

        FileConfiguration kits = plugin.getKits();
        FileConfiguration config = plugin.getConfig();

        String message = ChatColor.stripColor(event.getMessage());
        MessageHandler messageHandler = plugin.getMessageHandler();

        event.setCancelled(true);

        //TODO: Reduce if statements.

        String step = kit.getStep();
        switch (step) {
            case "slot":

                if (message.equalsIgnoreCase("none")) {
                    messageHandler.sendManualMessage(player, "&aSlot defined to: &e %s", message);
                    kits.set("Kits." + kit.getKit() + ".slot", null);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                            InventoryEdit.crearInventario(player, kit.getKit(), plugin), 3L);
                    return;
                }

                int max = config.getInt("inventory.size") - 1;
                try {
                    int num = Integer.parseInt(message);
                    if (num < 0 || num > max) {
                        messageHandler.sendManualMessage(player, "Slot must from 0 to " + max + " or write 'none'");
                        return;
                    }

                    messageHandler.sendManualMessage(player, "&aSlot defined to: &e %s", num);
                    kits.set("Kits." + kit.getKit() + ".slot", num);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> InventoryEdit.crearInventario(player, kit.getKit(), plugin), 3L);
                } catch (NumberFormatException e) {
                    messageHandler.sendManualMessage(player, "&cUse a valid number or write 'none'.");
                }
                break;
            case "page":
                try {
                    int num = Integer.parseInt(message);
                    if (num >= 1) {
                        messageHandler.sendManualMessage(player, "&aPage defined to: &e %s", num);
                        kits.set("Kits." + kit.getKit() + ".page", num);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> InventoryEdit.crearInventario(player, kit.getKit(), plugin), 3L);
                    } else {
                        messageHandler.sendManualMessage(player, "&cUse a valid number.");
                    }
                } catch (NumberFormatException e) {
                    messageHandler.sendManualMessage(player, "&cUse a valid number.");
                }
                break;
            case "cooldown":
                try {
                    int num = Integer.parseInt(message);
                    if (num >= 0) {
                        messageHandler.sendManualMessage(player, "&aCooldown defined to: &e%s", num);
                        kits.set("Kits." + kit.getKit() + ".cooldown", num);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> InventoryEdit.crearInventario(player, kit.getKit(), plugin), 3L);
                    } else {
                        messageHandler.sendManualMessage(player, "&cUse a valid number.");
                    }
                } catch (NumberFormatException e) {
                    messageHandler.sendManualMessage(player, "&cUse a valid number.");
                }
                break;
            case "permission":
                if (message.equalsIgnoreCase("none")) {
                    kits.set("Kits." + kit.getKit() + ".permission", null);
                } else {
                    kits.set("Kits." + kit.getKit() + ".permission", message);
                }
                messageHandler.sendManualMessage(player, "&aPermission defined to: &e%s", message);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> InventoryEdit.crearInventario(player, kit.getKit(), plugin), 3L);
                break;
            case "comando":
                List<String> comandos = new ArrayList<>();
                if (kits.contains("Kits." + kit.getKit() + ".Commands")) {
                    comandos = kits.getStringList("Kits." + kit.getKit() + ".Commands");
                }
                comandos.add(message);
                kits.set("Kits." + kit.getKit() + ".Commands", comandos);
                messageHandler.sendManualMessage(player, "&aCommand added: &e%s", message);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> InventoryEdit.crearInventarioComandos(player, kit.getKit(), plugin), 3L);
                break;
            case "price":
                if (message.equalsIgnoreCase("none")) {
                    kits.set("Kits." + kit.getKit() + ".price", null);
                    messageHandler.sendManualMessage(player, "&aPrice defined to: &e%s", message);

                } else {
                    double price = 0D;
                    try {
                        price = Double.parseDouble(message);
                    } catch (NumberFormatException e) {
                        messageHandler.sendManualMessage(player, "El valor del kit solo puede ser de tipo doble (0.0) o \"none\"");
                        return;
                    }

                    kits.set("Kits." + kit.getKit() + ".price", price);
                    messageHandler.sendManualMessage(player, "&aPrice defined to: &e%s", price);
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> InventoryEdit.crearInventario(player, kit.getKit(), plugin), 3L);
                break;
            case "display_name": {
                final String tipoDisplay = kit.getDisplayType();
                if (tipoDisplay.equals("normal")) {
                    kits.set("Kits." + kit.getKit() + ".display_name", message);
                } else {
                    kits.set("Kits." + kit.getKit() + "." + tipoDisplay + ".display_name", message);
                }
                messageHandler.sendManualMessage(player, "&aDisplay Name defined to: &e%s", message);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> InventoryEdit.crearInventarioDisplayItem(player, kit.getKit(), plugin, tipoDisplay), 3L);
                break;
            }
            case "lore": {
                List<String> lore = new ArrayList<>();
                final String tipoDisplay = kit.getDisplayType();
                if (tipoDisplay.equals("normal")) {
                    if (kits.contains("Kits." + kit.getKit() + ".display_lore")) {
                        lore = kits.getStringList("Kits." + kit.getKit() + ".display_lore");
                    }
                } else {
                    if (kits.contains("Kits." + kit.getKit() + "." + tipoDisplay + ".display_lore")) {
                        lore = kits.getStringList("Kits." + kit.getKit() + "." + tipoDisplay + ".display_lore");
                    }
                }
                if (message.equals("empty")) {
                    lore.add("");
                } else {
                    lore.add(message);
                }
                if (tipoDisplay.equals("normal")) {
                    kits.set("Kits." + kit.getKit() + ".display_lore", lore);
                } else {
                    kits.set("Kits." + kit.getKit() + "." + tipoDisplay + ".display_lore", lore);
                }

                messageHandler.sendManualMessage(player, "&aLore line added: &e%s", message);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> InventoryEdit.crearInventarioDisplayItemLore(player, kit.getKit(), plugin, tipoDisplay), 3L);
                break;
            }
        }
    }
}
