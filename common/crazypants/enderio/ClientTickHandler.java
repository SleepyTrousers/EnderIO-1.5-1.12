package crazypants.enderio;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import crazypants.enderio.conduit.ConduitUtil;

public class ClientTickHandler implements ITickHandler {

  private boolean hidingConduits = false;
  private boolean first = true;
  
  @Override
  public void tickStart(EnumSet<TickType> type, Object... tickData) {
    EntityPlayer player = (EntityPlayer) tickData[0];        
    boolean curVal = ConduitUtil.isToolEquipped(player) || ConduitUtil.isConduitEquipped(player);
    if(first) {
      hidingConduits = curVal;
      first = false;
    }
    if(curVal != hidingConduits) {      
      hidingConduits = curVal;      
      WorldClient world = FMLClientHandler.instance().getClient().theWorld;
      
      @SuppressWarnings("unchecked")
      List<TileEntity> checkList = world.loadedTileEntityList;
      for(TileEntity o : checkList) {
        if(o.blockType != null && o.blockType.blockID == EnderIO.blockConduitBundle.blockID) {
          if(o.getDistanceFrom(player.posX, player.posY, player.posZ) < o.getMaxRenderDistanceSquared()) {
            world.markBlockForRenderUpdate(o.xCoord, o.yCoord, o.zCoord);
          }
        }
      }
    }
      
      
  }

  @Override
  public void tickEnd(EnumSet<TickType> type, Object... tickData) {
  }

  @Override
  public EnumSet<TickType> ticks() {
    return EnumSet.of(TickType.PLAYER);
  }

  @Override
  public String getLabel() {
    return "EnderIO Client Tick Handler";
  }

  
}
