package crazypants.enderio.power.wireless;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.config.Config;
import crazypants.enderio.integration.baubles.BaublesUtil;
import crazypants.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class WirelessChargerController {

  public static @Nonnull WirelessChargerController instance = new WirelessChargerController();

  public static final int RANGE = Config.wirelessChargerRange;
  public static final int RANGE_SQ = RANGE * RANGE;

  static {
    MinecraftForge.EVENT_BUS.register(WirelessChargerController.instance);
  }

  private final @Nonnull Map<Integer, Map<BlockPos, IWirelessCharger>> perWorldChargers = new HashMap<Integer, Map<BlockPos, IWirelessCharger>>();
  private int changeCount;

  private WirelessChargerController() {
  }

  public void registerCharger(@Nonnull IWirelessCharger charger) {
    Map<BlockPos, IWirelessCharger> chargers = getChargersForWorld(charger.getworld());
    chargers.put(charger.getLocation(), charger);
    changeCount++;
  }

  public void deregisterCharger(@Nonnull IWirelessCharger capBank) {
    Map<BlockPos, IWirelessCharger> chargers = getChargersForWorld(capBank.getworld());
    chargers.remove(capBank.getLocation());
    changeCount++;
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.side == Side.CLIENT || event.phase != TickEvent.Phase.END || event.player.isSpectator()) {
      return;
    }
    chargePlayersItems(NullHelper.notnullF(event.player, "TickEvent.PlayerTickEvent without player"));
  }

  public int getChangeCount() {
    return changeCount;
  }

  public void getChargers(@Nonnull World world, @Nonnull BlockPos bc, @Nonnull Collection<IWirelessCharger> res) {
    Map<BlockPos, IWirelessCharger> chargers = getChargersForWorld(world);
    for (IWirelessCharger wc : chargers.values()) {
      if (inRange(wc.getLocation(), bc)) {
        res.add(wc);
      }
    }
  }

  public void chargePlayersItems(@Nonnull EntityPlayer player) {
    Map<BlockPos, IWirelessCharger> chargers = getChargersForWorld(player.world);
    if (chargers.isEmpty()) {
      return;
    }
    BlockPos bc = BlockCoord.get(player);
    for (IWirelessCharger capBank : chargers.values()) {
      if (capBank.isActive() && inRange(capBank.getLocation(), bc)) {
        boolean done = chargeFromCapBank(player, capBank);
        if (done) {
          return;
        }
      }
    }
  }

  private boolean inRange(BlockPos a, BlockPos b) {
    // distSq can overflow int, so check for square coords first.
    int dx = a.getX() - b.getX();
    if (dx > RANGE || dx < -RANGE) {
      return false;
    }
    int dz = a.getZ() - b.getZ();
    if (dz > RANGE || dz < -RANGE) {
      return false;
    }
    return a.distanceSq(b) <= RANGE_SQ;
  }

  private boolean chargeFromCapBank(@Nonnull EntityPlayer player, @Nonnull IWirelessCharger capBank) {
    boolean res = capBank.chargeItems(player.inventory.armorInventory);
    res |= capBank.chargeItems(player.inventory.mainInventory);
    res |= capBank.chargeItems(player.inventory.offHandInventory);
    IInventory baubles = BaublesUtil.instance().getBaubles(player);
    if (baubles != null) {
      for (int i = 0; i < baubles.getSizeInventory(); i++) {
        ItemStack item = baubles.getStackInSlot(i);
        if (Prep.isValid(item)) {
          // mustn't change the item that is in the slot or Baubles will ignore the change
          item = item.copy();
          if (capBank.chargeItems(new NNList<>(item))) {
            baubles.setInventorySlotContents(i, item);
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

  private @Nonnull Map<BlockPos, IWirelessCharger> getChargersForWorld(@Nonnull World world) {
    Map<BlockPos, IWirelessCharger> res = perWorldChargers.get(world.provider.getDimension());
    if (res == null) {
      res = new HashMap<BlockPos, IWirelessCharger>();
      perWorldChargers.put(world.provider.getDimension(), res);
    }
    return res;
  }

  public Collection<IWirelessCharger> getChargers(@Nonnull World world) {
    return getChargerMap(world).values();
  }

  public Map<BlockPos, IWirelessCharger> getChargerMap(@Nonnull World world) {
    return perWorldChargers.get(world.provider.getDimension());
  }
}
