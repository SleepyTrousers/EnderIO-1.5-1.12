package crazypants.enderio.machine.obelisk.relocator;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.obelisk.render.ObeliskSpecialRenderer;

@SideOnly(Side.CLIENT)
public class RelocatorObeliskRenderer extends ObeliskSpecialRenderer<TileRelocatorObelisk> {

  private ItemStack offStack = new ItemStack(Blocks.prismarine);
  private ItemStack onStack = new ItemStack(Blocks.prismarine);

  public RelocatorObeliskRenderer() {
    super(EnderIO.blockSpawnGuard, null);
  }

  @Override
  protected ItemStack getFloatingItem(TileRelocatorObelisk te) {
    if (te != null && te.isActive()) {
      return onStack;
    }
    return offStack;
  }
}
