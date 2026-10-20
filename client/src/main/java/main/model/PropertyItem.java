package main.model;
import main.status.ItemState;

public class PropertyItem {
    private int id;
    private String inventoryNumber;
    private String name;
    private String category;
    private ItemState statusItem;

    public PropertyItem() {}

    public PropertyItem(int id, String inventoryNumber, String name, String category, ItemState statusItem) {
        this.id = id;
        this.inventoryNumber = inventoryNumber;
        this.name = name;
        this.category = category;
        this.statusItem = statusItem != null ? statusItem : ItemState.AVAILABLE;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getInventoryNumber() { return inventoryNumber; }
    public void setInventoryNumber(String inventoryNumber) { this.inventoryNumber = inventoryNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public ItemState getStatusItem() { return statusItem; }
    public void setStatusItem(ItemState statusItem) {
        this.statusItem = statusItem != null ? statusItem : ItemState.AVAILABLE;
    }
}