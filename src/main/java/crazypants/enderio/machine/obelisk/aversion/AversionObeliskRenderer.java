package crazypants.enderio.machine.obelisk.aversion;

import crazypants.enderio.EnderIO;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.obelisk.render.ObeliskSpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AversionObeliskRenderer extends ObeliskSpecialRenderer<TileAversionObelisk> {

  private ItemStack offStack = new ItemStack(EnderIO.blockEndermanSkull, 1, BlockEndermanSkull.SkullType.TORMENTED.ordinal());
  private ItemStack onStack = new ItemStack(EnderIO.blockEndermanSkull, 1, BlockEndermanSkull.SkullType.REANIMATED_TORMENTED.ordinal());

  public AversionObeliskRenderer() {
    super(null, EnderIO.blockSpawnGuard);
  }

  @Override
  protected ItemStack getFloatingItem(TileAversionObelisk te) {
    if (te != null && te.isActive()) {
      return onStack;
    }
    return offStack;
  }
}
