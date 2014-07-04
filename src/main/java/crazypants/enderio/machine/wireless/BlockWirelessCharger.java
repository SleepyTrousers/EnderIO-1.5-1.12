package crazypants.enderio.machine.wireless;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.machine.power.PacketPowerStorage;
import crazypants.enderio.machine.vacuum.BlockVacuumChest;
import crazypants.enderio.machine.vacuum.ContainerVacuumChest;
import crazypants.enderio.machine.vacuum.GuiVacuumChest;
import crazypants.enderio.machine.vacuum.TileVacuumChest;
import crazypants.enderio.network.PacketHandler;

public class BlockWirelessCharger extends BlockEio implements IResourceTooltipProvider /*IGuiHandler*/ {

  public static BlockWirelessCharger create() {
    
    PacketHandler.INSTANCE.registerMessage(PacketStoredEnergy.class, PacketStoredEnergy.class, PacketHandler.nextID(), Side.CLIENT);
    
    BlockWirelessCharger res = new BlockWirelessCharger();
    res.init();
    return res;
  }

  public static int renderId = 0;
  
  private IIcon centerOn;
  private IIcon centerOff;

  protected BlockWirelessCharger() {
    super(ModObject.blockWirelessCharger.unlocalisedName, TileWirelessCharger.class);    
    setLightOpacity(1);
  }
  
//  @Override
//  protected void init() {  
//    super.init();
//    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_WIRELESS_CHARGER, this);
//  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float par7, float par8, float par9) {

    if(ConduitUtil.isToolEquipped(entityPlayer)) {
      if(entityPlayer.isSneaking() && entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
        IToolWrench wrench = (IToolWrench) entityPlayer.getCurrentEquippedItem().getItem();
        if(wrench.canWrench(entityPlayer, x, y, z)) {
          removedByPlayer(world, entityPlayer, x, y, z, false);
          if(entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
            ((IToolWrench) entityPlayer.getCurrentEquippedItem().getItem()).wrenchUsed(entityPlayer, x, y, z);
          }
          return true;
        }
      } 
    }
//    if(entityPlayer.isSneaking()) {
//      return false;
//    }
//    entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_WIRELESS_CHARGER, world, x, y, z);
//    return true;
    return false;
  }
  
  

  @Override
  public void registerBlockIcons(IIconRegister iIconRegister) {    
    centerOn = iIconRegister.registerIcon("enderio:blockWirelessChargerOn");
    centerOff = iIconRegister.registerIcon("enderio:blockWirelessChargerOff");
  }  

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int p_149673_5_) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileWirelessCharger) {
      TileWirelessCharger twc = (TileWirelessCharger)te;
      if(twc.storedEnergy > 0) {
        return centerOn;
      }
    }
    return centerOff;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int p_149691_1_, int p_149691_2_) {    
    return centerOff;
  }

  public IIcon getCenterOn() {
    return centerOn;
  }

  public IIcon getCenterOff() {
    return centerOff;
  }

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

  public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_) {
    super.breakBlock(world, x, y, z, block, p_149749_6_);
    world.removeTileEntity(x, y, z);
  }

@Override
public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
  return getUnlocalizedName();
}

}
