package pk.ajneb97.model;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerKit implements ConfigurationSerializable {

    private String name;
    private long cooldown;
    private boolean oneTime;
    private boolean bought;
    private boolean oneTimeBuy;

    public PlayerKit(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.cooldown = map.containsKey("cooldown") ? (int) map.get("cooldown") : -1;
        this.oneTime = map.containsKey("one-time") && (boolean) map.get("one-time");
        this.bought = map.containsKey("bought") && (boolean) map.get("bought");
        this.oneTimeBuy = map.containsKey("one-time-buy") && (boolean) map.get("one-time-buy");
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

    public boolean isOneTimeBuy() {
        return oneTimeBuy;
    }

    public void setOneTimeBuy(boolean oneTimeBuy) {
        this.oneTimeBuy = oneTimeBuy;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("name", name);
        map.put("cooldown", cooldown);
        map.put("bought", bought);
        map.put("one-time", oneTime);
        map.put("one-time-buy", oneTimeBuy);

        return map;
    }
}
