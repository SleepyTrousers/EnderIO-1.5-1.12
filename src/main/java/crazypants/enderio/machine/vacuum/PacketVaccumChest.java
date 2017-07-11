package crazypants.enderio.machine.vacuum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.network.MessageTileEntity;

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
  private ItemStack stack;

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

  public static PacketVaccumChest setFilterSlot(@Nonnull TileVacuumChest tile, int slot, @Nonnull ItemStack stack) {
    PacketVaccumChest msg = new PacketVaccumChest(tile, CMD_SET_SLOT);
    msg.value = slot;
    msg.stack = stack;
    tile.setItemFilterSlot(slot, stack);
    return msg;
  }

  public static PacketVaccumChest setFilterBlacklist(@Nonnull TileVacuumChest tile, boolean isBlacklist) {
    PacketVaccumChest msg = new PacketVaccumChest(tile, CMD_SET_BLACKLIST);
    msg.value = isBlacklist ? 1 : 0;
    tile.setFilterBlacklist(isBlacklist);
    return msg;
  }

  public static PacketVaccumChest setFilterMatchMeta(@Nonnull TileVacuumChest tile, boolean matchMeta) {
    PacketVaccumChest msg = new PacketVaccumChest(tile, CMD_SET_MATCHMETA);
    msg.value = matchMeta ? 1 : 0;
    tile.setFilterMatchMeta(matchMeta);
    return msg;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    cmd = buf.readByte() & 255;
    value = buf.readInt();
    stack = ByteBufUtils.readItemStack(buf);
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
        case CMD_SET_SLOT:
          final ItemStack stack = msg.stack;
          if (stack != null) {
            te.setItemFilterSlot(msg.value, stack);
          }
          break;
        case CMD_SET_BLACKLIST:
          te.setFilterBlacklist(msg.value != 0);
          break;
        case CMD_SET_MATCHMETA:
          te.setFilterMatchMeta(msg.value != 0);
          break;
        }
      }
      return null;
    }

  }

}
