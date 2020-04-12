package com.blendycat.blendytech;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.blendycat.blendytech.item.CustomItemListener;
import com.blendycat.blendytech.item.CustomRecipes;
import com.blendycat.blendytech.machine.BoringMinecart;
import com.blendycat.blendytech.machine.Machine;
import com.blendycat.blendytech.machine.inventory.FuelInventory;
import com.blendycat.blendytech.machine.inventory.RailInventory;
import com.blendycat.blendytech.machine.inventory.StorageInventory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class Main extends JavaPlugin {

    private static Main instance;

    private final Map<UUID, Machine> machines = new HashMap<>();
    private final Map<UUID, Machine> unloaded = new HashMap<>();

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
        pluginManager.registerEvents(new StorageInventory.Events(), this);
        
        File file = new File(getDataFolder(), "saves.json");
        
        if(file.exists()) {
            try(Reader reader = new FileReader(file)) {
                JsonParser parser = new JsonParser();
                JsonArray array = parser.parse(reader).getAsJsonArray();
                
                for(JsonElement element: array) {
                    JsonObject obj = element.getAsJsonObject();
                    
                    if(obj.get("type").getAsString().equals(BoringMinecart.TYPE)) {
                        Map<String, Object> map = serializeJson(obj);
                        
                        BoringMinecart cart = BoringMinecart.deserialize(map);
                        machines.put(cart.getUUID(), cart);
                        if(cart.getMinecart() == null) unloaded.put(cart.getUUID(), cart);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, ()-> {
            for(Machine machine : machines.values()) {
                machine.update();
            }
        }, 1,1);
    }

    // This is a rather hacky solution but it conserves your existing way of (de)serializing
    private Map<String, Object> serializeJson(JsonObject obj) {
        Map<String, Object> map = new HashMap<>();
        
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            if (entry.getValue().isJsonObject()) {
                map.put(entry.getKey(), serializeJson(entry.getValue().getAsJsonObject()));
            }
            else if (entry.getValue().isJsonPrimitive()) {
                JsonPrimitive primitive = entry.getValue().getAsJsonPrimitive();
                
                if (primitive.isNumber()) {
                    double number = primitive.getAsDouble();
                    int integer = primitive.getAsInt();
                    
                    if (integer == number) {
                        map.put(entry.getKey(), integer);
                    }
                    else {
                        map.put(entry.getKey(), number);
                    }
                }
                else if (primitive.isBoolean()) {
                    map.put(entry.getKey(), entry.getValue().getAsBoolean());
                }
                else if (primitive.isString()) {
                    map.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
        }
        
        return map;
    }

    @Override
    public void onDisable() {
        JsonArray array = new JsonArray();
        for(Machine machine : machines.values()) {
            array.add(new Gson().toJsonTree(machine.serialize()));
        }
        File file = new File(getDataFolder(), "saves.json");

        if(!file.exists()) {
            getDataFolder().mkdir();
        }
        
        try(FileWriter writer = new FileWriter(file)) {
            writer.write(array.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Main getInstance() {
        return instance;
    }


    public static Map<UUID, Machine> getMachines() {
        return instance.machines;
    }

    public static Map<UUID, Machine> getUnloaded() {
        return instance.unloaded;
    }


}
