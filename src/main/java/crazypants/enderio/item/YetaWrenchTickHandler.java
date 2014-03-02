package crazypants.enderio.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConduitDisplayMode;

public class YetaWrenchTickHandler {
  protected int slotSelected = -1;
  public static int dWheel;

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
    if(player != null) {
      if(slotSelected > -1 && dWheel != Mouse.getDWheel()) {
        player.inventory.currentItem = slotSelected;
        Minecraft.getMinecraft().playerController.updateController();

        ItemStack stack = player.inventory.getStackInSlot(slotSelected);
        ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(stack);
        if(curMode == null) {
          curMode = ConduitDisplayMode.ALL;
        }

        int dif = dWheel - Mouse.getDWheel();
        ConduitDisplayMode newMode = null;
        if(dif < 0) {
          newMode = curMode.next();
          ConduitDisplayMode.setDisplayMode(stack, newMode);
        } else if(dif > 0) {
          newMode = curMode.previous();
          ConduitDisplayMode.setDisplayMode(stack, newMode);
        }
        if(newMode != null) {
          EnderIO.packetPipeline.sendToServer(new YetaWrenchPacketProcessor(slotSelected, newMode));
        }
      }
      slotSelected = -1;

    }
  }

}
