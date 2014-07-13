package crazypants.enderio.machine.power;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.util.BlockCoord;

public class PacketPowerStorage implements IMessage, IMessageHandler<PacketPowerStorage, IMessage> {

  private int x;
  private int y;
  private int z;
  private double storedEnergy;

  public PacketPowerStorage() {
  }

  public PacketPowerStorage(TileCapacitorBank ent) {
    x = ent.xCoord;
    y = ent.yCoord;
    z = ent.zCoord;
    storedEnergy = ent.storedEnergy;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeDouble(storedEnergy);

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    storedEnergy = buf.readDouble();
  }

  @Override
  public IMessage onMessage(PacketPowerStorage message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
    if(te instanceof TileCapacitorBank) {
      TileCapacitorBank me = (TileCapacitorBank) te;
      me.storedEnergy = message.storedEnergy;

      double dif = Math.abs(me.lastRenderStoredRatio - me.getEnergyStoredRatio());
      if(dif > 0.025) { //update rendering at a 2.5% diff
        if(!me.isMultiblock()) {
          player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
        } else {
          BlockCoord[] mb = me.multiblock;
          for (BlockCoord bc : mb) {
            updateGaugeRender(player.worldObj, bc);
          }
        }

      }
    }
    return null;
  }

  private void updateGaugeRender(World worldObj, BlockCoord bc) {
    TileEntity te = worldObj.getTileEntity(bc.x, bc.y, bc.z);
    if(te instanceof TileCapacitorBank) {
      TileCapacitorBank me = (TileCapacitorBank) te;
      List<GaugeBounds> gb = me.getGaugeBounds();
      if(gb != null && !gb.isEmpty()) {
        worldObj.markBlockForUpdate(bc.x, bc.y, bc.z);
      }
    }

  }

}
