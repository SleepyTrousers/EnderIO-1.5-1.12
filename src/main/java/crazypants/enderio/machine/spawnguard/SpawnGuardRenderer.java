package crazypants.enderio.machine.spawnguard;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.init.EIOBlocks;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.attractor.ObeliskRenderer;

@SideOnly(Side.CLIENT)
public class SpawnGuardRenderer extends ObeliskRenderer<TileSpawnGuard> {

  private ItemStack offStack = new ItemStack(EIOBlocks.blockEndermanSkull, 1, BlockEndermanSkull.SkullType.TORMENTED.ordinal());
  private ItemStack onStack = new ItemStack(EIOBlocks.blockEndermanSkull, 1, BlockEndermanSkull.SkullType.REANIMATED_TORMENTED.ordinal());

  public SpawnGuardRenderer() {
    super(null);
  }

  @Override
  protected ItemStack getFloatingItem(TileSpawnGuard te) {
    if(te == null) {
      return offStack;
    }
    TileSpawnGuard sg = te;
    if(sg.isActive()) {
      return onStack;
    }
    return offStack;
  }



}
