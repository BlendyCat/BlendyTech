package com.blendycat.yeettech.machine;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.mongodb.morphia.Datastore;

public interface Machine extends InventoryHolder {

    void update();

    void save(Datastore ds);

}
