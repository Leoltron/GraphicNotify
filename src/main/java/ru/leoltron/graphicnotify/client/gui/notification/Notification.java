package ru.leoltron.graphicnotify.client.gui.notification;

import net.minecraft.item.ItemStack;
import ru.leoltron.graphicnotify.ModInfo;

public class Notification {

    private ItemStack itemStack;
    private String message;
    private int displayPeriod;
    private String url;

    public Notification(ItemStack itemStack, String message, int displayPeriod, String url) {
        this.itemStack = itemStack;
        this.message = message;
        this.displayPeriod = displayPeriod;
        this.url = url;
    }


    public Notification(ItemStack itemStack, String message) {
        this(itemStack, message, ModInfo.NOTIFICATION_DEFAULT_DISPLAY_TIME_SECONDS, "");
    }

    public Notification(String message) {
        this(null, message);
    }

    public boolean hasItemStack() {
        return itemStack != null;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getMessage() {
        return message;
    }

    public int getDisplayPeriod() {
        return displayPeriod;
    }

    public String getUrl() {
        return url;
    }


    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDisplayPeriod(int displayPeriod) {
        this.displayPeriod = displayPeriod;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
