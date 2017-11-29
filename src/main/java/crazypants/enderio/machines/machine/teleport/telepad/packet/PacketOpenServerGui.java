package crazypants.enderio.machines.machine.teleport.telepad.packet;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.GuiID;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenServerGui extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketOpenServerGui, IMessage> {

  public PacketOpenServerGui() {
  }

  int id;

  public PacketOpenServerGui(TileTelePad te, GuiID guiId) {
    super(te.getTileEntity());
    this.id = guiId.ordinal();
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

  @Override
  public IMessage onMessage(PacketOpenServerGui message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().player;
    World world = message.getWorld(ctx);
    TileEntity te = message.getTileEntity(world);    
    if(te instanceof TileTelePad) {
      GuiID.byID(message.id).openGui(world, te.getPos(), player, null);
    }
    return null;
  }
}
