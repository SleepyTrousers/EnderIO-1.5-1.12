package crazypants.enderio.machines.machine.killera;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSwing extends MessageTileEntity<TileKillerJoe> {

  public PacketSwing() {
  }

  public PacketSwing(@Nonnull TileKillerJoe tile) {
    super(tile);
  }

  public static class Handler implements IMessageHandler<PacketSwing, IMessage> {

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

}
