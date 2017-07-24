package ru.leoltron.graphicnotify.packet;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import ru.leoltron.graphicnotify.ModInfo;

@SuppressWarnings("WeakerAccess")
public final class PacketDispatcher {
    private static byte packetId = 0;

    private static SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MOD_ID);

    /**
     * Call this during pre-init or loading and register all of your packets (messages) here
     */
    public static void registerPackets() {
        PacketDispatcher.registerMessage(NotificationMessage.Handler.class, NotificationMessage.class, Side.CLIENT);
    }

    /**
     * Registers a message and message handler
     */
    private static void registerMessage(Class handlerClass, Class messageClass, Side side) {
        //noinspection unchecked
        PacketDispatcher.dispatcher.registerMessage(handlerClass, messageClass, packetId++, side);
    }

    /**
     * Send this message to the specified player.
     * See {@link SimpleNetworkWrapper#sendTo(IMessage, EntityPlayerMP)}
     */
    public static void sendTo(IMessage message, EntityPlayerMP player) {
        PacketDispatcher.dispatcher.sendTo(message, player);
    }

    /**
     * Send this message to everyone
     */
    public static void sendToAll(IMessage message) {
        PacketDispatcher.dispatcher.sendToAll(message);
    }

}