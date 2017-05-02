package crazypants.enderio.machine.wireless;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.config.Config;
import crazypants.util.BaublesUtil;
import crazypants.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class WirelessChargerController {

  public static WirelessChargerController instance = new WirelessChargerController();

  public static final int RANGE = Config.wirelessChargerRange;
  public static final int RANGE_SQ = RANGE * RANGE;

  static {    
    MinecraftForge.EVENT_BUS.register(WirelessChargerController.instance);
  }

  private final Map<Integer, Map<BlockCoord, IWirelessCharger>> perWorldChargers = new HashMap<Integer, Map<BlockCoord, IWirelessCharger>>();
  private int changeCount;

  private WirelessChargerController() {
  }

  public void registerCharger(IWirelessCharger charger) {
    if(charger == null) {
      return;
    }
    Map<BlockCoord, IWirelessCharger> chargers = getChargersForWorld(charger.getworld());
    chargers.put(charger.getLocation(), charger);
    changeCount++;
  }

  public void deregisterCharger(IWirelessCharger capBank) {
    if(capBank == null) {
      return;
    }
    Map<BlockCoord, IWirelessCharger> chargers = getChargersForWorld(capBank.getworld());
    chargers.remove(capBank.getLocation());
    changeCount++;
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if(event.side == Side.CLIENT || event.phase != TickEvent.Phase.END) {
      return;
    }
    chargePlayersItems(event.player);
  }

  public int getChangeCount() {
    return changeCount;
  }

  public void getChargers(World world, BlockCoord bc, Collection<IWirelessCharger> res) {
    Map<BlockCoord, IWirelessCharger> chargers = getChargersForWorld(world);
    for (IWirelessCharger wc : chargers.values()) {
      if (inRange(wc.getLocation(), bc)) {
        res.add(wc);
      }
    }
  }

  public void chargePlayersItems(EntityPlayer player) {
    Map<BlockCoord, IWirelessCharger> chargers = getChargersForWorld(player.world);
    if(chargers.isEmpty()) {
      return;
    }
    BlockCoord bc = new BlockCoord(player);
    for (IWirelessCharger capBank : chargers.values()) {
      if (capBank.isActive() && inRange(capBank.getLocation(), bc)) {
        boolean done = chargeFromCapBank(player, capBank);
        if(done) {
          return;
        }
      }
    }
  }

  private boolean inRange(BlockCoord a, BlockCoord b) {
    // distSq can overflow int, so check for square coords first.
    int dx = a.x - b.x;
    if (dx > RANGE || dx < -RANGE) {
      return false;
    }
    int dz = a.z - b.z;
    if (dz > RANGE || dz < -RANGE) {
      return false;
    }
    return a.getDistSq(b) <= RANGE_SQ;
  }

  private boolean chargeFromCapBank(EntityPlayer player, IWirelessCharger capBank) {
    boolean res = capBank.chargeItems(player.inventory.armorInventory);
    res |= capBank.chargeItems(player.inventory.mainInventory);
    res |= capBank.chargeItems(player.inventory.offHandInventory);
    IInventory baubles = BaublesUtil.instance().getBaubles(player);
    if (baubles != null) {
      ItemStack[] item = new ItemStack[1];
      for (int i = 0; i < baubles.getSizeInventory(); i++) {
        item[0] = baubles.getStackInSlot(i);
        if (Prep.isValid(item[0])) {
          // mustn't change the item that is in the slot or Baubles will ignore the change
          item[0] = item[0].copy();
          if (capBank.chargeItems(item)) {
            baubles.setInventorySlotContents(i, item[0]);
            res = true;
          }
        }
      }
    }
    if (res) {
      player.inventoryContainer.detectAndSendChanges();
    }
    return res;
  }

  private Map<BlockCoord, IWirelessCharger> getChargersForWorld(World world) {
    Map<BlockCoord, IWirelessCharger> res = perWorldChargers.get(world.provider.getDimension());
    if(res == null) {
      res = new HashMap<BlockCoord, IWirelessCharger>();
      perWorldChargers.put(world.provider.getDimension(), res);
    }
    return res;
  }

  public Collection<IWirelessCharger> getChargers(World world) {
    return getChargerMap(world).values();
  }

  public Map<BlockCoord, IWirelessCharger> getChargerMap(World world) {
    return perWorldChargers.get(world.provider.getDimension());
  }
}
