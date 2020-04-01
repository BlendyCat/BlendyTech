package com.blendycat.blendytech.machine.inventory;

import com.blendycat.blendytech.machine.Machine;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FuelInventory extends MachineInventory {

    private Inventory inventory;

    public FuelInventory(Machine machine, int size, String name) {
        super(machine);
        inventory = Bukkit.createInventory(this, size, name);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
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
