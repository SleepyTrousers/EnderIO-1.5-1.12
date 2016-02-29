package crazypants.enderio.machine.transceiver;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import com.enderio.core.common.util.Util;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.transceiver.gui.ContainerTransceiver;
import crazypants.enderio.machine.transceiver.gui.GuiTransceiver;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTransceiver extends AbstractMachineBlock<TileTransceiver> {

  public static BlockTransceiver create() {

    PacketHandler.INSTANCE.registerMessage(PacketSendRecieveChannel.class, PacketSendRecieveChannel.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketAddRemoveChannel.class, PacketAddRemoveChannel.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketAddRemoveChannel.class, PacketAddRemoveChannel.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketChannelList.class, PacketChannelList.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketSendRecieveChannelList.class, PacketSendRecieveChannelList.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketItemFilter.class, PacketItemFilter.class, PacketHandler.nextID(), Side.SERVER);

    ConnectionHandler ch = new ConnectionHandler();   
    MinecraftForge.EVENT_BUS.register(ch);

    BlockTransceiver res = new BlockTransceiver();
    res.init();
    return res;
  }

  //TODO: 1.8
  @SideOnly(Side.CLIENT)
  private TextureAtlasSprite portalIcon;

//  private ExtendedBlockState state = new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{OBJModel.OBJProperty.instance});
  
  private BlockTransceiver() {
    super(ModObject.blockTransceiver, TileTransceiver.class);
    if(!Config.transceiverEnabled) {
      setCreativeTab(null);
    }    
  }
  
  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getPortalIcon() {    
    return portalIcon;
  }
  
//  @Override
//  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
////      TileTransceiver tileEntity = (TileTransceiver) world.getTileEntity(pos);
//      OBJModel.OBJState retState = new OBJModel.OBJState(Lists.newArrayList(OBJModel.Group.ALL),true);
//      return ((IExtendedBlockState) this.state.getBaseState()).withProperty(OBJModel.OBJProperty.instance, retState);
//  }


  @Override  
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    if(!world.isRemote) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileTransceiver) {
        ((TileTransceiver)te).getRailController().dropNonSpawnedCarts();
      }
    }
    return super.removedByPlayer(world, pos, player, willHarvest);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileTransceiver) {
      return new ContainerTransceiver(player.inventory, (TileTransceiver) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    return new GuiTransceiver(player.inventory, (TileTransceiver) te);
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_TRANSCEIVER;
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  protected void registerOverlayIcons(IIconRegister iIconRegister) {
//    super.registerOverlayIcons(iIconRegister);
//    overlayIconPull = iIconRegister.registerIcon("enderio:overlays/transcieverPull");
//    overlayIconPush = iIconRegister.registerIcon("enderio:overlays/transcieverPush");
//    overlayIconPushPull = iIconRegister.registerIcon("enderio:overlays/transcieverPushPull");
//    overlayIconDisabled = iIconRegister.registerIcon("enderio:overlays/transcieverDisabled");
//  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  
  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
  }


  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileTransceiver && player.isSneaking()) {
      TileTransceiver trans = (TileTransceiver) te;
      for (ChannelType type : ChannelType.VALUES) {

        Set<Channel> recieving = trans.getRecieveChannels(type);
        Set<Channel> sending = trans.getSendChannels(type);
        String recieve = "[" + buildString(recieving) + "]";
        String send = "[" + buildString(sending) + "]";

        if(isEmpty(recieve) && isEmpty(send)) {
          continue;
        }

        tooltip.add(EnumChatFormatting.WHITE + EnderIO.lang.localize("trans." + type.name().toLowerCase(Locale.US)));

        if(!isEmpty(recieve)) {
          tooltip.add(String.format("%s%s " + Util.TAB + ": %s%s", Util.TAB, EnderIO.lang.localize("trans.receiving"), Util.TAB + Util.ALIGNRIGHT
              + EnumChatFormatting.WHITE, recieve));
        }
        if(!isEmpty(send)) {
          tooltip.add(String.format("%s%s " + Util.TAB + ": %s%s", Util.TAB, EnderIO.lang.localize("trans.sending"), Util.TAB + Util.ALIGNRIGHT
              + EnumChatFormatting.WHITE, send));
        }
      }
    }
  }

  private boolean isEmpty(String str) {
    return "[]".equals(str);
  }

  private String buildString(Set<Channel> recieving) {
    StringBuilder sb = new StringBuilder();
    Iterator<Channel> iter = recieving.iterator();
    while (iter.hasNext()) {
      Channel c = iter.next();
      sb.append(c.getName());
      if (iter.hasNext()) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }


}
