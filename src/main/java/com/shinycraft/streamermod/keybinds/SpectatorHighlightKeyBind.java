package com.shinycraft.streamermod.keybinds;

import com.shinycraft.streamermod.StreamerMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

/**
 * Created by ShinyDialga45 on 8/22/2015.
 */
public class SpectatorHighlightKeyBind {

    /** Key index for easy handling */
    public static final int CUSTOM_INV = 0;
    /** Key descriptions; use a language file to localize the description later */
    private static final String[] desc = {"See lines through players"};
    /** Default key values */
    private static final int[] keyValues = {Keyboard.KEY_B};
    private final KeyBinding[] keys;

    public SpectatorHighlightKeyBind() {
        keys = new KeyBinding[desc.length];
        for (int i = 0; i < desc.length; ++i) {
            keys[i] = new KeyBinding(desc[i], keyValues[i], "Streamer Mod");
            ClientRegistry.registerKeyBinding(keys[i]);
        }
    }

    /**
     * KeyInputEvent is in the FML package, so we must register to the FML event bus
     */
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (keys[CUSTOM_INV].isPressed() && FMLClientHandler.instance().getClient().inGameHasFocus) {
            StreamerMod.seePlayerHighlights = !StreamerMod.seePlayerHighlights;
        }
    }
}
