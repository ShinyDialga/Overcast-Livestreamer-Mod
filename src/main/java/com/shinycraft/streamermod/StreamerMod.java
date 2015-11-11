package com.shinycraft.streamermod;

import com.shinycraft.streamermod.keybinds.GameModeKeyBind;
import com.shinycraft.streamermod.keybinds.SpectatorHighlightKeyBind;
import com.shinycraft.streamermod.listeners.RenderListener;
import com.shinycraft.streamermod.renderer.ModRenderer;
import com.shinycraft.streamermod.renderer.ScreenDisplay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
    public static boolean playerDisplay = true;
    public static boolean coreLeakDistance = true;
    public static File timeFile;
    public static File mapNameFile;
    public static File team1ColorInput;
    public static File team1ColorOutput;
    public static File team2ColorInput;
    public static File team2ColorOutput;

    public static boolean seePlayerHighlights = false;
    public static ScreenDisplay screenDisplay;
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(RenderListener.instance);
        FMLCommonHandler.instance().bus().register(new GameModeKeyBind());
        FMLCommonHandler.instance().bus().register(new SpectatorHighlightKeyBind());
        screenDisplay = new ScreenDisplay();
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
        defaultYLevel = config.getInt("DefaultGUIyLevel", Configuration.CATEGORY_GENERAL, 16, 0, 1980, "How far down the GUI starts");
        scoreboard = config.getBoolean("ScoreboardEnabled", Configuration.CATEGORY_GENERAL, true, "Enables the scoreboard");
        playerDisplay = config.getBoolean("PlayerDisplayEnabled", Configuration.CATEGORY_GENERAL, true, "Enables the player display");
        coreLeakDistance = config.getBoolean("CoreLeakDistanceEnabled", Configuration.CATEGORY_GENERAL, true, "Enables the core leak distance display");
        timeFile = new File(config.getString("TimetxtFile", Configuration.CATEGORY_GENERAL, "time.txt", "Displays how much time is left in a match"));
        mapNameFile = new File(config.getString("MapNametxtFile", Configuration.CATEGORY_GENERAL, "mapname.txt", "Shows what map is currently being played"));
        team1ColorInput = new File(config.getString("Team1OverlayInputFile", Configuration.CATEGORY_GENERAL, "team1overlayinput.png", "Links the template required for team 1's color changing overlay output"));
        team1ColorOutput = new File(config.getString("Team1OverlayOutputFile", Configuration.CATEGORY_GENERAL, "team1overlayoutput.png", "An image that is colored based on team 1's color"));
        team2ColorInput = new File(config.getString("Team2OverlayInputFile", Configuration.CATEGORY_GENERAL, "team2overlayinput.png", "Links the template required for team 2's color changing overlay output"));
        team2ColorOutput = new File(config.getString("Team2OverlayOutputFile", Configuration.CATEGORY_GENERAL, "team2overlayoutput.png", "An image that is colored based on team 2's color"));
        team1File = new File(team1FileString);
        team2File = new File(team2FileString);
        config.load();
        config.save();
    }

    @EventHandler
    public void change(PlayerEvent.PlayerLoggedInEvent event) {
        ModRenderer.teams.clear();
    }

}
