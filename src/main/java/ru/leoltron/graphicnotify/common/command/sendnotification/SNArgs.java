package ru.leoltron.graphicnotify.common.command.sendnotification;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import ru.leoltron.graphicnotify.ModInfo;
import ru.leoltron.graphicnotify.packet.NotificationMessage;
import ru.leoltron.graphicnotify.packet.PacketDispatcher;

import java.util.List;

class SNArgs {

    String url;
    ItemStack itemStack;
    int displayTime;
    String message;
    private EntityPlayerMP targetPlayer;

    SNArgs() {
        url = "";
        itemStack = null;
        displayTime = ModInfo.NOTIFICATION_DEFAULT_DISPLAY_TIME_SECONDS;
        message = "";
    }

    void formAndSendMessage() {
        if (targetPlayer == null)
            PacketDispatcher.sendToAll(toMessage());
        else
            PacketDispatcher.sendTo(toMessage(), targetPlayer);
    }

    private NotificationMessage toMessage() {
        return new NotificationMessage(message, itemStack, displayTime, url);
    }

    void setTargetPlayer(String playerName) throws SNException {
        EntityPlayer foundPlayer = findPlayerByName(playerName);

        if (foundPlayer instanceof EntityPlayerMP)
            targetPlayer = (EntityPlayerMP) foundPlayer;
        else {
            throw new SNException("Cannot cast to EntityPlayerMP ?!");
        }
    }

    private EntityPlayer findPlayerByName(String name) throws SNException {
        List<EntityPlayer> playerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for (EntityPlayer player : playerList)
            if (player.getCommandSenderName().equals(name))
                return player;
        throw new SNException("Can't find player " + name + '.');
    }
}
