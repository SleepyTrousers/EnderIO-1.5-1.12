package crazypants.enderio.machine.wireless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scala.Array;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelController;
import crazypants.enderio.item.darksteel.DarkSteelRecipeManager;
import crazypants.util.BlockCoord;

public class WirelessChargerController {

  public static WirelessChargerController instance = new WirelessChargerController();

  public static final int RANGE = Config.wirelessChargerRange;
  public static final int RANGE_SQ = RANGE * RANGE;

  static {
    FMLCommonHandler.instance().bus().register(WirelessChargerController.instance);
    MinecraftForge.EVENT_BUS.register(WirelessChargerController.instance);
  }

  private Map<Integer, Map<BlockCoord, IWirelessCharger>> perWorldChargers = new HashMap<Integer, Map<BlockCoord, IWirelessCharger>>();

  private WirelessChargerController() {
  }

  public void registerCharger(IWirelessCharger capBank) {
    if(capBank == null) {
      return;
    }    
    Map<BlockCoord, IWirelessCharger> chargers = getChargersForWorld(capBank.getWorldObj());
    chargers.put(capBank.getLocation(), capBank);
  }

  public void deregisterCharger(IWirelessCharger capBank) {
    if(capBank == null) {
      return;
    }
    Map<BlockCoord, IWirelessCharger> chargers = getChargersForWorld(capBank.getWorldObj());
    chargers.remove(capBank.getLocation());
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if(event.side == Side.CLIENT || event.phase != TickEvent.Phase.END) {
      return;
    }
    Map<BlockCoord, IWirelessCharger> chargers = getChargersForWorld(event.player.worldObj);
    if(chargers.isEmpty()) {
      return;
    }
    BlockCoord bc = new BlockCoord((int) event.player.posX, (int) event.player.posY, (int) event.player.posZ);
    for (IWirelessCharger capBank : chargers.values()) {
      if(capBank.getLocation().distanceSquared(bc) <= RANGE_SQ) {
        boolean done = chargeFromCapBank(event.player, capBank);
        if(done) {
          return;
        }
      }
    }
  }

  private boolean chargeFromCapBank(EntityPlayer player, IWirelessCharger capBank) {
    boolean res = capBank.chargeItems(player.inventory.armorInventory);
    res |= capBank.chargeItems(player.inventory.mainInventory);
    if(res) {
      player.inventory.markDirty();
    }
    return res;
  }

  private Map<BlockCoord, IWirelessCharger> getChargersForWorld(World world) {
    Map<BlockCoord, IWirelessCharger> res = perWorldChargers.get(world.provider.dimensionId);
    if(res == null) {
      res = new HashMap<BlockCoord, IWirelessCharger>();
      perWorldChargers.put(world.provider.dimensionId, res);
    }
    return res;
  }

 
}
