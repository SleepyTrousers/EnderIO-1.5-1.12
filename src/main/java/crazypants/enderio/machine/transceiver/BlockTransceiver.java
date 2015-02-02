package crazypants.enderio.machine.transceiver;

import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.transceiver.gui.ContainerTransceiver;
import crazypants.enderio.machine.transceiver.gui.GuiTransceiver;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.Lang;
import crazypants.util.Util;

public class BlockTransceiver extends AbstractMachineBlock<TileTransceiver> {

  public static BlockTransceiver create() {
    
    PacketHandler.INSTANCE.registerMessage(PacketSendRecieveChannel.class, PacketSendRecieveChannel.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketAddRemoveChannel.class, PacketAddRemoveChannel.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketAddRemoveChannel.class, PacketAddRemoveChannel.class, PacketHandler.nextID(), Side.CLIENT);    
    PacketHandler.INSTANCE.registerMessage(PacketChannelList.class, PacketChannelList.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketSendRecieveChannelList.class, PacketSendRecieveChannelList.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketItemFilter.class, PacketItemFilter.class, PacketHandler.nextID(), Side.SERVER);

    ConnectionHandler ch = new ConnectionHandler();
    FMLCommonHandler.instance().bus().register(ch);
    MinecraftForge.EVENT_BUS.register(ch);
    
    BlockTransceiver res = new BlockTransceiver();
    res.init();
    return res;
  }

  private BlockTransceiver() {
    super(ModObject.blockTransceiver, TileTransceiver.class);
    if(!Config.transceiverEnabled) {
      setCreativeTab(null);
    }
  }

  @Override
  public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean doHarvest) {   
    if(!world.isRemote) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileTransceiver) {
        ((TileTransceiver)te).getRailController().dropNonSpawnedCarts();
      }
    }        
    return super.removedByPlayer(world, player, x, y, z, doHarvest);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileTransceiver) {
      return new ContainerTransceiver(player.inventory, (TileTransceiver) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    return new GuiTransceiver(player.inventory, (TileTransceiver) te);
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_TRANSCEIVER;
  }

  
  @Override
  protected void registerOverlayIcons(IIconRegister iIconRegister) {
    overlayIconPull = iIconRegister.registerIcon("enderio:transcieverOverlayPull");
    overlayIconPush = iIconRegister.registerIcon("enderio:transcieverOverlayPush");
    overlayIconPushPull = iIconRegister.registerIcon("enderio:transcieverOverlayPushPull");
    overlayIconDisabled = iIconRegister.registerIcon("enderio:transcieverOverlayDisabled");
    overlayIconNone = iIconRegister.registerIcon("enderio:machineOverlayNone");
    selectedFaceIcon= iIconRegister.registerIcon("enderio:machineOverlaySelectedFace");
  }
  
  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:alloySmelterFrontOn";
    }
    return "enderio:alloySmelterFront";
  }
  
  @Override
  public int getRenderType() {
    return -1;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {    
  }
  
  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileTransceiver && player.isSneaking()) {
      TileTransceiver trans = (TileTransceiver) te;
      for (ChannelType type : ChannelType.VALUES) {

        List<Channel> recieving = trans.getRecieveChannels(type);
        List<Channel> sending = trans.getSendChannels(type);
        String recieve = "[" + buildString(recieving) + "]";
        String send = "[" + buildString(sending) + "]";

        if(isEmpty(recieve) && isEmpty(send)) {
          continue;
        }

        tooltip.add(EnumChatFormatting.WHITE + Lang.localize("trans." + type.name().toLowerCase()));

        if(!isEmpty(recieve)) {
          tooltip.add(String.format("%s%s " + Util.TAB + ": %s%s", Util.TAB, Lang.localize("trans.receiving"), Util.TAB + Util.ALIGNRIGHT
              + EnumChatFormatting.WHITE, recieve));
        }
        if(!isEmpty(send)) {
          tooltip.add(String.format("%s%s " + Util.TAB + ": %s%s", Util.TAB, Lang.localize("trans.sending"), Util.TAB + Util.ALIGNRIGHT
              + EnumChatFormatting.WHITE, send));
        }
      }
    }
  }
  
  private boolean isEmpty(String str) {
    return "[]".equals(str);
  }
  
  private String buildString(List<Channel> channels) {
    StringBuilder sb = new StringBuilder();
    for (Channel c : channels) {
      sb.append(c.getName());
      if (channels.indexOf(c) != channels.size() - 1) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }
  
}
