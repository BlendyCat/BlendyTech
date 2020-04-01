package com.blendycat.blendytech.machine.inventory;

import com.blendycat.blendytech.machine.Machine;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

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
