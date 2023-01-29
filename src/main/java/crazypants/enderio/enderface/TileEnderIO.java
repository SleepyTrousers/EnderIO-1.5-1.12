package crazypants.enderio.enderface;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;

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
    public void readCustomNBT(NBTTagCompound par1nbtTagCompound) {
        super.readCustomNBT(par1nbtTagCompound);
        initUiPitch = par1nbtTagCompound.getFloat("defaultUiPitch");
        initUiYaw = par1nbtTagCompound.getFloat("defaultUiYaw");
        lastUiPitch = initUiPitch;
        lastUiYaw = initUiYaw;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound par1nbtTagCompound) {
        super.writeCustomNBT(par1nbtTagCompound);
        par1nbtTagCompound.setFloat("defaultUiPitch", initUiPitch);
        par1nbtTagCompound.setFloat("defaultUiYaw", initUiYaw);
    }
}
