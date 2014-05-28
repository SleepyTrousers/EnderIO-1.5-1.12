package crazypants.enderio.machine.power;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.network.IPacketEio;
import crazypants.util.BlockCoord;

public class PacketPowerStorage implements IPacketEio {

  private int x;
  private int y;
  private int z;
  private float storedEnergy;

  public PacketPowerStorage() {
  }

  public PacketPowerStorage(TileCapacitorBank ent) {
    x = ent.xCoord;
    y = ent.yCoord;
    z = ent.zCoord;
    storedEnergy = ent.storedEnergy;
  }
  
  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeFloat(storedEnergy);    

  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    storedEnergy = buf.readFloat();
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    handle(player);
  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    handle(player);
  }

  private void handle(EntityPlayer player) {    
    TileEntity te = player.worldObj.getTileEntity(x, y, z);
    if(te instanceof TileCapacitorBank) {
      TileCapacitorBank me = (TileCapacitorBank) te;      
      me.storedEnergy = storedEnergy;   
      
      float dif = Math.abs(me.lastRenderStoredRatio - me.getEnergyStoredRatio());      
      if(dif > 0.025) { //update rendering at a 2.5% diff
        if(!me.isMultiblock()) {
          player.worldObj.markBlockForUpdate(x, y, z);
        } else {
          BlockCoord[] mb = me.multiblock;
          for(BlockCoord bc : mb) {
            updateGaugeRender(player.worldObj, bc);
          }
        }
        
      }
    } 
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
