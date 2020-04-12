package com.blendycat.blendytech.machine.inventory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.blendycat.blendytech.machine.Machine;

public abstract class MachineInventory implements InventoryHolder, Serializable {
    
    private static final long serialVersionUID = 4836481158047798250L;
    
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
            if(contents[i] != null) {
                ItemStack item = contents[i];
                Map<String, Object> serial = item.serialize();
                if(item.getItemMeta() != null) {
                    ItemMeta meta = item.getItemMeta();
                    serial.put("meta", meta.serialize());
                }
                map.put(String.valueOf(i), serial);
            }
        }
        return map;
    }
}
