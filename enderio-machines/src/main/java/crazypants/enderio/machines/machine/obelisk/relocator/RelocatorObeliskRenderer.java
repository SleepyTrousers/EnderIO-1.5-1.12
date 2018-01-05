package crazypants.enderio.machines.machine.obelisk.relocator;

import javax.annotation.Nonnull;

import crazypants.enderio.machines.machine.obelisk.render.ObeliskSpecialRenderer;
import crazypants.enderio.util.Prep;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.init.MachineObject.block_relocator_obelisk;

@SideOnly(Side.CLIENT)
public class RelocatorObeliskRenderer extends ObeliskSpecialRenderer<TileRelocatorObelisk> {

  private final @Nonnull ItemStack offStack = new ItemStack(Blocks.PRISMARINE);
  private final @Nonnull ItemStack onStack = new ItemStack(Blocks.PRISMARINE);

  public RelocatorObeliskRenderer() {
    super(Prep.getEmpty(), block_relocator_obelisk.getBlock());
  }

  @Override
  protected @Nonnull ItemStack getFloatingItem(TileRelocatorObelisk te) {
    if (te != null && te.isActive()) {
      return onStack;
    }
    return offStack;
  }
}
