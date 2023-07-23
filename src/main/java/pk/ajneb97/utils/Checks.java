package pk.ajneb97.utils;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import pk.ajneb97.PlayerKits;

public class Checks {

    public static boolean checkTodo(PlayerKits plugin, CommandSender jugador) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();
        String prefix = messages.getString("prefix");
        String mensaje = prefix + config.getString("Messages.materialNameError");


        ConfigurationSection section = config.getConfigurationSection("Config.Inventory");
        //Check config.yml
        if (section != null) {
            for (String key : section.getKeys(false)) {
                //TODO: wtf does this check.
                if (!comprobarMaterial(config.getString("Config.Inventory." + key + ".id"), jugador, mensaje)) {
                    return false;
                }
            }
        }

        return true;
    }

    @SuppressWarnings({"deprecation", "unused"})
    public static boolean comprobarMaterial(String key, CommandSender jugador, String mensaje) {

        // TODO: this needs a better code and check.
        if (key.contains(":")) {
            String[] idsplit = key.split(":");
            String stringDataValue = idsplit[1];
            short DataValue = Short.parseShort(stringDataValue);
            Material mat = Material.getMaterial(idsplit[0].toUpperCase());
            ItemStack item = new ItemStack(mat, 1, (short) DataValue);
        } else {
            ItemStack item = new ItemStack(Material.getMaterial(key), 1);
        }

        //jugador.sendMessage(MessageUtils.getMensajeColor(mensaje.replace("%material%", key)));

        return true;
    }
}
