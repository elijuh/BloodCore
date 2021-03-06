package dev.bloodcore.etc;

import com.google.common.collect.ImmutableList;
import dev.bloodcore.Core;
import lombok.Getter;

import java.util.List;

@Getter
public enum Messages {
    CORE_PREFIX("prefix", "&6&lCore &8» &r"),
    FREEZE_MESSAGE("staff.freeze-message", ImmutableList.of("&f████&c█&f████", "&f███&c█&6█&c█&f███ &4&lDo NOT log out!", "&f██&c█&6█&0█&6█&c█&f██ &cIf you do, you will be banned!", "&f██&c█&6█&0█&6█&c█&f██ &ePlease join waiting room below:", "&f█&c█&6██&0█&6██&c█&f█ &cts.example.net", "&f█&c█&6█████&c█&f█", "&c█&6███&0█&6███&c█", "&c█████████")),
    CHAT_COOLDOWN_MSG("chat-manager.cooldown-message", "&cPlease wait &7%chat-cooldown%s &cbefore sending another msg."),
    GLOBAL_CHAT_MUTED("chat-manager.muted", "&cChat has been muted by %player%&c."),
    GLOBAL_CHAT_UNMUTED("chat-manager.unmuted", "&aChat has been unmuted by %player%&a."),
    GLOBAL_CHAT_CLEARED("chat-manager.cleared", "&aChat has been cleared by %player%&a."),
    PING("commands.ping", "&e%prefix%%target%&e's Ping: &a%ping%ms"),
    MSG_TO("commands.msg.to", "&7(To %rank_color%%name%&7) "),
    MSG_FROM("commands.msg.from", "&7(From %rank_color%%name%&7) "),
    IGNORE("commands.ignore", "&eYou are now ignoring %target_prefix%%target%&e."),
    UNIGNORE("commands.unignore", "&aYou are no longer ignoring %target_prefix%%target%&a."),
    FLY_TOGGLE_SELF("commands.fly.toggle-self", "&eYou've %state% &eflight."),
    FLY_TOGGLE_OTHER("commands.fly.toggle-other", "&eYou've %state% &efly for %target_prefix%%target%&e."),
    FLY_TOGGLE_OTHER_RECEIVER("commands.fly.toggle-other-receiver", "&eYour fly was %state% &eby %sender_prefix%%sender%&e."),
    FEED_SELF("commands.feed.self", "&eYou've been fed."),
    FEED_OTHER("commands.feed.other", "&eYou've fed %target_prefix%%target%&e."),
    FEED_OTHER_RECEIVER("commands.feed.other-receiver", "&eYou've been fed by %sender_prefix%%sender%&e."),
    HEAL_SELF("commands.heal.self", "&eYou've been healed."),
    HEAL_OTHER("commands.heal.other", "&eYou've healed %target_prefix%%target%&e."),
    HEAL_OTHER_RECEIVER("commands.heal.other-receiver", "&eYou've been healed by %sender_prefix%%sender%&e."),
    GAMEMODE_SELF("commands.gamemode.self", "&eYour gamemode has been set to &6%gamemode%&e."),
    GAMEMODE_OTHER("commands.gamemode.other", "&eYou have set the gamemode of %target_prefix%%target% to &6%gamemode%&e."),
    GAMEMODE_OTHER_RECEIVER("commands.gamemode.other-receiver", "&eYour gamemode has been set to &6%gamemode% &eby %sender_prefix%%sender%&e."),
    CHAT_DENY_MUTED("chat-deny-muted", "&c&m------------------------------------\n&cYou are currently muted for %reason%.\n&7Duration: &c%duration%\n \n&6You can appeal at %appeal%\n&c&m------------------------------------"),
    SILENT_PREFIX("staff.silent-prefix", "&7(Silent) &r"),
    CONSOLE_NAME("staff.console-name", "&4&lConsole"),
    STAFF_CHAT_FORMAT("staff.chat-format", "&7[%server%] %prefix_color%%player%&7: %message%"),
    STAFF_JOIN("staff.join", "&6[Staff] %prefix_color%%player% &ehas connected to &6%server%&6."),
    STAFF_LEAVE("staff.leave", "&6[Staff] %prefix_color%%player% &ehas disconnected from &6%server%&6."),
    PUNISH_HOVER_INFO("staff.punish-hover-info", "&7Reason: &f%reason%\n&7Duration: &f%duration%"),
    REMOVAL_HOVER_INFO("staff.removal-hover-info", "&7Reason: &f%reason%"),
    KICK_WARN_HOVER_INFO("staff.kick-warn-hover-info", "&7Reason: &f%reason%"),
    MUTED_NOTIFY("staff.muted-notify", "&c&m------------------------------------\n&cYou were muted for %reason%.\n&7Duration: &c%duration%\n \n&6You can appeal at %appeal%\n&c&m------------------------------------"),
    WARN_NOTIFY("staff.warn-notify", "&c&m------------------------------------\n&cYou were warned for %reason%.\n&c&m------------------------------------"),
    MUTE_MESSAGE("staff.mute-message", "%silent%%target% &awas muted by &r%executor%&a."),
    TEMPMUTE_MESSAGE("staff.tempmute-message", "%silent%%target% &awas &etemporarily &emuted by &r%executor%&a."),
    UNMUTE_MESSAGE("staff.unmute-message", "%silent%%target% &awas unmuted by &r%executor%&a."),
    BAN_MESSAGE("staff.ban-message", "%silent%%target% &awas banned by &r%executor%&a."),
    TEMPBAN_MESSAGE("staff.tempban-message", "%silent%%target% &awas &etemporarily &abanned by &r%executor%&a."),
    UNBAN_MESSAGE("staff.unban-message", "%silent%%target% &awas unbanned by &r%executor%&a."),
    IPBAN_MESSAGE("staff.ipban-message", "%silent%%target% &awas ipbanned by &r%executor%&a."),
    UNIPBAN_MESSAGE("staff.unipban-message", "%silent%%target% &awas unipbanned by &r%executor%&a."),
    KICK_MESSAGE("staff.kick-message", "%silent%%target% &awas kicked by &r%executor%&a."),
    WARN_MESSAGE("staff.warn-message", "%silent%%target% &awas warned by &r%executor%&a."),
    BAN_SCREEN("staff.ban-screen", "&cYour account is currently %bantype% from ExampleServer\n&cYou were banned for: &7%reason%\n&cExpires in: &7%duration%\n \n&6You can appeal at %appeal%"),
    KICK_SCREEN("staff.kick-screen", "&cYou have been kicked from ExampleServer\n&cYou were kicked for: &7%reason%"),
    APPEAL("staff.appeal-link", "example.net/appeal"),
    STAFFMODE("staff.staffmode", "&eYour &6Staff Mode&e has been %state%&e."),
    VANISH("staff.vanish", "&eYour &6Vanish&e has been %state%&e."),
    SUDO_YOURSELF("commands.sudo.self", "&cYou can't /sudo yourself!"),
    SUDO_OTHER("commands.sudo.other", "&aYou made %target_prefix%%target% &aexecute &r\"%command%\""),
    SUDO_STAFF("commands.sudo.staffmessage", "&c&l[SUDO] %sender_prefix%%sender% &7made %target_prefix%%target% &7execute &r\"%command%\"");
    private static final YamlStorage messages = Core.i().getMessages();
    private final String path;
    private final Object def;

    Messages(String path, Object def) {
        this.path = path;
        this.def = def;
    }

    public String getString() {
        return messages.getString(path);
    }


    @Override
    public String toString() {
        return getString();
    }

    public List<String> getStringList() {
        return messages.getStringList(path);
    }
}
