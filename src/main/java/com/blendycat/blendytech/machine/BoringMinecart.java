package com.blendycat.blendytech.machine;

import com.blendycat.blendytech.Main;
import com.blendycat.blendytech.item.CustomItems;
import com.blendycat.blendytech.machine.inventory.FuelInventory;
import com.blendycat.blendytech.machine.inventory.MachineInventory;
import com.blendycat.blendytech.machine.inventory.RailInventory;
import com.blendycat.blendytech.machine.inventory.StorageInventory;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;


public class BoringMinecart implements Machine {

    /**
     * Buttons is an enum which represents all of the buttons that are used by this machine
     */
    public enum Button {
        START(Material.GREEN_WOOL, ChatColor.GREEN + "Start"),
        STOP(Material.RED_WOOL, ChatColor.RED + "Stop"),
        STORAGE(Material.CHEST, ChatColor.DARK_PURPLE + "Storage"),
        FUEL(Material.CHEST, ChatColor.DARK_PURPLE + "Fuel"),
        RAILS(Material.CHEST, ChatColor.DARK_PURPLE + "Rails")
        ;

        private ItemStack item;

        Button(Material material, String name) {
            item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(name);
                item.setItemMeta(meta);
            }
        }

        public ItemStack getItem() {
            return item;
        }

        public boolean matches(ItemStack item1) {
            return item.equals(item1);
        }
    }

    public static final String TYPE = "boring-minecart";

    private boolean running = false;
    private long burnTime = 0;
    private long mineTime;

    private Inventory controlInv;
    private StorageInventory storageInv;
    private RailInventory railsInv;
    private FuelInventory fuelInv;

    // the tool to break the blocks with
    private ItemStack tool;

    // final variables for naming inventory
    public static final String INVENTORY_NAME = ChatColor.LIGHT_PURPLE + "Boring Minecart";

    public static final String MINECART_NAME = ChatColor.AQUA + "Boring Minecart";
    public static final String ARMORSTAND_NAME = ChatColor.AQUA + "Boring Armorstand";

    // animation
    private float pickVelocity = -0.3f;
    private float pickPos = -1f;

    // tunnel dimensions
    private static final int TUNNEL_HEIGHT = 3;
    private static final int TUNNEL_WIDTH = 3;

    private Minecart minecart;
    private ArmorStand armorStand;
    private UUID minecartUUID;
    private UUID armorStandUUID;

    private int x = 0;
    private int z = 0;

    public BoringMinecart(Minecart minecart, ArmorStand armorStand) {
        this.minecart = minecart;
        this.armorStand = armorStand;

        this.minecartUUID = minecart.getUniqueId();
        this.armorStandUUID = armorStand.getUniqueId();

        controlInv = Bukkit.createInventory(this, 9, INVENTORY_NAME);
        storageInv = new StorageInventory(54, ChatColor.DARK_PURPLE + "Boring Minecart Storage");
        fuelInv = new FuelInventory(27, ChatColor.DARK_PURPLE + "Boring Minecart Fuel");
        railsInv = new RailInventory(27, ChatColor.DARK_PURPLE + "Boring Minecart Rails");
        // this is the tool that will be used to break the blocks
        tool = new ItemStack(Material.DIAMOND_PICKAXE);

        controlInv.setItem(2, Button.START.getItem());
        controlInv.setItem(3, Button.STOP.getItem());
        controlInv.setItem(4, Button.STORAGE.getItem());
        controlInv.setItem(5, Button.FUEL.getItem());
        controlInv.setItem(6, Button.RAILS.getItem());
    }

    protected BoringMinecart(UUID minecartUUID, UUID armorStandUUID) {
        this.minecartUUID = minecartUUID;
        this.armorStandUUID = armorStandUUID;

        minecart = (Minecart) Bukkit.getEntity(minecartUUID);
        armorStand = (ArmorStand) Bukkit.getEntity(armorStandUUID);

        controlInv = Bukkit.createInventory(this, 9, INVENTORY_NAME);

        controlInv.setItem(2, Button.START.getItem());
        controlInv.setItem(3, Button.STOP.getItem());
        controlInv.setItem(4, Button.STORAGE.getItem());
        controlInv.setItem(5, Button.FUEL.getItem());
        controlInv.setItem(6, Button.RAILS.getItem());
    }

    public void update() {
        if(Main.getUnloaded().containsKey(minecartUUID)) return;
        if(minecart == null || armorStand == null) {
            Main.getUnloaded().put(minecartUUID, this);
            return;
        }
        x = 0;
        z = 0;
        switch(minecart.getFacing()) {
            case NORTH:
                armorStand.setRotation(180, 0);
                z = -1;
                break;
            case EAST:
                x = 1;
                armorStand.setRotation(-90, 0);
                break;
            case SOUTH:
                z = 1;
                armorStand.setRotation(0, 0);
                break;
            case WEST:
                x = -1;
                armorStand.setRotation(90, 0);
                break;
        }
        if(running) {
            if(burnTime == 0) {
                Inventory fuel = fuelInv.getInventory();
                if(fuel.contains(Material.COAL, 1)) {
                    fuel.removeItem(new ItemStack(Material.COAL));
                    burnTime += 6*20;
                } else {
                    running = false;
                    mineTime = 0;
                    return;
                }
            }
            burnTime--;
            if(mineTime > 0) {
                mineTime--;
                // run animation calculation and update armorstand
                if (pickPos <= -1 || pickPos >= 0) {
                    pickVelocity *= -1;
                }
                pickPos += pickVelocity;
                armorStand.setRightArmPose(new EulerAngle(pickPos, 0, 0));

                if(mineTime == 0) {
                    for(Block block : getFacingWall()) {
                        if(block != null) {
                            if (block.getType().getHardness() >= 0) {
                                Inventory storage = storageInv.getInventory();
                                Collection<ItemStack> drops = block.getDrops(tool);
                                Collection<ItemStack> rejects = new ArrayList<>();
                                for(ItemStack i : drops) {
                                    if(i.hasItemMeta()) {
                                        rejects.add(i);
                                        drops.remove(i);
                                    }
                                }
                                block.setType(Material.AIR);
                                HashMap<Integer, ItemStack> unfilled = storage.addItem(drops.toArray(new ItemStack[0]));
                                if(unfilled.size() > 0) {
                                    running = false;
                                    rejects.addAll(unfilled.values());
                                }
                                for (ItemStack i : rejects) {
                                    minecart.getWorld().dropItemNaturally(block.getLocation(), i);
                                }
                            } else {
                                running = false;
                            }
                        }
                    }
                    if(placeRail()) {
                        minecart.setVelocity(new Vector(((double) x) / 5, 0, ((double) z) / 5));
                    } else {
                        running = false;
                    }
                }
            } else {
                if(getRelativeBlock(x,0,z).getType() != Material.RAIL) {
                    List<Block> wall = getFacingWall();
                    for (Block block : wall) {
                        if (block != null) {
                            if (block.getType().getHardness() >= 0 && !block.isLiquid()) {
                                mineTime += block.getType().getHardness() * 2;
                            } else {
                                running = false;
                                mineTime = 0;
                                break;
                            }
                        }
                    }
                    // check if there are blocks to break
                    if (mineTime > 0) {
                        minecart.setVelocity(new Vector(0, 0, 0));
                    } else {
                        // try to place rails
                        if(placeRail()) {
                            minecart.setVelocity(new Vector(((double) x) / 5, 0, ((double) z) / 5));
                        } else {
                            minecart.setVelocity(new Vector(0, 0, 0));
                            running = false;
                        }
                    }
                } else {
                    minecart.setVelocity(new Vector(((double) x) / 5, 0, ((double) z) / 5));
                }
            }
        }
    }

    private List<Block> getFacingWall() {
        Location location = minecart.getLocation();
        World world = minecart.getWorld();
        List<Block> blocks = new ArrayList<>();
        for (int w = 0; w < TUNNEL_WIDTH; w++) {
            for (int h = 0; h < TUNNEL_HEIGHT; h++) {
                int x1;
                int y1;
                int z1;
                if (x == 0) {
                    x1 = (location.getBlockX() - (TUNNEL_WIDTH - 1) / 2) + w;
                    y1 = location.getBlockY() + h;
                    z1 = location.getBlockZ() + z;
                } else {
                    x1 = location.getBlockX() + x;
                    y1 = location.getBlockY() + h;
                    z1 = (location.getBlockZ() - (TUNNEL_WIDTH - 1) / 2) + w;
                }
                Block block = world.getBlockAt(x1, y1, z1);
                blocks.add(block);
            }
        }
        return blocks;
    }

    private Block getRelativeBlock(int x, int y, int z) {
        Location location = minecart.getLocation();
        World world = minecart.getWorld();
        int x1 = location.getBlockX() + x;
        int y1 = location.getBlockY() + y;
        int z1 = location.getBlockZ() + z;
        return world.getBlockAt(x1, y1, z1);
    }

    private boolean placeRail() {
        if(getRelativeBlock(x, -1, z).getType().isSolid()) {
            Inventory rails = railsInv.getInventory();
            if (rails.contains(Material.RAIL, 1)) {
                Block block = getRelativeBlock(x, 0, z);
                if(block.getType() == Material.AIR) {
                    block.setType(Material.RAIL);
                    rails.removeItem(new ItemStack(Material.RAIL));
                }
                return true;
            }
        }
        return false;
    }

    public void setRunning(boolean running) {
        this.running = running;
        mineTime = 0;
    }

    public Minecart getMinecart() {
        return minecart;
    }

    public UUID getUUID() {
        return minecartUUID;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    @Override
    public Inventory getInventory() {
        return controlInv;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> object = new HashMap<>();
        object.put("type", TYPE);
        object.put("minecartUUID", minecartUUID.toString());
        object.put("armorStandUUID", armorStandUUID.toString());
        object.put("tool", tool.serialize());
        object.put("storage", storageInv.serialize());
        object.put("fuel", fuelInv.serialize());
        object.put("rails", railsInv.serialize());
        return object;
    }

    public static BoringMinecart deserialize(Map<String, Object> object) {
        UUID armorStand = UUID.fromString((String) object.get("armorStandUUID"));
        UUID minecart = UUID.fromString((String) object.get("minecartUUID"));
        BoringMinecart boringMinecart = new BoringMinecart(minecart, armorStand);
        boringMinecart.fuelInv = FuelInventory.deserialize((Map<String, Object>) object.get("fuel"));
        boringMinecart.storageInv = StorageInventory.deserialize((Map<String, Object>) object.get("storage"));
        boringMinecart.railsInv = RailInventory.deserialize((Map<String, Object>) object.get("rails"));
        boringMinecart.tool = ItemStack.deserialize((Map<String, Object>) object.get("tool"));

        return boringMinecart;
    }

    /**
     * Events handles all events related to the BoringMinecart
     */
    public static class Events implements Listener {

        @EventHandler
        public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
            Entity entity = e.getRightClicked();
            if(entity.getCustomName() != null) {
                if(entity.getCustomName().equals(BoringMinecart.ARMORSTAND_NAME) ||
                        entity.getCustomName().equals(BoringMinecart.MINECART_NAME)) {
                    e.setCancelled(true);
                    for(Machine machine : Main.getMachines().values()) {
                        if(machine instanceof BoringMinecart) {
                            BoringMinecart cart = (BoringMinecart) machine;
                            if(cart.minecartUUID.equals(entity.getUniqueId()) || cart.armorStandUUID.equals(entity.getUniqueId())) {
                                e.getPlayer().openInventory(cart.getInventory());
                                break;
                            }
                        }
                    }
                }
            }
        }

        @EventHandler
        public void onInventoryClickEvent(InventoryClickEvent e) {
            Inventory inventory = e.getInventory();
            Player player = (Player) e.getWhoClicked();
            if(inventory.getHolder() instanceof BoringMinecart) {
                e.setCancelled(true);
                BoringMinecart cart = (BoringMinecart) inventory.getHolder();
                ItemStack item = e.getCurrentItem();
                if(item != null) {
                    if(Button.START.matches(item)) {
                        cart.setRunning(true);
                        player.closeInventory();
                    } else if(Button.STOP.matches(item)) {
                        cart.setRunning(false);
                        cart.minecart.setVelocity(new Vector(0,0,0));
                        player.closeInventory();
                    } else if(Button.STORAGE.matches(item)) {
                        player.openInventory(cart.storageInv.getInventory());
                    } else if(Button.FUEL.matches(item)) {
                        player.openInventory(cart.fuelInv.getInventory());
                    } else if(Button.RAILS.matches(item)) {
                        player.openInventory(cart.railsInv.getInventory());
                    }
                }
            }
        }

        @EventHandler
        public void onVehicleDestroy(VehicleDestroyEvent e) {
            if(e.getVehicle() instanceof Minecart) {
                Minecart minecart = (Minecart) e.getVehicle();
                HashMap<UUID, Machine> machines = Main.getMachines();
                for(Machine machine : machines.values()) {
                    if(machine instanceof BoringMinecart) {
                        BoringMinecart cart = (BoringMinecart) machine;
                        if(cart.minecartUUID.equals(minecart.getUniqueId())) {
                            // cancel the event
                            e.setCancelled(true);
                            // get the location and world of the minecart
                            Location location = minecart.getLocation();
                            World world = minecart.getWorld();
                            // create a list of items to be dropped where the minecart is
                            List<ItemStack> drops = new ArrayList<>();
                            drops.add(CustomItems.BORING_MINECART.getItem());
                            drops.addAll(Arrays.asList(cart.storageInv.getInventory().getContents()));
                            drops.addAll(Arrays.asList(cart.fuelInv.getInventory().getContents()));
                            drops.addAll(Arrays.asList(cart.railsInv.getInventory().getContents()));
                            // remove the boring minecart from the machine list
                            machines.remove(cart.getUUID());
                            // remove all passengers from the minecart
                            for(Entity entity : minecart.getPassengers()) {
                                entity.remove();
                            }
                            // remove the minecart itself
                            minecart.remove();
                            // drop the items
                            for(ItemStack item : drops) {
                                if(item != null) world.dropItemNaturally(location, item);
                            }
                            // exit for loop
                            break;
                        }
                    }
                }
            }
        }

        @EventHandler
        public void onLoadChunk(ChunkLoadEvent e) {
            Entity[] entities = e.getChunk().getEntities();
            for(Entity entity : entities) {
                for(UUID uuid : Main.getUnloaded().keySet()) {
                    if(uuid.equals(entity.getUniqueId())) {
                        Machine machine = Main.getUnloaded().get(uuid);
                        if(machine instanceof BoringMinecart) {
                            BoringMinecart cart = (BoringMinecart) machine;
                            cart.minecart = (Minecart) entity;
                            cart.armorStand = (ArmorStand) Bukkit.getEntity(cart.armorStandUUID);
                            Main.getUnloaded().remove(uuid);
                        }
                    }
                }
            }
        }
    }
}
