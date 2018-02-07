package crazypants.enderio.base.material.glass;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.block.painted.BlockItemPaintedBlock;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.render.ICustomItemResourceLocation;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
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
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new BlockItemFusedQuartzBase(this));
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(FusedQuartzType.KIND, FusedQuartzType.getTypeFromMeta(meta));
  }

  @SuppressWarnings("null")
  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return FusedQuartzType.getMetaFromType(state.getValue(FusedQuartzType.KIND));
  }

  @Override
  public float getExplosionResistance(@Nonnull World world, @Nonnull BlockPos pos, @Nullable Entity exploder, @Nonnull Explosion explosion) {
    if (world.getBlockState(pos).getValue(FusedQuartzType.KIND).isBlastResistant()) {
      return Config.EXPLOSION_RESISTANT;
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
    return state.getValue(FusedQuartzType.KIND).getLightOpacity();
  }

  @Deprecated
  @Override
  public int getLightValue(@Nonnull IBlockState state) {
    return state.getValue(FusedQuartzType.KIND).isEnlightened() ? 15 : super.getLightValue(state);
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
  public boolean canBeWrenched() {
    return false;
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState state) {
    return false;
  }

  public static class BlockItemFusedQuartzBase extends BlockItemPaintedBlock implements ICustomItemResourceLocation {

    public BlockItemFusedQuartzBase(@Nonnull Block block) {
      super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      FusedQuartzType type = determineQuartzType(stack);
      if (type.isBlastResistant()) {
        tooltip.add(Lang.BLOCK_BLAST_RESISTANT.get());
      }
      if (type.isEnlightened()) {
        tooltip.add(Lang.BLOCK_LIGHT_EMITTER.get());
      }
      if (type.getLightOpacity() > 0) {
        tooltip.add(Lang.BLOCK_LIGHT_BLOCKER.get());
      }
    }

    @SideOnly(Side.CLIENT)
    protected FusedQuartzType determineQuartzType(ItemStack par1ItemStack) {
      int meta = par1ItemStack.getItemDamage();
      FusedQuartzType type = FusedQuartzType.getTypeFromMeta(meta);
      return type;
    }

    @SuppressWarnings("null")
    @Override
    @Nonnull
    public ResourceLocation getRegistryNameForCustomModelResourceLocation() {
      return ModObject.blockFusedQuartz.getItem().getRegistryName();
    }

  }
}