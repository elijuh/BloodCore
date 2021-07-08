package dev.bloodcore.etc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.bloodcore.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Disguise {
    private final GameProfile gameProfile;
    private final Player player;
    private final String name, texture, signature,
            originalName, originalTexture, originalSignature;

    public Disguise(Player player, String name, String texture, String signature) {
        GameProfile gameProfile;

        try {
            gameProfile = ReflectionUtil.getGameProfile(player);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            gameProfile = null;
            e.printStackTrace();
        }
        this.gameProfile = gameProfile;
        this.player = player;
        this.name = name;
        this.texture = texture;
        this.signature = signature;

        Property originalTextures = ReflectionUtil.getTexturesProperty(gameProfile);
        this.originalName = player.getName();
        this.originalTexture = originalTextures.getValue();
        this.originalSignature = originalTextures.getSignature();
    }

    public void apply() {
        apply(name, texture, signature);
    }

    private void apply(String name, String texture, String signature) {
        gameProfile.getProperties().clear();
        gameProfile.getProperties()
                .put("textures",
                        new Property(
                                "textures",
                                texture,
                                signature
                        ));

        try {
            Field field = gameProfile.getClass().getDeclaredField("name");
            field.setAccessible(true);
            field.set(gameProfile, name);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (name != null) player.setDisplayName(name);

        Bukkit.getOnlinePlayers().forEach(all -> {
            all.hidePlayer(player);
            all.showPlayer(player);
        });
    }

    public void remove() {
        apply(originalName, originalTexture, originalSignature);
    }

}
