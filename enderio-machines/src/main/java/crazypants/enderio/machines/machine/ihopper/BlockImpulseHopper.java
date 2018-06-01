package crazypants.enderio.machines.machine.ihopper;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractCapabilityPoweredMachineBlock;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockImpulseHopper extends AbstractCapabilityPoweredMachineBlock<TileImpulseHopper> implements ISmartRenderAwareBlock, IResourceTooltipProvider {

  public static BlockImpulseHopper create(@Nonnull IModObject modObject) {
    BlockImpulseHopper iHopper = new BlockImpulseHopper(modObject);
    iHopper.init();
    return iHopper;
  }

  public BlockImpulseHopper(@Nonnull IModObject mo) {
    super(mo);
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileImpulseHopper tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.isActive());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return ImpulseRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return ImpulseRenderMapper.instance;
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileImpulseHopper te) {
    return new ContainerImpulseHopper(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileImpulseHopper te) {
    return new GuiImpulseHopper(player.inventory, te);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState stateIn, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    TileImpulseHopper te = getTileEntity(world, pos);

    if (te != null && te.isActive() && !world.getBlockState(pos.up()).isOpaqueCube()) {
      if (rand.nextInt(8) == 0) {
        float startX = pos.getX() + 0.8F - rand.nextFloat() * 0.6F;
        float startY = pos.getY() + 1.0F;
        float startZ = pos.getZ() + 0.8F - rand.nextFloat() * 0.6F;
        world.spawnParticle(EnumParticleTypes.REDSTONE, startX, startY, startZ, 0, 0, 0);
      }
    }
  }

  @Override
  public boolean hasComparatorInputOverride(@Nonnull IBlockState state) {
    return true;
  }

}
