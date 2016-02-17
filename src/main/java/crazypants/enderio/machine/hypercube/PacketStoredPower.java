package crazypants.enderio.machine.hypercube;

import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketStoredPower implements IMessage, IMessageHandler<PacketStoredPower, IMessage> {

  private int x;
  private int y;
  private int z;
  private int storedEnergy;

  public PacketStoredPower() {
  }

  public PacketStoredPower(TileHyperCube ent) {
    x = ent.getPos().getX();
    y = ent.getPos().getY();
    z = ent.getPos().getZ();
    storedEnergy = ent.getEnergyStored();    
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeInt(storedEnergy);

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    storedEnergy = buf.readInt();
  }

  @Override
  public IMessage onMessage(PacketStoredPower message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity te = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
    if(te instanceof TileHyperCube) {
      TileHyperCube me = (TileHyperCube) te;      
      me.setEnergyStored(message.storedEnergy);      
    }
    return null;
  }

}
