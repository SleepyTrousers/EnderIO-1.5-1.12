package crazypants.enderio.render.dummy;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.render.EnumRenderPart;

public class BlockMachineBase extends Block {

  public static BlockMachineBase block;

  public static String name() {
    return "machineBase";
  }

  public static void create() {
    GameRegistry.registerBlock(block = new BlockMachineBase(), name());
  }

  public BlockMachineBase() {
    super(Material.circuits);
    this.setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderPart.SUB, EnumRenderPart.DEFAULTS));
    setUnlocalizedName(name());
    setCreativeTab(null);
    disableStats();
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, new IProperty[] { EnumRenderPart.SUB });
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
