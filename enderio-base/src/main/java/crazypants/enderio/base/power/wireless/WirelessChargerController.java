package crazypants.enderio.base.power.wireless;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.ChargerConfig;
import crazypants.enderio.base.integration.baubles.BaublesUtil;
import crazypants.enderio.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIO.MODID)
public class WirelessChargerController {

  public static @Nonnull WirelessChargerController instance = new WirelessChargerController();

  private final @Nonnull Map<Integer, Map<BlockPos, IWirelessCharger>> perWorldChargers = new HashMap<Integer, Map<BlockPos, IWirelessCharger>>();
  private int changeCount;

  private WirelessChargerController() {
  }

  public void registerCharger(@Nonnull IWirelessCharger charger) {
    Map<BlockPos, IWirelessCharger> chargers = getChargersForWorld(charger.getworld());
    chargers.put(charger.getLocation(), charger);
    changeCount++;
  }

  public void deregisterCharger(@Nonnull IWirelessCharger charger) {
    Map<BlockPos, IWirelessCharger> chargers = getChargersForWorld(charger.getworld());
    chargers.remove(charger.getLocation());
    changeCount++;
  }

  @SubscribeEvent
  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.side == Side.CLIENT || event.phase != TickEvent.Phase.END || event.player.isSpectator()) {
      return;
    }
    instance.chargePlayersItems(NullHelper.notnullF(event.player, "TickEvent.PlayerTickEvent without player"));
  }

  public int getChangeCount() {
    return changeCount;
  }

  public void getChargers(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Collection<IWirelessCharger> res) {
    Map<BlockPos, IWirelessCharger> chargers = getChargersForWorld(world);
    for (IWirelessCharger wc : chargers.values()) {
      if (inRange(wc.getLocation(), pos)) {
        res.add(wc);
      }
    }
  }

  public void chargePlayersItems(@Nonnull EntityPlayer player) {
    Map<BlockPos, IWirelessCharger> chargers = getChargersForWorld(player.world);
    if (chargers.isEmpty()) {
      return;
    }
    BlockPos pos = BlockCoord.get(player);
    for (IWirelessCharger charger : chargers.values()) {
      if (charger.isActive() && inRange(charger.getLocation(), pos)) {
        boolean done = chargeFromCapBank(player, charger);
        if (done) {
          return;
        }
      }
    }
  }

  private boolean inRange(BlockPos a, BlockPos b) {
    int RANGE = ChargerConfig.wirelessChargerRange.get();
    // distSq can overflow int, so check for square coords first.
    int dx = a.getX() - b.getX();
    if (dx > RANGE || dx < -RANGE) {
      return false;
    }
    int dz = a.getZ() - b.getZ();
    if (dz > RANGE || dz < -RANGE) {
      return false;
    }
    int dy = a.getY() - b.getY();
    return (dx * dx + dy * dy + dz * dz) <= (RANGE * RANGE);
  }

  private boolean chargeFromCapBank(@Nonnull EntityPlayer player, @Nonnull IWirelessCharger charger) {
    boolean res = charger.chargeItems(player.inventory.armorInventory);
    res |= charger.chargeItems(player.inventory.mainInventory);
    res |= charger.chargeItems(player.inventory.offHandInventory);
    IInventory baubles = BaublesUtil.instance().getBaubles(player);
    if (baubles != null) {
      for (int i = 0; i < baubles.getSizeInventory(); i++) {
        ItemStack item = baubles.getStackInSlot(i);
        if (Prep.isValid(item)) {
          // mustn't change the item that is in the slot or Baubles will ignore the change
          item = item.copy();
          if (charger.chargeItems(new NNList<>(item))) {
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
