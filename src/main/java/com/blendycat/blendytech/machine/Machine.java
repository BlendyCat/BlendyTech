package com.blendycat.blendytech.machine;

import org.bukkit.inventory.InventoryHolder;
import org.mongodb.morphia.Datastore;

public interface Machine extends InventoryHolder {

    void update();

    void save(Datastore ds);

}
