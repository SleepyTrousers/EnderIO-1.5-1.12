package crazypants.enderio.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.network.PacketHandler;

public class ToolTickHandler {
  protected int slotSelected = -1;
  public static int dWheel;

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {

    if(event.phase == Phase.START) {
      dWheel = Mouse.getDWheel() / 120;
      EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
      if(isToolSelected(player)
          && player.isSneaking()) {
        slotSelected = player.inventory.currentItem;
      } else {
        slotSelected = -1;
      }
    } else {

      EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
      if(player != null) {
        if(slotSelected > -1 && dWheel != Mouse.getDWheel()) {
          player.inventory.currentItem = slotSelected;
          Minecraft.getMinecraft().playerController.updateController();

          ItemStack stack = player.inventory.getStackInSlot(slotSelected);
          if(stack == null) {
            slotSelected = -1;
            return;
          }          
          if(stack.getItem() == EnderIO.itemConduitProbe) {            
            changeConduitProbeMode(stack);              
          } else if(stack.getItem() ==EnderIO.itemYetaWench) {          
            changeYetaWrenchMode(stack);
          }
        }
        slotSelected = -1;

      }
    }
  }

  private void changeYetaWrenchMode(ItemStack stack) {
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
      PacketHandler.INSTANCE.sendToServer(new YetaWrenchPacketProcessor(slotSelected, newMode));
    }
  }

  private void changeConduitProbeMode(ItemStack stack) {
    int newMeta = stack.getItemDamage() == 0 ? 1 : 0;
    stack.setItemDamage(newMeta);
    PacketHandler.INSTANCE.sendToServer(new PacketConduitProbeMode());
  }

  private boolean isToolSelected(EntityClientPlayerMP player) {
    return player != null && player.getCurrentEquippedItem() != null && 
        (player.getCurrentEquippedItem().getItem() == EnderIO.itemYetaWench || player.getCurrentEquippedItem().getItem() == EnderIO.itemConduitProbe);
  }

}
