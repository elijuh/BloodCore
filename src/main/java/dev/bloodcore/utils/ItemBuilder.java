package dev.bloodcore.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;

    public static ItemBuilder create(Material material) {
        return new ItemBuilder(material);
    }

    private ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder material(Material material) {
        this.item.setType(material);
        return this;
    }

    public ItemBuilder dura(int dura) {
        this.item.setDurability((short) dura);
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.item.setAmount(Math.min(amount, 64));
        return this;
    }

    public ItemBuilder name(String name) {
        this.meta.setDisplayName(ChatUtil.color(name));
        return this;
    }

    public ItemBuilder lore(String line) {
        if (line.isEmpty()) return this;

        List<String> lore = this.meta.hasLore() ? this.meta.getLore() : new ArrayList<>();
        lore.add(ChatUtil.color("&r" + line));
        this.meta.setLore(lore);
        return this;
    }

    public ItemBuilder lore(Collection<String> lines) {
        for (String line : lines) {
            this.lore(line);
        }
        return this;
    }

    public ItemBuilder enchant(Enchantment enchant, int level) {
        this.meta.addEnchant(enchant, level, true);
        return this;
    }

    public ItemBuilder flag(ItemFlag flag) {
        meta.addItemFlags(flag);
        return this;
    }

    public ItemBuilder color(int hex) {
        LeatherArmorMeta lMeta = (LeatherArmorMeta) meta;
        lMeta.setColor(Color.fromRGB(hex));
        return this;
    }

    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return item;
    }
}
