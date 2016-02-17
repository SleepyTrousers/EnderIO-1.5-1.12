package crazypants.enderio.machine.power;

import java.util.List;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketPowerStorage implements IMessage, IMessageHandler<PacketPowerStorage, IMessage> {

  private int x;
  private int y;
  private int z;
  private int storedEnergy;

  public PacketPowerStorage() {
  }

  public PacketPowerStorage(TileCapacitorBank ent) {
    BlockPos p = ent.getPos();
    x = p.getX();
    y = p.getY();
    z = p.getZ();
    storedEnergy = ent.storedEnergyRF;
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
  public IMessage onMessage(PacketPowerStorage message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity te = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
    if(te instanceof TileCapacitorBank) {
      TileCapacitorBank me = (TileCapacitorBank) te;
      me.storedEnergyRF = message.storedEnergy;

      double dif = Math.abs(me.lastRenderStoredRatio - me.getEnergyStoredRatio());
      if(dif > 0.025) { //update rendering at a 2.5% diff
        if(!me.isMultiblock()) {
          player.worldObj.markBlockForUpdate(new BlockPos(message.x, message.y, message.z));
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
    TileEntity te = worldObj.getTileEntity(new BlockPos(bc.x, bc.y, bc.z));
    if(te instanceof TileCapacitorBank) {
      TileCapacitorBank me = (TileCapacitorBank) te;
      List<GaugeBounds> gb = me.getGaugeBounds();
      if(gb != null && !gb.isEmpty()) {
        worldObj.markBlockForUpdate(new BlockPos(bc.x, bc.y, bc.z));
      }
    }

  }

}
