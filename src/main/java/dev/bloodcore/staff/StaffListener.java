package dev.bloodcore.staff;

import dev.bloodcore.Core;
import dev.bloodcore.etc.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class StaffListener implements Listener {

    @EventHandler
    public void on(PlayerInteractEvent e) {
        User user = Core.i().getUser(e.getPlayer().getName());
        if (user != null) {
            if (user.isStaffMode()) {
                e.setCancelled(true);
                ItemStack item = e.getItem();
                if (item != null) {
                    if (item.getType() == Material.INK_SACK && e.getAction() == Action.RIGHT_CLICK_AIR) {
                        e.getPlayer().performCommand("vanish");
                    } else if (item.getType() == Material.SKULL_ITEM && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                        e.getPlayer().performCommand("stafflist");
                    }
                }
            } else if (user.isVanished()) {
                if (e.getAction() == Action.PHYSICAL) {
                    e.setCancelled(true);
                }
            }
            /*
            if (user.isVanished()) {
                e.setCancelled(true);
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof InventoryHolder) {
                        InventoryHolder holder = (InventoryHolder) e.getClickedBlock().getState();
                        Inventory inventory = holder.getInventory();

                        Inventory fakeInv = Bukkit.createInventory(null, inventory.getSize(), ChatUtil.color("&7Silent Chest"));
                        fakeInv.setContents(inventory.getContents());

                        e.getPlayer().openInventory(fakeInv);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (e.getPlayer().getInventory().getName().contains("Silent Chest")) {

                                    cancel();
                                }
                                inventory.setContents(fakeInv.getContents());


                            }
                        }.runTaskTimer(Core.i(), 0L, 2L);

                    }
                }
            }
             */
        }
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        User user = Core.i().getUser(e.getWhoClicked().getName());
        if (user != null) {
            if (user.isStaffMode()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(BlockBreakEvent e) {
        User user = Core.i().getUser(e.getPlayer().getName());
        if (user != null) {
            if (user.isStaffMode()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PlayerMoveEvent e) {
        User user = Core.i().getUser(e.getPlayer().getName());
        if (user != null) {
            if (user.isFrozen()) {
               if(e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
                   e.setTo(e.getFrom());
               }
            }
        }
    }

    @EventHandler
    public void on(BlockPlaceEvent e) {
        User user = Core.i().getUser(e.getPlayer().getName());
        if (user != null) {
            if (user.isStaffMode()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent e) {
        User user = Core.i().getUser(e.getPlayer().getName());
        if (user != null) {
            if (user.isStaffMode() || user.isVanished()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent e) {
        User user = Core.i().getUser(e.getDamager().getName());
        if (e.getDamager() instanceof Player && user != null) {
            if (user.isStaffMode() || user.isVanished() || user.isFrozen()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(FoodLevelChangeEvent e) {
        User user = Core.i().getUser(e.getEntity().getName());
        if (e.getEntity() instanceof Player && user != null) {
            if (user.isStaffMode() || user.isVanished()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(EntityDamageEvent e) {
        User user = Core.i().getUser(e.getEntity().getName());
        if (e.getEntity() instanceof Player && user != null) {
            if (user.isStaffMode() || user.isVanished() || user.isFrozen()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PlayerPickupItemEvent e) {
        User user = Core.i().getUser(e.getPlayer().getName());
        if (user != null) {
            if (user.isStaffMode() || user.isVanished()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(InventoryDragEvent e) {
        User user = Core.i().getUser(e.getWhoClicked().getName());
        if (user != null) {
            if (user.isStaffMode()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PlayerInteractAtEntityEvent e) {
        User user = Core.i().getUser(e.getPlayer().getName());
        if (user != null) {
            if (user.isStaffMode() && e.getRightClicked() instanceof Player) {
                e.setCancelled(true);
                ItemStack item = e.getPlayer().getItemInHand();
                if (item != null) {
                    if (item.getType() == Material.BOOK) {
                        e.getPlayer().performCommand("invsee " + e.getRightClicked().getName());
                    } else if (item.getType() == Material.ICE) {
                        e.getPlayer().performCommand("freeze " + e.getRightClicked().getName());
                    }
                }
            }
        }
    }

    @EventHandler
    public void on(EntityTargetEvent e) {
        User user = Core.i().getUser(e.getTarget().getName());
        if (user != null && e.getTarget() instanceof Player) {
            if (user.isStaffMode() || user.isVanished()) {
                e.setCancelled(true);
            }
        }
    }
}
