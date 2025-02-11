package pk.ajneb97.manager;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import pk.ajneb97.PlayerKits;
import pk.ajneb97.inventory.PurchaseConfirmationMenu;
import pk.ajneb97.model.CurrentPlayerInventory;
import pk.ajneb97.model.PlayerData;
import pk.ajneb97.model.PlayerKit;
import pk.ajneb97.util.Cooldown;
import pk.ajneb97.util.MessageHandler;
import pk.ajneb97.util.MessageUtils;
import pk.ajneb97.util.Placeholder;
import pk.ajneb97.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KitManager {

    private final PlayerKits plugin;

    public KitManager(PlayerKits plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    public static void saveItem(ItemStack item, FileConfiguration kitConfig, String path, FileConfiguration config) {
//		if(config.getString("Config.item_serializer").equals("true")) {
//			String serializedItem = ItemSerializer.serialize(item);
//			if(serializedItem != null) {
//				kitConfig.set(path+".full_data", serializedItem);
//			}
//		}

        Material id = item.getType();
        int datavalue = 0;
        int amount = item.getAmount();
        String idtext = String.valueOf(id);
        if (Utils.isLegacy()) {
            if (id == Material.POTION) {
                datavalue = item.getDurability();
            } else {
                datavalue = item.getData().getData();
            }
        }
        if (id.name().contains("POTION") || id.name().equals("TIPPED_ARROW")) {
            if (item.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                if (meta.hasCustomEffects()) {
                    List<PotionEffect> efectos = meta.getCustomEffects();
                    List<String> lista = new ArrayList<>();
                    for (PotionEffect efecto : efectos) {
                        String tipo = efecto.getType().getName();
                        int amplifier = efecto.getAmplifier();
                        int duracion = efecto.getDuration();
                        lista.add(tipo + ";" + amplifier + ";" + duracion);
                    }
                    kitConfig.set(path + ".potion-effects", lista);
                }
                if (!Bukkit.getVersion().contains("1.8")) {
                    if (!Bukkit.getVersion().contains("1.9")) {
                        if (meta.hasColor()) {
                            kitConfig.set(path + ".potion-color", String.valueOf(meta.getColor().asRGB()));
                        }
                    }
                    PotionData data = meta.getBasePotionData();
                    kitConfig.set(path + ".potion-type", String.valueOf(data.getType()));
                    kitConfig.set(path + ".potion-upgraded", String.valueOf(data.isUpgraded()));
                    kitConfig.set(path + ".potion-extended", String.valueOf(data.isExtended()));
                }
            }
        }
        if (Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
            if (id == Material.valueOf("MONSTER_EGG")) {
                SpawnEggMeta eggMeta = (SpawnEggMeta) item.getItemMeta();
                datavalue = eggMeta.getSpawnedType().getTypeId();
            }
        }

        if (id == Material.LEATHER_BOOTS || id == Material.LEATHER_HELMET || id == Material.LEATHER_LEGGINGS || id == Material.LEATHER_CHESTPLATE) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            int color = meta.getColor().asRGB();
            kitConfig.set(path + ".color", color);
        }

        if (id == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            Map<Enchantment, Integer> enchants = meta.getStoredEnchants();
            List<String> enchantsList = new ArrayList<>();
            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                enchantsList.add(entry.getKey().getName() + ";" + entry.getValue());
            }
            kitConfig.set(path + ".book-enchants", enchantsList);
        }

        if (id == Material.WRITTEN_BOOK) {
            BookMeta meta = (BookMeta) item.getItemMeta();
            String author = meta.getAuthor();
            String title = meta.getTitle();
            String generation = null;
            List<String> pages = new ArrayList<>();

            if (!Bukkit.getVersion().contains("1.12") && Utils.isLegacy()) {
                pages = new ArrayList<>(meta.getPages());
            } else {
                if (meta.getGeneration() != null) {
                    generation = meta.getGeneration().name();
                }
                for (BaseComponent[] page : meta.spigot().getPages()) {
                    pages.add(ComponentSerializer.toString(page));
                }
            }

            kitConfig.set(path + ".book-generation", generation);
            kitConfig.set(path + ".book-author", author);
            kitConfig.set(path + ".book-title", title);
            kitConfig.set(path + ".book-pages", pages);
        }

        if (id.name().equals("FIREWORK") || id.name().equals("FIREWORK_ROCKET")) {
            FireworkMeta meta = (FireworkMeta) item.getItemMeta();
            List<FireworkEffect> efectos = meta.getEffects();
            List<String> effectList = new ArrayList<>();
            for (FireworkEffect e : efectos) {
                String linea = e.getType().name() + ";";
                List<Color> colores = e.getColors();
                String lineaColores = "";
                for (int i = 0; i < colores.size(); i++) {
                    if (colores.size() <= (i + 1)) {
                        lineaColores = lineaColores + colores.get(i).asRGB() + ";";
                    } else {
                        lineaColores = lineaColores + colores.get(i).asRGB() + ",";
                    }
                }
                List<Color> coloresFade = e.getFadeColors();
                String lineaColoresFade = "";
                for (int i = 0; i < coloresFade.size(); i++) {
                    if (coloresFade.size() <= (i + 1)) {
                        lineaColoresFade = lineaColoresFade + coloresFade.get(i).asRGB();
                    } else {
                        lineaColoresFade = lineaColoresFade + coloresFade.get(i).asRGB() + ",";
                    }
                }
                linea = linea + lineaColores + lineaColoresFade + ";" + e.hasFlicker() + ";" + e.hasTrail();
                effectList.add(linea);
            }
            kitConfig.set(path + ".firework-effects", effectList);
            kitConfig.set(path + ".firework-power", String.valueOf(meta.getPower()));
        }

        boolean esBanner = false;
        boolean esEscudo = false;
        if (!Utils.isLegacy()) {
            if (id.name().contains("BANNER") && !id.name().contains("PATTERN")) {
                esBanner = true;
            } else if (id == Material.SHIELD) {
                esEscudo = true;
            }
        } else {
            if (id == Material.valueOf("BANNER")) {
                esBanner = true;
            } else {
                if (!Bukkit.getVersion().contains("1.8")) {
                    if (id == Material.SHIELD) {
                        esEscudo = true;
                    }
                }
            }


        }
        if (esBanner) {
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            List<Pattern> patterns = meta.getPatterns();
            String patternsPath = "";
            for (Pattern p : patterns) {
                patternsPath = patternsPath + ";" + p.getColor().name() + ":" + p.getPattern().name();
            }
            if (patternsPath.startsWith(";")) {
                patternsPath = patternsPath.replaceFirst(";", "");
            }
            kitConfig.set(path + ".banner-pattern", patternsPath);
        } else if (esEscudo) {
            BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
            if (meta.hasBlockState()) {
                Banner banner = (Banner) meta.getBlockState();
                List<Pattern> patterns = banner.getPatterns();
                String patternsPath = "";
                for (Pattern p : patterns) {
                    patternsPath = patternsPath + ";" + p.getColor().name() + ":" + p.getPattern().name();
                }
                if (patternsPath.startsWith(";")) {
                    patternsPath = patternsPath.replaceFirst(";", "");
                }
                kitConfig.set(path + ".banner-pattern", patternsPath);
                if (Utils.isLegacy()) {
                    kitConfig.set(path + ".banner-color", banner.getBaseColor().name());
                } else {
                    kitConfig.set(path + ".banner-color", banner.getType().name());
                }
            }
        }

        Utils.saveSkull(item, kitConfig, path, false);
        Utils.saveAttributes(item, kitConfig, path);
        Utils.saveNBT(item, kitConfig, path);

        if (datavalue != 0 && Utils.isLegacy()) {
            idtext = idtext + ":" + datavalue;
        }
        kitConfig.set(path + ".id", idtext);
        kitConfig.set(path + ".amount", amount);
        if (!Utils.isLegacy()) {
            kitConfig.set(path + ".durability", String.valueOf(item.getDurability()));
        }
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName()) {
                String name = item.getItemMeta().getDisplayName().replaceAll("\\xa7", "&");
                kitConfig.set(path + ".name", name);
            }
            if (item.getItemMeta().hasLore()) {
                List<String> lore = item.getItemMeta().getLore();
                lore.replaceAll(s -> s.replaceAll("\\xa7", "&"));
                kitConfig.set(path + ".lore", lore);
            }
            if (item.getItemMeta().hasEnchants()) {
                String pathench = path + ".enchants";
                item.getEnchantments().forEach((enchant, level) -> {
                    kitConfig.set(pathench + "." + enchant.getName(), level);
                });
            }

            Set<ItemFlag> flags = item.getItemMeta().getItemFlags();
            if (flags != null && !flags.isEmpty()) {
                List<String> stringflags = new ArrayList<>();
                List<ItemFlag> listflags = new ArrayList<>(flags);
                for (int i = 0; i < flags.size(); i++) {
                    stringflags.add(listflags.get(i).name());
                }

                kitConfig.set(path + ".hide-flags", stringflags);
            }

            //kitConfig.set(path + ".unbreakable", Utils.getUnbreakable(item));

            if (Utils.isNew()) {
                if (item.getItemMeta().hasCustomModelData()) {
                    kitConfig.set(path + ".custom_model_data", String.valueOf(item.getItemMeta().getCustomModelData()));
                }
            }
        }
    }

    //TODO: Ion wanna do this no more
    public static ItemStack getItem(FileConfiguration kitConfig, String path, Player jugador) {
//		if(config.getString("Config.item_serializer").equals("true")
//				&& kitConfig.contains(path+".full_data")) {
//			String serializedItem = kitConfig.getString(path+".full_data");
//			return ItemSerializer.deserialize(serializedItem);
//		}
        String id = kitConfig.getString(path + ".id");
        int IDint = 0;
        int DataValue = 0;
        String[] idsplit = new String[2];
        boolean placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        idsplit = id.split(":");
        List<String> lore = new ArrayList<>();
        if (kitConfig.contains(path + ".lore")) {
            lore = kitConfig.getStringList(path + ".lore");
            for (int i = 0; i < lore.size(); i++) {
                if (placeholderAPI) {
                    String nueva = MessageUtils.translateColor(PlaceholderAPI.setPlaceholders(jugador, lore.get(i)).replace("%player%", jugador.getName()));
                    lore.set(i, nueva);
                } else {
                    lore.set(i, MessageUtils.translateColor(lore.get(i).replace("%player%", jugador.getName())));
                }
            }
        }
        List<String> flags = new ArrayList<>();
        flags = kitConfig.getStringList(path + ".hide-flags");
        String pathamount = path + ".amount";
        int pathamountInt = 1;
        if (kitConfig.contains(pathamount)) {
            pathamountInt = kitConfig.getInt(pathamount);
        }
        // TODO: get rid of getItem method.
        ItemStack crafteos = Utils.getItem(id);
        String pathdurability = path + ".durability";
        if (!Utils.isLegacy() && kitConfig.contains(pathdurability)) {
            crafteos.setDurability((short) kitConfig.getDouble(pathdurability));
        }
        if ((crafteos.getType().name().contains("POTION") || crafteos.getType().name().contains("TIPPED_ARROW"))) {
            if (crafteos.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) crafteos.getItemMeta();
                if (!Bukkit.getVersion().contains("1.8")) {
                    boolean isUpgraded = kitConfig.getBoolean(path + ".potion-upgraded");
                    boolean isExtended = kitConfig.getBoolean(path + ".potion-extended");
                    PotionType type = PotionType.valueOf(kitConfig.getString(path + ".potion-type"));
                    meta.setBasePotionData(new PotionData(type, isExtended, isUpgraded));
                    String pathColor = path + ".potion-color";
                    if (kitConfig.contains(pathColor)) {
                        meta.setColor(Color.fromRGB(kitConfig.getInt(pathColor)));
                    }
                }
                String pathefectos = path + ".potion-effects";
                if (kitConfig.contains(pathefectos)) {
                    List<String> efectos = kitConfig.getStringList(pathefectos);
                    for (String efecto : efectos) {
                        String[] separados = efecto.split(";");
                        String tipoPocion = separados[0];
                        int amplifier = Integer.parseInt(separados[1]);
                        int duracion = Integer.parseInt(separados[2]);
                        meta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(tipoPocion), duracion, amplifier), false);
                    }
                }

                crafteos.setItemMeta(meta);
            }

        }
        if (id.contains(":")) {
            if (idsplit[0].equals("383") || idsplit[0].equals("MONSTER_EGG")) {
                if (Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
                    String stringDataValue = idsplit[1];
                    DataValue = Integer.parseInt(stringDataValue);
                    crafteos = new ItemStack(Material.valueOf("MONSTER_EGG"), pathamountInt);
                    SpawnEggMeta eggMeta = (SpawnEggMeta) crafteos.getItemMeta();
                    eggMeta.setSpawnedType(EntityType.fromId(DataValue));
                    crafteos.setItemMeta(eggMeta);
                }
            }
        }

        String colorpath = path + ".color";
        if (kitConfig.contains(colorpath)) {
            int color = kitConfig.getInt(colorpath);
            LeatherArmorMeta meta = (LeatherArmorMeta) crafteos.getItemMeta();
            meta.setColor(Color.fromRGB(color));
            crafteos.setItemMeta(meta);
        }

        String book_enchants = path + ".book-enchants";
        if (kitConfig.contains(book_enchants)) {
            List<String> bookEnchantsList = kitConfig.getStringList(book_enchants);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) crafteos.getItemMeta();
            for (String s : bookEnchantsList) {
                String[] sep = s.split(";");
                String nombre = sep[0];
                int nivel = Integer.parseInt(sep[1]);
                meta.addStoredEnchant(Enchantment.getByName(nombre), nivel, true);
            }
            crafteos.setItemMeta(meta);
        }

        String book_properties = path + ".book-title";
        if (kitConfig.contains(book_properties)) {
            BookMeta meta = (BookMeta) crafteos.getItemMeta();

            String author = kitConfig.getString(path + ".book-author");
            String title = kitConfig.getString(path + ".book-title");
            String generation = null;
            if (kitConfig.contains(path + ".book-generation")) {
                generation = kitConfig.getString(path + ".book-generation");
            }

            List<String> pages = kitConfig.getStringList(path + ".book-pages");

            if (!Bukkit.getVersion().contains("1.12") && Utils.isLegacy()) {
                meta.setPages(new ArrayList<>(pages));
            } else {
                ArrayList<BaseComponent[]> pagesBaseComponent = new ArrayList<>();
                for (String page : pages) {
                    pagesBaseComponent.add(ComponentSerializer.parse(page));
                }
                meta.spigot().setPages(pagesBaseComponent);
                if (generation != null && !generation.isEmpty()) {
                    meta.setGeneration(Generation.valueOf(generation));
                }
            }

            meta.setAuthor(author);
            meta.setTitle(title);
            crafteos.setItemMeta(meta);
        }

        //TODO: THIS!
        attemptFireworkFormatting(kitConfig, path, crafteos);

        attemptBannerFormatting(kitConfig, path, crafteos);

        ItemMeta crafteosMeta = crafteos.getItemMeta();
        if (kitConfig.contains(path + ".name")) {
            if (placeholderAPI) {
                String nombre = MessageUtils.translateColor(kitConfig.getString(path + ".name"));
                String nueva = PlaceholderAPI.setPlaceholders(jugador, nombre).replace("%player%", jugador.getName());
                crafteosMeta.setDisplayName(nueva);
            } else {
                crafteosMeta.setDisplayName(MessageUtils.translateColor(kitConfig.getString(path + ".name").replace("%player%", jugador.getName())));
            }

        }

        crafteosMeta.setLore(lore);

        ConfigurationSection enchantSection = kitConfig.getConfigurationSection(path + ".enchants");
        if (enchantSection != null) {
            Bukkit.getPluginManager().getPlugin("PlayerKits").getLogger().info(enchantSection.toString());

            for (String key : enchantSection.getKeys(false)) {
                int enchantmentLevel = enchantSection.getInt(key);
                crafteosMeta.addEnchant(Enchantment.getByName(key), enchantmentLevel, true);
            }
        }

        for (String flag : flags) {
            crafteosMeta.addItemFlags(ItemFlag.valueOf(flag));
        }
        if (Utils.isNew()) {
            if (kitConfig.contains(path + ".custom_model_data")) {
                int customModelData = kitConfig.getInt(path + ".custom_model_data");
                crafteosMeta.setCustomModelData(customModelData);
            }
        }
        crafteos.setItemMeta(crafteosMeta);

        String unbreakablepath = path + ".unbreakable";
        if (kitConfig.contains(unbreakablepath)) {
            if (kitConfig.getString(unbreakablepath).equals("true")) {
                crafteos = Utils.setUnbreakable(crafteos);
            }
        }
        if (idsplit[0].equals("PLAYER_HEAD") || id.equals("SKULL_ITEM:3")) {
            crafteos = Utils.setSkull(crafteos, path, kitConfig);
        }
        if (kitConfig.contains(path + ".attributes")) {
            crafteos = Utils.setAttributes(crafteos, kitConfig, path);
        }
        if (kitConfig.contains(path + ".nbt")) {
            crafteos = Utils.setNBT(crafteos, kitConfig, path);
        }
        return crafteos;
    }

    private static void attemptFireworkFormatting(FileConfiguration kitConfig, String path, ItemStack crafteos) {
        String firework_effects = path + ".firework-effects";
        if (!kitConfig.contains(firework_effects)) return;

        List<String> fireworkEffectsList = kitConfig.getStringList(firework_effects);
        FireworkMeta meta = (FireworkMeta) crafteos.getItemMeta();
        for (String s : fireworkEffectsList) {
            String[] sep = s.split(";");
            String type = sep[0];
            String[] colores = sep[1].split(",");
            List<Color> coloresList = new ArrayList<>();
            for (String colore : colores) {
                coloresList.add(Color.fromRGB(Integer.parseInt(colore)));
            }
            List<Color> coloresListFade = new ArrayList<>();
            if (!sep[2].equals("")) {
                String[] coloresFade = sep[2].split(",");
                for (String value : coloresFade) {
                    coloresListFade.add(Color.fromRGB(Integer.parseInt(value)));
                }
            }

            boolean flicker = Boolean.parseBoolean(sep[3]);
            boolean trail = Boolean.parseBoolean(sep[4]);
            meta.addEffect(FireworkEffect.builder().flicker(flicker).trail(trail).with(Type.valueOf(type)).withColor(coloresList).withFade(coloresListFade).build());
        }
        int power = kitConfig.getInt(path + ".firework-power");
        meta.setPower(power);
        crafteos.setItemMeta(meta);
    }

    private static void attemptBannerFormatting(FileConfiguration kitConfig, String path, ItemStack crafteos) {

        if (!kitConfig.contains(path + ".banner-pattern")) return;

        boolean esBanner = false;
        boolean esEscudo = false;

        if (!Utils.isLegacy()) {
            if (crafteos.getType().name().contains("BANNER")) {
                esBanner = true;
            } else if (crafteos.getType() == Material.SHIELD) {
                esEscudo = true;
            }
        } else {
            if (crafteos.getType() == Material.valueOf("BANNER")) {
                esBanner = true;
            } else {
                if (!Bukkit.getVersion().contains("1.8")) {
                    if (crafteos.getType() == Material.SHIELD) {
                        esEscudo = true;
                    }
                }
            }
        }

        if (esBanner) {
            BannerMeta meta = (BannerMeta) crafteos.getItemMeta();
            List<Pattern> patterns = new ArrayList<>();
            String patternsPath = kitConfig.getString(path + ".banner-pattern"); // COLOR:TIPO;COLOR:TIPO
            if (!patternsPath.equals("")) {
                String[] patternsSeparados = patternsPath.split(";");
                for (String patternsSeparado : patternsSeparados) {
                    String[] lineaSep = patternsSeparado.split(":");
                    String tipoB = lineaSep[1];
                    String colorB = lineaSep[0];
                    patterns.add(new Pattern(DyeColor.valueOf(colorB), PatternType.valueOf(tipoB)));
                }
                meta.setPatterns(patterns);
            }

//		  		  	if(!Bukkit.getVersion().contains("1.13")){
//		  		  		String mainColor = config.getString(path+".banner-color");
//		  		  		meta.setBaseColor(DyeColor.valueOf(mainColor));
//		  		  	}

            crafteos.setItemMeta(meta);
        } else if (esEscudo) {
            BlockStateMeta meta = (BlockStateMeta) crafteos.getItemMeta();
            Banner banner = (Banner) meta.getBlockState();
            if (Utils.isLegacy()) {
                String mainColor = kitConfig.getString(path + ".banner-color");
                banner.setBaseColor(DyeColor.valueOf(mainColor));
            } else {
                String mainColor = kitConfig.getString(path + ".banner-color");
                banner.setBaseColor(Utils.getBannerColor(mainColor));
            }
            String patternsPath = kitConfig.getString(path + ".banner-pattern"); // COLOR:TIPO;COLOR:TIPO
            if (!patternsPath.equals("")) {
                String[] patternsSeparados = patternsPath.split(";");
                for (String patternsSeparado : patternsSeparados) {
                    String[] lineaSep = patternsSeparado.split(":");
                    String tipoB = lineaSep[1];
                    String colorB = lineaSep[0];
                    banner.addPattern(new Pattern(DyeColor.valueOf(colorB), PatternType.valueOf(tipoB)));
                }
            }

            banner.update();
            meta.setBlockState(banner);
            crafteos.setItemMeta(meta);
        }

    }

    public static boolean getArmadura(ItemStack item) {
        String name = item.getType().name();
        return name.contains("_HELMET") || name.contains("_CHESTPLATE") || name.contains("_LEGGINGS") || name.contains("_BOOTS");
    }

    public boolean save(Player player, String kitName) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration configKits = plugin.getKits();

        ItemStack[] contents = player.getInventory().getContents();
        int c = 1;
        for (ItemStack content : contents) {
            if (content != null && !content.getType().equals(Material.AIR)) {
                String path = "Kits." + kitName + ".Items." + c;
                KitManager.saveItem(content, configKits, path, config);
                c++;
            }
        }
        if (c == 1) {
            return false;
        } else {
            int slot = Utils.getSlotDisponible(configKits, config);
            if (slot != -1) {
                configKits.set("Kits." + kitName + ".slot", slot);
            }

            configKits.set("Kits." + kitName + ".display_item", "IRON_SWORD");
            configKits.set("Kits." + kitName + ".display_name", "&6&l" + kitName + " &aKit");
            List<String> lore = new ArrayList<>();
            lore.add("&7This is a description of the kit.");
            lore.add("&7You can add multiples &alines&7.");
            configKits.set("Kits." + kitName + ".display_lore", lore);
            configKits.set("Kits." + kitName + ".cooldown", 3600);

            configKits.set("Kits." + kitName + ".permission", "playerkits.kit." + kitName);
            configKits.set("Kits." + kitName + ".noPermissionsItem.display_item", "BARRIER");
            configKits.set("Kits." + kitName + ".noPermissionsItem.display_name", "&6&l" + kitName + " &aKit");
            lore = new ArrayList<>();
            lore.add("&cYou don't have permissions to claim");
            lore.add("&cthis kit.");
            configKits.set("Kits." + kitName + ".noPermissionsItem.display_lore", lore);

            return true;
        }

    }

    public void attemptBuyKit(Player player, String kit) {
        FileConfiguration configKits = plugin.getKits();

        // Kit cannot be purchased.
        if (!configKits.contains("Kits." + kit + ".price")) return;

        FileConfiguration config = plugin.getConfig();
        PlayerManager playerManager = plugin.getPlayerManager();
        MessageHandler messageHandler = plugin.getMessageHandler();
        PlayerKit playerKit = playerManager.getOrCreatePlayerKit(player, kit);

        Economy eco = plugin.getEconomy();
        if (eco == null) {
            messageHandler.sendManualMessage(player, "There isn't any economy provider available.");
            return;
        }

        double balance = eco.getBalance(player);
        double price = configKits.getDouble("Kits." + kit + ".price");

        if (balance < price) {
            messageHandler.sendMessage(player, "purchase.error.not-enough-money",
                    new Placeholder("%required_money%", price),
                    new Placeholder("%current_money%", balance),
                    new Placeholder("%needed_money%", price - balance));
            playErrorSound(player, config);
            player.closeInventory();
            return;
        }

        if (configKits.contains("Kits." + kit + ".one_time_buy") && configKits.getBoolean("Kits." + kit + ".one_time_buy")) {
            playerKit.setOneTimeBuy(true);
        }

        playerKit.setBought(true);
        messageHandler.sendMessage(player, "purchase.success", new Placeholder("%name%", kit));
        claimKit(player, kit, false, true);
        eco.withdrawPlayer(player, price);
    }

    public void claimKit(Player player, String kit, boolean notify, boolean ignoreValues) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration configKits = plugin.getKits();
        MessageHandler messageHandler = plugin.getMessageHandler();

        PlayerManager playerManager = plugin.getPlayerManager();
        PlayerData playerData = playerManager.getOrCreatePlayer(player);
        PlayerKit playerKit = playerManager.getOrCreatePlayerKit(player, kit);

        if (!ignoreValues) {
            // If kit has permissions.
            if (configKits.contains("Kits." + kit + ".permission") && !player.hasPermission(configKits.getString("Kits." + kit + ".permission"))) {
                messageHandler.sendMessage(player, "kit.error.noPermissions");
                playErrorSound(player, config);

                if (config.getBoolean("close_inventory_no_permission")) {
                    player.closeInventory();
                    player.updateInventory();
                }
                return;
            }

            // If kit is on-time use.
            if (configKits.contains("Kits." + kit + ".one_time") && configKits.getBoolean("Kits." + kit + ".one_time")) {
                if (playerKit.isOneTime()) {
                    messageHandler.sendMessage(player, "kit.error.one-time");
                    playErrorSound(player, config);
                    return;
                }
            }

            // If kit has cooldown.
            if (configKits.contains("Kits." + kit + ".cooldown")) {
                if (playerData.hasCooldown(kit)) {
                    Cooldown cooldown = playerData.getCooldown(kit);

                    List<Placeholder> placeholderList = new ArrayList<>();
                    placeholderList.add(new Placeholder("%kit%", kit));
                    placeholderList.add(new Placeholder("%timeleft%", cooldown.getTimeLeft()));
                    placeholderList.add(new Placeholder("%timer%", cooldown.getTimeLeftTimer()));
                    placeholderList.add(new Placeholder("%seconds%", cooldown.getTimeLeftSeconds()));
                    placeholderList.add(new Placeholder("%plainseconds%", cooldown.getTimeLeftPlainSeconds()));
                    placeholderList.add(new Placeholder("%roundedseconds%", cooldown.getTimeLeftRoundedSeconds()));

                    messageHandler.sendMessage(player, "cooldown.wait", placeholderList);
                    playErrorSound(player, config);
                    return;
                }
            }

            // If kit require to be purchased.
            if (configKits.contains("Kits." + kit + ".price")) {

                if (configKits.contains("Kits." + kit + ".one_time_buy") && configKits.getBoolean("Kits." + kit + ".one_time_buy")) {
                    if (playerKit.isOneTimeBuy()) {
                        messageHandler.sendMessage(player, "purchase.error.one-time-buy");
                        return;
                    }
                }

                if (!playerKit.isBought()) {
                    //Abrir inventario confirmacion
                    CurrentPlayerInventory inv = plugin.getInventarioJugador(player.getName());
                    int pag = -1;
                    if (inv != null) {
                        pag = inv.getPage();
                    }

                    double price = configKits.getDouble("Kits." + kit + ".price");
                    PurchaseConfirmationMenu.openInventory(player, plugin, price, kit, pag);
                    return;
                }
            }
        }

        //Estos contents son solo del inventario, ignoran armadura y offhand
        ItemStack[] contents = Utils.isLegacy() ? player.getInventory().getContents() : player.getInventory().getStorageContents();
        int espaciosUsados = 0;
        for (ItemStack content : contents) {
            if (content != null && !content.getType().equals(Material.AIR)) {
                espaciosUsados++;
            }
        }

        PlayerInventory inventory = player.getInventory();
        int espaciosLibres = 36 - espaciosUsados;
        int cantidadItems = 0; //items normales

        String itemCabeza = null;
        String itemPechera = null;
        String itemPantalones = null;
        String itemBotas = null;
        String itemOffhand = null;
        if (configKits.contains("Kits." + kit + ".Items")) {
            for (String i : configKits.getConfigurationSection("Kits." + kit + ".Items").getKeys(false)) {
                String name = configKits.getString("Kits." + kit + ".Items." + i + ".id");
                if (configKits.contains("Kits." + kit + ".auto_armor") && configKits.getString("Kits." + kit + ".auto_armor").equals("true")) {
                    if (name.contains("_HELMET") && itemCabeza == null && (inventory.getHelmet() == null || inventory.getHelmet().getType().equals(Material.AIR))) {
                        itemCabeza = i;
                        continue;
                    } else if (name.contains("_CHESTPLATE") && itemPechera == null && (inventory.getChestplate() == null || inventory.getChestplate().getType().equals(Material.AIR))) {
                        itemPechera = i;
                        continue;
                    } else if (name.contains("_LEGGINGS") && itemPantalones == null && (inventory.getLeggings() == null || inventory.getLeggings().getType().equals(Material.AIR))) {
                        itemPantalones = i;
                        continue;
                    } else if (name.contains("_BOOTS") && itemBotas == null && (inventory.getBoots() == null || inventory.getBoots().getType().equals(Material.AIR))) {
                        itemBotas = i;
                        continue;
                    } else if ((name.equals("SKULL_ITEM") || name.equals("PLAYER_HEAD")) && itemCabeza == null && (inventory.getHelmet() == null || inventory.getHelmet().getType().equals(Material.AIR))) {
                        itemCabeza = i;
                        continue;
                    } else if (!Utils.isLegacy() && name.equals("ELYTRA") && itemPechera == null && (inventory.getChestplate() == null || inventory.getChestplate().getType().equals(Material.AIR))) {
                        itemPechera = i;
                        continue;
                    }
                }
                if (configKits.contains("Kits." + kit + ".Items." + i + ".offhand") && configKits.getString("Kits." + kit + ".Items." + i + ".offhand").equals("true")) {
                    if (itemOffhand == null && (inventory.getItemInOffHand() == null || inventory.getItemInOffHand().getType().equals(Material.AIR))) {
                        itemOffhand = i;
                        continue;
                    }
                }

                cantidadItems++;
            }
        }

        boolean tirarItems = config.getBoolean("drop_items_if_full_inventory");
        boolean ejecutarComandosPrimero = config.getBoolean("commands_before_items");

        if (espaciosLibres < cantidadItems && !tirarItems) {
            messageHandler.sendMessage(player, "noSpaceError");
            playErrorSound(player, config);
            return;
        }

        if (!ignoreValues) {
            if (configKits.contains("Kits." + kit + ".cooldown")) {
                if (!player.hasPermission("playerkits.bypasscooldown")) {
                    playerData.registerCooldown(kit, new Cooldown(configKits.getInt("Kits." + kit + ".cooldown") * 1000L));
                }
            }

            if (configKits.contains("Kits." + kit + ".one_time") && configKits.getBoolean("Kits." + kit + ".one_time")) {
                if (!player.hasPermission("playerkits.admin")) {
                    playerKit.setOneTime(true);
                }
            }

            if (!config.getString("sounds.claim_sound").equals("none")) {
                String[] separados = config.getString("sounds.claim_sound").split(";");
                try {
                    Sound sound = Sound.valueOf(separados[0]);
                    player.playSound(player.getLocation(), sound, Float.parseFloat(separados[1]), Float.parseFloat(separados[2]));
                } catch (Exception ex) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PlayerKits.pluginPrefix + "&7Sound Name: &c" + separados[0] + " &7is not valid. Change the name of the sound corresponding to your Minecraft version."));
                }
            }
        }

        if (ejecutarComandosPrimero) {
            ejecutarComandos(configKits, kit, player);
        }

        if (configKits.contains("Kits." + kit + ".Items")) {
            for (String i : configKits.getConfigurationSection("Kits." + kit + ".Items").getKeys(false)) {
                String path = "Kits." + kit + ".Items." + i;
                ItemStack item = KitManager.getItem(configKits, path, player);

                // TODO: pleaseeee
                if (itemCabeza != null && i.equals(itemCabeza)) {
                    inventory.setHelmet(item);
                } else if (itemPechera != null && i.equals(itemPechera)) {
                    inventory.setChestplate(item);
                } else if (itemPantalones != null && i.equals(itemPantalones)) {
                    inventory.setLeggings(item);
                } else if (itemBotas != null && i.equals(itemBotas)) {
                    inventory.setBoots(item);
                } else if (itemOffhand != null && i.equals(itemOffhand)) {
                    inventory.setItemInOffHand(item);
                } else {
                    if (tirarItems && player.getInventory().firstEmpty() == -1) {
                        player.getWorld().dropItemNaturally(player.getLocation(), item);
                    } else {
                        player.getInventory().addItem(item);
                    }
                }
            }
        }

        if (!ejecutarComandosPrimero) {
            ejecutarComandos(configKits, kit, player);
        }

        Placeholder placeholder = new Placeholder("%name%", kit);
        messageHandler.sendMessage(player, "kit.claim.success", placeholder);
        if (notify) {
            messageHandler.sendMessage(player, "kit.give.notify", placeholder);
        }

        if (config.getBoolean("close_inventory_on_claim")) {
            player.closeInventory();
        }
    }

    public void playErrorSound(Player jugador, FileConfiguration config) {
        if (config.getString("sounds.error_sound").equals("none")) {
            return;
        }
        String[] separados = config.getString("sounds.error_sound").split(";");
        try {
            Sound sound = Sound.valueOf(separados[0]);
            jugador.playSound(jugador.getLocation(), sound, Float.parseFloat(separados[1]), Float.parseFloat(separados[2]));
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PlayerKits.pluginPrefix + "&7Sound Name: &c" + separados[0] + " &7is not valid. Change the name of the sound corresponding to your Minecraft version."));
        }
    }

    public void ejecutarComandos(FileConfiguration configKits, String kit, Player jugador) {
        if (!configKits.contains("Kits." + kit + ".Commands")) {
            return;
        }

        List<String> comandos = configKits.getStringList("Kits." + kit + ".Commands");
        CommandSender consola = Bukkit.getServer().getConsoleSender();
        for (String comando : comandos) {
            if (comando.startsWith("msg %player% ")) {
                String mensaje = comando.replace("msg %player% ", "");
                jugador.sendMessage(MessageUtils.translateColor(mensaje));
            } else {
                String comandoAEnviar = comando.replace("%player%", jugador.getName());
                Bukkit.dispatchCommand(consola, comandoAEnviar);
            }
        }
    }

    public boolean existsKit(String kitName) {
        FileConfiguration kits = plugin.getKits();
        ConfigurationSection section = kits.getConfigurationSection("Kits");

        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (key.equalsIgnoreCase(kitName)) {
                    return true;
                }
            }
        }

        return false;
    }
}
