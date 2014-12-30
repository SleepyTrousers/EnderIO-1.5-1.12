package crazypants.util;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SoundUtil {

  @SideOnly(Side.CLIENT)
  public static void playClientSoundFX(String name, TileEntity te) {
    World world = Minecraft.getMinecraft().thePlayer.worldObj;    
    Minecraft.getMinecraft().theWorld.playSound(te.xCoord + 0.5, te.yCoord + 0.5, te.zCoord + 0.5, name, 0.1F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F),true);
  }
  
}
