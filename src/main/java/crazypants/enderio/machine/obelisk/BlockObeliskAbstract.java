package crazypants.enderio.machine.obelisk;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;

public abstract class BlockObeliskAbstract<T extends AbstractMachineEntity> extends AbstractMachineBlock<T> {

  public static int defaultObeliskRenderId;

  protected BlockObeliskAbstract(ModObject mo, Class<T> teClass) {
    super(mo, teClass);
    setBlockBounds(0.11f, 0, 0.11f, 0.91f, 0.48f, 0.91f);
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:blockAttractorSideOn";
    }
    return "enderio:blockAttractorSide";
  }

  @Override
  protected String getSideIconKey(boolean active) {
    return getMachineFrontIconKey(active);
  }

  @Override
  protected String getBackIconKey(boolean active) {
    return getMachineFrontIconKey(active);
  }

  @Override
  protected String getTopIconKey(boolean active) {
    return "enderio:blockSoulMachineTop";
  }

  @Override
  protected String getBottomIconKey(boolean active) {
    return "enderio:obeliskBottom";
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public int getLightOpacity() {
    return 0;
  }

  @Override
  public int getRenderType() {
    return defaultObeliskRenderId;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    if(isActive(world, x, y, z) && shouldDoWorkThisTick(world, x, y, z, 5)) {
      float startX = x + 1.0F;
      float startY = y + 0.85F;
      float startZ = z + 1.0F;
      for (int i = 0; i < 1; i++) {
        float xOffset = -0.2F - rand.nextFloat() * 0.6F;
        float yOffset = -0.1F + rand.nextFloat() * 0.2F;
        float zOffset = -0.2F - rand.nextFloat() * 0.6F;

        EntityFX fx = Minecraft.getMinecraft().renderGlobal.doSpawnParticle("spell", startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
        if(fx != null) {
          fx.setRBGColorF(0.2f, 0.2f, 0.8f);
          fx.motionY *= 0.5f;
        }

      }
    }
  }
}
