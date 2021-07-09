package dev.bloodcore.utils;

import dev.bloodcore.Core;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class LibManager {
    private final String[] libs = new String[]{"mongo.jar"};
    public LibManager() {
        try {
            File file = new File(Core.i().getDataFolder() + "/lib");
            if (!file.exists()) {
                file.mkdir();
            }
            for (String lib : libs) {
                File library = new File(file.getPath() + "/" + lib);
                if(!library.exists()){
                    System.out.println("Missing lib " + lib + ", fetching...");
                    FileUtils.copyURLToFile(new URL("https://hardstyles.me/bloodcore/" + lib), library);
                    System.out.println("Fetched " + lib);
                }else{
                    System.out.println("Library " + lib + " found.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
