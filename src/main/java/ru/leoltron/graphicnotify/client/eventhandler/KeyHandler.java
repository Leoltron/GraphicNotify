package ru.leoltron.graphicnotify.client.eventhandler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import ru.leoltron.graphicnotify.GraphicNotify;

import java.net.URI;

public class KeyHandler {

    public static final int TO_LINK = 0;
    private static final String[] desc = {"key.go_to_notification_link.desc"};

    /**
     * Default key values
     */
    private static final int[] keyValues = {Keyboard.KEY_F};
    private static final KeyBinding[] keys = new KeyBinding[desc.length];

    public KeyHandler() {
        for (int i = 0; i < desc.length; ++i) {
            keys[i] = new KeyBinding(desc[i], keyValues[i], "key.graphicnotify.category");
            ClientRegistry.registerKeyBinding(keys[i]);
        }
    }


    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (!FMLClientHandler.instance().isGUIOpen(GuiChat.class)) {
            if (keys[TO_LINK].isPressed() && GraphicNotify.notificationManager.getCurrentURL() != null) {
                try {
                    openLink(GraphicNotify.notificationManager.getCurrentURL());
                } catch (Throwable throwable) {
                    GraphicNotify.instance.error("Couldn\'t open link", throwable);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void openLink(URI url) throws Throwable {
        Class desktopClass = Class.forName("java.awt.Desktop");
        Object desktopInstance = desktopClass.getMethod("getDesktop").invoke(null);
        desktopClass.getMethod("browse", URI.class).invoke(desktopInstance, url);
    }

    public static boolean isValidKeyCode(int id) {
        return KeyHandler.keys[id].getKeyCode() < Keyboard.KEYBOARD_SIZE && KeyHandler.keys[id].getKeyCode() >= 0;
    }

    public static String getKeyName(int id) {
        return Keyboard.getKeyName(KeyHandler.keys[id].getKeyCode());
    }
}
