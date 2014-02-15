package crazypants.enderio.teleport;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileTravelAnchor extends TileEntity {

  private static final int REND_DIST_SQ = TravelSource.getMaxDistanceSq();

  @Override
  public boolean canUpdate() {
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public double getMaxRenderDistanceSquared() {
    return TravelSource.getMaxDistanceSq();
  }

  @Override
  public boolean shouldRenderInPass(int pass) {
    return pass == 1;
  }

}
