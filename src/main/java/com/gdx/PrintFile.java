package com.gdx;

import java.io.File;

public class PrintFile{
    public static void main(String[] args) {
        File directory = new File("./res/graphics/pokemon/");

        // Get all files from the directory
        File[] files = directory.listFiles();

        if (files != null) { // Ensure the directory is not empty
            for (File file : files) {
//                System.out.println(file.getName());
                String msg = "assetManager.load(\"res/graphics/pokemon/" + file.getName() + "\", Texture.class);";
                System.out.println(msg);
            }
        }
    }
}
