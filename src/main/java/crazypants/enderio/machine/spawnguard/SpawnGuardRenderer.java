package crazypants.enderio.machine.spawnguard;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.EnderIO;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.attractor.AttractorRenderer;

public class SpawnGuardRenderer extends AttractorRenderer {

  private ItemStack offStack = new ItemStack(EnderIO.blockEndermanSkull, 1, BlockEndermanSkull.SkullType.TORMENTED.ordinal());
  private ItemStack onStack = new ItemStack(EnderIO.blockEndermanSkull, 1, BlockEndermanSkull.SkullType.REANIMATED_TORMENTED.ordinal());
  
  @Override
  protected ItemStack getFloatingStack(TileEntity te, double x, double y, double z, float tick) {
    if(te == null) {
      return offStack;
    }
    TileSpawnGuard sg = (TileSpawnGuard)te;
    if(sg.isActive()) {
      return onStack;
    }
    return offStack;
  }

  
  
}
