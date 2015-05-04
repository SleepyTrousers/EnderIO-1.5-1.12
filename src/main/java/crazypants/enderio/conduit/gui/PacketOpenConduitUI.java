package crazypants.enderio.conduit.gui;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;

public class PacketOpenConduitUI extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketOpenConduitUI, IMessage> {

  private ForgeDirection dir;

  public PacketOpenConduitUI() {
  }

  public PacketOpenConduitUI(TileEntity tile, ForgeDirection dir) {
    super(tile);
    this.dir = dir;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort(dir.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    dir = ForgeDirection.values()[buf.readShort()];
  }

  public IMessage onMessage(PacketOpenConduitUI message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity tile = message.getWorld(ctx).getTileEntity(message.x, message.y, message.z);
    player
        .openGui(EnderIO.instance, GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + message.dir.ordinal(), player.worldObj, tile.xCoord, tile.yCoord, tile.zCoord);
    return null;
  }

}
