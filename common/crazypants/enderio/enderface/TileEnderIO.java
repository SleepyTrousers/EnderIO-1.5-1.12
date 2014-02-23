package crazypants.enderio.enderface;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.teleport.TileTravelAnchor;
import crazypants.enderio.teleport.TravelController;

public class TileEnderIO extends TileTravelAnchor {

  float lastUiPitch = -45;
  float lastUiYaw = 45;
  double lastUiDistance = 10;

  float initUiPitch = -45;
  float initUiYaw = 45;

  @Override
  @SideOnly(Side.CLIENT)
  public double getMaxRenderDistanceSquared() {
    return TravelController.instance.getMaxTravelDistanceSq();
  }

  @Override
  public boolean shouldRenderInPass(int passNo) {
    return passNo == 1;
  }

  @Override
  public boolean canUpdate() {
    return false;
  }

  @Override
  public Packet getDescriptionPacket() {
    return PacketHandler.getPacket(this);
  }

  @Override
  public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
    super.readFromNBT(par1nbtTagCompound);
    initUiPitch = par1nbtTagCompound.getFloat("defaultUiPitch");
    initUiYaw = par1nbtTagCompound.getFloat("defaultUiYaw");
    lastUiPitch = initUiPitch;
    lastUiYaw = initUiYaw;
  }

  @Override
  public void writeToNBT(NBTTagCompound par1nbtTagCompound) {
    super.writeToNBT(par1nbtTagCompound);
    par1nbtTagCompound.setFloat("defaultUiPitch", initUiPitch);
    par1nbtTagCompound.setFloat("defaultUiYaw", initUiYaw);
  }

}
