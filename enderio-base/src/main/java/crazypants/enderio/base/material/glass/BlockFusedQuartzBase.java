package crazypants.enderio.base.material.glass;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.interfaces.IOverlayRenderAware;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.block.painted.BlockItemPaintedBlock;
import crazypants.enderio.base.config.config.BaseConfig;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.render.ICustomItemResourceLocation;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.itemoverlay.FusedQuartzOverlayRenderHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockFusedQuartzBase<T extends TileEntityEio> extends BlockEio<T> implements ISmartRenderAwareBlock {

  @SuppressWarnings("null")
  public BlockFusedQuartzBase(@Nonnull IModObject modObject) {
    super(modObject, Material.GLASS);
    setSoundType(SoundType.GLASS);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  /**
   * This should be a private final field, but it is needed in the super constructor. So this is the workaround...
   */
  protected @Nonnull PropertyEnum<FusedQuartzType> getKind() {
    return FusedQuartzType.KIND;
  }

  abstract public @Nonnull IFusedBlockstate getFusedBlockstate(@Nonnull IBlockState state);

  @Override
  public float getExplosionResistance(@Nonnull World world, @Nonnull BlockPos pos, @Nullable Entity exploder, @Nonnull Explosion explosion) {
    if (IFusedBlockstate.get(world.getBlockState(pos)).isBlastResistant()) {
      return BaseConfig.explosionResistantBlockHardness.get();
    } else {
      return super.getExplosionResistance(world, pos, exploder, explosion);
    }
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public int getLightOpacity(@Nonnull IBlockState state) {
    return IFusedBlockstate.get(state).getLightOpacity();
  }

  @Deprecated
  @Override
  public int getLightValue(@Nonnull IBlockState state) {
    return IFusedBlockstate.get(state).isEnlightened() ? 15 : super.getLightValue(state);
  }

  @Override
  public int damageDropped(@Nonnull IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  public boolean isSideSolid(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    if (side == EnumFacing.UP) { // stop drips
      return false;
    }
    return true;
  }

  @Override
  public boolean canPlaceTorchOnTop(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return true;
  }

  @Override
  public boolean doesSideBlockChestOpening(@Nonnull IBlockState blockStateIn, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return side != EnumFacing.DOWN;
  }

  @Override
  public boolean canBeWrenched() {
    return false;
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public void addCollisionBoxToList(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox,
      @Nonnull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
    if (entityIn == null || !getFusedBlockstate(state).canPass(entityIn)) {
      super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
    }
  }

  public static class BlockItemFusedQuartzBase extends BlockItemPaintedBlock implements ICustomItemResourceLocation, IOverlayRenderAware {

    public BlockItemFusedQuartzBase(@Nonnull BlockFusedQuartzBase<?> block) {
      super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      determineQuartzType(stack).addInformation(stack, worldIn, tooltip, flagIn);
    }

    @SideOnly(Side.CLIENT)
    protected @Nonnull FusedQuartzType determineQuartzType(ItemStack par1ItemStack) {
      return IFusedBlockstate.get(block.getStateFromMeta(par1ItemStack.getItemDamage())).getType();
    }

    @SuppressWarnings("null")
    @Override
    @Nonnull
    public ResourceLocation getRegistryNameForCustomModelResourceLocation() {
      return ModObject.blockFusedQuartz.getItemNN().getRegistryName();
    }

    @Override
    public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
      FusedQuartzOverlayRenderHelper.doItemOverlayIntoGUI(determineQuartzType(stack), xPosition, yPosition);
    }

  }
}
