package com.blendycat.yeettech;

import com.blendycat.yeettech.item.CustomItemListener;
import com.blendycat.yeettech.item.CustomRecipes;
import com.blendycat.yeettech.machine.Machine;
import com.blendycat.yeettech.machine.BoringMinecart;
import com.blendycat.yeettech.machine.inventory.FuelInventory;
import com.blendycat.yeettech.machine.inventory.RailInventory;
import com.mongodb.MongoClient;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {

    private static Main instance;

    private static MongoClient mc;
    private static Morphia morphia;
    private static Datastore datastore;

    private static List<Machine> machines;

    @Override
    public void onEnable() {
        instance = this;

        mc = new MongoClient();
        morphia = new Morphia();
        morphia.map();

        datastore = morphia.createDatastore(mc, "Minecraft");
        datastore.ensureIndexes();

        CustomRecipes customRecipes = new CustomRecipes(this);
        customRecipes.registerRecipes();

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(customRecipes, this);
        pluginManager.registerEvents(new CustomItemListener(), this);
        pluginManager.registerEvents(new BoringMinecart.Events(), this);
        pluginManager.registerEvents(new FuelInventory.Events(), this);
        pluginManager.registerEvents(new RailInventory.Events(), this);

        machines = new ArrayList<>();

        Query<BoringMinecart.StorageObject> query = datastore.createQuery(BoringMinecart.StorageObject.class);
        List<BoringMinecart.StorageObject> results = query.asList();
        for(BoringMinecart.StorageObject object : results) {
            BoringMinecart cart = BoringMinecart.load(datastore, object);
            if(cart != null) {
                machines.add(BoringMinecart.load(datastore, object));
            }
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), ()-> {
            for(Machine machine : machines) {
                machine.update();
            }
        }, 1,1);
    }

    @Override
    public void onDisable() {
        for(Machine machine : machines) {
            machine.save(datastore);
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public static Datastore getDatastore() {
        return datastore;
    }


    public static List<Machine> getMachines() {
        return machines;
    }


}
