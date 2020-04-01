package com.blendycat.blendytech.machine.inventory;

import com.blendycat.blendytech.machine.Machine;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class StorageInventory extends MachineInventory {

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
        long size = (long) object.get("size");
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
}
