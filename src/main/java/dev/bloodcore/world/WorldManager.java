package dev.bloodcore.world;

import dev.bloodcore.Core;
import dev.bloodcore.etc.YamlStorage;
import dev.bloodcore.world.generators.VoidGenerator;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Set;

public class WorldManager {
    public WorldManager() {
        //
    }

    public void loadWorlds() {
        Set<String> entries = Core.i().getWorldConfig().getKeys(false);
        for (String entry : entries) {
            if(Core.i().getWorldConfig().getBoolean(entry + ".autoload")){
                String generator = Core.i().getWorldConfig().getString(entry + ".generator");
                WorldCreator creator = new WorldCreator(entry);
                if (generator != null) {
                    GeneratorType type = GeneratorType.valueOf(generator);
                    switch (type){
                        case VOID: {
                            creator.generator(new VoidGenerator());
                            break;
                        }
                    }
                }
                creator.createWorld();
            }
        }

    }

    public ChunkGenerator getGenerator(String worldName){
        YamlStorage storage = Core.i().getWorldConfig();
        if(storage.getKeys(false).contains(worldName)){
            GeneratorType type = GeneratorType.valueOf(storage.getString(worldName + ".generator"));
            switch (type){
                case VOID:
                    return new VoidGenerator();
                case NORMAL:
                    return null;
            }
        }
        return null;
    }
}
