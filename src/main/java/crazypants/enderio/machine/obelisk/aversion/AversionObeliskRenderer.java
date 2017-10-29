package crazypants.enderio.machine.obelisk.aversion;

import crazypants.enderio.item.skull.SkullType;
import crazypants.enderio.machine.obelisk.render.ObeliskSpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machine.MachineObject.blockEndermanSkull;
import static crazypants.enderio.machine.MachineObject.blockSpawnGuard;

@SideOnly(Side.CLIENT)
public class AversionObeliskRenderer extends ObeliskSpecialRenderer<TileAversionObelisk> {

  private ItemStack offStack = new ItemStack(blockEndermanSkull.getBlock(), 1, SkullType.TORMENTED.ordinal());
  private ItemStack onStack = new ItemStack(blockEndermanSkull.getBlock(), 1, SkullType.REANIMATED_TORMENTED.ordinal());

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
