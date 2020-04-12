package com.blendycat.blendytech.machine.inventory;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RailInventory extends MachineInventory {
    
    private static final long serialVersionUID = -6904959823797294164L;
    
    private Inventory inventory;
    private int size;
    private String name;

    public RailInventory(int size, String name) {
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

    public static RailInventory deserialize(Map<String, Object> object) {
        int size = (int) object.get("size");
        String name = (String) object.get("name");
        RailInventory railInventory = new RailInventory((int)size, name);
        Inventory inv = railInventory.getInventory();
        for(String key : object.keySet()) {
            if(!(key.equals("size") || key.equals("name"))) {
                int slot = Integer.parseInt(key);
                ItemStack item = ItemStack.deserialize((Map<String, Object>) object.get(key));
                inv.setItem(slot, item);
            }
        }
        return railInventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public static class Events implements Listener {

        @EventHandler
        public void onInventoryClickEvent(InventoryClickEvent e) {
            Inventory inventory = e.getInventory();
            if(inventory.getHolder() instanceof RailInventory) {
                ItemStack clicked = e.getCurrentItem();
                if(clicked != null && clicked.getType() != Material.RAIL) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
