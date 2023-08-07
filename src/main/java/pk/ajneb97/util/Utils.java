package pk.ajneb97.util;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            stack = Utils.setSkull(stack, skulldata);
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
        //item = Utils.setUnbreakable(item);
        if (kits.contains(path + ".display_item_skulldata")) {
            String skulldata = kits.getString(path + ".display_item_skulldata");
            item = Utils.setSkull(item, skulldata);
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
        if (config.contains("inventory.items")) {
            for (String key : config.getConfigurationSection("inventory.items").getKeys(false)) {
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

    // Saving an skull.
    public static void saveSkull(ItemStack item, FileConfiguration config, String path, boolean isDisplay) {
        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasNBTData()) return;

        NBTCompound skullOwnerCompound = nbtItem.getCompound("SkullOwner");
        if (skullOwnerCompound == null) return;

        String value = skullOwnerCompound
                .getOrCreateCompound("Properties")
                .getCompoundList("textures")
                .get(0)
                .getString("Value");

        config.set(path + (isDisplay ? ".display_item_skulldata" : ".skull-texture"), value);
    }

    public static ItemStack setSkull(ItemStack item, String texture) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.getOrCreateCompound("SkullOwner");

        compound.setUUID("Id", UUID.randomUUID());
        compound.getOrCreateCompound("Properties")
                .getCompoundList("textures")
                .addCompound()
                .setString("Value", texture);

        return nbtItem.getItem();
    }

    public static ItemStack setSkull(ItemStack item, String path, FileConfiguration config) {
        String texturePath = path + ".skull-texture";

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.getOrCreateCompound("SkullOwner");

        boolean hasTexture = config.contains(texturePath);

        compound.setUUID("Id", UUID.randomUUID());

        if (hasTexture) {
            compound.getOrCreateCompound("Properties")
                    .getCompoundList("textures")
                    .addCompound()
                    .setString("Value", config.getString(texturePath));
        }

        return nbtItem.getItem();
    }

    public static ItemStack setAttributes(ItemStack item, FileConfiguration config, String key) {
        // set attributes to item.
        return null;
    }

    public static void saveAttributes(ItemStack item, FileConfiguration config, String path) {
        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasNBTData()) return;

        Bukkit.getPluginManager().getPlugin("PlayerKits").getLogger().info("save attributes" + nbtItem.asNBTString());

//        if (!nbtItem.hasTag("AttributeModifiers")) return;
//
//
//        NBTCompoundList compound = nbtItem.getCompoundList("Attributes");
//        if (compound.isEmpty()) return;
//
//        List<String> savedAttributes = new ArrayList<>();
//        for (ReadWriteNBT nbt : compound) {
//            String attributeName = nbt.getString("AttributeName");
//            String name = nbt.getString("Name");
//            double amount = nbt.getDouble("Amount");
//            int operation = nbt.getInteger("Operation");
//            int uuidLeast = nbt.getInteger("UUIDLeast");
//            int uuidMost = nbt.getInteger("UUIDMost");
//            String slot = nbt.getString("Slot");
//
//            savedAttributes.add(attributeName + ";" + name + ";" + amount + ";" + operation + ";" + uuidLeast + ";" + uuidMost + ";" + slot);
//        }
//
//        config.set(path + ".attributes", savedAttributes);
    }

    public static void saveNBT(ItemStack item, FileConfiguration config, String path) {
        NBTContainer nbtContainer = NBTItem.convertItemtoNBT(item);

        Bukkit.getPluginManager().getPlugin("PlayerKits").getLogger().info("save nbt" + nbtContainer.asNBTString());
        //config.set(path + ".nbt", nbtContainer.asNBTString());
    }

    public static ItemStack setNBT(ItemStack item, FileConfiguration config, String key) {
//        NBTItem nbtItem = new NBTItem(item);
//        NBTContainer nbtContainer = new NBTContainer(config.getString(key + ".nbt"));
//        nbtItem.applyNBT(NBTItem.convertNBTtoItem(nbtContainer));
//
//        return nbtItem.getItem();
        return null;
    }

    public static ItemStack setUnbreakable(ItemStack item) {
        ItemStack itemStack = item.clone();
        ItemMeta meta = itemStack.getItemMeta();
        // TODO: NPE in 1.8
        // It doesn't exists in 1.8 ?!?!
        meta.setUnbreakable(true);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public static boolean getUnbreakable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return (meta != null && meta.isUnbreakable());
    }
}
