package dev.bloodcore.etc;

import dev.bloodcore.Core;
import lombok.Getter;

@Getter
public enum Messages {
    CORE_PREFIX("prefix", "&6&lCore &8» &r"),
    CHAT_COOLDOWN_MSG("chat-manager.cooldown-message", "&cPlease wait &7%chat-cooldown%s &cbefore sending another msg."),
    FLY_TOGGLE_SELF("commands.fly.toggle-self", "&eYou've %state% flight"),
    FLY_TOGGLE_OTHER("commands.fly.toggle-other", "&eYou've %state% fly for %target_prefix%%target%"),
    FLY_TOGGLE_OTHER_RECEIVER("commands.fly.toggle-other-receiver", "&eYour fly was %state% by %sender_prefix%%sender%"),
    FEED_SELF("commands.feed.self", "&eYou've been fed!"),
    FEED_OTHER("commands.feed.other", "&eYou've fed %target_prefix%%target%&e!"),
    FEED_OTHER_RECEIVER("commands.feed.other-receiver", "&eYou've been fed by %sender_prefix%%sender%&e!"),
    HEAL_SELF("commands.heal.self", "&eYou've been healed!"),
    HEAL_OTHER("commands.heal.other", "&eYou've healed %target_prefix%%target%&e!"),
    HEAL_OTHER_RECEIVER("commands.heal.other-receiver", "&eYou've been healed by %sender_prefix%%sender%&e!"),
    CHAT_DENY_MUTED("chat-deny-muted", "&c&m------------------------------------\n&cYou are currently muted for %reason%.\n&7Duration:&c%duration%\n \n&6You can appeal at %appeal%\n&c&m------------------------------------"),
    SILENT_PREFIX("staff.silent-prefix", "&7(Silent) &r"),
    CONSOLE_NAME("staff.console-name", "&4&lConsole"),
    HOVER_INFO("staff.hover-info", "&7Reason: &f%reason%"),
    MUTE_MESSAGE("staff.mute-message", "%silent%%target% &awas muted by &r%executor%&a."),
    UNMUTE_MESSAGE("staff.unmute-message", "%silent%%target% &awas unmuted by &r%executor%&a."),
    BAN_MESSAGE("staff.ban-message", "%silent%%target% &awas banned by &r%executor%&a."),
    UNBAN_MESSAGE("staff.unban-message", "%silent%%target% &awas unbanned by &r%executor%&a."),
    IPBAN_MESSAGE("staff.ipban-message", "%silent%%target% &awas ipbanned by &r%executor%&a."),
    UNIPBAN_MESSAGE("staff.unipban-message", "%silent%%target% &awas unipbanned by &r%executor%&a."),
    KICK_MESSAGE("staff.kick-message", "%silent%%target% &awas kicked by &r%executor%&a."),
    BAN_SCREEN("staff.ban-screen.message", "&cYour account is currently %bantype% from ExampleServer\n&cYou were banned for: &7%reason%\n \n&6You can appeal at %appeal%"),
    APPEAL("staff.ban-screen.appeal-link", "example.net/appeal");
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
