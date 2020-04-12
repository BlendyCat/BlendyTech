package com.blendycat.blendytech.machine.inventory;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FuelInventory extends MachineInventory {
    
    private static final long serialVersionUID = -8777033135554689508L;
    
    private Inventory inventory;
    private int size;
    private String name;

    public FuelInventory(int size, String name) {
        inventory = Bukkit.createInventory(this, size, name);
        this.size = size;
        this.name = name;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> object = super.serialize();
        object.put("size", size);
        object.put("name", name);
        return object;
    }

    public static FuelInventory deserialize(Map<String, Object> object) {
        int size = (int) object.get("size");
        String name = (String) object.get("name");
        FuelInventory fuelInventory = new FuelInventory(size, name);
        Inventory inv = fuelInventory.getInventory();
        for(String key : object.keySet()) {
            if(!(key.equals("size") || key.equals("name"))) {
                int slot = Integer.parseInt(key);
                ItemStack item = ItemStack.deserialize((Map<String, Object>) object.get(key));
                inv.setItem(slot, item);
            }
        }
        return fuelInventory;
    }


    public static class Events implements Listener {

        @EventHandler
        public void onInventoryClickEvent(InventoryClickEvent e) {
            Inventory inventory = e.getInventory();
            if(inventory.getHolder() instanceof FuelInventory) {
                ItemStack clicked = e.getCurrentItem();

                if(clicked != null && clicked.getType() != Material.COAL) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
