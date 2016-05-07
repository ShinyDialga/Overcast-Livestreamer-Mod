package com.shinycraft.streamermod.renderer;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.shinycraft.streamermod.StreamerMod;
import com.shinycraft.streamermod.listeners.RenderListener;
import com.shinycraft.streamermod.utils.FileUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Created by ShinyDialga45 on 8/12/2015.
 */
public class ModRenderer {
    private static Minecraft mc = Minecraft.getMinecraft();
    public static List<ScorePlayerTeam> teams = new ArrayList<ScorePlayerTeam>();
    public static Map<NetworkPlayerInfo, ScorePlayerTeam> playerTeams = new HashMap<NetworkPlayerInfo, ScorePlayerTeam>();
    public static Map<NetworkPlayerInfo, Long> deadPlayers = new HashMap<NetworkPlayerInfo, Long>();
    public static Map<String, ScorePlayerTeam> playerNameTeams = new HashMap<String, ScorePlayerTeam>();


    public static org.jdom2.Document currentDocument = null;
    public static World currentWorld = null;

    public static void renderToHud() {
        if ((mc.inGameHasFocus ||
                (mc.currentScreen != null && mc.currentScreen instanceof GuiChat))
                && !mc.gameSettings.showDebugInfo
                && mc.getCurrentServerData() != null
                && mc.getCurrentServerData().serverIP.contains("oc.tc")) {
            ScaledResolution res = new ScaledResolution(mc);
            FontRenderer fontRenderer = mc.fontRendererObj;
            int width = res.getScaledWidth();

            int x1 = 2;
            int y1 = StreamerMod.defaultYLevel;
            int x2;
            int y2 = StreamerMod.defaultYLevel;

            NetHandlerPlayClient nethandlerplayclient = Minecraft.getMinecraft().thePlayer.sendQueue;
            List<NetworkPlayerInfo> list = Ordering.from(new PlayerComparator()).sortedCopy(nethandlerplayclient.getPlayerInfoMap());
            for (NetworkPlayerInfo player : list) {
                try {
                    if (!player.getGameProfile().getName().contains("-")) {
                        ScorePlayerTeam team = player.getPlayerTeam();
                        Color teamColor = Color.getColor(team.getColorPrefix().charAt(team.getColorPrefix().length() - 1));
                        if (teamColor.equals(Color.AQUA)) {
                            continue;
                        }
                        if (!teams.contains(team) && teams.size() == 0) {
                            teams.add(team);
                            try {
                                FileUtil.stringToFile(teams.get(0).getTeamName(), StreamerMod.team1File.getAbsolutePath());
                            } catch (Exception ignored) {

                            }
                            try {
                                if (team != null && StreamerMod.playerDisplay) {
                                    fontRenderer.drawStringWithShadow(team.getTeamName(), x1, StreamerMod.defaultYLevel - 16, teamColor.hex);
                                    fontRenderer.drawStringWithShadow("-----", x1, StreamerMod.defaultYLevel - 8, Color.GRAY.hex);
                                }
                            } catch (Exception ignored) {

                            }
                            try {
                                if (currentWorld == null || !currentWorld.equals(RenderListener.previousWorld)) {
                                    BufferedImage output = ImageIO.read(StreamerMod.team1ColorOutput);
                                    int imageint = output.getRGB(0, 0);
                                    java.awt.Color imageRGB = new java.awt.Color(imageint);
                                    if (!(imageRGB.equals(teamColor.getColor()))) {
                                        BufferedImage image = ImageIO.read(StreamerMod.team1ColorInput);
                                        for(int y = 0; y < image.getHeight(); y++) {
                                            for (int x = 0; x < image.getWidth(); x++) {
                                                int pixelRGB = output.getRGB(x, y);
                                                if (((pixelRGB>>24) & 0xff) >= 100) {
                                                    java.awt.Color color = teamColor.getColor();
                                                    //mix imageColor and desired color
                                                    output.setRGB(x, y, color.getRGB());
                                                }
                                            }
                                        }
                                        ImageIO.write(output, "png", StreamerMod.team1ColorOutput);
                                    }
                                }
                            } catch (Exception e) {

                            }
                        } else if (!teams.contains(team) && teams.size() == 1) {
                            teams.add(team);
                            try {
                                FileUtil.stringToFile(teams.get(1).getTeamName(), StreamerMod.team2File.getAbsolutePath());
                            } catch (Exception ignored) {

                            }
                            try {
                                if (team != null) {
                                    x2 = width - fontRenderer.getStringWidth(team.getTeamName());
                                    fontRenderer.drawStringWithShadow(team.getTeamName(), x2, StreamerMod.defaultYLevel - 16, teamColor.hex);
                                    x2 = width - fontRenderer.getStringWidth("-----");
                                    fontRenderer.drawStringWithShadow("-----", x2, StreamerMod.defaultYLevel - 8, Color.GRAY.hex);
                                }
                            } catch (Exception ignored) {

                            }
                            try {
                                if (currentWorld == null || !currentWorld.equals(RenderListener.previousWorld)) {
                                    currentWorld = RenderListener.previousWorld;
                                    BufferedImage output = ImageIO.read(StreamerMod.team2ColorOutput);
                                    int imageint = output.getRGB(0, 0);
                                    java.awt.Color imageRGB = new java.awt.Color(imageint);
                                    if (!(imageRGB.equals(teamColor.getColor()))) {
                                        BufferedImage image = ImageIO.read(StreamerMod.team2ColorInput);
                                        for(int y = 0; y < image.getHeight(); y++) {
                                            for (int x = 0; x < image.getWidth(); x++) {
                                                int pixelRGB = output.getRGB(x, y);
                                                if (((pixelRGB>>24) & 0xff) >= 100) {
                                                    java.awt.Color color = teamColor.getColor();
                                                    //mix imageColor and desired color
                                                    output.setRGB(x, y, color.getRGB());
                                                }
                                            }
                                        }
                                        ImageIO.write(output, "png", StreamerMod.team2ColorOutput);
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                } catch (Exception ignored) {

                }
            }

            if (StreamerMod.playerDisplay) {
                for (NetworkPlayerInfo player : list) {
                    try {
                        if (!player.getGameProfile().getName().contains("-")) {
                            ScorePlayerTeam team = player.getPlayerTeam();
                            try {
                                if (!team.getColorPrefix().contains(Color.DARK_GRAY.getChatColor()) && !playerNameTeams.get(player.getGameProfile().getName()).equals(team)) {
                                    playerTeams.put(player, team);
                                    playerNameTeams.put(player.getGameProfile().getName(), team);
                                }
                            } catch (Exception ignored) {
                                playerTeams.put(player, team);
                                playerNameTeams.put(player.getGameProfile().getName(), team);
                            }
                        }
                    } catch (Exception ignored) {

                    }
                }
                for (NetworkPlayerInfo player : playerTeams.keySet()) {
                    try {
                        if (!player.getGameProfile().getName().contains("-")) {
                        /*if (mc.theWorld.getPlayerEntityByUUID(player.getGameProfile().getId()) == null) {
                            if (!deadPlayers.containsKey(player)) {
                                deadPlayers.put(player, System.currentTimeMillis());
                            } else if (System.currentTimeMillis() > (deadPlayers.get(player) + 15000) || team != null) {
                                deadPlayers.remove(player);
                                playerNameTeams.remove(player.getGameProfile().getName());
                                removePlayers.add(player);
                                continue;
                            }
                        } else {
                            deadPlayers.remove(player);
                        }*/
                            try {
                                GlStateManager.enableAlpha();
                                GlStateManager.enableBlend();
                                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                                String name;
                                try {
                                    name = getName(player);
                                } catch (Exception e) {
                                    name = Color.DARK_GRAY.getChatColor() + player.getGameProfile().getName();
                                }
                                if (deadPlayers.containsKey(player)) {
                                    name = Color.DARK_GRAY.getChatColor() + player.getGameProfile().getName();
                                }
                                ScorePlayerTeam team = player.getPlayerTeam();
                                Color teamColor = Color.getColor(team.getColorPrefix().charAt(team.getColorPrefix().length() - 1));
                                if (!name.contains(teamColor.getChatColor()) || teamColor.equals(Color.AQUA)) {
                                    continue;
                                }
                                if (playerTeams.get(player).equals(teams.get(0))) {
                                    Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationSkin());
                                    x1 = 2;
                                    Gui.drawScaledCustomSizeModalRect(x1, y1, 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
                                    EntityPlayer entityplayer = Minecraft.getMinecraft().theWorld.getPlayerEntityByUUID(player.getGameProfile().getId());
                                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                                        Gui.drawScaledCustomSizeModalRect(x1, y1, 40.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
                                    }
                                    x1 = 11;
                                    fontRenderer.drawStringWithShadow(name, x1, y1, 0xffffff);
                                    y1 = y1 + 9;
                                } else if (playerTeams.get(player).equals(teams.get(1))) {
                                    Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationSkin());
                                    x2 = width - 10;
                                    Gui.drawScaledCustomSizeModalRect(x2, y2, 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
                                    EntityPlayer entityplayer = Minecraft.getMinecraft().theWorld.getPlayerEntityByUUID(player.getGameProfile().getId());
                                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                                        Gui.drawScaledCustomSizeModalRect(x2, y2, 40.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
                                    }
                                    x2 = width - fontRenderer.getStringWidth(name) - 11;
                                    fontRenderer.drawStringWithShadow(name, x2, y2, 0xffffff);
                                    y2 = y2 + 9;
                                }
                            } catch (Exception ignored) {

                            }
                        }
                    } catch (Exception ignored) {

                    }
                }
            }

            try {
                if (teams.get(0) != null && y1 != StreamerMod.defaultYLevel) {
                    fontRenderer.drawStringWithShadow("-----", x1, y1, Color.GRAY.hex);
                    y1 = y1 + 9;
                }
            } catch (Exception ignored) {

            }
            try {
                if (teams.get(1) != null && y2 != StreamerMod.defaultYLevel) {
                    x2 = width - fontRenderer.getStringWidth("-----");
                    fontRenderer.drawStringWithShadow("-----", x2, y2, Color.GRAY.hex);
                    y2 = y2 + 9;
                }
            } catch (Exception ignored) {

            }

            if (StreamerMod.scoreboard) {
                try {
                    List<Score> scores = new ArrayList(Minecraft.getMinecraft().thePlayer.getWorldScoreboard().getScores());
                    Collections.sort(scores, new Comparator<Score>() {
                        @Override
                        public int compare(Score o1, Score o2) {
                            if (o1.getScorePoints() < o2.getScorePoints()) {
                                return 1;
                            }
                            return 0;
                        }
                    });

                    for (Score score : scores) {
                        Minecraft.getMinecraft().thePlayer.getWorldScoreboard().setObjectiveInDisplaySlot(score.getScorePoints(), null);
                        ScorePlayerTeam displayTeam = score.getScoreScoreboard().getPlayersTeam(score.getPlayerName());
                        try {
                            String line = displayTeam.getColorPrefix() + displayTeam.getColorSuffix();
                            //fontRenderer.drawStringWithShadow(line, x1, y1, 0xffffff);
                            //y1 = y1 + 9;
                            x2 = width - 2 - fontRenderer.getStringWidth(displayTeam.getColorPrefix() + displayTeam.getColorSuffix());
                            fontRenderer.drawStringWithShadow(line, x2, y2, 0xffffff);
                            y2 = y2 + 9;
                        } catch (Exception ignored) {

                        }
                        try {
                            if (displayTeam.getColorPrefix().contains(teams.get(0).getTeamName())) {
                                FileUtil.stringToFile(teams.get(0).getTeamName(), StreamerMod.team1File.getAbsolutePath());
                            } else if (displayTeam.getColorPrefix().contains(teams.get(1).getTeamName())) {
                                FileUtil.stringToFile(teams.get(1).getTeamName(), StreamerMod.team2File.getAbsolutePath());
                            }
                        } catch (Exception ignored) {

                        }
                    }
                } catch (Exception ignored) {

                }
            }

        }
    }

    private static String getName(NetworkPlayerInfo player)
    {
        return player.getDisplayName() != null ? player.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(player.getPlayerTeam(), player.getGameProfile().getName());
    }

    public enum Color {

        BLACK(0x000000, '0'),
        DARK_BLUE(0x0000AA, '1'),
        DARK_GREEN(0x00AA00, '2'),
        DARK_AQUA(0x00AAAA, '3'),
        DARK_RED(0xAA0000, '4'),
        DARK_PURPLE(0xAA00AA, '5'),
        GOLD(0xFFAA00, '6'),
        GRAY(0xAAAAAA, '7'),
        DARK_GRAY(0x555555, '8'),
        BLUE(0x5555FF, '9'),
        GREEN(0x55FF55, 'a'),
        AQUA(0x55FFFF, 'b'),
        RED(0xFF5555, 'c'),
        LIGHT_PURPLE(0xFF55FF, 'd'),
        YELLOW(0xFFFF55, 'e'),
        WHITE(0xFFFFFF, 'f');

        int hex;
        char code;

        Color(int hex, char code) {
            this.hex = hex;
            this.code = code;
        }

        public static Color getColor(char code) {
            for (Color color : values()) {
                if (color.code == code) {
                    return color;
                }
            }
            return Color.AQUA;
        }

        public java.awt.Color getColor() {
            return new java.awt.Color(hex);
        }

        public String getChatColor() {
            return "\u00a7" + code;
        }

    }

    @SideOnly(Side.CLIENT)
    static class PlayerComparator implements Comparator
    {
        private static final String __OBFID = "CL_00001941";

        private PlayerComparator() {}

        public int func_178952_a(NetworkPlayerInfo p_178952_1_, NetworkPlayerInfo p_178952_2_) {
            ScorePlayerTeam scoreplayerteam = p_178952_1_.getPlayerTeam();
            ScorePlayerTeam scoreplayerteam1 = p_178952_2_.getPlayerTeam();
            return ComparisonChain.start().compareTrueFirst(p_178952_1_.getGameType() != WorldSettings.GameType.SPECTATOR, p_178952_2_.getGameType() != WorldSettings.GameType.SPECTATOR).compare(scoreplayerteam != null ? scoreplayerteam.getRegisteredName() : "", scoreplayerteam1 != null ? scoreplayerteam1.getRegisteredName() : "").compare(p_178952_1_.getGameProfile().getName(), p_178952_2_.getGameProfile().getName()).result();
        }

        public int compare(Object p_compare_1_, Object p_compare_2_) {
            return this.func_178952_a((NetworkPlayerInfo)p_compare_1_, (NetworkPlayerInfo)p_compare_2_);
        }

    }

}
