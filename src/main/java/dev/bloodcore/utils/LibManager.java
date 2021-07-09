package dev.bloodcore.utils;

import dev.bloodcore.Core;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class LibManager {
    public LibManager() {
        try {
            File file = new File(Core.i().getDataFolder() + "/lib");
            if (!file.exists()) {
                file.mkdir();
            }
            File library = new File(file.getPath() + "/mongo.jar");
            if(!library.exists()){
                System.out.println("Mongo jar not found!");
                FileUtils.copyURLToFile(new URL("https://hardstyles.me/bloodcore/mongo.jar"), library);
                System.out.println("Downloaded jar.");
            }else{
                System.out.println("Mongo jar WAS found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
