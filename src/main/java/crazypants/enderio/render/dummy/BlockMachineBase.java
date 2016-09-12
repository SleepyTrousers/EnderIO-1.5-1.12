package crazypants.enderio.render.dummy;

import java.util.List;

import crazypants.enderio.render.property.EnumRenderPart;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMachineBase extends Block {

  public static BlockMachineBase block;

  public static String name() {
    return "machineBase";
  }

  public static void create() {
    GameRegistry.register(block = new BlockMachineBase());
  }

  public BlockMachineBase() {
    super(Material.CIRCUITS);
    this.setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderPart.SUB, EnumRenderPart.DEFAULTS));
    setUnlocalizedName(name());
    setRegistryName(name());
    setCreativeTab(null);
    disableStats();
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderPart.SUB });
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
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

}
