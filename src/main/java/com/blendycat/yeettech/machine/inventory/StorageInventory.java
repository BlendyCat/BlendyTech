package com.blendycat.yeettech.machine.inventory;

import com.blendycat.yeettech.machine.Machine;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class StorageInventory extends MachineInventory {

    private Inventory inventory;

    public StorageInventory(Machine machine, int size, String name) {
        super(machine);
        inventory = Bukkit.createInventory(this, size, name);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
