package crazypants.enderio.machines.machine.teleport.telepad.packet;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.teleport.anchor.BlockTravelAnchor;
import crazypants.enderio.machines.machine.teleport.telepad.BlockTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenServerGui extends MessageTileEntity<TileTelePad> {

  public PacketOpenServerGui() {
  }

  int id;

  public PacketOpenServerGui(@Nonnull TileTelePad te, int guiId) {
    super(te);
    this.id = guiId;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(id);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    this.id = buf.readInt();
  }

  public static class Handler implements IMessageHandler<PacketOpenServerGui, IMessage> {

    @Override
    public IMessage onMessage(PacketOpenServerGui message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      World world = message.getWorld(ctx);
      TileTelePad te = message.getTileEntity(world);
      if (te != null) {
        if (message.id == BlockTelePad.GUI_ID_TELEPAD_TRAVEL && !te.canUiBeAccessed(player)) {
          BlockTravelAnchor.sendPrivateStatusMessage(world, player, te.getOwner());
        } else {
          MachineObject.block_tele_pad.openGui(world, te.getPos(), player, null, message.id);
        }
      }
      return null;
    }
  }
}
