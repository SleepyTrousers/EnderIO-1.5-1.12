package crazypants.enderio.render.dummy;

import crazypants.enderio.render.IOMode;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMachineIO extends Block {

  public static BlockMachineIO block;

  public static String name() {
    return "machineIO";
  }

  public static void create() {
    GameRegistry.registerBlock(block = new BlockMachineIO(), name());
  }

  public BlockMachineIO() {
    super(Material.circuits);
    setUnlocalizedName(name());
    this.setDefaultState(this.blockState.getBaseState().withProperty(IOMode.IO, IOMode.get(EnumFacing.DOWN, IOMode.EnumIOMode.NONE)));
    // setCreativeTab(CreativeTabs.tabRedstone);
    disableStats();
  }

  protected BlockState createBlockState() {
    return new BlockState(this, new IProperty[] { IOMode.IO });
  }

  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  public int getMetaFromState(IBlockState state) {
    return 0;
  }

  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return getDefaultState();
  }

  @SideOnly(Side.CLIENT)
  public EnumWorldBlockLayer getBlockLayer() {
    return EnumWorldBlockLayer.CUTOUT;
  }

  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
    if (playerIn.capabilities.isCreativeMode) {
      playerIn.addChatMessage(new ChatComponentText(state + (worldIn.isRemote ? " on client" : " on server") + " default: " + getDefaultState()));
      return true;
    } else {
      return false;
    }
  }

}
