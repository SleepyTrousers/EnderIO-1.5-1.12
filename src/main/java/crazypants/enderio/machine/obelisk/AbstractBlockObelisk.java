package crazypants.enderio.machine.obelisk;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.client.ClientUtil;

import crazypants.enderio.IModObject;
import crazypants.enderio.machine.obelisk.render.ObeliskRenderMapper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ITESRItemBlock;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractBlockObelisk<T extends AbstractMachineEntity> extends AbstractMachineBlock<T> implements ITESRItemBlock {

  public static final AxisAlignedBB AABB = new AxisAlignedBB(0.11f, 0, 0.11f, 0.91f, 0.48f, 0.91f);

  protected AbstractBlockObelisk(@Nonnull IModObject mo, Class<T> teClass) {
    super(mo, teClass, new Material(MapColor.IRON) {

      @Override
      public boolean isOpaque() {
        return false;
      }

    });
    setLightOpacity(5);
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return AABB;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }
  
  @Override
  public boolean isFullCube(IBlockState bs) {
    return false;
  }  
  
  @Override
  public void randomDisplayTick(IBlockState bs, World world, BlockPos pos, Random rand) {
    if (world != null && pos != null && rand != null && isActive(world, pos) && shouldDoWorkThisTick(world, pos, 5)) {
      float startX = pos.getX() + 1.0F;
      float startY = pos.getY() + 0.85F;
      float startZ = pos.getZ() + 1.0F;
      for (int i = 0; i < 1; i++) {
        float xOffset = -0.2F - rand.nextFloat() * 0.6F;
        float yOffset = -0.1F + rand.nextFloat() * 0.2F;
        float zOffset = -0.2F - rand.nextFloat() * 0.6F;

        Particle fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
        if(fx != null) {
          fx.setRBGColorF(0.2f, 0.2f, 0.8f);
          ClientUtil.setParticleVelocityY(fx, ClientUtil.getParticleVelocityY(fx) * 0.5);
        }

      }
    }
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull T tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.isActive());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IItemRenderMapper getItemRenderMapper() {
    return ObeliskRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return ObeliskRenderMapper.instance;
  }

}
