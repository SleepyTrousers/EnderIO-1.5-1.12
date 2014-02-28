package crazypants.enderio.teleport;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.enderface.BlockEio;
import crazypants.enderio.teleport.packet.PacketAccessMode;
import crazypants.enderio.teleport.packet.PacketConfigSync;
import crazypants.enderio.teleport.packet.PacketDrainStaff;
import crazypants.enderio.teleport.packet.PacketOpenAuthGui;
import crazypants.enderio.teleport.packet.PacketTravelEvent;
import crazypants.util.Lang;

public class BlockTravelAnchor extends BlockEio implements IGuiHandler, ITileEntityProvider {

  public static BlockTravelAnchor create() {

    EnderIO.packetPipeline.registerPacket(PacketAccessMode.class);
    EnderIO.packetPipeline.registerPacket(PacketTravelEvent.class);
    EnderIO.packetPipeline.registerPacket(PacketDrainStaff.class);
    EnderIO.packetPipeline.registerPacket(PacketOpenAuthGui.class);
    EnderIO.packetPipeline.registerPacket(PacketConfigSync.class);

    BlockTravelAnchor result = new BlockTravelAnchor();
    result.init();

    FMLCommonHandler.instance().bus().register(result);

    return result;
  }

  //to lazy to create a new class
  @SubscribeEvent
  public void onPlayerLoggon(PlayerLoggedInEvent evt) {
    EnderIO.packetPipeline.sendTo(new PacketConfigSync(), (EntityPlayerMP) evt.player);
  }

  IIcon selectedOverlayIcon;
  IIcon highlightOverlayIcon;

  private BlockTravelAnchor() {
    super(ModObject.blockTravelAnchor.unlocalisedName, TileTravelAnchor.class);
    if(!Config.travelAnchorEnabled) {
      setCreativeTab(null);
    }
  }

  @Override
  protected void init() {
    super.init();
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TRAVEL_ACCESSABLE, this);
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TRAVEL_AUTH, this);
  }

  @Override
  public void registerBlockIcons(IIconRegister iIconRegister) {
    super.registerBlockIcons(iIconRegister);
    highlightOverlayIcon = iIconRegister.registerIcon("enderio:blockTravelAnchorHighlight");
    selectedOverlayIcon = iIconRegister.registerIcon("enderio:blockTravelAnchorSelected");
  }

  @Override
  public TileEntity createNewTileEntity(World var1, int var2) {
    return new TileTravelAnchor();
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack par6ItemStack) {
    if(entity instanceof EntityPlayer) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileTravelAnchor) {
        ((TileTravelAnchor) te).setPlacedBy((EntityPlayer) entity);
        world.markBlockForUpdate(x, y, z);
      }
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
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof ITravelAccessable) {
      if(ID == GuiHandler.GUI_ID_TRAVEL_ACCESSABLE) {
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
      if(ID == GuiHandler.GUI_ID_TRAVEL_ACCESSABLE) {
        return new GuiTravelAccessable(player.inventory, (ITravelAccessable) te, world);
      } else {
        return new GuiTravelAuth(player, (ITravelAccessable) te, world);
      }
    }
    return null;
  }

}
