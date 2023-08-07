package pk.ajneb97.util;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pk.ajneb97.PlayerKits;

public class Checks {


    public static boolean mainInventoryContainsBadItems(PlayerKits plugin, Player player) {
        boolean badItems = false;

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("inventory.items");

        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (Checks.containsBadItems(plugin, player, section.getString(key + ".id"), key)) {
                    badItems = true;
                    break;
                }
            }
        }

        return badItems;
    }

    public static boolean containsBadItems(PlayerKits plugin, CommandSender player, String id, String key) {

        if (!materialExists(id)) {
            FileConfiguration messages = plugin.getMessages();
            String prefix = messages.getString("prefix");
            String message = prefix + messages.getString("materialNameError");
            player.sendMessage(MessageUtils.translateColor(message.replace("%material%", id).replace("%key%", key)));
            return true;
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    public static boolean materialExists(String key) {
        String materialID;
        short dataValue = 0;
        if (key.contains(":")) {
            String[] split = key.split(":");
            if (split.length == 1) {
                return false;
            }
            materialID = split[0];
            dataValue = Short.parseShort(split[1]);
        } else {
            materialID = key;
        }

        Material material = Material.matchMaterial(materialID);
        if (material == null) return false;

        try {
            new ItemStack(material, 1, dataValue);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
