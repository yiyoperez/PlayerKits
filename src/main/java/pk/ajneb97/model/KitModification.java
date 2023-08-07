package pk.ajneb97.model;

import org.bukkit.entity.Player;

public class KitModification {

    private Player player;
    private String kit;
    private String step;
    private String displayType;

    public KitModification(Player player, String kit, String displayType) {
        this.player = player;
        this.kit = kit;
        this.displayType = displayType;
        this.step = "";
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getKit() {
        return kit;
    }

    public void setKit(String kit) {
        this.kit = kit;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getStep() {
        return this.step;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

}
