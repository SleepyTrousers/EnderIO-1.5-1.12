package crazypants.enderio.enderface;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.enderface.te.MeProxy;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.teleport.ITravelAccessable;
import crazypants.util.Lang;

public class BlockEnderIO extends BlockEio implements IResourceTooltipProvider {

  public static BlockEnderIO create() {

    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_ME_ACCESS_TERMINAL, new IGuiHandler() {

      @Override
      public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        try {
          return MeProxy.createMeTerminalContainer(player, x, y, z, false);
        } catch (Exception e) {
          Log.warn("BlockEnderIO: Error occured creating the server gui element for an ME Terminal " + e);
        }
        return null;
      }

      @Override
      public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return MeProxy.instance.createTerminalGui(player, x, y, z);
      }

    });

    EnderIO.packetPipeline.registerPacket(PacketOpenRemoteUi.class);

    BlockEnderIO result = new BlockEnderIO();
    result.init();
    return result;
  }

  IIcon frameIcon;
  IIcon selectedOverlayIcon;
  IIcon highlightOverlayIcon;

  static int pass;

  private BlockEnderIO() {
    super(ModObject.blockEnderIo.unlocalisedName, TileEnderIO.class);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getRenderBlockPass() {
    return 1;
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack item) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEnderIO) {
      TileEnderIO eio = (TileEnderIO) te;
      eio.initUiPitch = -player.rotationPitch;
      eio.initUiYaw = -player.rotationYaw + 180;
      eio.lastUiPitch = eio.initUiPitch;
      eio.lastUiYaw = eio.initUiYaw;
      if(player instanceof EntityPlayer) {
        eio.setPlacedBy((EntityPlayer) player);
      }
      world.markBlockForUpdate(x, y, z);
    }
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
    if(entityPlayer.isSneaking()) {
      return false;
    }
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof ITravelAccessable) {
      ITravelAccessable ta = (ITravelAccessable) te;
      if(ta.canUiBeAccessed(entityPlayer)) {
        entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_TRAVEL_ACCESSABLE, world, x, y, z);
      } else {
        if(world.isRemote) {
          entityPlayer.addChatComponentMessage(new ChatComponentText(Lang.localize("gui.travelAccessable.privateBlock1") + " " + ta.getPlacedBy() + " "
              + Lang.localize("gui.travelAccessable.privateBlock2")));
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public int getRenderType() {
    return -1;
  }

  @Override
  public int getLightValue(IBlockAccess world, int x, int y, int z) {
    return 13;
  }

  @Override
  public int getLightOpacity() {
    return 100;
  }

  @Override
  public void registerBlockIcons(IIconRegister iIconRegister) {
    super.registerBlockIcons(iIconRegister);
    frameIcon = iIconRegister.registerIcon("enderio:enderIOFrame");
    highlightOverlayIcon = iIconRegister.registerIcon("enderio:enderIOHighlight");
    selectedOverlayIcon = iIconRegister.registerIcon("enderio:enderIOSelected");
  }

  @Override
  public String getUnlocalizedNameForTooltip() {
    return getUnlocalizedName();
  }

  public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_) {
      super.breakBlock(world, x, y, z, block, p_149749_6_);
      world.removeTileEntity(x, y, z);
  }


}
