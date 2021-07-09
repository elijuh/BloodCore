package dev.bloodcore;

import dev.bloodcore.etc.User;
import dev.bloodcore.ranks.Rank;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bson.Document;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BloodExpansion extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "blood";
    }

    @Override
    public String getAuthor() {
        return "elijuh, Hardstyles";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        Document user = Core.i().getMongoManager().getUserFromUUID(player.getUniqueId().toString());
        if (user != null) {
            switch (params.toLowerCase()) {
                case "prefix": {
                    Rank rank = Core.i().getRankManager().getRank(user.getString("rank"), true);
                    return rank.getPrefix();
                }
                case "rank": {
                    Rank rank = Core.i().getRankManager().getRank(user.getString("rank"), true);
                    return rank.getId();
                }
                case "rank_color":
                case "prefix_color": {
                    Rank rank = Core.i().getRankManager().getRank(user.getString("rank"), true);
                    return rank.getColor();
                }
                case "tag":
                    return user.containsKey("tag") ? user.getString("tag") : "";
            }
        }
        return "";
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        User user = Core.i().getUser(player);
        if (user != null) {
            switch (params.toLowerCase()) {
                case "mod":
                case "staff":
                case "modmode":
                case "staffmode": {
                    return (user.getData().containsKey("staffmode") && (boolean) user.get("staffmode")) ? "&aEnabled" : "&cDisabled";
                }
                case "vanish":
                case "vanished": {
                    return user.isHidden() ? "&aEnabled" : "&cDisabled";
                }
                default: {
                    return this.onRequest(player, params);
                }
            }
        }
        return "";
    }
}
