package crazypants.enderio.render.dummy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
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
    // setCreativeTab(CreativeTabs.tabRedstone);
    disableStats();
  }

  protected BlockState createBlockState() {
    return new BlockState(this, new IProperty[] { EnumRenderPart.SUB });
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
