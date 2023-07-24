package pk.ajneb97.managers;

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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
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
import pk.ajneb97.inventory.PlayerInventory;
import pk.ajneb97.utils.MessageUtils;
import pk.ajneb97.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KitManager {


    public static boolean save(String kitName, FileConfiguration configKits, FileConfiguration config, Player jugador) {
        ItemStack[] contents = jugador.getInventory().getContents();
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


        //todo ???
        if (!Utils.isLegacy()) {
            if (id == Material.getMaterial("PLAYER_HEAD")) {
                Utils.guardarSkull(item, kitConfig, path, "");
            }
        } else {
            if (id == Material.valueOf("SKULL_ITEM") && datavalue == 3) {
                Utils.guardarSkull(item, kitConfig, path, "");
                idtext = "SKULL_ITEM";
            }

        }

        Utils.guardarAttributes(item, kitConfig, path);
        Utils.guardarNBT(item, kitConfig, path);

        if (datavalue != 0 && Utils.isLegacy()) {
            idtext = idtext + ":" + datavalue;
        }
        kitConfig.set(path + ".id", idtext);
        kitConfig.set(path + ".amount", String.valueOf(amount));
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
                Map<Enchantment, Integer> e = item.getEnchantments();
                String pathench = path + ".enchants";
                List<String> enchantments = new ArrayList<>();
                for (Map.Entry<Enchantment, Integer> entry : e.entrySet()) {
                    String enchant = entry.getKey().getName();
                    int level = entry.getValue();
                    String juntos = enchant + ";" + level;
                    enchantments.add(juntos);
                }
                kitConfig.set(pathench, enchantments);
            }
//			if(item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES) || item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DESTROYS)
//					|| item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS) || item.getItemMeta().hasItemFlag(ItemFlag.HIDE_PLACED_ON)
//					|| item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS) || item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)){
//				
//							
//			}
            Set<ItemFlag> flags = item.getItemMeta().getItemFlags();
            if (flags != null && !flags.isEmpty()) {
                List<String> stringflags = new ArrayList<>();
                List<ItemFlag> listflags = new ArrayList<>(flags);
                for (int i = 0; i < flags.size(); i++) {
                    stringflags.add(listflags.get(i).name());
                }

                kitConfig.set(path + ".hide-flags", stringflags);
            }


            if (Utils.getUnbreakable(item)) {
                kitConfig.set(path + ".unbreakable", "true");
            } else {
                kitConfig.set(path + ".unbreakable", "false");
            }
            if (Utils.isNew()) {
                if (item.getItemMeta().hasCustomModelData()) {
                    kitConfig.set(path + ".custom_model_data", String.valueOf(item.getItemMeta().getCustomModelData()));
                }
            }

        }

    }

    public static ItemStack getItem(FileConfiguration kitConfig, String path, FileConfiguration config, Player jugador) {
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
                    String nueva = MessageUtils.getMensajeColor(PlaceholderAPI.setPlaceholders(jugador, lore.get(i))
                            .replace("%player%", jugador.getName()));
                    lore.set(i, nueva);
                } else {
                    lore.set(i, MessageUtils.getMensajeColor(lore.get(i).replace("%player%", jugador.getName())));
                }
            }
        }
        List<String> enchants = new ArrayList<>();
        enchants = kitConfig.getStringList(path + ".enchants");
        List<String> flags = new ArrayList<>();
        flags = kitConfig.getStringList(path + ".hide-flags");
        String pathamount = path + ".amount";
        int pathamountInt = 1;
        if (kitConfig.contains(pathamount)) {
            pathamountInt = kitConfig.getInt(pathamount);
        }
        ItemStack crafteos = Utils.getItem(id, pathamountInt, "");
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
        String firework_effects = path + ".firework-effects";
        if (kitConfig.contains(firework_effects)) {
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
                meta.addEffect(FireworkEffect.builder().flicker(flicker).trail(trail).with(Type.valueOf(type))
                        .withColor(coloresList).withFade(coloresListFade).build());
            }
            int power = kitConfig.getInt(path + ".firework-power");
            meta.setPower(power);
            crafteos.setItemMeta(meta);
        }

        if (kitConfig.contains(path + ".banner-pattern")) {
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

        String unbreakablepath = path + ".unbreakable";
        ItemMeta crafteosMeta = crafteos.getItemMeta();
        if (kitConfig.contains(path + ".name")) {
            if (placeholderAPI) {
                String nombre = MessageUtils.getMensajeColor(kitConfig.getString(path + ".name"));
                String nueva = PlaceholderAPI.setPlaceholders(jugador, nombre)
                        .replace("%player%", jugador.getName());
                crafteosMeta.setDisplayName(nueva);
            } else {
                crafteosMeta.setDisplayName(MessageUtils.getMensajeColor(kitConfig.getString(path + ".name").replace("%player%", jugador.getName())));
            }

        }

        crafteosMeta.setLore(lore);
        //Todo: check boxing.
        for (String enchant : enchants) {
            String[] split = new String[2];
            split = enchant.split(";");
            int level = 0;
            if (split[1].contains("-")) {
                //Random r = new Random();
                String[] corte = new String[2];
                corte = split[1].split("-");
                //int max = Integer.valueOf(corte[1]);
                //level = r.nextInt(max-min)+min;
                level = Integer.parseInt(corte[0]);
            } else {
                level = Integer.parseInt(split[1]);
            }
            crafteosMeta.addEnchant(Enchantment.getByName(split[0]), level, true);
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

    public static void claimKit(Player jugador, String kit, PlayerKits plugin, boolean message, boolean ignoreValues, boolean comprandoKit) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration messages = plugin.getMessages();
        FileConfiguration configKits = plugin.getKits();
        JugadorManager jManager = plugin.getJugadorManager();
        String prefix = messages.getString("prefix");
        if (!ignoreValues) {
            if (configKits.contains("Kits." + kit + ".one_time") && configKits.getString("Kits." + kit + ".one_time").equals("true")) {
                if (jManager.isOneTime(jugador, kit)) {
                    jugador.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("oneTimeError")));
                    errorSonido(jugador, config);
                    return;
                }
            }
            if (configKits.contains("Kits." + kit + ".permission") && !jugador.hasPermission(configKits.getString("Kits." + kit + ".permission"))) {
                jugador.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("kitNoPermissions")));
                errorSonido(jugador, config);
                return;
            }
            if (configKits.contains("Kits." + kit + ".cooldown")) {
                String cooldown = Utils.getCooldown(kit, jugador, configKits, config, jManager);
                if (!cooldown.equals("ready")) {
                    jugador.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("cooldownError").replace("%time%", cooldown)));
                    errorSonido(jugador, config);
                    return;
                }
            }
            if (!comprandoKit && configKits.contains("Kits." + kit + ".price")
                    && !jManager.isBuyed(jugador, kit)) {
                double price = configKits.getDouble("Kits." + kit + ".price");
                if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
                    Economy econ = plugin.getEconomy();
                    double balance = econ.getBalance(jugador);
                    if (balance < price) {
                        jugador.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noMoneyError")
                                .replace("%current_money%", balance + "").replace("%required_money%", price + "")));
                        errorSonido(jugador, config);
                    } else {
                        //Abrir inventario confirmacion
                        PlayerInventory inv = plugin.getInventarioJugador(jugador.getName());
                        int pag = -1;
                        if (inv != null) {
                            pag = inv.getPagina();
                        }
                        InventarioConfirmacionDinero.crearInventario(jugador, plugin, price, kit, pag);
                    }
                    return;
                }
            }
        }

        String version = Bukkit.getServer().getVersion();

        //Estos contents son solo del inventario, ignoran armadura y offhand
        ItemStack[] contents = null;
        if (version.contains("1.8")) {
            contents = jugador.getInventory().getContents();
        } else {
            contents = jugador.getInventory().getStorageContents();
        }
        int espaciosUsados = 0;
        for (ItemStack content : contents) {
            if (content != null && !content.getType().equals(Material.AIR)) {
                espaciosUsados++;
            }
        }

        org.bukkit.inventory.PlayerInventory invJ = jugador.getInventory();
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
                    if (name.contains("_HELMET") && itemCabeza == null
                            && (invJ.getHelmet() == null || invJ.getHelmet().getType().equals(Material.AIR))) {
                        itemCabeza = i;
                        continue;
                    } else if (name.contains("_CHESTPLATE") && itemPechera == null
                            && (invJ.getChestplate() == null || invJ.getChestplate().getType().equals(Material.AIR))) {
                        itemPechera = i;
                        continue;
                    } else if (name.contains("_LEGGINGS") && itemPantalones == null
                            && (invJ.getLeggings() == null || invJ.getLeggings().getType().equals(Material.AIR))) {
                        itemPantalones = i;
                        continue;
                    } else if (name.contains("_BOOTS") && itemBotas == null
                            && (invJ.getBoots() == null || invJ.getBoots().getType().equals(Material.AIR))) {
                        itemBotas = i;
                        continue;
                    } else if ((name.equals("SKULL_ITEM") || name.equals("PLAYER_HEAD")) && itemCabeza == null
                            && (invJ.getHelmet() == null || invJ.getHelmet().getType().equals(Material.AIR))) {
                        itemCabeza = i;
                        continue;
                    } else if (!version.contains("1.8") && name.equals("ELYTRA") && itemPechera == null
                            && (invJ.getChestplate() == null || invJ.getChestplate().getType().equals(Material.AIR))) {
                        itemPechera = i;
                        continue;
                    }
                }
                if (configKits.contains("Kits." + kit + ".Items." + i + ".offhand") && configKits.getString("Kits." + kit + ".Items." + i + ".offhand").equals("true")) {
                    if (itemOffhand == null && (invJ.getItemInOffHand() == null || invJ.getItemInOffHand().getType().equals(Material.AIR))) {
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
            jugador.sendMessage(MessageUtils.getMensajeColor(prefix + messages.getString("noSpaceError")));
            errorSonido(jugador, config);
            return;
        }

        if (!ignoreValues) {
            if (comprandoKit && configKits.contains("Kits." + kit + ".price")
                    && !jManager.isBuyed(jugador, kit)) {
                double price = configKits.getDouble("Kits." + kit + ".price");
                if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
                    Economy econ = plugin.getEconomy();
                    econ.withdrawPlayer(jugador, price);
                }
                if (configKits.contains("Kits." + kit + ".one_time_buy") && configKits.getString("Kits." + kit + ".one_time_buy").equals("true")) {
                    jManager.setBuyed(jugador, kit);
                    return;
                }
            }

            if (configKits.contains("Kits." + kit + ".cooldown")) {
                if (!jugador.isOp() && !jugador.hasPermission("playerkits.admin") && !jugador.hasPermission("playerkits.bypasscooldown")) {
                    long millis = System.currentTimeMillis();
                    jManager.setCooldown(jugador, kit, millis);
                }
            }

            if (configKits.contains("Kits." + kit + ".one_time") && configKits.getString("Kits." + kit + ".one_time").equals("true")) {
                if (!jugador.isOp() && !jugador.hasPermission("playerkits.admin")) {
                    jManager.setOneTime(jugador, kit);
                }
            }

            if (!config.getString("kit_claim_sound").equals("none")) {
                String[] separados = config.getString("kit_claim_sound").split(";");
                try {
                    Sound sound = Sound.valueOf(separados[0]);
                    jugador.playSound(jugador.getLocation(), sound, Float.parseFloat(separados[1]), Float.parseFloat(separados[2]));
                } catch (Exception ex) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PlayerKits.pluginPrefix + "&7Sound Name: &c" + separados[0] + " &7is not valid. Change the name of the sound corresponding to your Minecraft version."));
                }
            }
        }

        if (ejecutarComandosPrimero) {
            ejecutarComandos(configKits, kit, jugador);
        }

        if (configKits.contains("Kits." + kit + ".Items")) {
            for (String i : configKits.getConfigurationSection("Kits." + kit + ".Items").getKeys(false)) {
                String path = "Kits." + kit + ".Items." + i;
                ItemStack item = KitManager.getItem(configKits, path, config, jugador);

                // TODO: pleaseeee
                if (itemCabeza != null && i.equals(itemCabeza)) {
                    invJ.setHelmet(item);
                } else if (itemPechera != null && i.equals(itemPechera)) {
                    invJ.setChestplate(item);
                } else if (itemPantalones != null && i.equals(itemPantalones)) {
                    invJ.setLeggings(item);
                } else if (itemBotas != null && i.equals(itemBotas)) {
                    invJ.setBoots(item);
                } else if (itemOffhand != null && i.equals(itemOffhand)) {
                    invJ.setItemInOffHand(item);
                } else {
                    if (tirarItems && jugador.getInventory().firstEmpty() == -1) {
                        jugador.getWorld().dropItemNaturally(jugador.getLocation(), item);
                    } else {
                        jugador.getInventory().addItem(item);
                    }
                }
            }
        }

        if (!ejecutarComandosPrimero) {
            ejecutarComandos(configKits, kit, jugador);
        }

        if (message) {
            String kitReceived = messages.getString("kitReceived").replace("%name%", kit);
            if (!kitReceived.equals("") && !kitReceived.isEmpty()) {
                jugador.sendMessage(MessageUtils.getMensajeColor(prefix + kitReceived));
            }
        }

        if (config.getBoolean("close_inventory_on_claim")) {
            jugador.closeInventory();
        }
    }

    public static void errorSonido(Player jugador, FileConfiguration config) {
        if (config.getString("kit_error_sound").equals("none")) {
            return;
        }
        String[] separados = config.getString("kit_error_sound").split(";");
        try {
            Sound sound = Sound.valueOf(separados[0]);
            jugador.playSound(jugador.getLocation(), sound, Float.parseFloat(separados[1]), Float.parseFloat(separados[2]));
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', PlayerKits.pluginPrefix + "&7Sound Name: &c" + separados[0] + " &7is not valid. Change the name of the sound corresponding to your Minecraft version."));
        }
    }

    public static boolean getArmadura(ItemStack item) {
        String name = item.getType().name();
        return name.contains("_HELMET") || name.contains("_CHESTPLATE") ||
                name.contains("_LEGGINGS") || name.contains("_BOOTS");
    }

    public static void ejecutarComandos(FileConfiguration configKits, String kit, Player jugador) {
        if (configKits.contains("Kits." + kit + ".Commands")) {
            List<String> comandos = configKits.getStringList("Kits." + kit + ".Commands");
            CommandSender consola = Bukkit.getServer().getConsoleSender();
            for (String comando : comandos) {
                if (comando.startsWith("msg %player% ")) {
                    String mensaje = comando.replace("msg %player% ", "");
                    jugador.sendMessage(MessageUtils.getMensajeColor(mensaje));
                } else {
                    String comandoAEnviar = comando.replace("%player%", jugador.getName());
                    Bukkit.dispatchCommand(consola, comandoAEnviar);
                }
            }
        }
    }
}
