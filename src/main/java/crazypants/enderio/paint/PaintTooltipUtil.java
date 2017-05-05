package crazypants.enderio.paint;

import crazypants.util.Prep;
import crazypants.util.Things;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Adds a tooltip with paint information to items thatc an be painted.
 *
 */
public class PaintTooltipUtil {

  public static void create() {
    MinecraftForge.EVENT_BUS.register(PaintTooltipUtil.class);
  }

  private PaintTooltipUtil() {
  }

  private static Things paintables = new Things();

  /**
   * Registers blocks that can be painted but do not implement IPaintable themselves. Used for blocks that are not Ender IO blocks, e.g. vanilla fences.
   */
  public static void registerPaintable(Block... blocks) {
    for (Block block : blocks) {
      if (!(block instanceof IPaintable)) {
        paintables.add(block);
      }
    }
  }

  /**
   * Registers items that can be painted but are not items for blocks implement IPaintable themselves. Used for items that do not have a block , e.g. Dark Steel
   * Helmets and Facades.
   */
  public static void registerPaintable(Item... items) {
    for (Item item : items) {
      if (!(Block.getBlockFromItem(item) instanceof IPaintable)) {
        paintables.add(item);
      }
    }
  }

  /**
   * Checks if an item is paintable.
   * <p>
   * An item can be painted if
   * <ul>
   * <li>it already is painted,
   * <li>it represents a block that implements IPaintable,
   * <li>it represents a block that was registered as being paintable, or
   * <li>it was registered as being paintable.
   * </ul>
   */
  public static boolean isPaintable(ItemStack stack) {
    if (Prep.isInvalid(stack)) {
      return false;
    }
    return PainterUtil2.isPainted(stack) || Block.getBlockFromItem(stack.getItem()) instanceof IPaintable || paintables.contains(stack);
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void addTooltip(ItemTooltipEvent evt) {
    if (isPaintable(evt.getItemStack())) {
      evt.getToolTip().add(PainterUtil2.getTooltTipText(evt.getItemStack()));
    }
  }

}
