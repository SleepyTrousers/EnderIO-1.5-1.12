package crazypants.enderio.machines.machine.killera;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.EnderIO;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSwing extends MessageTileEntity<TileKillerJoe> implements IMessageHandler<PacketSwing, IMessage> {

  public PacketSwing() {
  }

  public PacketSwing(TileKillerJoe tile) {
    super(tile);
  }

  @Override
  public IMessage onMessage(PacketSwing message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileKillerJoe tile = message.getTileEntity(player.world);
    if (tile != null) {
      tile.swingWeapon();
    }
    return null;
  }


}
