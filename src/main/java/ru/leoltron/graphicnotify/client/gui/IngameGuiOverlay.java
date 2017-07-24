package ru.leoltron.graphicnotify.client.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import ru.leoltron.graphicnotify.GraphicNotify;

public class IngameGuiOverlay extends Gui {

    private Minecraft mc;
    private RenderItem itemRender = new RenderItem();


    public IngameGuiOverlay(Minecraft mc) {
        super();
        this.mc = mc;
    }

    @SubscribeEvent
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent event) {

        if (event.isCancelable() || event.type != ElementType.EXPERIENCE)
            return;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);

        GraphicNotify.notificationManager.drawNotification(this, itemRender, scaledresolution);

    }


}
