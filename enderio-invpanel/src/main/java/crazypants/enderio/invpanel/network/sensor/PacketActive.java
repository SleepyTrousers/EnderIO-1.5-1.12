package crazypants.enderio.invpanel.network.sensor;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.invpanel.sensor.TileInventoryPanelSensor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketActive extends MessageTileEntity<TileInventoryPanelSensor> {

  private boolean active;

  public PacketActive() {
  }

  public PacketActive(@Nonnull TileInventoryPanelSensor tile) {
    super(tile);
    this.active = tile.isActive();
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    active = buf.readBoolean();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeBoolean(active);
  }
  
  public static class Handler implements IMessageHandler<PacketActive, IMessage> {
  
    @Override
    public IMessage onMessage(@Nonnull PacketActive message, @Nonnull MessageContext ctx) {
      EntityPlayer player = EnderIO.proxy.getClientPlayer();
      TileInventoryPanelSensor te = message.getTileEntity(player.world);
      if(te != null) {
        if(message.active != te.isActive()) {
          te.setActive(message.active);
          player.world.markBlockRangeForRenderUpdate(te.getPos(), te.getPos());
        }
      }
      return null;
    }
  }
}
