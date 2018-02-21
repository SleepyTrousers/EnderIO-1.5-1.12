package crazypants.enderio.conduit.handler;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.conduit.EnderIOConduits;
import crazypants.enderio.conduit.init.ConduitObject;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODID)
public class ConduitBreakSpeedHandler {

  @SubscribeEvent
  public static void onBreakSpeed(BreakSpeed event) {
    if (event.getState().getBlock() == ConduitObject.block_conduit_bundle.getBlockNN()) {
      ItemStack held = event.getEntityPlayer().getHeldItemMainhand();
      if (held.getItem().getHarvestLevel(held, "pickaxe", event.getEntityPlayer(), event.getState()) == -1) {
        event.setNewSpeed(event.getNewSpeed() + 2);
      }
      IConduitBundle te = (IConduitBundle) event.getEntity().world.getTileEntity(NullHelper.notnullF(event.getPos(), "BreakSpeed#getPos"));
      if (te != null && te.getFacadeType().isHardened()) {
        if (!YetaUtil.isSolidFacadeRendered(te, event.getEntityPlayer())) {
          event.setNewSpeed(event.getNewSpeed() * 6);
        } else {
          event.setNewSpeed(event.getNewSpeed() * 2);
        }
      }
    }
  }

}
