package dev.bloodcore.etc;

import dev.bloodcore.Core;
import lombok.Getter;

@Getter
public enum Messages {
    CORE_PREFIX("prefix", "&6&lCore &8» &r"),
    CHAT_COOLDOWN_MSG("chat-manager.cooldown-message", "&cPlease wait &7%chat-cooldown%s &cbefore sending another msg."),
    PING("commands.ping", "&e%prefix%%target%&e's Ping: &a%ping%ms"),
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
    APPEAL("staff.appeal-link", "example.net/appeal");
    private static final YamlStorage messages = Core.i().getMessages();
    private final String path;
    private final String def;

    Messages(String path, String def) {
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
}
