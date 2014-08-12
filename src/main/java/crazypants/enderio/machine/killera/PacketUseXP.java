package crazypants.enderio.machine.killera;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.MessageTileEntity;

public class PacketUseXP extends MessageTileEntity<TileKillerJoe> implements IMessageHandler<PacketUseXP, IMessage> {

  public PacketUseXP() {
  }

  public PacketUseXP(TileKillerJoe tile) {
    super(tile);
  }

  @Override
  public IMessage onMessage(PacketUseXP message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileKillerJoe tile = message.getTileEntity(player.worldObj);
    if (tile != null) {
      tile.givePlayerXp(player);
    }
    return null;
  }

}
