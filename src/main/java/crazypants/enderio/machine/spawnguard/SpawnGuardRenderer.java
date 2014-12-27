package crazypants.enderio.machine.spawnguard;

import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.attractor.ObeliskRenderer;

public class SpawnGuardRenderer extends ObeliskRenderer<TileSpawnGuard> {

  private ItemStack offStack = new ItemStack(EnderIO.blockEndermanSkull, 1, BlockEndermanSkull.SkullType.TORMENTED.ordinal());
  private ItemStack onStack = new ItemStack(EnderIO.blockEndermanSkull, 1, BlockEndermanSkull.SkullType.REANIMATED_TORMENTED.ordinal());
  
  public SpawnGuardRenderer() {
    super(null);
  }

  @Override
  protected ItemStack getFloatingItem(TileSpawnGuard te) {
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
