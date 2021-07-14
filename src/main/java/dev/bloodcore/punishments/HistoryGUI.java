package dev.bloodcore.punishments;

import dev.bloodcore.Core;
import dev.bloodcore.etc.GUI;
import dev.bloodcore.etc.User;
import dev.bloodcore.ranks.Rank;
import dev.bloodcore.utils.ChatUtil;
import dev.bloodcore.utils.ItemBuilder;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class HistoryGUI implements GUI {
    private static final ItemStack filler = ItemBuilder.create(Material.STAINED_GLASS_PANE).dura(15).name(" ").build();
    private final Inventory inv;
    private boolean menu;
    private final Document target;
    private final User user;
    private int page, punishCount;
    private PType type;

    public HistoryGUI(User user, Document target) {
        this.user = user;
        this.target = target;
        inv = Bukkit.createInventory(null, 36, ChatUtil.color("&6&lHistory &8» " + target.getString("display")));
        mainMenu();
        user.getPlayer().openInventory(inv);
    }

    public void mainMenu() {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        DateFormat format = Core.i().getDateFormat();
        String timezone = " &7(" + format.getTimeZone().getDisplayName(format.getTimeZone().inDaylightTime(new Date()), TimeZone.SHORT) + ")";
        Rank rank = Core.i().getRankManager().getRank(target.getString("rank"), true);
        inv.setItem(4, ItemBuilder.create(Material.SKULL_ITEM).dura(3)
                .owner(target.getString("name"))
                .name(rank.getPrefix() + target.getString("name"))
                .lore("&eID: &7" + target.getString("uuid"))
                .lore(" ")
                .lore("&eFirst Join: &a" + format.format(new Date(target.get("data", Document.class).getLong("firstJoin"))) + timezone)
                .lore("&eLast Online: &a" + format.format(new Date(target.get("data", Document.class).getLong("lastJoin"))) + timezone)
                .build());

        for (int i = 0; i < 5; i++) {
            PType type = PType.values()[i];
            inv.setItem(i + 20, ItemBuilder.create(Material.BOOK).name("&c" + type.getDisplay() + "s").build());
        }

        menu = true;
        type = null;
    }

    public void handle(InventoryEvent event) {
        if (event instanceof InventoryClickEvent) {
            InventoryClickEvent e = (InventoryClickEvent) event;
            if (!e.getView().getTitle().equals(inv.getTitle())) return;
            e.setCancelled(true);
            if (menu) {
                if (e.getRawSlot() < 25 && e.getRawSlot() > 19) {
                    type = PType.values()[e.getRawSlot() - 20];
                    page = 1;
                    reloadPage();
                }
            } else {
                if (e.getRawSlot() == 0) {
                    page--;
                    reloadPage();
                } else if (e.getRawSlot() == 8) {
                    page++;
                    reloadPage();
                } else if (e.getRawSlot() == 31) {
                    mainMenu();
                } else {
                    ItemStack item = e.getCurrentItem();
                    if (item != null && item.getType() == Material.BOOK) {
                        if (e.getWhoClicked().hasPermission("blood.history.delete")) {
                            int id = Integer.parseInt(item.getItemMeta().getDisplayName().substring(6));
                            Core.i().getPunishmentManager().deletePunishment(id);
                            user.msg("&aSuccessfully deleted history for punishment &f#" + id + "&a.");
                            reloadPage();
                        }
                    }
                }
            }
        }
    }

    private void reloadPage() {
        menu = false;
        int maxPage = Math.max((int) Math.ceil(punishCount / 18.0), 1);
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
        inv.setItem(4, ItemBuilder.create(Material.PAPER).name("&6&l" + type.getDisplay() + "s").lore("&7Page: &f" + page).build());
        inv.setItem(31, ItemBuilder.create(Material.NETHER_STAR).name("&cBack to menu").build());
        Bukkit.getScheduler().runTaskAsynchronously(Core.i(), ()-> {
            List<Document> punishments = new ArrayList<>();
            for (Document data : Core.i().getPunishmentManager().getPunishments(target.getString("uuid"), type)) {
                punishments.add(data);
            }

            punishCount = punishments.size();

            int slot = 9;
            int index = (page - 1) * 18;
            while (index < Math.min(page * 18, punishments.size())) {
                Document data = punishments.get(index++);
                String timezone = Core.i().getDateFormat().getTimeZone().getDisplayName(Core.i().getDateFormat().getTimeZone().inDaylightTime(new Date()), TimeZone.SHORT);
                boolean inactive = data.containsKey("removed") || (data.getLong("length") != -1 && System.currentTimeMillis() > data.getLong("time") + data.getLong("length"));
                Document executedBy = data.containsKey("executor") ? Core.i().getMongoManager().getUserFromUUID(data.getString("executor")) : null;
                Document removedBy = data.containsKey("removed") ? Core.i().getMongoManager().getUserFromUUID(data.get("removed", Document.class).getString("by")) : null;
                ItemBuilder item = ItemBuilder.create(Material.BOOK).name("&6ID: " + data.getInteger("_id"))
                        .lore("&7&m---------------------------------")
                        .lore("&8» &eStatus: " + (inactive ? "&cInactive" : "&aActive"))
                        .lore("&8» &ePunished By: &f" + (executedBy == null ? "Console" : executedBy.getString("name")))
                        .lore("&8» &eRemoved By: " + (data.containsKey("removed") ? (removedBy == null ? "&fConsole" : removedBy.getString("name")) : "&cN/A"))
                        .lore("&8» &eDate Of: &f" + Core.i().getDateFormat().format(new Date(data.getLong("time"))) + " " + timezone)
                        .lore("&8» &eLength: &f" + (data.getLong("length") == -1 ? "Permanent" : Core.i().getPunishmentManager().formatMillis(data.getLong("length"))))
                        .lore("&8» &eReason: &f" + data.getString("reason"))
                        .lore("&8» &eServer: &f" + data.getString("server"))
                        .lore("&7&m---------------------------------");
                if (user.getPlayer().hasPermission("blood.history.delete")) {
                    item
                            .lore("&cClick to remove this history")
                            .lore("&7&m---------------------------------");
                }
                inv.setItem(slot++, item.build());
            }
        });

    }
}
