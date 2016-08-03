package crazypants.enderio.teleport.telepad;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.EnderIO;
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

  public PacketOpenServerGui(ITileTelePad te, int guiId) {
    super(te.getTileEntity());
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

  @Override
  public IMessage onMessage(PacketOpenServerGui message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    World world = message.getWorld(ctx);
    TileEntity te = message.getTileEntity(world);    
    if(te instanceof ITileTelePad) {
      player.openGui(EnderIO.instance, message.id, world, te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
    }    
    return null;
  }
}
