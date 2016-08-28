package crazypants.enderio.machine.enchanter;

import java.util.Random;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.Util;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.render.ITESRItemBlock;
import crazypants.enderio.render.SmartModelAttacher;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class BlockEnchanter extends BlockEio<TileEnchanter> implements IGuiHandler, IResourceTooltipProvider, ITESRItemBlock {

  public static BlockEnchanter create() {
    BlockEnchanter res = new BlockEnchanter();
    res.init();
    return res;
  }

  protected BlockEnchanter() {
    super(ModObject.blockEnchanter.getUnlocalisedName(), TileEnchanter.class);
    setLightOpacity(0);
  }

  @Override
  protected void init() {
    super.init();
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_ENCHANTER, this);
    SmartModelAttacher.registerItemOnly(this);
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, player, stack);

    TileEnchanter te = getTileEntity(world, pos);
    if (te != null) {
      te.setFacing(Util.getFacingFromEntity(player));
    }
    if (world.isRemote) {
      return;
    }
    world.notifyBlockUpdate(pos, state, state, 3);
  }

  @Override
  public EnumBlockRenderType getRenderType(IBlockState bs) { 
    return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
  }

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    if (!world.isRemote) {
      entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_ENCHANTER, world, pos.getX(), pos.getY(), pos.getZ());
    }
    return true;
  }
  
  @Override
  public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
    
    TileEnchanter te = getTileEntity(worldIn, pos);
    if(te != null) {
      dropItems(worldIn, pos, te);
    }    
    super.breakBlock(worldIn, pos, state);
  }

  public boolean doNormalDrops(World world, int x, int y, int z) {
    return false;
  }

  private void dropItems(World world, BlockPos pos, TileEnchanter te) {
    if (te.getStackInSlot(0) != null) {
      Util.dropItems(world, te.getStackInSlot(0), pos, true);
    }
    if (te.getStackInSlot(1) != null) {
      Util.dropItems(world, te.getStackInSlot(1), pos, true);
    }
  }

  @Override
  public int quantityDropped(Random p_149745_1_) {
    return 1;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }

  @Override
  public boolean isFullCube(IBlockState bs) {
    return false;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileEnchanter) {
      return new ContainerEnchanter(player, player.inventory, (TileEnchanter) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileEnchanter) {
      return new GuiEnchanter(player, player.inventory, (TileEnchanter) te);
    }
    return null;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

}
