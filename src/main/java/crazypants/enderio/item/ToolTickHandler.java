package crazypants.enderio.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.network.PacketHandler;

@SideOnly(Side.CLIENT)
public class ToolTickHandler {

  @SubscribeEvent
  public void onMouseEvent(MouseEvent event) {
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
    if(event.dwheel != 0 && player != null && player.isSneaking()) {
      ItemStack stack = player.getCurrentEquippedItem();
      if (stack != null) {
        Item item = stack.getItem();
        if (item instanceof IConduitControl) {
          changeDisplayMode(stack, player, event.dwheel);
          event.setCanceled(true);
        } else if (item instanceof ItemConduitProbe) {
          changeConduitProbeMode(stack);
          event.setCanceled(true);
        }
      }
    }
  }

  private void changeDisplayMode(ItemStack stack, EntityPlayer player, int dWheel) {
    if (!((IConduitControl)stack.getItem()).showOverlay(stack, player)) {
      return;
    }
    ConduitDisplayMode mode = ConduitDisplayMode.getDisplayMode(stack);
    if(mode == null) {
      mode = ConduitDisplayMode.ALL;
    }
    mode = dWheel < 0 ? mode.next() : mode.previous();
    ConduitDisplayMode.setDisplayMode(stack, mode);
    PacketHandler.INSTANCE.sendToServer(new YetaWrenchPacketProcessor(player.inventory.currentItem, mode));
  }

  private void changeConduitProbeMode(ItemStack stack) {
    int newMeta = stack.getItemDamage() == 0 ? 1 : 0;
    stack.setItemDamage(newMeta);
    PacketHandler.INSTANCE.sendToServer(new PacketConduitProbeMode());
  }

}
