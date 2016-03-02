package crazypants.enderio.machine.vacuum;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.redstone.IRedstoneConnectable;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVacuumChest extends BlockEio<TileVacuumChest> implements IGuiHandler, IResourceTooltipProvider, IRedstoneConnectable {

  public static BlockVacuumChest create() {
    PacketHandler.INSTANCE.registerMessage(PacketVaccumChest.class,PacketVaccumChest.class,PacketHandler.nextID(), Side.SERVER);
    BlockVacuumChest res = new BlockVacuumChest();
    res.init();
    return res;
  }

  protected BlockVacuumChest() {
    super(ModObject.blockVacuumChest.unlocalisedName, TileVacuumChest.class);
  }

  @Override
  public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, EnumFacing from) {
    return true;
  }


  @Override
  public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
    TileEntity ent = world.getTileEntity(pos);
    if(ent instanceof TileVacuumChest) {
      ((TileVacuumChest) ent).onNeighborBlockChange(neighborBlock);
    }
  }

  @Override
  protected void init() {
    super.init();
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_VACUUM_CHEST, this);
  }

  

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    if(!world.isRemote) {
      entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_VACUUM_CHEST, world, pos.getX(), pos.getY(), pos.getZ());
    }
    return true;
  }

  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {  
    return false;
  }

  @Override
  protected void processDrop(IBlockAccess world, BlockPos pos, TileVacuumChest te, ItemStack drop) {
    drop.setTagCompound(new NBTTagCompound());
    if(te != null) {
      te.writeContentsToNBT(drop.getTagCompound());
    }
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    if(!world.isRemote) {
      TileEntity te = world.getTileEntity(pos);
      if(stack != null && stack.getTagCompound() != null && te instanceof TileVacuumChest) {
        ((TileVacuumChest) te).readContentsFromNBT(stack.getTagCompound());
        world.markBlockForUpdate(pos);
      }
    }
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public EnumWorldBlockLayer getBlockLayer() {
    return EnumWorldBlockLayer.CUTOUT;
  }
  
  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileVacuumChest) {
      return new ContainerVacuumChest(player, player.inventory, (TileVacuumChest) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileVacuumChest) {
      return new GuiVacuumChest(player, player.inventory, (TileVacuumChest) te);
    }
    return null;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

}
