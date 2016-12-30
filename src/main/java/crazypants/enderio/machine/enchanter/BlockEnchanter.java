package crazypants.enderio.machine.enchanter;

import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.Util;

import crazypants.enderio.BlockEio;
import crazypants.enderio.GuiID;
import crazypants.enderio.ModObject;
import crazypants.enderio.render.IHaveTESR;
import crazypants.enderio.render.ITESRItemBlock;
import crazypants.enderio.render.registry.SmartModelAttacher;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.blockEnchanter;

public class BlockEnchanter extends BlockEio<TileEnchanter> implements IGuiHandler, IResourceTooltipProvider, ITESRItemBlock, IHaveTESR {

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
    GuiID.registerGuiHandler(GuiID.GUI_ID_ENCHANTER, this);
    SmartModelAttacher.registerItemOnly(this);
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, player, stack);

    TileEnchanter te = getTileEntity(world, pos);
    if (te != null) {
      te.readFromItemStack(stack);
      te.setFacing(Util.getFacingFromEntity(player));
    }
    if (world.isRemote) {
      return;
    }
    world.notifyBlockUpdate(pos, state, state, 3);
  }

  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {
    return false;
  }

  @Override
  public EnumBlockRenderType getRenderType(IBlockState bs) { 
    return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
  }

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    GuiID.GUI_ID_ENCHANTER.openGui(world, pos, entityPlayer, side);
    return true;
  }
  
  @Override
  protected void processDrop(IBlockAccess world, BlockPos pos, @Nullable TileEnchanter te, ItemStack drop) {
    if(te != null) {
      te.writeToItemStack(drop);
    }
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

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileEnchanter.class, new EnchanterModelRenderer());
    ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(blockEnchanter.getBlock()), 0, TileEnchanter.class);
  }

}
