package pk.ajneb97.utils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import pk.ajneb97.managers.JugadorManager;

import java.util.ArrayList;
import java.util.List;

public class Utils {


    public static boolean isLegacy() {
        return !Bukkit.getVersion().contains("1.13") && !Bukkit.getVersion().contains("1.14") &&
                !Bukkit.getVersion().contains("1.15") && !Bukkit.getVersion().contains("1.16")
                && !Bukkit.getVersion().contains("1.17") && !Bukkit.getVersion().contains("1.18")
                && !Bukkit.getVersion().contains("1.19") && !Bukkit.getVersion().contains("1.20");
    }

    public static boolean isNew() {
        return Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")
                || Bukkit.getVersion().contains("1.19") || Bukkit.getVersion().contains("1.20");
    }

    public static String getCooldown(String kit, Player jugador, FileConfiguration kitConfig, FileConfiguration config, JugadorManager jManager) {
        //1000millis claimea un kit de 5 segundos
        //6000millis puede claimearlo otra vez (timecooldown)

        long timecooldown = jManager.getCooldown(jugador, kit);

        long millis = System.currentTimeMillis();
        if (!kitConfig.contains("Kits." + kit + ".cooldown")) {
            return "no_existe";
        }
        long cooldown = kitConfig.getLong("Kits." + kit + ".cooldown");
        long cooldownmil = cooldown * 1000;

        long espera = millis - timecooldown;
        long esperaDiv = espera / 1000;
        long esperatotalseg = cooldown - esperaDiv;
        long esperatotalmin = esperatotalseg / 60;
        long esperatotalhour = esperatotalmin / 60;
        long esperatotalday = esperatotalhour / 24;
        if (((timecooldown + cooldownmil) > millis) && (timecooldown != 0)) {
            if (esperatotalseg > 59) {
                esperatotalseg = esperatotalseg - 60 * esperatotalmin;
            }
            String time = esperatotalseg + config.getString("Messages.seconds");
            if (esperatotalmin > 59) {
                esperatotalmin = esperatotalmin - 60 * esperatotalhour;
            }
            if (esperatotalmin > 0) {
                time = esperatotalmin + config.getString("Messages.minutes") + " " + time;
            }
            if (esperatotalhour > 24) {
                esperatotalhour = esperatotalhour - 24 * esperatotalday;
            }
            if (esperatotalhour > 0) {
                time = esperatotalhour + config.getString("Messages.hours") + " " + time;
            }
            if (esperatotalday > 0) {
                time = esperatotalday + config.getString("Messages.days") + " " + time;
            }

            return time;
        } else {
            return "ready";
        }
    }

    @SuppressWarnings("deprecation")
    public static ItemStack getItem(String id, int amount, String skulldata) {
        String[] idsplit = new String[2];
        int DataValue = 0;

        ItemStack stack = null;

        if (id.contains(":")) {
            idsplit = id.split(":");
            String stringDataValue = idsplit[1];
            DataValue = Integer.parseInt(stringDataValue);
            Material mat = Material.getMaterial(idsplit[0].toUpperCase());
            stack = new ItemStack(mat, amount, (short) DataValue);
        } else {
            Material mat = Material.getMaterial(id.toUpperCase());
            stack = new ItemStack(mat, amount);
        }
        if (!skulldata.isEmpty()) {
            String[] sep = skulldata.split(";");
            stack = Utils.setSkull(stack, sep[0], sep[1]);
        }


        return stack;
    }

    public static ItemStack getDisplayItem(FileConfiguration kits, String path) {
        ItemStack item = getItem(kits.getString(path + ".display_item"), 1, "");
        ItemMeta meta = item.getItemMeta();
        if (kits.contains(path + ".display_name")) {
            meta.setDisplayName(MessageUtils.getMensajeColor(kits.getString(path + ".display_name")));
        }
        if (kits.contains(path + ".display_lore")) {
            List<String> lore = kits.getStringList(path + ".display_lore");
            lore.replaceAll(MessageUtils::getMensajeColor);
            meta.setLore(lore);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        if (kits.contains(path + ".display_item_leathercolor")) {
            LeatherArmorMeta meta2 = (LeatherArmorMeta) meta;
            int color = kits.getInt(path + ".display_item_leathercolor");
            meta2.setColor(Color.fromRGB(color));
            item.setItemMeta(meta2);
        }
        if (kits.contains(path + ".display_item_custom_model_data")) {
            int customModelData = kits.getInt(path + ".display_item_custom_model_data");
            meta = item.getItemMeta();
            meta.setCustomModelData(customModelData);
            item.setItemMeta(meta);
        }
        item = Utils.setUnbreakable(item);
        if (kits.contains(path + ".display_item_skulldata")) {
            String[] skulldata = kits.getString(path + ".display_item_skulldata").split(";");
            item = Utils.setSkull(item, skulldata[0], skulldata[1]);
        }

        return item;
    }

    public static int getSlotDisponible(FileConfiguration kitConfig, FileConfiguration config) {
        ArrayList<Integer> slotsOcupados = new ArrayList<>();
        if (kitConfig.contains("Kits")) {
            for (String path : kitConfig.getConfigurationSection("Kits").getKeys(false)) {
                if (kitConfig.contains("Kits." + path + ".slot")) {
                    int slotOcupado = kitConfig.getInt("Kits." + path + ".slot");
                    slotsOcupados.add(slotOcupado);
                }
            }
        }
        if (config.contains("Inventory")) {
            for (String key : config.getConfigurationSection("Inventory").getKeys(false)) {
                int slotOcupado = Integer.parseInt(key);
                slotsOcupados.add(slotOcupado);
            }
        }

        int slotsMaximos = config.getInt("inventory.size");
        for (int i = 0; i < slotsMaximos; i++) {
            if (!slotsOcupados.contains(i)) {
                return i;
            }
        }
        return -1;
    }

    public static DyeColor getBannerColor(String mainColor) {
        String fixed = mainColor.replace("_BANNER", "");
        return DyeColor.valueOf(fixed);
    }

    // TODO: Fix NMS stuff.
    // This will be a pain.

    public static void guardarSkullDisplay(ItemStack item, FileConfiguration config, String path) {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        //save skull display
    }

    public static void guardarSkull(ItemStack item, FileConfiguration config, String path, String nombreJugador) {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        // save skull
    }

    public static void guardarAttributes(ItemStack item, FileConfiguration config, String path) {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        // save attributes
    }

    public static void guardarNBT(ItemStack item, FileConfiguration config, String path) {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        // save item nbt
    }

    public static ItemStack setUnbreakable(ItemStack item) {
        ItemStack itemStack = item.clone();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setUnbreakable(true);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public static boolean getUnbreakable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return (meta != null && meta.isUnbreakable());
    }

    public static ItemStack setSkull(ItemStack crafteos, String path, FileConfiguration config) {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        // set item to skull
        return null;
    }

    public static ItemStack setNBT(ItemStack item, FileConfiguration config, String key) {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        // set nbt to item.
        return null;
    }

    public static ItemStack setAttributes(ItemStack item, FileConfiguration config, String key) {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        // set attributes to item.
        return null;
    }

    public static ItemStack setSkull(ItemStack item, String id, String textura) {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        // set skull with no id
        return null;
    }
}
