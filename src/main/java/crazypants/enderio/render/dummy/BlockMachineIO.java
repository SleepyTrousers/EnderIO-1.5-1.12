package crazypants.enderio.render.dummy;

import javax.annotation.Nonnull;

import crazypants.enderio.init.IModObject;
import crazypants.enderio.render.property.IOMode;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMachineIO extends Block {

  public static BlockMachineIO create(IModObject modObject) {
    BlockMachineIO block = new BlockMachineIO(modObject);
    GameRegistry.register(block);
    return block;
  }

  @SuppressWarnings("null")
  public BlockMachineIO(IModObject modObject) {
    super(Material.CIRCUITS);
    modObject.apply(this);
    this.setDefaultState(this.blockState.getBaseState().withProperty(IOMode.IO, IOMode.get(EnumFacing.DOWN, IOMode.EnumIOMode.NONE)));
    setCreativeTab(null);
    disableStats();
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { IOMode.IO });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return 0;
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return getDefaultState();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(@Nonnull Item itemIn, @Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
  }

}
