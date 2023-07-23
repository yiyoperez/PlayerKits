package pk.ajneb97.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    public static String getMensajeColor(String texto) {
        if (Utils.isNew()) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher match = pattern.matcher(texto);

            while (match.find()) {
                String color = texto.substring(match.start(), match.end());
                texto = texto.replace(color, ChatColor.of(color) + "");

                match = pattern.matcher(texto);
            }
        }

        texto = ChatColor.translateAlternateColorCodes('&', texto);

        return texto;
    }
}
