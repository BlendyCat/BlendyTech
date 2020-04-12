package com.blendycat.blendytech.machine.inventory;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StorageInventory extends MachineInventory {
    
    private static final long serialVersionUID = -7272438715760480145L;
    
    private Inventory inventory;
    private int size;
    private String name;

    public StorageInventory(int size, String name) {
        inventory = Bukkit.createInventory(this, size, name);
        this.size = size;
        this.name = name;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> object = super.serialize();
        object.put("size", size);
        object.put("name", name);
        return object;
    }

    public static StorageInventory deserialize(Map<String, Object> object) {
        int size = (int) object.get("size");
        String name = (String) object.get("name");
        StorageInventory storageInventory = new StorageInventory((int)size, name);
        Inventory inv = storageInventory.getInventory();
        for(String key : object.keySet()) {
            if(!(key.equals("size") || key.equals("name"))) {
                int slot = Integer.parseInt(key);
                ItemStack item = ItemStack.deserialize((Map<String, Object>) object.get(key));
                inv.setItem(slot, item);
            }
        }
        return storageInventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public static class Events implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if(e.getCurrentItem() != null && e.getInventory().getHolder() instanceof StorageInventory) {
                ItemStack item = e.getCurrentItem();
                if(item.hasItemMeta()) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
