package crazypants.enderio.machine.obelisk.aversion;

import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.obelisk.render.ObeliskSpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.blockEndermanSkull;
import static crazypants.enderio.ModObject.blockSpawnGuard;

@SideOnly(Side.CLIENT)
public class AversionObeliskRenderer extends ObeliskSpecialRenderer<TileAversionObelisk> {

  private ItemStack offStack = new ItemStack(blockEndermanSkull.getBlock(), 1, BlockEndermanSkull.SkullType.TORMENTED.ordinal());
  private ItemStack onStack = new ItemStack(blockEndermanSkull.getBlock(), 1, BlockEndermanSkull.SkullType.REANIMATED_TORMENTED.ordinal());

  public AversionObeliskRenderer() {
    super(null, blockSpawnGuard.getBlock());
  }

  @Override
  protected ItemStack getFloatingItem(TileAversionObelisk te) {
    if (te != null && te.isActive()) {
      return onStack;
    }
    return offStack;
  }
}
