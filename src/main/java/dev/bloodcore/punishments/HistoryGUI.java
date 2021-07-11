package dev.bloodcore.punishments;

import dev.bloodcore.Core;
import dev.bloodcore.etc.User;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.ItemBuilder;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class HistoryGUI {
    private final ItemStack filler = ItemBuilder.create(Material.STAINED_GLASS_PANE).dura(15).name(" ").build();
    @Getter private final Map<User, Document> currentlyOpen = new HashMap<>();

    public void open(User user, Document target) {
        Inventory inv = Bukkit.createInventory(null, 36, ChatUtil.color("&6History &8» &a" + target.getString("name")));
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        DateFormat format = Core.i().getDateFormat();
        String timezone = "&7(" + format.getTimeZone().getDisplayName(format.getTimeZone().inDaylightTime(new Date()), TimeZone.SHORT) + ")";
        Rank rank = Core.i().getRankManager().getRank(target.getString("rank"), true);
        inv.setItem(4, ItemBuilder.create(Material.SKULL_ITEM)
                .owner(target.getString("name"))
                .name(rank.getPrefix() + target.getString("name"))
                .lore("&eFirst Join: &a" + format.format(new Date(target.get("data", Document.class).getLong("firstJoin"))) + timezone)
                .lore("&eLast Online: &a" + format.format(new Date(target.get("data", Document.class).getLong("lastJoin"))) + timezone)
                .build());

        for (int i = 0; i < 5; i++) {
            PType type = PType.values()[i];
            inv.setItem(i + 20, ItemBuilder.create(Material.BOOK).build());
        }

        user.getPlayer().openInventory(inv);
        currentlyOpen.put(user, target);
    }

    public void handle(InventoryClickEvent e) {

    }
}
