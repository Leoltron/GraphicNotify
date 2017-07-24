package ru.leoltron.graphicnotify.client.gui.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import ru.leoltron.graphicnotify.client.eventhandler.KeyHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Queue;

public class NotificationManager {

    private Minecraft mc;
    private static final ResourceLocation ACHIEVEMENT_BG = new ResourceLocation("textures/gui/achievement/achievement_background.png");

    private Queue<Notification> notificationQueue;
    private Notification currentNotification;
    private long timeMark;

    public URI getCurrentURL() {
        return currentURL;
    }

    private URI currentURL;

    public NotificationManager(Minecraft mc) {
        this.mc = mc;
        notificationQueue = new LinkedList<Notification>();
    }

    private void formatNotification(Notification notification) {
        if (KeyHandler.isValidKeyCode(KeyHandler.TO_LINK)) {
            StringBuilder builder = new StringBuilder();
            boolean screen = false;
            for (char c : notification.getMessage().toCharArray()) {
                switch (c) {
                    case '\\':
                        if (screen)
                            builder.append('\\');
                        screen = !screen;
                        break;
                    case '#':
                        if (screen) {
                            builder.append('#');
                            screen = false;
                        } else
                            builder.append(KeyHandler.getKeyName(KeyHandler.TO_LINK));
                        break;
                    case '$':
                        if (screen) {
                            builder.append('$');
                            screen = false;
                        } else
                            builder.append(mc.thePlayer.getDisplayName());
                        break;
                    default:
                        builder.append(c);
                        screen = false;
                        break;
                }
            }
            notification.setMessage(builder.toString());
        }
    }

    public void addNotification(Notification notification) {
        if (notification != null)
            formatNotification(notification);
        notificationQueue.add(notification);
        if (currentNotification == null)
            switchToNext();
    }

    private void switchToNext() {
        currentNotification = null;
        if (notificationQueue.isEmpty()) return;
        currentNotification = notificationQueue.remove();
        if (currentNotification != null) {
            timeMark = Minecraft.getSystemTime();
            if (currentNotification.getUrl().startsWith("http://")
                    || currentNotification.getUrl().startsWith("https://"))
                try {
                    currentURL = new URI(currentNotification.getUrl());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    currentURL = null;
                }
        } else
            switchToNext();
    }

    private void prepareToDraw(ScaledResolution scaledResolution) {
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        int scaledWidth = scaledResolution.getScaledWidth();
        int scaledHeight = scaledResolution.getScaledHeight();
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double) scaledWidth, (double) scaledHeight, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    public void drawNotification(Gui gui, RenderItem renderItem, ScaledResolution scaledResolution) {
        if (this.currentNotification != null && this.timeMark != 0L && mc.thePlayer != null) {
            double lifeTime = Minecraft.getSystemTime() - this.timeMark;
            final double displayPeriodMills = currentNotification.getDisplayPeriod() * 1000.0D;
            double lifeTimeRemain = displayPeriodMills - lifeTime;
            double d0 = lifeTime / displayPeriodMills;

            if (d0 > 1.0D) {
                this.timeMark = 0L;
                switchToNext();
                return;
            }

            final int animationLengthMillis = 800;
            if (lifeTime < animationLengthMillis) {
                d0 = 1 - lifeTime / animationLengthMillis;
            } else if (lifeTimeRemain < animationLengthMillis) {
                d0 = 1 - lifeTimeRemain / animationLengthMillis;
            } else {
                d0 = 0;
            }


            double d1 = Math.pow(d0, 3);
            prepareToDraw(scaledResolution);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            int x = 0;//scaledWidth - 160;
            int y = 0 - (int) (d1 * 36.0D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            mc.getTextureManager().bindTexture(ACHIEVEMENT_BG);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            gui.drawTexturedModalRect(x, y, 96, 202, 160, 32);

            // if (this.field_146262_n) {
            mc.fontRenderer.drawSplitString(this.currentNotification.getMessage(),
                    x + 30, y + 7, 120, -1);
            /*} else {
                this.mc.fontRenderer.drawString(this.field_146268_i, x + 30, y + 7, -256);
                this.mc.fontRenderer.drawString(this.field_146265_j, x + 30, y + 18, -1);
            }*/

            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_LIGHTING);
            if (currentNotification.hasItemStack() && currentNotification.getItemStack().getItem() != null)
                renderItem.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(),
                        currentNotification.getItemStack(), x + 8, y + 8);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
    }

}
