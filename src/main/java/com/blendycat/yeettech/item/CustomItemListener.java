package com.blendycat.yeettech.item;

import com.blendycat.yeettech.Main;
import com.blendycat.yeettech.machine.BoringMinecart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class CustomItemListener implements Listener {

    public CustomItemListener() {
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if(CustomItems.INTEGRATED_CIRCUIT.matches(item) ||
        CustomItems.BORING_UNIT.matches(item) ||
        CustomItems.BORING_DRILL.matches(item) ||
        CustomItems.COMPUTER_UNIT.matches(item) ||
        CustomItems.MOTHERBOARD.matches(item)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        if(CustomItems.BORING_MINECART.matches(e.getItem())) {
            Block clicked = e.getClickedBlock();
            if(clicked != null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if(clicked.getType() == Material.RAIL ||
                clicked.getType() == Material.POWERED_RAIL ||
                clicked.getType() == Material.DETECTOR_RAIL ||
                clicked.getType() == Material.ACTIVATOR_RAIL) {
                    e.setCancelled(true);
                    World world = clicked.getWorld();
                    Location location = clicked.getLocation();
                    // Minecart
                    Minecart minecart = (Minecart) world.spawnEntity(location, EntityType.MINECART);
                    float yaw = minecart.getLocation().getYaw();

                    minecart.setCustomName(BoringMinecart.MINECART_NAME);
                    minecart.setDisplayBlockData(Material.DISPENSER.createBlockData());
                    // ArmorStand
                    ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setInvulnerable(true);
                    armorStand.setCustomName(BoringMinecart.ARMORSTAND_NAME);
                    armorStand.setCustomNameVisible(false);
                    armorStand.setRightArmPose(new EulerAngle(-1, 0 ,0));
                    EntityEquipment equipment = armorStand.getEquipment();
                    if(equipment != null) equipment.setItemInMainHand(new ItemStack(Material.DIAMOND_PICKAXE));
                    // Riding time
                    minecart.addPassenger(armorStand);
                    BoringMinecart boringMinecart = new BoringMinecart(minecart, armorStand);
                    Main.getMachines().add(boringMinecart);
                    e.getItem().setAmount(e.getItem().getAmount()-1);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), ()-> {
                        minecart.setRotation(yaw, 0);
                    }, 2);
                }
            }
        }
    }
}
