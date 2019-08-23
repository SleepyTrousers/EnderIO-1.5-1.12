package crazypants.enderio.base.item.conduitprobe;

import javax.annotation.Nonnull;

import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.item.yetawrench.PacketYetaWrenchDisplayMode;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class ToolTickHandler {

  @SubscribeEvent
  public static void onMouseEvent(MouseEvent event) {
    if (PersonalConfig.yetaUseSneakMouseWheel.get()) {
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      if (event.getDwheel() != 0 && player.isSneaking()) {
        ItemStack stack = player.getHeldItemMainhand();
        Item item = stack.getItem();
        if (item instanceof IConduitControl) {
          changeDisplayMode(stack, player, event.getDwheel());
          event.setCanceled(true);
        } else if (item instanceof ItemConduitProbe) {
          changeConduitProbeMode(stack);
          event.setCanceled(true);
        }
      }
    }
  }

  private static void changeDisplayMode(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, int dWheel) {
    if (!((IConduitControl) stack.getItem()).showOverlay(stack, player)) {
      return;
    }
    ConduitDisplayMode mode = ConduitDisplayMode.getDisplayMode(stack);
    mode = dWheel < 0 ? mode.next() : mode.previous();
    ConduitDisplayMode.setDisplayMode(stack, mode);
    PacketHandler.INSTANCE.sendToServer(new PacketYetaWrenchDisplayMode(player.inventory.currentItem, mode));
  }

  private static void changeConduitProbeMode(@Nonnull ItemStack stack) {
    int newMeta = stack.getItemDamage() == 0 ? 1 : 0;
    stack.setItemDamage(newMeta);
    PacketHandler.INSTANCE.sendToServer(new PacketConduitProbeMode());
  }

}
