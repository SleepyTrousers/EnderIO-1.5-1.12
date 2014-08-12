package crazypants.enderio.machine.killera;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.MessageTileEntity;
import crazypants.enderio.network.NetworkUtil;

public class PacketExperianceTotal extends MessageTileEntity<TileKillerJoe> implements IMessageHandler<PacketExperianceTotal, IMessage> {

  private int level;
  private float exp;

  public PacketExperianceTotal() {
  }

  public PacketExperianceTotal(TileKillerJoe tile) {
    super(tile);
    level = tile.experienceLevel;
    exp = tile.experience;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(level);
    buf.writeFloat(exp);    
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    level = buf.readInt();
    exp = buf.readFloat();
  }

  @Override
  public IMessage onMessage(PacketExperianceTotal message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileKillerJoe tile = message.getTileEntity(player.worldObj);
    if (tile != null) {      
      tile.experienceLevel = message.level;
      tile.experience = message.exp;      
    }
    return null;
  }
}
