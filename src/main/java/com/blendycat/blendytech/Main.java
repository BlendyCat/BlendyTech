package com.blendycat.blendytech;

import com.blendycat.blendytech.item.CustomItemListener;
import com.blendycat.blendytech.item.CustomRecipes;
import com.blendycat.blendytech.machine.Machine;
import com.blendycat.blendytech.machine.BoringMinecart;
import com.blendycat.blendytech.machine.inventory.FuelInventory;
import com.blendycat.blendytech.machine.inventory.RailInventory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin {

    private static Main instance;

    private static List<Machine> machines;

    @Override
    public void onEnable() {
        instance = this;

        CustomRecipes customRecipes = new CustomRecipes(this);
        customRecipes.registerRecipes();

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(customRecipes, this);
        pluginManager.registerEvents(new CustomItemListener(), this);
        pluginManager.registerEvents(new BoringMinecart.Events(), this);
        pluginManager.registerEvents(new FuelInventory.Events(), this);
        pluginManager.registerEvents(new RailInventory.Events(), this);

        machines = new ArrayList<>();
        File file = new File(getDataFolder(), "saves.json");
        if(file.exists()) {
            try {
                Reader reader = new FileReader(file);
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(reader);
                for(Object object: array) {
                    Map<String, Object> map = (Map<String, Object>) object;
                    if(map.get("type").equals(BoringMinecart.TYPE)) {
                        BoringMinecart cart = BoringMinecart.deserialize(map);
                        if(cart != null) machines.add(cart);
                    }
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
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
        JSONArray array = new JSONArray();
        for(Machine machine : machines) {
            array.add(machine.serialize());
        }
        File file = new File(getDataFolder(), "saves.json");
        try {
            if(!file.exists()) {
                getDataFolder().mkdir();
            }
            FileWriter writer = new FileWriter(file);
            writer.write(array.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Main getInstance() {
        return instance;
    }


    public static List<Machine> getMachines() {
        return machines;
    }


}
