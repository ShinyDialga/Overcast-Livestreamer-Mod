package com.shinycraft.streamermod.renderer;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.shinycraft.streamermod.StreamerMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by ShinyDialga45 on 8/12/2015.
 */
public class ModRenderer {
    private static Minecraft mc = Minecraft.getMinecraft();
    public static List<ScorePlayerTeam> teams = new ArrayList<ScorePlayerTeam>();
    private static Map<NetworkPlayerInfo, ScorePlayerTeam> playerTeams = new HashMap<NetworkPlayerInfo, ScorePlayerTeam>();

    public static void renderToHud() {
        if ((mc.inGameHasFocus ||
                (mc.currentScreen != null && mc.currentScreen instanceof GuiChat))
                && !mc.gameSettings.showDebugInfo
                && mc.getCurrentServerData() != null
                && mc.getCurrentServerData().serverIP.contains("oc.tc")) {
            ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            FontRenderer fontRenderer = mc.fontRendererObj;
            int width = res.getScaledWidth();

            int x1 = 2;
            int y1 = StreamerMod.defaultYLevel;
            int x2;
            int y2 = StreamerMod.defaultYLevel;

            NetHandlerPlayClient nethandlerplayclient = Minecraft.getMinecraft().thePlayer.sendQueue;
            List<NetworkPlayerInfo> list = Ordering.from(new PlayerComparator()).sortedCopy(nethandlerplayclient.func_175106_d());
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
                                    byte[] encoded = Files.readAllBytes(Paths.get(StreamerMod.team1File.getAbsolutePath()));
                                    String file = new String(encoded, StandardCharsets.UTF_8);
                                    if (!file.equals(teams.get(0).func_96669_c())) {
                                        FileUtils.writeStringToFile(new File(StreamerMod.team1File.getAbsolutePath()), teams.get(0).func_96669_c());
                                    }

                            } catch (Exception ignored) {

                            }
                            try {
                                if (team != null) {
                                    fontRenderer.drawStringWithShadow(team.func_96669_c(), x1, StreamerMod.defaultYLevel - 16, teamColor.hex);
                                    fontRenderer.drawStringWithShadow("-----", x1, StreamerMod.defaultYLevel - 8, Color.GRAY.hex);
                                }
                            } catch (Exception ignored) {

                            }
                        } else if (!teams.contains(team) && teams.size() == 1) {
                            teams.add(team);
                            try {
                                    byte[] encoded = Files.readAllBytes(Paths.get(StreamerMod.team2File.getAbsolutePath()));
                                    String file = new String(encoded, StandardCharsets.UTF_8);
                                    if (!file.equals(teams.get(1).func_96669_c())) {
                                        FileUtils.writeStringToFile(new File(StreamerMod.team2File.getAbsolutePath()), teams.get(1).func_96669_c());
                                    }
                            } catch (Exception ignored) {

                            }
                            try {
                                if (team != null) {
                                    x2 = width - fontRenderer.getStringWidth(team.func_96669_c());
                                    fontRenderer.drawStringWithShadow(team.func_96669_c(), x2, StreamerMod.defaultYLevel - 16, teamColor.hex);
                                    x2 = width - fontRenderer.getStringWidth("-----");
                                    fontRenderer.drawStringWithShadow("-----", x2, StreamerMod.defaultYLevel - 8, Color.GRAY.hex);
                                }
                            } catch (Exception ignored) {

                            }
                        }
                    }
                } catch (Exception ignored) {

                }
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
                            fontRenderer.drawStringWithShadow(line, x1, y1, 0xffffff);
                            y1 = y1 + 9;
                            x2 = width - 2 - fontRenderer.getStringWidth(displayTeam.getColorPrefix() + displayTeam.getColorSuffix());
                            fontRenderer.drawStringWithShadow(line, x2, y2, 0xffffff);
                            y2 = y2 + 9;
                        } catch (Exception ignored) {

                        }
                        try {
                            if (displayTeam.getColorPrefix().contains(teams.get(0).func_96669_c())) {
                                byte[] encoded = Files.readAllBytes(Paths.get(StreamerMod.team1File.getAbsolutePath()));
                                String file = new String(encoded, StandardCharsets.UTF_8);
                                if (!file.equals(teams.get(0).func_96669_c())) {
                                    FileUtils.writeStringToFile(new File(StreamerMod.team1File.getAbsolutePath()), teams.get(0).func_96669_c());
                                }
                            } else if (displayTeam.getColorPrefix().contains(teams.get(1).func_96669_c())) {
                                byte[] encoded = Files.readAllBytes(Paths.get(StreamerMod.team2File.getAbsolutePath()));
                                String file = new String(encoded, StandardCharsets.UTF_8);
                                if (!file.equals(teams.get(1).func_96669_c())) {
                                    FileUtils.writeStringToFile(new File(StreamerMod.team2File.getAbsolutePath()), teams.get(1).func_96669_c());
                                }
                            }
                        } catch (Exception ignored) {

                        }
                    }
                } catch (Exception ignored) {

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

            for (NetworkPlayerInfo player : list) {
                try {
                    if (!player.getGameProfile().getName().contains("-")) {
                        ScorePlayerTeam team = player.getPlayerTeam();
                        try {
                            if (!team.getColorPrefix().contains(EnumChatFormatting.DARK_GRAY.toString())) {
                                playerTeams.put(player, team);
                            }
                            GlStateManager.enableAlpha();
                            GlStateManager.enableBlend();
                            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                            String name;
                            try {
                                name = getName(player);
                            } catch (Exception e) {
                                name = player.getGameProfile().getName();
                            }
                            if (playerTeams.get(player).equals(teams.get(0))) {
                                Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationSkin());
                                x1 = 2;
                                Gui.drawScaledCustomSizeModalRect(x1, y1, 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
                                EntityPlayer entityplayer = Minecraft.getMinecraft().theWorld.getPlayerEntityByUUID(player.getGameProfile().getId());
                                if (entityplayer != null && entityplayer.func_175148_a(EnumPlayerModelParts.HAT)) {
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
                                if (entityplayer != null && entityplayer.func_175148_a(EnumPlayerModelParts.HAT)) {
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
    }

    private static String getName(NetworkPlayerInfo player)
    {
        return player.func_178854_k() != null ? player.func_178854_k().getFormattedText() : ScorePlayerTeam.formatPlayerName(player.getPlayerTeam(), player.getGameProfile().getName());
    }

    private enum Color {

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

        static Color getColor(char code) {
            for (Color color : values()) {
                if (color.code == code) {
                    return color;
                }
            }
            return Color.AQUA;
        }

        String getChatColor() {
            return "\u00A7" + code;
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
