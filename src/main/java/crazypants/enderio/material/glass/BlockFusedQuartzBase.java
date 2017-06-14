package crazypants.enderio.material.glass;

import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.block.painted.BlockItemPaintedBlock;
import crazypants.enderio.config.Config;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.render.ICustomItemResourceLocation;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
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
  public BlockFusedQuartzBase(@Nonnull IModObject modObject, Class<T> teClass) {
    super(modObject, teClass, Material.GLASS);
    setSoundType(SoundType.GLASS);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }
  
  @Override
  protected ItemBlock createItemBlock() {
    return modobject.apply(new BlockItemFusedQuartzBase(this));
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
  public float getExplosionResistance(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Entity par1Entity, @Nonnull Explosion explosion) {
    if (world.getBlockState(pos).getValue(FusedQuartzType.KIND).isBlastResistant()) {
      return Config.EXPLOSION_RESISTANT;
    } else {
      return super.getExplosionResistance(par1Entity);
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
  public boolean isBlockSolid(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return true;
  }

  @Override
  public boolean isSideSolid(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    if(side == EnumFacing.UP) { //stop drips
      return false;
    }
    return true;
  }

  @Override
  public boolean canPlaceTorchOnTop(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return true;
  }

  @Override
  public boolean shouldWrench(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumFacing side) {
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
    public void addInformation(@Nonnull ItemStack par1ItemStack, @Nonnull EntityPlayer par2EntityPlayer, @Nonnull List<String> par3List, boolean par4) {
      super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
      FusedQuartzType type = determineQuartzType(par1ItemStack);
      if (type.isBlastResistant()) {
        par3List.add(EnderIO.lang.localize("blastResistant"));
      }
      if (type.isEnlightened()) {
        par3List.add(EnderIO.lang.localize("lightEmitter"));
      }
      if (type.getLightOpacity() > 0) {
        par3List.add(EnderIO.lang.localize("lightBlocker"));
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