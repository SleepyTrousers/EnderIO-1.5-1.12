package crazypants.enderio.teleport.telepad;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.teleport.ContainerTravelAccessable;
import crazypants.enderio.teleport.ContainerTravelAuth;
import crazypants.enderio.teleport.GuiTravelAccessable;
import crazypants.enderio.teleport.GuiTravelAuth;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import crazypants.util.Lang;

public class BlockTelePad extends BlockTravelAnchor {

  @SideOnly(Side.CLIENT)
  private IIcon unconnected;
  @SideOnly(Side.CLIENT)
  private IIcon connected;

  public static BlockTelePad create() {
    BlockTelePad ret = new BlockTelePad();
    ret.init();
    return ret;
  }

  protected BlockTelePad() {
    super(ModObject.blockTelePad.unlocalisedName, TileTelePad.class);
  }

  @Override
  protected void init() {
    super.init();
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TELEPAD, this);
  }
  
  @Override
  public void registerBlockIcons(IIconRegister iIconRegister) {
    unconnected = iIconRegister.registerIcon("EnderIO:blockTelePad");
    connected = iIconRegister.registerIcon("EnderIO:blockTelePadConnected");
  }

  @Override
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
    TileTelePad te = (TileTelePad) world.getTileEntity(x, y, z);
    if(te != null && te.inNetwork) {
      return connected;
    }
    return unconnected;
  }

  @Override
  public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
    return unconnected;
  }
  
  @Override
  public int getRenderType() {
    return 0;
  }

  @Override
  public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
    super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
    ((TileTelePad) world.getTileEntity(x, y, z)).updateConnectedState(true);
  }

  @Override
  public boolean openGui(World world, int x, int y, int z, EntityPlayer entityPlayer, int side) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof ITravelAccessable) {
      ITravelAccessable ta = (ITravelAccessable) te;
      if(ta.canUiBeAccessed(entityPlayer)) {
        entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_TELEPAD, world, x, y, z);
      } else {
        if(world.isRemote && !entityPlayer.isSneaking()) {
          entityPlayer.addChatComponentMessage(new ChatComponentText(Lang.localize("gui.travelAccessable.privateBlock1") + " " + ta.getPlacedBy() + " "
              + Lang.localize("gui.travelAccessable.privateBlock2")));
        }
      }
    }
    return true;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof ITravelAccessable) {
      if(ID == GuiHandler.GUI_ID_TELEPAD) {
        return new ContainerTravelAccessable(player.inventory, (ITravelAccessable) te, world);
      } else {
        return new ContainerTravelAuth(player.inventory);
      }
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof ITravelAccessable) {
      if(ID == GuiHandler.GUI_ID_TELEPAD) {
        return new GuiTelePad(player.inventory, (ITravelAccessable) te, world);
      } else {
        return new GuiTravelAuth(player, (ITravelAccessable) te, world);
      }
    }
    return null;
  }

}
