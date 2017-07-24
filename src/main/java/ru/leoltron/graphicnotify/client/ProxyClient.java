package ru.leoltron.graphicnotify.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import ru.leoltron.graphicnotify.GraphicNotify;
import ru.leoltron.graphicnotify.client.eventhandler.KeyHandler;
import ru.leoltron.graphicnotify.client.gui.IngameGuiOverlay;
import ru.leoltron.graphicnotify.client.gui.notification.NotificationManager;
import ru.leoltron.graphicnotify.common.ProxyCommon;

@SuppressWarnings("unused")
public class ProxyClient extends ProxyCommon {

    @Override
    public void registerThings() {
        super.registerThings();

        MinecraftForge.EVENT_BUS.register(new IngameGuiOverlay(Minecraft.getMinecraft()));

        FMLCommonHandler.instance().bus().register(new KeyHandler());

        GraphicNotify.notificationManager = new NotificationManager(Minecraft.getMinecraft());
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
    }

}
