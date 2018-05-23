package crazypants.enderio.machines.machine.vacuum.chest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.util.NullHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketVaccumChest extends MessageTileEntity<TileVacuumChest> {

  public static final int CMD_SET_RANGE = 0;
  public static final int CMD_SET_SLOT = 1;
  public static final int CMD_SET_BLACKLIST = 2;
  public static final int CMD_SET_MATCHMETA = 3;

  private int cmd;
  private int value;
  private @Nonnull ItemStack stack = ItemStack.EMPTY;

  public PacketVaccumChest() {
  }

  private PacketVaccumChest(@Nonnull TileVacuumChest tile, int cmd) {
    super(tile);
    this.cmd = cmd;
  }

  public static PacketVaccumChest setRange(@Nonnull TileVacuumChest tile, int range) {
    PacketVaccumChest msg = new PacketVaccumChest(tile, CMD_SET_RANGE);
    msg.value = range;
    tile.setRange(range);
    return msg;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    cmd = buf.readByte() & 255;
    value = buf.readInt();
    stack = NullHelper.notnullF(ByteBufUtils.readItemStack(buf), "readItemStack returned null");
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeByte(cmd);
    buf.writeInt(value);
    ByteBufUtils.writeItemStack(buf, stack);
  }

  public static class Handler implements IMessageHandler<PacketVaccumChest, IMessage> {

    @Override
    public @Nullable IMessage onMessage(PacketVaccumChest msg, MessageContext ctx) {
      TileVacuumChest te = msg.getTileEntity(ctx.getServerHandler().player.world);
      if (te != null) {
        switch (msg.cmd) {
        case CMD_SET_RANGE:
          te.setRange(msg.value);
          break;
        }
      }
      return null;

    }
  }
}