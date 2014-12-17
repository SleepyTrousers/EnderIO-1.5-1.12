package crazypants.enderio.item.skull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.TileEntityEio;

public class TileEndermanSkull extends TileEntityEio {

  float yaw;
  
  @Override
  protected void writeCustomNBT(NBTTagCompound root) {
    root.setFloat("yaw", yaw);    
  }

  @Override
  protected void readCustomNBT(NBTTagCompound root) {
    yaw = root.getFloat("yaw");    
  }

  public void setYaw(float yaw) {
    this.yaw = yaw;    
  }

  @Override
  public boolean canUpdate() {
    return false;
  }

public boolean rotate(ForgeDirection axis) {
	if(axis==ForgeDirection.DOWN)
		yaw += 90;
	else if(axis==ForgeDirection.UP)
		yaw -= 90;
	else
		return false; //if neither up nor down
	return true; //if either up or down
}

}
