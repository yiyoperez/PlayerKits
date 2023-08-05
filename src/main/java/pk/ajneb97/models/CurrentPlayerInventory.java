package pk.ajneb97.models;

import org.bukkit.entity.Player;
import pk.ajneb97.managers.InventarioManager;

public class CurrentPlayerInventory {

    private Player player;
    private int page;
    private InventarioManager inventoryManager;
    private String inventoryType;

    public CurrentPlayerInventory(Player player, int page, InventarioManager inventoryManager, String inventoryType) {
        this.player = player;
        this.page = page;
        this.inventoryManager = inventoryManager;
        this.inventoryType = inventoryType;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public InventarioManager getInventoryManager() {
        return inventoryManager;
    }

    public void setInventoryManager(InventarioManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }


}
