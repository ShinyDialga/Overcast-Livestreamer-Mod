package com.shinycraft.streamermod.utils;

import com.shinycraft.streamermod.StreamerMod;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by ShinyDialga45 on 10/16/2015.
 */
public class FileUtil {

    public static void stringToFile(String string, String file) throws IOException{
        byte[] encoded = Files.readAllBytes(Paths.get(file));
        String encodedfile = new String(encoded, StandardCharsets.UTF_8);
        if (!encodedfile.equals(string)) {
            FileUtils.writeStringToFile(new File(file), string);
        }
    }

}
