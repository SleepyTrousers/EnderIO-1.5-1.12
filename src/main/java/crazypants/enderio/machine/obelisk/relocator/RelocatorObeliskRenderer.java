package crazypants.enderio.machine.obelisk.relocator;

import crazypants.enderio.machine.obelisk.render.ObeliskSpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machine.MachineObject.block_relocator_obelisk;

@SideOnly(Side.CLIENT)
public class RelocatorObeliskRenderer extends ObeliskSpecialRenderer<TileRelocatorObelisk> {

  private ItemStack offStack = new ItemStack(Blocks.PRISMARINE);
  private ItemStack onStack = new ItemStack(Blocks.PRISMARINE);

  public RelocatorObeliskRenderer() {
    super(null, block_relocator_obelisk.getBlock());
  }

  @Override
  protected ItemStack getFloatingItem(TileRelocatorObelisk te) {
    if (te != null && te.isActive()) {
      return onStack;
    }
    return offStack;
  }
}
