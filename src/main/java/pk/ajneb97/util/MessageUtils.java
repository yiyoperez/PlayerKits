package pk.ajneb97.util;

import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    public static String translateColor(String texto) {
        if (Utils.isNew()) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher match = pattern.matcher(texto);

            while (match.find()) {
                String color = texto.substring(match.start(), match.end());
                texto = texto.replace(color, String.valueOf(ChatColor.of(color)));

                match = pattern.matcher(texto);
            }
        }

        texto = ChatColor.translateAlternateColorCodes('&', texto);

        return texto;
    }

    public static List<String> translateColor(String... strings) {
        return translateColor(Arrays.asList(strings));
    }

    public static List<String> translateColor(List<String> list) {
        list.replaceAll(MessageUtils::translateColor);
        return list;
    }
}
