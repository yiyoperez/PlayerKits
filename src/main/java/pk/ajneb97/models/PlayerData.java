package pk.ajneb97.models;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pk.ajneb97.utils.Cooldown;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerData implements ConfigurationSerializable {

    private final UUID uuid;
    private final String name;
    private final Set<PlayerKit> kits = new HashSet<>();
    private final Map<String, Cooldown> cooldownMap = new HashMap<>();

    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public PlayerData(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    public PlayerData(Map<String, Object> map) {
        this.uuid = UUID.fromString((String) map.get("unique"));
        this.name = (String) map.get("name");

        if (map.get("kits") != null && map.get("kits") instanceof MemorySection) {
            MemorySection section = (MemorySection) map.get("kits");

            // If kits are empty return.
            if (section.getKeys(false).isEmpty()) return;

            // Load player kits.
            section.getKeys(false)
                    .stream()
                    .map(key -> new PlayerKit(section.getConfigurationSection(key).getValues(false)))
                    .forEach(kits::add);

            kits.forEach(kit -> {
                // If kit cooldown has expired don't add to cooldown map.
                if (kit.getCooldown() <= System.currentTimeMillis()) return;
                cooldownMap.put(kit.getName(), new Cooldown(kit.getCooldown()));
            });
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public PlayerKit getPlayerKit(String kit) {
        return kits.stream()
                .filter(playerKit -> playerKit.getName().equalsIgnoreCase(kit))
                .findFirst()
                .orElse(new PlayerKit(kit, false, false));
    }

    public Set<PlayerKit> getKits() {
        return kits;
    }

    public void registerCooldown(String kit, Cooldown cooldown) {
        cooldownMap.put(kit, cooldown);
    }

    public void removeCooldown(String kit) {
        if (hasCooldown(kit)) {
            getCooldown(kit).retimeCooldown(System.currentTimeMillis());
        }
    }

    public Cooldown getCooldown(String kit) {
        return cooldownMap.get(kit);
    }

    public boolean hasCooldown(String kit) {
        return cooldownMap.containsKey(kit) && !getCooldown(kit).hasExpired();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("unique", getUuid().toString());
        map.put("name", getName());

        Map<String, Object> kitsMap = new LinkedHashMap<>();
        for (PlayerKit kit : kits) {
            if (hasCooldown(kit.getName())) {
                kit.setCooldown(getCooldown(kit.getName()).getRemaining());
            }
            kitsMap.put(kit.getName(), kit.serialize());
        }
        map.put("kits", kitsMap);

        return map;
    }
}
