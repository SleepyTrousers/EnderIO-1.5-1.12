package crazypants.enderio.machines.machine.crafter;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCrafter extends MessageTileEntity<TileCrafter> {

  private int slot;
  private @Nonnull ItemStack stack = ItemStack.EMPTY;

  public PacketCrafter() {
  }

  private PacketCrafter(@Nonnull TileCrafter tile) {
    super(tile);
  }

  public static PacketCrafter setSlot(@Nonnull TileCrafter te, int slot, @Nonnull ItemStack stack) {
    PacketCrafter msg = new PacketCrafter(te);
    msg.slot = slot;
    msg.stack = stack;
    msg.execute(te);
    return msg;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    slot = buf.readShort();
    stack = ByteBufUtils.readItemStack(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort(slot);
    ByteBufUtils.writeItemStack(buf, stack);
  }

  public static class Handler implements IMessageHandler<PacketCrafter, IMessage> {

    @Override
    public IMessage onMessage(PacketCrafter msg, MessageContext ctx) {
      TileCrafter te = msg.getTileEntity(ctx.getServerHandler().player.world);
      if (te != null) {
        msg.execute(te);
      }
      return null;
    }
  }

  private void execute(@Nonnull TileCrafter te) {
    te.craftingGrid.setInventorySlotContents(slot, stack);
    te.updateCraftingOutput();
  }

}
