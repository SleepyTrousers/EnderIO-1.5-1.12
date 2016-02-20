package crazypants.enderio.machine.wireless;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class BlockWirelessCharger extends BlockEio<TileWirelessCharger> implements IResourceTooltipProvider /* IGuiHandler */{

  public static BlockWirelessCharger create() {

    PacketHandler.INSTANCE.registerMessage(PacketStoredEnergy.class, PacketStoredEnergy.class, PacketHandler.nextID(), Side.CLIENT);

    BlockWirelessCharger res = new BlockWirelessCharger();
    res.init();
    return res;
  }

  public static int renderId = 0;

  protected BlockWirelessCharger() {
    super(ModObject.blockWirelessCharger.unlocalisedName, TileWirelessCharger.class);
    setLightOpacity(1);
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister iIconRegister) {
//    centerOn = iIconRegister.registerIcon("enderio:blockWirelessChargerOn");
//    centerOff = iIconRegister.registerIcon("enderio:blockWirelessChargerOff");
//  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int p_149673_5_) {
//    TileEntity te = world.getTileEntity(x, y, z);
//    if(te instanceof TileWirelessCharger) {
//      TileWirelessCharger twc = (TileWirelessCharger) te;
//      if(twc.isActive()) {
//        return centerOn;
//      }
//    }
//    return centerOff;
//  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
//    return centerOff;
//  }
//
//  public IIcon getCenterOn() {
//    return centerOn;
//  }
//
//  public IIcon getCenterOff() {
//    return centerOff;
//  }

  @Override
  public int getRenderType() {
    return renderId;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  //  @Override
  //  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
  //    TileEntity te = world.getTileEntity(x, y, z);
  //    if(te instanceof TileWire) {
  //      return new ContainerVacuumChest(player, player.inventory, (TileVacuumChest) te);
  //    }
  //    return null;
  //  }
  //
  //  @Override
  //  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
  //    TileEntity te = world.getTileEntity(x, y, z);
  //    if(te instanceof TileVacuumChest) {
  //      return new GuiVacuumChest(player, player.inventory, (TileVacuumChest) te);
  //    }
  //    return null;
  //  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }
  
  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {  
    return false;
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
  
    super.onBlockPlacedBy(world, pos, state, player, stack);

    if(stack.getTagCompound()!= null) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileWirelessCharger) {
        ((TileWirelessCharger) te).readCustomNBT(stack.getTagCompound());
      }
    }
  }

  @Override
  protected void processDrop(IBlockAccess world, BlockPos pos, TileWirelessCharger te, ItemStack drop) {
    drop.setTagCompound(new NBTTagCompound());    
    te.writeCustomNBT(drop.getTagCompound());    
  }

}
