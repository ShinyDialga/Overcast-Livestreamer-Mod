package com.shinycraft.streamermod;

import com.shinycraft.streamermod.listeners.RenderListener;
import com.shinycraft.streamermod.renderer.ModRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.File;

@Mod(modid = StreamerMod.MODID, version = StreamerMod.VERSION)
public class StreamerMod
{
    public static final String MODID = "streamermod";
    public static final String VERSION = "1.0";
    public static File team1File;
    public static File team2File;
    public static int defaultYLevel = 16;
    public static boolean scoreboard = true;
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(RenderListener.instance);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // you will be able to find the config file in .minecraft/config/ and it will be named Dummy.cfg
        // here our Configuration has been instantiated, and saved under the name "config"
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        config.save();
        String team1FileString = config.getString("Team1txtFile", Configuration.CATEGORY_GENERAL, "team1.txt", "This is where the first team's name will export to");
        String team2FileString = config.getString("Team2txtFile", Configuration.CATEGORY_GENERAL, "team2.txt", "This is where the second team's name will export to");
        int y = config.getInt("defaultGUIyLevel", Configuration.CATEGORY_GENERAL, 16, 0, 1980, "How far down the GUI starts");
        boolean sb = config.getBoolean("scoreboardEnabled", Configuration.CATEGORY_GENERAL, true, "Enables the scoreboard");
        team1File = new File(team1FileString);
        team2File = new File(team2FileString);
        defaultYLevel = y;
        scoreboard = sb;
        config.load();
        config.save();
    }

    @EventHandler
    public void change(PlayerEvent.PlayerLoggedInEvent event) {
        ModRenderer.teams.clear();
    }

}
