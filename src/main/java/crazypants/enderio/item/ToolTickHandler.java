package crazypants.enderio.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.network.PacketHandler;

public class ToolTickHandler {
  protected int slotSelected = -1;
  protected int dWheel = 0;

  /*
   * At the beginning of the tick we see if there's a player holding a yeta wrench
   * and sneaking. If so, then we'll "steal" the mouse wheel movement and store it
   * for ourself. Other mods will not see it anymore. Minecraft itself will, as
   * it uses the event system---which is not cleared by polling.
   * 
   * Because of that, Minecraft will happily change hotbar slots while we also 
   * handle the wheel. So we need to undo that.
   * 
   * Later, we'll process the information: Once the wheel has moved at least 120
   * units (that's one "tick" for mouse wheels that have "ticks"), we'll change
   * the mode according to the direction of the scroll. If not, we'll continue to
   * collect movement data.
   * 
   * If the wheel has been moved, but only a little bit, we keep recording the
   * change. This case can only happen when the user has a "tick"-less mouse or a 
   * touchpad that gives finer data.
   */
  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (event.phase == Phase.START) {
      EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
      if(player != null && player.isSneaking() && isToolSelected(player)) {
        if (slotSelected != -1) {
          dWheel += Mouse.getDWheel();
        } else {
          dWheel = Mouse.getDWheel();
          slotSelected = player.inventory.currentItem;
        }
      } else {
        slotSelected = -1;
      }
    } else if (slotSelected != -1) {
      EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
      if (player != null) {
        ItemStack stack = player.inventory.getStackInSlot(slotSelected);
        if (stack != null) {
          player.inventory.currentItem = slotSelected;
          Minecraft.getMinecraft().playerController.updateController();

          if (Math.abs(dWheel) >= 120) {
            if(stack.getItem() == EnderIO.itemConduitProbe) {            
              changeConduitProbeMode(stack);              
            } else if(stack.getItem() instanceof IConduitControl) {          
              changeDisplayMode(stack, player);
            }
            dWheel = 0;
          }
        }
      }
    }
  }

  private void changeDisplayMode(ItemStack stack, EntityPlayer player) {
    if (!((IConduitControl)stack.getItem()).showOverlay(stack, player)) {
      return;
    }
    ConduitDisplayMode mode = ConduitDisplayMode.getDisplayMode(stack);
    if(mode == null) {
      mode = ConduitDisplayMode.ALL;
    }
    mode = dWheel < 0 ? mode.next() : mode.previous();
    ConduitDisplayMode.setDisplayMode(stack, mode);
    PacketHandler.INSTANCE.sendToServer(new YetaWrenchPacketProcessor(slotSelected, mode));
  }

  private void changeConduitProbeMode(ItemStack stack) {
    int newMeta = stack.getItemDamage() == 0 ? 1 : 0;
    stack.setItemDamage(newMeta);
    PacketHandler.INSTANCE.sendToServer(new PacketConduitProbeMode());
  }

  private boolean isToolSelected(EntityClientPlayerMP player) {
    return player != null && player.getCurrentEquippedItem() != null && 
        (player.getCurrentEquippedItem().getItem() instanceof IConduitControl || player.getCurrentEquippedItem().getItem() == EnderIO.itemConduitProbe);
  }

}
