package com.blendycat.blendytech.machine;

import org.bukkit.inventory.InventoryHolder;

import java.io.Serializable;
import java.util.Map;

public interface Machine extends InventoryHolder {

    // update method to be run every tick
    void update();
    // should be serializable
    Map<String, Object> serialize();
}
