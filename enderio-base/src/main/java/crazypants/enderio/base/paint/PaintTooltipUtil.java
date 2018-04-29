package crazypants.enderio.base.paint;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Adds a tooltip with paint information to items that can be painted.
 *
 */
@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class PaintTooltipUtil {

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void addTooltip(@Nonnull ItemTooltipEvent evt) {
    if (PaintUtil.isPaintable(evt.getItemStack())) {
      evt.getToolTip().add(PaintUtil.getTooltTipText(evt.getItemStack()));
    }
  }

}
