package crazypants.enderio.item;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.network.IPacketEio;

public class YetaWrenchPacketProcessor implements IPacketEio {

  private int slot;
  private ConduitDisplayMode mode;

  public YetaWrenchPacketProcessor() {
  }

  public YetaWrenchPacketProcessor(int slot, ConduitDisplayMode mode) {
    this.slot = slot;
    this.mode = mode;
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buffer) {
    buffer.writeInt(slot);
    buffer.writeShort(mode.ordinal());
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buffer) {
    slot = buffer.readInt();
    mode = ConduitDisplayMode.values()[buffer.readShort()];
  }

  @Override
  public void handleClientSide(EntityPlayer player) {

  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    ItemStack stack = null;
    if(slot > -1 && slot < 9) {
      stack = player.inventory.getStackInSlot(slot);
    }
    if(stack == null) {
      return;
    }
    ConduitDisplayMode.setDisplayMode(stack, mode);
  }

}
