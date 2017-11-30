package crazypants.enderio.machines.machine.transceiver;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.property.IOMode;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.transceiver.Channel;
import crazypants.enderio.base.transceiver.ConnectionHandler;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.transceiver.gui.ContainerTransceiver;
import crazypants.enderio.machines.machine.transceiver.gui.GuiTransceiver;
import crazypants.enderio.machines.machine.transceiver.render.TransceiverRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTransceiver extends AbstractMachineBlock<TileTransceiver>
    implements IPaintable.INonSolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveTESR {

  public static BlockTransceiver create() {
    PacketHandler.INSTANCE.registerMessage(PacketSendRecieveChannel.class, PacketSendRecieveChannel.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketSendRecieveChannelList.class, PacketSendRecieveChannelList.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketItemFilter.class, PacketItemFilter.class, PacketHandler.nextID(), Side.SERVER);

    ConnectionHandler ch = new ConnectionHandler();
    MinecraftForge.EVENT_BUS.register(ch);

    BlockTransceiver res = new BlockTransceiver();
    res.init();
    return res;
  }

  private TextureSupplier portalIcon = TextureRegistry.registerTexture("blocks/ender_still");

  private BlockTransceiver() {
    super(MachineObject.block_transceiver, TileTransceiver.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getPortalIcon() {
    return portalIcon.get(TextureAtlasSprite.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileTransceiver te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerTransceiver(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileTransceiver te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiTransceiver(player.inventory, te);
    }
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return GuiID.GUI_ID_TRANSCEIVER;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
  }
  //
  // @Override
  // public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
  // TileTransceiver te = getTileEntity(world, new BlockPos(x, y, z));
  // if (te != null && player.isSneaking()) {
  // for (ChannelType type : ChannelType.VALUES) {
  // Set<Channel> recieving = te.getRecieveChannels(type);
  // Set<Channel> sending = te.getSendChannels(type);
  // String recieve = "[" + buildString(recieving) + "]";
  // String send = "[" + buildString(sending) + "]";
  //
  // if (isEmpty(recieve) && isEmpty(send)) {
  // continue;
  // }
  //
  // tooltip.add(TextFormatting.WHITE + EnderIO.lang.localize("trans." + type.name().toLowerCase(Locale.US)));
  //
  // if (!isEmpty(recieve)) {
  // tooltip.add(String.format("%s%s " + Util.TAB + ": %s%s", Util.TAB, EnderIO.lang.localize("trans.receiving"), Util.TAB + Util.ALIGNRIGHT
  // + TextFormatting.WHITE, recieve));
  // }
  // if (!isEmpty(send)) {
  // tooltip.add(String.format("%s%s " + Util.TAB + ": %s%s", Util.TAB, EnderIO.lang.localize("trans.sending"), Util.TAB + Util.ALIGNRIGHT
  // + TextFormatting.WHITE, send));
  // }
  // }
  // }
  // }

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

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IOMode.EnumIOMode mapIOMode(IoMode mode, EnumFacing side) {
    switch (mode) {
    case NONE:
      return IOMode.EnumIOMode.NONE;
    case PULL:
      return IOMode.EnumIOMode.TRANSCIEVERPULL;
    case PUSH:
      return IOMode.EnumIOMode.TRANSCIEVERPUSH;
    case PUSH_PULL:
      return IOMode.EnumIOMode.TRANSCIEVERPUSHPULL;
    case DISABLED:
      return IOMode.EnumIOMode.TRANSCIEVERDISABLED;
    }
    throw new RuntimeException("Hey, leave our enums alone!");
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileTransceiver tileEntity) {
    blockStateWrapper.addCacheKey(0);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileTransceiver.class, new TransceiverRenderer());
  }

}
