package ru.leoltron.graphicnotify;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;
import ru.leoltron.graphicnotify.client.gui.notification.NotificationManager;
import ru.leoltron.graphicnotify.common.ProxyCommon;
import ru.leoltron.graphicnotify.common.command.sendnotification.SendNotificationCommand;
import ru.leoltron.graphicnotify.packet.PacketDispatcher;

@Mod(modid = ModInfo.MOD_ID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class GraphicNotify {

    @SideOnly(Side.CLIENT)
    public static NotificationManager notificationManager;

    @Mod.Instance("GraphicNotify")
    public static GraphicNotify instance;
    private Logger logger;

    public void error(String message, Throwable t) {
        logger.error(message, t);
    }

    public void info(String message) {
        logger.info(message);
    }

    @SidedProxy(clientSide = "ru.leoltron.graphicnotify.client.ProxyClient",
            serverSide = "ru.leoltron.graphicnotify.common.ProxyCommon")
    public static ProxyCommon proxy = new ProxyCommon();
    public static PacketDispatcher net = new PacketDispatcher();

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event) {
        instance = this;

        logger = event.getModLog();

        PacketDispatcher.registerPackets();
    }

    @Mod.EventHandler
    public void LoadMod(FMLInitializationEvent event) {
        proxy.registerThings();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) throws IllegalAccessException {
        event.registerServerCommand(new SendNotificationCommand());
    }


}
