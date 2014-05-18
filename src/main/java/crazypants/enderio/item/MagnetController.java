package crazypants.enderio.item;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Config;
import crazypants.render.BoundingBox;
import crazypants.util.BlockCoord;

public class MagnetController implements IEntitySelector {

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    
    ActiveMagnet mag = getActiveMagnet(event.player);
    if(mag != null) {   
      doHoover(event.player);
      if(event.side == Side.SERVER) {
        ItemMagnet.drainPerTickPower(mag.item);
        event.player.inventory.setInventorySlotContents(mag.slot, mag.item);
        event.player.inventory.markDirty();
      }
    }
  }

  private ActiveMagnet getActiveMagnet(EntityPlayer player) {
    ItemStack[] inv = player.inventory.mainInventory;
    for(int i=0;i<9;i++) {
      if(ItemMagnet.isActive(inv[i]) && ItemMagnet.hasPower(inv[i])) {
        return new ActiveMagnet(inv[i], i);
      }
    }
    return null;
  }
  
  private void doHoover(EntityPlayer player) {
    
    AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
        player.posX - Config.magnetRange, player.posY - Config.magnetRange, player.posZ - Config.magnetRange,
        player.posX + Config.magnetRange, player.posY + Config.magnetRange, player.posZ + Config.magnetRange);
        
    List<EntityItem> interestingItems = player.worldObj.selectEntitiesWithinAABB(EntityItem.class, aabb, this);

    for (EntityItem entity : interestingItems) {
      double x = player.posX + 0.5D - entity.posX;
      double y = player.posY + 1D - entity.posY;
      double z = player.posZ + 0.5D - entity.posZ;

      double distance = Math.sqrt(x * x + y * y + z * z);
      if(distance < 1.25) {
        entity.onCollideWithPlayer(player);
      } else {
        double speed = 0.035;
        entity.motionX += x / distance * speed;
        entity.motionY += y * speed;
        if(y > 0) {
          entity.motionY = 0.12;
        }
        entity.motionZ += z / distance * speed;
      }

    }
  }

  @Override
  public boolean isEntityApplicable(Entity var1) {
    return true;
  }
  
  private static class ActiveMagnet {
    ItemStack item;
    int slot;
    
    ActiveMagnet(ItemStack item, int slot) {    
      this.item = item;
      this.slot = slot;
    }        
  }

}
