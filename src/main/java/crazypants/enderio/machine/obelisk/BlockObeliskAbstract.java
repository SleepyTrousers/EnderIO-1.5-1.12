package crazypants.enderio.machine.obelisk;

import java.util.Random;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public abstract class BlockObeliskAbstract<T extends AbstractMachineEntity> extends AbstractMachineBlock<T> {

  public BlockObeliskAbstract(ModObject mo, Class<T> teClass, Class<? extends ItemBlock> itemBlockClass) {
    super(mo, teClass, itemBlockClass);
    setBlockBounds(0.11f, 0, 0.11f, 0.91f, 0.48f, 0.91f);
  }

  protected BlockObeliskAbstract(ModObject mo, Class<T> teClass) {
    super(mo, teClass);
    setBlockBounds(0.11f, 0, 0.11f, 0.91f, 0.48f, 0.91f);
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }
  
  @Override
  public boolean isFullBlock() {
    return false;
  }  
  
  @Override
  public int getLightOpacity() {
    return 0;
  }

  @Override
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {  
    if(isActive(world, pos) && shouldDoWorkThisTick(world, pos, 5)) {
      float startX = pos.getX() + 1.0F;
      float startY = pos.getY() + 0.85F;
      float startZ = pos.getZ() + 1.0F;
      for (int i = 0; i < 1; i++) {
        float xOffset = -0.2F - rand.nextFloat() * 0.6F;
        float yOffset = -0.1F + rand.nextFloat() * 0.2F;
        float zOffset = -0.2F - rand.nextFloat() * 0.6F;

        EntityFX fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
        if(fx != null) {
          fx.setRBGColorF(0.2f, 0.2f, 0.8f);
          fx.motionY *= 0.5f;
        }

      }
    }
  }
}
