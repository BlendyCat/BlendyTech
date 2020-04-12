package com.blendycat.blendytech.machine;

import java.util.Map;

import org.bukkit.inventory.InventoryHolder;

public interface Machine extends InventoryHolder {

    // update method to be run every tick
    void update();
    // should be serializable
    Map<String, Object> serialize();
}
