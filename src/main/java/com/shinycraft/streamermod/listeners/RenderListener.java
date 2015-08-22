package com.shinycraft.streamermod.listeners;

import com.shinycraft.streamermod.renderer.ModRenderer;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by ShinyDialga45 on 8/12/2015.
 */
public class RenderListener {
    public static RenderListener instance = new RenderListener();

    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event) {
        // render everything onto the screen
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            ModRenderer.teams.clear();
            ModRenderer.renderToHud();
        }
    }

    @Mod.EventHandler
    public void change(PlayerEvent.PlayerLoggedInEvent event) {
        ModRenderer.teams.clear();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onServerChat(ClientChatReceivedEvent event) {

    }
}
