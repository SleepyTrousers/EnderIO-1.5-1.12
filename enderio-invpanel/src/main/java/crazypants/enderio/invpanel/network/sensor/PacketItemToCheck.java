package crazypants.enderio.invpanel.network.sensor;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.invpanel.sensor.TileInventoryPanelSensor;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketItemToCheck extends MessageTileEntity<TileInventoryPanelSensor> {

  private @Nonnull ItemStack item;

  public PacketItemToCheck() {
  }

  public PacketItemToCheck(@Nonnull TileInventoryPanelSensor tile) {
    super(tile);
    this.item = tile.getItemToCheck();
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    item = ByteBufUtils.readItemStack(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    ByteBufUtils.writeItemStack(buf, item);
  }
  
  public static class Handler implements IMessageHandler<PacketItemToCheck, IMessage> {
  
    @Override
    public IMessage onMessage(@Nonnull PacketItemToCheck message, @Nonnull MessageContext ctx) {
      TileInventoryPanelSensor te = message.getTileEntity(ctx.getServerHandler().player.world);
      if(te != null) {
        te.setItemToCheck(message.item);
      }
      return null;
    }
  }
}
