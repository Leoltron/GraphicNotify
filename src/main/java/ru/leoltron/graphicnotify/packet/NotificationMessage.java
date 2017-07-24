package ru.leoltron.graphicnotify.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import ru.leoltron.graphicnotify.GraphicNotify;
import ru.leoltron.graphicnotify.ModInfo;
import ru.leoltron.graphicnotify.client.gui.notification.Notification;

import java.nio.charset.Charset;

public class NotificationMessage implements IMessage {

    private String message;
    private String url;
    private ItemStack iconItemStack;
    private int displayTime;
    private boolean isTranslation;

    public NotificationMessage(String message, ItemStack iconItemStack,
                               int displayTime, String url, boolean isTranslation) {
        this.message = message;
        this.iconItemStack = iconItemStack == null ? new ItemStack(Blocks.air) : iconItemStack;
        this.displayTime = displayTime;
        this.url = url;
        this.isTranslation = isTranslation;
    }

    public NotificationMessage(String message, ItemStack iconItemStack, int displayTime, String url) {
        this(message, iconItemStack, displayTime, url, false);
    }

    public NotificationMessage(String message, ItemStack iconItemStack, boolean isTranslation) {
        this(message, iconItemStack, ModInfo.NOTIFICATION_DEFAULT_DISPLAY_TIME_SECONDS, "", isTranslation);
    }

    public NotificationMessage(String message, ItemStack iconItemStack) {
        this(message, iconItemStack, ModInfo.NOTIFICATION_DEFAULT_DISPLAY_TIME_SECONDS, "");
    }

    public NotificationMessage() {
        this("", new ItemStack(Blocks.air));
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        message = readStringFromBuffer(buf);

        int id = buf.readInt();
        int size = buf.readInt();
        int damage = buf.readInt();

        Item item = Item.getItemById(id);
        iconItemStack = new ItemStack(item, size, damage);

        displayTime = buf.readInt();

        isTranslation = buf.readBoolean();
        if (isTranslation)
            translateMessage();
        if (buf.readBoolean())
            url = readStringFromBuffer(buf);
    }

    private void translateMessage() {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (String s : this.getMessage().split(" ")) {
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append(' ');
            }
            builder.append(StatCollector.translateToLocal(s));
        }
        this.message = builder.toString();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeStringToBuffer(buf, message);


        int id = Item.getIdFromItem(iconItemStack.getItem());
        buf.writeInt(id);
        buf.writeInt(iconItemStack.stackSize);
        buf.writeInt(id == 0 ? 0 : this.iconItemStack.getItemDamage());

        buf.writeInt(displayTime);

        buf.writeBoolean(isTranslation);
        buf.writeBoolean(haveUrl());
        if (haveUrl())
            writeStringToBuffer(buf, url);

    }


    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }

    public boolean haveUrl() {
        return url.length() > 0;
    }

    public ItemStack getIconItemStack() {
        return iconItemStack;
    }

    public int getDisplayTime() {
        return displayTime;
    }

    /**
     * Writes string length and string itself into buffer
     */
    private static void writeStringToBuffer(ByteBuf buf, String string) {
        byte[] killedNameBytes = string.getBytes(Charset.forName("UTF-8"));
        int killedNameBytesLength = killedNameBytes.length;

        buf.writeInt(killedNameBytesLength);
        buf.writeBytes(killedNameBytes);
    }

    private static String readStringFromBuffer(ByteBuf buf) {
        int stringBytesLength = buf.readInt();
        byte[] stringBytes = new byte[stringBytesLength];
        buf.readBytes(stringBytes);
        return new String(stringBytes, Charset.forName("UTF-8"));
    }

    public static class Handler extends AbstractClientMessageHandler<NotificationMessage> {
        @Override
        public IMessage onMessage(NotificationMessage message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                return handleClientMessage(GraphicNotify.proxy.getPlayerEntity(ctx), message, ctx);
            } else {
                return handleServerMessage(GraphicNotify.proxy.getPlayerEntity(ctx), message, ctx);
            }
        }

        @Override
        public IMessage handleClientMessage(EntityPlayer player_, NotificationMessage message, MessageContext ctx) {
            String iteminfo = message.getIconItemStack().getItem() == null ? "air" : message.getIconItemStack().toString();
            GraphicNotify.instance.info("Received notification:" +
                    " icon_itemstack=" + iteminfo +
                    " message=" + message.getMessage() +
                    " url=" + message.getUrl() + "" +
                    " display_time=" + message.getDisplayTime());
            GraphicNotify.notificationManager.addNotification(
                    new Notification(message.getIconItemStack(), message.getMessage(), message.getDisplayTime(), message.getUrl()));
            return null;
        }
    }
}


