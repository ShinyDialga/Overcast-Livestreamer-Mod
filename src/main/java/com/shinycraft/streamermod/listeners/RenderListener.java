package com.shinycraft.streamermod.listeners;

import com.shinycraft.streamermod.StreamerMod;
import com.shinycraft.streamermod.renderer.ModRenderer;
import com.shinycraft.streamermod.utils.FileUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by ShinyDialga45 on 8/12/2015.
 */
public class RenderListener {
    public static RenderListener instance = new RenderListener();
    private static Minecraft mc = Minecraft.getMinecraft();



    @SubscribeEvent
    public void onRenderLiving(RenderPlayerEvent.Pre event)
    {
        try {
            if (StreamerMod.seePlayerHighlights && (mc.playerController.getCurrentGameType().isCreative() || mc.playerController.getCurrentGameType().equals(WorldSettings.GameType.SPECTATOR))) {
                EntityPlayer entityPlayer = event.getEntityPlayer();
                // System.out.println("12");
                NetworkPlayerInfo networkPlayerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(entityPlayer.getUniqueID());
                if (networkPlayerInfo.getGameProfile().getName().contains("-")) {
                    return;
                }
                // System.out.println("14");
                ScorePlayerTeam team = networkPlayerInfo.getPlayerTeam();

                ModRenderer.Color teamColor = ModRenderer.Color.getColor(team.getColorPrefix().charAt(team.getColorPrefix().length() - 1));
                if (teamColor.equals(ModRenderer.Color.AQUA)) {
                    return;
                }
                double playerX = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * event.getPartialRenderTick();
                double playerY = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * event.getPartialRenderTick();
                double playerZ = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * event.getPartialRenderTick();
                GL11.glPushMatrix();

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glLineWidth(6);
                GL11.glTranslated(-playerX, -playerY, -playerZ);
                GL11.glColor3ub((byte) teamColor.getColor().getRed(), (byte) teamColor.getColor().getGreen(), (byte) teamColor.getColor().getBlue());
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex3f((float) entityPlayer.posX, (float) entityPlayer.posY + 2, (float) entityPlayer.posZ);
                GL11.glVertex3f((float) entityPlayer.posX, (float) entityPlayer.posY, (float) entityPlayer.posZ);
                GL11.glEnd();
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glPopMatrix();
            }
        } catch (Exception e) {

        }

    }

    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event) {

        // render everything onto the screen
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            ModRenderer.teams.clear();
            ModRenderer.renderToHud();
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSHEALTH) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent.Chat event) {
        event.setPosX(StreamerMod.chatXLevel);
        event.setPosY(StreamerMod.chatYLevel);
    }

    public static World previousWorld = null;
    long lastUpdate = System.currentTimeMillis();

    @SubscribeEvent
    public void playerJoinWorld(EntityJoinWorldEvent event) {
        if (previousWorld != event.getWorld() && System.currentTimeMillis() > (lastUpdate + 3000) && event.getEntity().equals(Minecraft.getMinecraft().thePlayer)) {
            previousWorld = event.getWorld();
            lastUpdate = System.currentTimeMillis();
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/map");
        }
    }

    public static org.jdom2.Document document = null;

    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        double playerX = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * event.getPartialTicks();
        double playerY = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * event.getPartialTicks();
        double playerZ = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * event.getPartialTicks();
        try {
            if (StreamerMod.coreLeakDistance) {
                for (Element element : document.getRootElement().getChildren("cores")) {
                    for (Element coreElement : element.getChildren("core")) {
                        for (Element region : coreElement.getChildren()) {
                            int coreLeakDistance = 5;
                            if (element.getAttributeValue("leak") != null) {
                                coreLeakDistance = Integer.parseInt(element.getAttributeValue("leak"));
                            } else if (coreElement.getAttributeValue("leak") != null) {
                                coreLeakDistance = Integer.parseInt(coreElement.getAttributeValue("leak"));
                            }
                            double minX = 0;
                            double maxX = 0;
                            double minY = 0;
                            double maxY = 0;
                            double minZ = 0;
                            double maxZ = 0;
                            if (region.getName().equalsIgnoreCase("cuboid")) {
                                String[] min = region.getAttributeValue("min").split(",");
                                String[] max = region.getAttributeValue("max").split(",");
                                minX = Double.parseDouble(min[0]);
                                maxX = Double.parseDouble(max[0]);
                                minY = Double.parseDouble(min[1]);
                                maxY = Double.parseDouble(max[1]);;
                                minZ = Double.parseDouble(min[2]);;
                                maxZ = Double.parseDouble(max[2]);;
                            } else if (region.getName().equalsIgnoreCase("sphere")) {
                                String[] origin = region.getAttributeValue("origin").split(",");
                                int radius = Integer.parseInt(region.getAttributeValue("radius"));
                                minX = Double.parseDouble(origin[0]) - radius;
                                maxX = Double.parseDouble(origin[0]) + radius;
                                minY = Double.parseDouble(origin[0]) - radius;
                                maxY = Double.parseDouble(origin[0]) + radius;
                                minZ = Double.parseDouble(origin[0]) - radius;
                                maxZ = Double.parseDouble(origin[0]) + radius;
                            }
                            double lowestY = minY < maxY ? minY : maxY;

                            GL11.glPushMatrix();
                            GL11.glDisable(GL11.GL_LIGHTING);
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            GL11.glDisable(GL11.GL_DEPTH_TEST);
                            GL11.glLineWidth(6);
                            GL11.glTranslated(-playerX, -playerY, -playerZ);
                            GL11.glColor3ub((byte)253, (byte)0, (byte)0);
                            GL11.glEnable(GL11.GL_LINE_SMOOTH);
                            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
                            GL11.glBegin(GL11.GL_LINE_STRIP);
                            GL11.glVertex3f((int) maxX, (int) lowestY - coreLeakDistance, (int) maxZ);
                            GL11.glVertex3f((int) maxX, (int) lowestY - coreLeakDistance, (int) minZ);
                            GL11.glVertex3f((int) minX, (int) lowestY - coreLeakDistance, (int) minZ);
                            GL11.glVertex3f((int) minX, (int) lowestY - coreLeakDistance, (int) maxZ);
                            GL11.glVertex3f((int) maxX, (int) lowestY - coreLeakDistance, (int) maxZ);
                            GL11.glEnd();
                            GL11.glEnable(GL11.GL_LIGHTING);
                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                            GL11.glEnable(GL11.GL_DEPTH_TEST);
                            GL11.glPopMatrix();
                            Minecraft.getMinecraft().renderEngine.bindTexture(l);
                            GL11.glPushMatrix();
                            GL11.glTranslated(-playerX, -playerY, -playerZ);
                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            GL11.glColor4f(255F, 0F, 0F, 0.5F);
                            /*Tessellator tess = Tessellator.getInstance();
                            WorldRenderer worldRenderer = tess.getWorldRenderer();
                            GlStateManager.pushAttrib();
                            worldRenderer.startDrawingQuads();
                            worldRenderer.addVertexWithUV(minX, lowestY - coreLeakDistance + 0.05, minZ, 1, 1);
                            worldRenderer.addVertexWithUV(minX, lowestY - coreLeakDistance + 0.05, maxZ, 1, 0.0);
                            worldRenderer.addVertexWithUV(maxX, lowestY - coreLeakDistance + 0.05, maxZ, 0.0, 0.0);
                            worldRenderer.addVertexWithUV(maxX, lowestY - coreLeakDistance + 0.05, minZ, 0.0, 1);
                            tess.draw();*/
                            GL11.glDisable(GL11.GL_BLEND);
                            GlStateManager.popAttrib();
                            GlStateManager.popMatrix();


                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        try {

            for (Object o : mc.thePlayer.worldObj.getLoadedEntityList()) {
                Entity entity = ((Entity)o);
                String name = entity.getDisplayName().getFormattedText();
                if (entity instanceof EntityWither && entity.getDisplayName().getFormattedText().contains(":") && entity.getDisplayName().getFormattedText().contains("Time Remaining")) {
                    FileUtil.stringToFile(name.substring(20, name.length() - 2), StreamerMod.timeFile.getAbsolutePath());
                    //((EntityWither)entity).setCustomNameTag(" ");
                    //((EntityWither)entity).setHealth(1);
                    return;
                }
            }

            FileUtil.stringToFile("--:--", StreamerMod.timeFile.getAbsolutePath());

        } catch (Exception e) {

        }
    }

    private static final ResourceLocation l = new ResourceLocation("streamermod", "textures/core.png");

    @SubscribeEvent
    public void messageReceived(ClientChatReceivedEvent event) {
        try {
            String message = event.getMessage().getFormattedText();
            if (message.contains("XML")) {
                String str = message.replaceAll("�", "");
                str = str.substring(str.indexOf("https"), str.length() - 2);
                SAXBuilder builder = new SAXBuilder();
                InputStream input = new URL(str).openStream();
                org.jdom2.Document document = builder.build(input);
                this.document = document;
                FileUtil.stringToFile("Unknown Name", StreamerMod.mapNameFile.getAbsolutePath());
                for (Element element : document.getRootElement().getChildren("name")) {
                    FileUtil.stringToFile(element.getValue(), StreamerMod.mapNameFile.getAbsolutePath());
                }
                ModRenderer.playerTeams.clear();
                ModRenderer.playerNameTeams.clear();
                ModRenderer.deadPlayers.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void change(PlayerEvent.PlayerLoggedInEvent event) {
        ModRenderer.teams.clear();
    }

}
