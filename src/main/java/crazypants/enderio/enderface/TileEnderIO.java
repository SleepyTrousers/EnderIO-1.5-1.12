package crazypants.enderio.enderface;

import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public class TileEnderIO extends TileTravelAnchor {

  float lastUiPitch = -45;
  float lastUiYaw = 45;
  double lastUiDistance = 10;

  @Store
  float initUiPitch = -45;
  @Store
  float initUiYaw = 45;

  @Override
  @SideOnly(Side.CLIENT)
  public double getMaxRenderDistanceSquared() {
    return TravelController.instance.getMaxTravelDistanceSq();
  }

  @Override
  public boolean shouldRenderInPass(int passNo) {
    return true;
  }

  @Override
  public void readCustomNBT(NBTTagCompound par1nbtTagCompound) {
    super.readCustomNBT(par1nbtTagCompound);
    lastUiPitch = initUiPitch;
    lastUiYaw = initUiYaw;
  }

}
