package dev.bloodcore.staff;

import dev.bloodcore.Core;
import dev.bloodcore.etc.GUI;
import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.ItemBuilder;
import dev.bloodcore.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class StaffGUI implements GUI {
    private static final ItemStack filler = ItemBuilder.create(Material.STAINED_GLASS_PANE).dura(15).name(" ").build();
    private final Player holder;
    private final Inventory inv;
    private int page, staffCount;

    public StaffGUI(Player p) {
        holder = p;
        inv = Bukkit.createInventory(null, 36, ChatUtil.color("&6&lOnline Staff"));

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, filler);
            inv.setItem(i + 27, filler);
        }

        page = 1;
        reloadPage();
        p.openInventory(inv);
    }


    public void handle(InventoryEvent event) {
        if (event instanceof InventoryClickEvent) {
            InventoryClickEvent e = (InventoryClickEvent) event;
            if (!e.getView().getTitle().equals(inv.getTitle())) return;
            e.setCancelled(true);

            if (e.getRawSlot() == 0) {
                page--;
                reloadPage();
            } else if (e.getRawSlot() == 8) {
                page++;
                reloadPage();
            } else if (e.getAction() == InventoryAction.PICKUP_HALF) {
                ItemStack item = e.getCurrentItem();
                if (item != null && item.getType() == Material.SKULL_ITEM) {
                    if (e.getWhoClicked().hasPermission("blood.command.tp")) {
                        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                        String owner = skullMeta.getOwner();
                        ((Player) e.getWhoClicked()).performCommand("tp " + owner);
                    }
                }
            }

        }
    }

    private void reloadPage() {
        int maxPage = Math.max((int) Math.ceil(staffCount / 18.0), 1);
        if (page > maxPage) {
            page = maxPage;
            return;
        } else if (page < 1) {
            page = 1;
            return;
        }
        inv.clear();
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, filler);
            inv.setItem(i + 27, filler);
        }
        inv.setItem(0, ItemBuilder.create(Material.CARPET).dura(14).name("&cLast Page").build());
        inv.setItem(8, ItemBuilder.create(Material.CARPET).dura(14).name("&cNext Page").build());
        Bukkit.getScheduler().runTaskAsynchronously(Core.i(), () -> {
            List<User> staff = PlayerUtil.getOnlineStaff();
            staffCount = staff.size();

            int slot = 9;
            int index = (page - 1) * 18;
            while (index < Math.min(page * 18, staff.size())) {
                User user = staff.get(index++);
                ItemBuilder item = ItemBuilder.create(Material.SKULL_ITEM).name(user.getRank().getPrefix() + user.name())
                        .lore("&7&m---------------------------------")
                        .lore("&8» &eRank: " + user.getRank().getColor() + user.getRank().getId())
                        .lore("&8» &eVanish: " + (user.isVanished() ? "&aEnabled" : "&cDisabled"))
                        .lore("&8» &eStaff Mode: " + (user.isStaffMode() ? "&aEnabled" : "&cDisabled"));
                if (holder.hasPermission("blood.command.tp")) {
                    item.lore(" ").lore("&7Right-Click to teleport.");
                }
                item.lore("&7&m---------------------------------");
                item.owner(user.name());
                item.dura(3);
                inv.setItem(slot++, item.build());
            }
        });

    }
}
