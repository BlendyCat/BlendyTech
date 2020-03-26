package com.blendycat.yeettech.item;

import com.blendycat.yeettech.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.*;

public class CustomRecipes implements Listener {

    private Main plugin;

    public CustomRecipes(Main plugin) {
        this.plugin = plugin;
    }

    public void registerRecipes() {
        Server server = plugin.getServer();
        // Integrated Circuit
        ShapedRecipe integratedCircuit = new ShapedRecipe(
                new NamespacedKey(plugin, "int-circuit"),
                CustomItems.INTEGRATED_CIRCUIT.getItem()
        );
        integratedCircuit.shape("CRC","RTR","CRC");
        integratedCircuit.setIngredient('C', Material.COMPARATOR);
        integratedCircuit.setIngredient('R', Material.REDSTONE);
        integratedCircuit.setIngredient('T', Material.REDSTONE_TORCH);
        server.addRecipe(integratedCircuit);

        // Motherboard
        ShapedRecipe motherboard = new ShapedRecipe(
                new NamespacedKey(plugin,"motherboard"),
                CustomItems.MOTHERBOARD.getItem()
        );
        motherboard.shape("III","III","GGG");
        motherboard.setIngredient('I', CustomItems.INTEGRATED_CIRCUIT.getMaterial());
        motherboard.setIngredient('G', Material.GOLD_BLOCK);
        server.addRecipe(motherboard);

        // Computer Unit
        ShapedRecipe computerUnit = new ShapedRecipe(
                new NamespacedKey(plugin, "computer-unit"),
                CustomItems.COMPUTER_UNIT.getItem()
        );

        computerUnit.shape("III","IMI","IRI");
        computerUnit.setIngredient('I', Material.IRON_BLOCK);
        computerUnit.setIngredient('M', CustomItems.MOTHERBOARD.getMaterial());
        computerUnit.setIngredient('R', Material.REDSTONE);
        server.addRecipe(computerUnit);

        // Boring Drill
        ShapedRecipe boringDrill = new ShapedRecipe(
                new NamespacedKey(plugin, "boring-drill"),
                CustomItems.BORING_DRILL.getItem()
        );
        boringDrill.shape("PPP","PPP","PPP");
        boringDrill.setIngredient('P', Material.DIAMOND_PICKAXE);
        server.addRecipe(boringDrill);

        // Boring Unit
        ShapelessRecipe boringUnit = new ShapelessRecipe(
                new NamespacedKey(plugin, "boring-unit"),
                CustomItems.BORING_UNIT.getItem()
        );
        boringUnit.addIngredient(CustomItems.BORING_DRILL.getMaterial());
        boringUnit.addIngredient(CustomItems.COMPUTER_UNIT.getMaterial());
        server.addRecipe(boringUnit);

        // Boring Minecart
        ShapelessRecipe boringMinecart = new ShapelessRecipe(
                new NamespacedKey(plugin, "boring-minecart"),
                CustomItems.BORING_MINECART.getItem()
        );
        boringMinecart.addIngredient(CustomItems.BORING_UNIT.getMaterial());
        boringMinecart.addIngredient(Material.MINECART);
        server.addRecipe(boringMinecart);
    }

    @EventHandler
    public void onCraftEvent(PrepareItemCraftEvent e) {
        if(e.getRecipe() != null) {
            Recipe recipe = e.getRecipe();
            CraftingInventory table = e.getInventory();
            ItemStack[] matrix = table.getMatrix();
            if(CustomItems.MOTHERBOARD.matches(recipe.getResult())) {
                for(int i = 0; i < 6; i++) {
                    ItemStack item = matrix[i];
                    if(!CustomItems.INTEGRATED_CIRCUIT.matches(item)) {
                        table.setResult(null);
                        break;
                    }
                }
            } else if(CustomItems.COMPUTER_UNIT.matches(recipe.getResult())) {
                if(!CustomItems.MOTHERBOARD.matches(matrix[4])) {
                    table.setResult(null);
                }
            } else if(CustomItems.BORING_UNIT.matches(recipe.getResult())) {
                for(ItemStack item : matrix) {
                    if (!(item == null || CustomItems.COMPUTER_UNIT.matches(item) || CustomItems.BORING_DRILL.matches(item))) {
                        table.setResult(null);
                        break;
                    }
                }
            } else if(CustomItems.BORING_MINECART.matches(recipe.getResult())) {
                for(ItemStack item : matrix) {
                    if(!(item == null || item.getType() == Material.MINECART || CustomItems.BORING_UNIT.matches(item))) {
                        table.setResult(null);
                        break;
                    }
                }
            }
        }
    }
}
