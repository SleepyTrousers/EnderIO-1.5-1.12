package crazypants.enderio.render.dummy;

import java.util.List;

import crazypants.enderio.render.property.IOMode;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMachineIO extends Block {

  public static BlockMachineIO block;

  public static String name() {
    return "machineIO";
  }

  public static void create() {
    GameRegistry.register(block = new BlockMachineIO());
  }

  public BlockMachineIO() {
    super(Material.CIRCUITS);
    setUnlocalizedName(name());
    setRegistryName(name());
    this.setDefaultState(this.blockState.getBaseState().withProperty(IOMode.IO, IOMode.get(EnumFacing.DOWN, IOMode.EnumIOMode.NONE)));
    setCreativeTab(null);
    disableStats();
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { IOMode.IO });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return 0;
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return getDefaultState();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

}
