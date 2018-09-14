package crazypants.enderio.conduits.handler;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.conduits.EnderIOConduits;
import crazypants.enderio.conduits.conduit.BlockConduitBundle;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODID)
public class ConduitBreakSpeedHandler {

  @SubscribeEvent
  public static void onBreakSpeed(BreakSpeed event) {
    if (event.getState().getBlock() instanceof BlockConduitBundle) {
      ItemStack held = event.getEntityPlayer().getHeldItemMainhand();
      if (held.getItem().getHarvestLevel(held, "pickaxe", event.getEntityPlayer(), event.getState()) == -1) {
        event.setNewSpeed(event.getNewSpeed() + 2);
      }
      IConduitBundle te = BlockConduitBundle.getAnyTileEntity(event.getEntity().world, NullHelper.notnullF(event.getPos(), "BreakSpeed#getPos"),
          IConduitBundle.class);
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
