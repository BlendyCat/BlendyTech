package com.blendycat.yeettech.machine.inventory;

import com.blendycat.yeettech.machine.Machine;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.mongodb.morphia.annotations.Embedded;

import java.util.Map;

public abstract class MachineInventory implements InventoryHolder {

    private Machine machine;

    public MachineInventory(Machine machine) {
        this.machine = machine;
    }

    public Machine getMachine() {
        return machine;
    }

    @Embedded
    public static class Item {
        public Map<String, Object> map;

        public Item(org.bukkit.inventory.ItemStack item) {
            if(item != null) {
                this.map = item.serialize();
            }
        }
        public Item(){}
    }
}
