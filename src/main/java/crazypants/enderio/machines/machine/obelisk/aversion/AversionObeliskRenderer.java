package crazypants.enderio.machines.machine.obelisk.aversion;


import crazypants.enderio.base.block.skull.SkullType;
import crazypants.enderio.machines.machine.obelisk.render.ObeliskSpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.init.ModObject.blockEndermanSkull;
import static crazypants.enderio.machines.init.MachineObject.block_aversion_obelisk;


@SideOnly(Side.CLIENT)
public class AversionObeliskRenderer extends ObeliskSpecialRenderer<TileAversionObelisk> {

  private ItemStack offStack = new ItemStack(blockEndermanSkull.getBlock(), 1, SkullType.TORMENTED.ordinal());
  private ItemStack onStack = new ItemStack(blockEndermanSkull.getBlock(), 1, SkullType.REANIMATED_TORMENTED.ordinal());

  public AversionObeliskRenderer() {
    super(null, block_aversion_obelisk.getBlock());
  }

  @Override
  protected ItemStack getFloatingItem(TileAversionObelisk te) {
    if (te != null && te.isActive()) {
      return onStack;
    }
    return offStack;
  }
}
