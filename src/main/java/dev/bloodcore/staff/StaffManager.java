package dev.bloodcore.staff;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import dev.bloodcore.api.events.PlayerToggleStaffEvent;
import dev.bloodcore.api.events.PlayerToggleVanishEvent;
import dev.bloodcore.etc.User;
import dev.bloodcore.etc.YamlStorage;
import dev.bloodcore.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;

@Getter
public class StaffManager {
    private ItemStack compass, inspect, freeze, carpet, staffOnline, vanish, unvanish;
    private int compassSlot, inspectSlot, freezeSlot, carpetSlot, staffOnlineSlot, vanishSlot;

    private final YamlStorage staffConfig = new YamlStorage(new File(Core.i().getDataFolder(), "staffconfig.yml"));

    public StaffManager() {
        setItems();
    }

    public void reload() {
        staffConfig.reload();
        staffConfig.copyDefaults();
        staffConfig.addDefault("vanish.enable-on-join", true);
        staffConfig.addDefault("staffmode.enable-on-join", true);
        staffConfig.addDefault("staffmode.compass.slot", 0);
        staffConfig.addDefault("staffmode.compass.name", "&ePhase Compass");
        staffConfig.addDefault("staffmode.compass.lore", ImmutableList.of("&7WorldEdit navigation tool."));
        staffConfig.addDefault("staffmode.inspect.slot", 1);
        staffConfig.addDefault("staffmode.inspect.name", "&eInspection Book");
        staffConfig.addDefault("staffmode.inspect.lore", ImmutableList.of("&7Right-Click a player to view their inventory."));
        staffConfig.addDefault("staffmode.freeze.slot", 2);
        staffConfig.addDefault("staffmode.freeze.name", "&eFreeze");
        staffConfig.addDefault("staffmode.freeze.lore", ImmutableList.of("&7Right-Click a player to freeze/unfreeze them."));
        staffConfig.addDefault("staffmode.carpet.slot", -1);
        staffConfig.addDefault("staffmode.carpet.data", 1);
        staffConfig.addDefault("staffmode.carpet.name", "&eBetter Looking");
        staffConfig.addDefault("staffmode.carpet.lore", ImmutableList.of("&7Hold this to hide your hand."));
        staffConfig.addDefault("staffmode.staff-online.slot", 7);
        staffConfig.addDefault("staffmode.staff-online.name", "&eStaff Online");
        staffConfig.addDefault("staffmode.staff-online.lore", ImmutableList.of("&7Right-Click to view other online staff."));
        staffConfig.addDefault("staffmode.vanish.slot", 8);
        staffConfig.addDefault("staffmode.vanish.name", "&eBecome Invisible");
        staffConfig.addDefault("staffmode.unvanish.name", "&eBecome Visible");
        staffConfig.addDefault("staffmode.vanish.lore", ImmutableList.of("&7Right-Click to vanish."));
        staffConfig.addDefault("staffmode.unvanish.lore", ImmutableList.of("&7Right-Click to unvanish."));
        staffConfig.save();

        setItems();
    }

    public void setItems() {
        for (User user : Core.i().getUsers()) {
            if (user.isStaffMode()) {
                leaveStaffMode(user);
            }
            if (user.isVanished()) {
                unvanish(user);
            }
        }

        //totally not messy at all
        compassSlot = staffConfig.getInt("staffmode.compass.slot");
        compass = ItemBuilder.create(Material.COMPASS).name(staffConfig.getString("staffmode.compass.name"))
                        .lore(staffConfig.getStringList("staffmode.compass.lore")).build();

        inspectSlot = staffConfig.getInt("staffmode.inspect.slot");
        inspect = ItemBuilder.create(Material.BOOK).name(staffConfig.getString("staffmode.inspect.name"))
                .lore(staffConfig.getStringList("staffmode.inspect.lore")).build();

        freezeSlot = staffConfig.getInt("staffmode.freeze.slot");
        freeze = ItemBuilder.create(Material.ICE).name(staffConfig.getString("staffmode.freeze.name"))
                .lore(staffConfig.getStringList("staffmode.freeze.lore")).build();

        carpetSlot = staffConfig.getInt("staffmode.carpet.slot");
        carpet = ItemBuilder.create(Material.CARPET).dura(staffConfig.getInt("staffmode.carpet.data")).name(staffConfig.getString("staffmode.carpet.name"))
                .lore(staffConfig.getStringList("staffmode.carpet.lore")).build();

        staffOnlineSlot = staffConfig.getInt("staffmode.staff-online.slot");
        staffOnline = ItemBuilder.create(Material.SKULL_ITEM).dura(3).name(staffConfig.getString("staffmode.staff-online.name"))
                .lore(staffConfig.getStringList("staffmode.staff-online.lore")).build();

        vanishSlot = staffConfig.getInt("staffmode.vanish.slot");
        vanish = ItemBuilder.create(Material.INK_SACK).dura(10).name(staffConfig.getString("staffmode.vanish.name"))
                        .lore(staffConfig.getStringList("staffmode.vanish.lore")).build();

        unvanish = ItemBuilder.create(Material.INK_SACK).dura(8).name(staffConfig.getString("staffmode.unvanish.name"))
                        .lore(staffConfig.getStringList("staffmode.unvanish.lore")).build();
    }

    public void vanish(User user) {
        PlayerToggleVanishEvent event = new PlayerToggleVanishEvent(user.getPlayer(), PlayerToggleVanishEvent.State.ENTER);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        user.setVanished(true);
        user.getPlayer().setMetadata("vanished", new FixedMetadataValue(Core.i(), true));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("blood.vanish.see")) {
                player.hidePlayer(user.getPlayer());
            }
        }

        if (user.isStaffMode()) {
            user.getPlayer().getInventory().setItem(vanishSlot, unvanish);
        }
    }

    public void unvanish(User user) {
        PlayerToggleVanishEvent event = new PlayerToggleVanishEvent(user.getPlayer(), PlayerToggleVanishEvent.State.LEAVE);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        user.setVanished(false);
        user.getPlayer().removeMetadata("vanished", Core.i());

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.canSee(user.getPlayer())) {
                player.showPlayer(user.getPlayer());
            }
        }

        if (user.isStaffMode()) {
            user.getPlayer().getInventory().setItem(vanishSlot, vanish);
        }
    }

    public void enterStaffMode(User user) {
        PlayerToggleStaffEvent event = new PlayerToggleStaffEvent(user.getPlayer(), PlayerToggleStaffEvent.State.ENTER);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        user.getPlayer().setAllowFlight(true);
        user.getPlayer().setFlying(true);
        user.getPlayer().setHealth(user.getPlayer().getMaxHealth());
        user.getPlayer().setFoodLevel(40);
        user.getPlayer().setSaturation(40);
        user.setStaffMode(true);
        user.getPlayer().setMetadata("staffmode", new FixedMetadataValue(Core.i(), true));
        ItemStack[] contents = new ItemStack[40];

        for (int i = 0; i < 40; i++) {
            contents[i] = user.getPlayer().getInventory().getItem(i);
            user.getPlayer().getInventory().setItem(i, null);
        }
        int[] slots = {compassSlot, inspectSlot, freezeSlot, carpetSlot, staffOnlineSlot};
        ItemStack[] items = {compass, inspect, freeze, carpet, staffOnline};
        for (int i = 0; i < 5; i++) {
            int slot = slots[i];
            ItemStack item = items[i];
            if (slot > -1 && slot < 9) {
                user.getPlayer().getInventory().setItem(slot, item);
            }
        }
        if (vanishSlot > -1 && vanishSlot < 9) {
            user.getPlayer().getInventory().setItem(vanishSlot, user.isVanished() ? unvanish : vanish);
        }

        user.getData().put("staffmode_inv", contents);
    }

    public void leaveStaffMode(User user) {
        PlayerToggleStaffEvent event = new PlayerToggleStaffEvent(user.getPlayer(), PlayerToggleStaffEvent.State.LEAVE);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        if (user.getPlayer().getGameMode() != GameMode.CREATIVE) {
            user.getPlayer().setAllowFlight(false);
            user.getPlayer().setFlying(false);
        }
        user.setStaffMode(false);
        user.getPlayer().removeMetadata("staffmode", Core.i());
        ItemStack[] contents = user.get("staffmode_inv");

        if (contents != null) {
            for (int i = 0; i < 40; i++) {
                user.getPlayer().getInventory().setItem(i, contents[i]);
            }
            user.getData().remove("staffmode_inv");
        }
    }
}
