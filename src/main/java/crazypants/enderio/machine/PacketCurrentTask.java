package crazypants.enderio.machine;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.MessageTileEntity;
import crazypants.enderio.network.NetworkUtil;

public class PacketCurrentTask extends MessageTileEntity<AbstractPoweredTaskEntity> implements IMessageHandler<PacketCurrentTask, IMessage>  {

  private NBTTagCompound nbtRoot;

  public PacketCurrentTask() {
  }

  public PacketCurrentTask(AbstractPoweredTaskEntity tile) {
    super(tile);
    nbtRoot = new NBTTagCompound();
    if(tile.currentTask != null) {      
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.currentTask.writeToNBT(tankRoot);
      nbtRoot.setTag("currentTask", tankRoot);
    } 
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    NetworkUtil.writeNBTTagCompound(nbtRoot, buf);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    nbtRoot = NetworkUtil.readNBTTagCompound(buf);
  }

  @Override
  public IMessage onMessage(PacketCurrentTask message, MessageContext ctx) {
    
    AbstractPoweredTaskEntity tile = message.getTileEntity(EnderIO.proxy.getClientWorld());
    if (tile != null) {      
      if(message.nbtRoot.hasKey("currentTask")) {        
        NBTTagCompound tankRoot = message.nbtRoot.getCompoundTag("currentTask");
        tile.currentTask = PoweredTask.readFromNBT(message.nbtRoot.getCompoundTag("currentTask"));        
      } else {
        tile.currentTask = null;
      } 
    }
    return null;
  }
}
