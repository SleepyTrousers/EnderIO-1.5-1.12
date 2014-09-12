package crazypants.enderio.machine.killera;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.MessageTileEntity;

public class PacketUseXP extends MessageTileEntity<TileKillerJoe> implements IMessageHandler<PacketUseXP, IMessage> {

  int levels;
  
  public PacketUseXP() {
  }

  public PacketUseXP(TileKillerJoe tile, int levels) {
    super(tile);
    this.levels = levels;
  }
  
  

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort((short)levels);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    levels = buf.readShort();
  }

  @Override
  public IMessage onMessage(PacketUseXP message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileKillerJoe tile = message.getTileEntity(player.worldObj);
    if (tile != null) {
      tile.givePlayerXp(player, message.levels);
    }
    return null;
  }

}
