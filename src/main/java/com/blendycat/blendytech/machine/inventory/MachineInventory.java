package com.blendycat.blendytech.machine.inventory;

import com.blendycat.blendytech.machine.Machine;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.mongodb.morphia.annotations.Embedded;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class MachineInventory implements InventoryHolder, Serializable {

    private Machine machine;

    public Machine getMachine() {
        return machine;
    }

    /**
     * Goodbye MongoDB, I want to make this plugin usable
     * @return a serialized version of the inventory
     */
    public Map<String, Object> serialize() {
        Inventory inventory = getInventory();
        Map<String, Object> map = new HashMap<>();
        ItemStack[] contents = inventory.getContents();
        for(int i = 0; i < contents.length; i++) {
            if(contents[i] != null)
            map.put(String.valueOf(i), contents[i].serialize());
        }
        return map;
    }
}
