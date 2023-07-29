package pk.ajneb97.models;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import pk.ajneb97.utils.PluginLogger;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerKit implements ConfigurationSerializable {

    private String name;
    private long cooldown;
    private boolean oneTime;
    private boolean bought;

    public PlayerKit(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.cooldown = (int) map.get("cooldown");
        this.oneTime = (boolean) map.get("one-time");
        this.bought = (boolean) map.get("bought");
    }

    public PlayerKit(String name, boolean oneTime, boolean bought) {
        this.name = name;
        this.oneTime = oneTime;
        this.bought = bought;
    }

    public PlayerKit(String name, long cooldown, boolean oneTime, boolean bought) {
        this.name = name;
        this.cooldown = cooldown;
        this.oneTime = oneTime;
        this.bought = bought;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isOneTime() {
        return oneTime;
    }

    public void setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("name", name);
        map.put("cooldown", cooldown);
        map.put("bought", bought);
        map.put("one-time", oneTime);

        return map;
    }
}
