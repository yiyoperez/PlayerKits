package pk.ajneb97.utils;

import java.util.logging.Logger;

public class PluginLogger {

    private static final Logger logger = org.bukkit.plugin.PluginLogger.getLogger("PlayerKits");

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void warn(String msg) {
        logger.warning(msg);
    }

    public static void error(String msg) {
        logger.severe(msg);
    }

    public static void fine(String msg) {
        logger.fine(msg);
    }
}