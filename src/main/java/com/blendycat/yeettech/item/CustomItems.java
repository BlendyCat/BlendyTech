package com.blendycat.yeettech.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum CustomItems {
    BORING_MINECART(Material.FURNACE_MINECART, ChatColor.AQUA + "Boring Minecart"),
    BORING_UNIT(Material.DISPENSER, ChatColor.AQUA + "Boring Unit"),
    BORING_DRILL(Material.HOPPER, ChatColor.AQUA + "Boring Drill"),
    COMPUTER_UNIT(Material.OBSERVER, ChatColor.AQUA + "Computer Unit"),
    MOTHERBOARD(Material.DAYLIGHT_DETECTOR, ChatColor.AQUA + "Motherboard"),
    INTEGRATED_CIRCUIT(Material.STONE_BUTTON, ChatColor.AQUA + "Integrated Circuit"),
    ;

    private ItemStack item;
    private String displayName;
    private Material material;

    CustomItems(Material material, String displayName) {
        // set the enum variables to the passed in values
        this.material = material;
        this.displayName = displayName;
        item = new ItemStack(material);
        // get the item meta
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            // set the display name to the meta
            meta.setDisplayName(displayName);
        }
        // set the meta to the item
        item.setItemMeta(meta);
    }

    public ItemStack getItem() {
        return item;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean matches(ItemStack item1) {
        if(item1 != null) {
            if (item1.hasItemMeta()) {
                ItemMeta meta = item1.getItemMeta();
                // null check meta
                if (meta != null) {
                    return meta.getDisplayName().equals(displayName);
                }
            }
        }
        return false;
    }
}
