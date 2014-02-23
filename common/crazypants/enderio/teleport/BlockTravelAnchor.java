package crazypants.enderio.teleport;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.util.Lang;

public class BlockTravelAnchor extends Block implements ITileEntityProvider, IGuiHandler {

  public static BlockTravelAnchor create() {
    TravelPacketHandler pp = new TravelPacketHandler();
    PacketHandler.instance.addPacketProcessor(pp);
    NetworkRegistry.instance().registerConnectionHandler(pp);

    BlockTravelAnchor result = new BlockTravelAnchor();
    result.init();
    return result;
  }

  Icon selectedOverlayIcon;
  Icon highlightOverlayIcon;

  private BlockTravelAnchor() {
    super(ModObject.blockTravelPlatform.id, Material.rock);
    setHardness(0.5F);
    setStepSound(Block.soundStoneFootstep);
    setUnlocalizedName("enderio." + ModObject.blockTravelPlatform);
    if(Config.travelAnchorEnabled) {
      setCreativeTab(EnderIOTab.tabEnderIO);
    } else {
      setCreativeTab(null);
    }
  }

  private void init() {
    GameRegistry.registerBlock(this, ModObject.blockTravelPlatform.unlocalisedName);
    GameRegistry.registerTileEntity(TileTravelAnchor.class, ModObject.blockTravelPlatform.unlocalisedName + "TileEntity");
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TRAVEL_ACCESSABLE, this);
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TRAVEL_AUTH, this);
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon("enderio:blockTravelAnchor");
    highlightOverlayIcon = iconRegister.registerIcon("enderio:blockTravelAnchorHighlight");
    selectedOverlayIcon = iconRegister.registerIcon("enderio:blockTravelAnchorSelected");
  }

  @Override
  public TileEntity createNewTileEntity(World world) {
    return new TileTravelAnchor();
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack par6ItemStack) {
    if(entity instanceof EntityPlayer) {
      String name = ((EntityPlayer) entity).username;
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te instanceof TileTravelAnchor) {
        ((TileTravelAnchor) te).setPlacedBy(name);
        world.markBlockForUpdate(x, y, z);
      }
    }
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
    if(entityPlayer.isSneaking()) {
      return false;
    }
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof ITravelAccessable) {
      ITravelAccessable ta = (ITravelAccessable) te;
      if(ta.canUiBeAccessed(entityPlayer.username)) {
        entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_TRAVEL_ACCESSABLE, world, x, y, z);
      } else {
        if(world.isRemote) {
          entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText(Lang.localize("gui.travelAccessable.privateBlock1") + " " + ta.getPlacedBy() + " "
              + Lang.localize("gui.travelAccessable.privateBlock2")));
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
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
    TileEntity te = world.getBlockTileEntity(x, y, z);
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
