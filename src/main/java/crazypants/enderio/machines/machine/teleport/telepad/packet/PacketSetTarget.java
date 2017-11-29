package crazypants.enderio.machines.machine.teleport.telepad.packet;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.EnderIO;
import crazypants.enderio.item.coordselector.TelepadTarget;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetTarget extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketSetTarget, IMessage> {

  public PacketSetTarget() {
    super();
  }
  
  private TelepadTarget target;
  
  public PacketSetTarget(TileTelePad te, TelepadTarget target) {
    super(te.getTileEntity());    
    this.target = target;
  }
  
  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    NBTTagCompound nbt = new NBTTagCompound();
    target.writeToNBT(nbt);
    ByteBufUtils.writeTag(buf, nbt);

  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    NBTTagCompound nbt = ByteBufUtils.readTag(buf);
    target = TelepadTarget.readFromNBT(nbt);
  }
  
  @Override
  public IMessage onMessage(PacketSetTarget message, MessageContext ctx) {
    TileEntity te = message.getTileEntity(ctx.side.isClient() ? EnderIO.proxy.getClientWorld() : message.getWorld(ctx));
    if(te instanceof TileTelePad) {
      TileTelePad tp = (TileTelePad)te;      
      tp.setTarget(message.target); 
    }
    return null;
  }
}
