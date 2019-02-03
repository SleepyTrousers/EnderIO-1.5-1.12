package crazypants.enderio.base.power.wireless;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
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
public final class WirelessChargerController {

  public static final @Nonnull WirelessChargerController instance = new WirelessChargerController();

  private final @Nonnull Map<Integer, Collection<BlockPos>> perWorldChargers = new HashMap<>();
  private int changeCount;

  private WirelessChargerController() {
  }

  public void registerCharger(@Nonnull IWirelessCharger charger) {
    getChargersForWorld(charger.getworld()).add(charger.getLocation().toImmutable());
    changeCount++;
  }

  public void deregisterCharger(@Nonnull IWirelessCharger charger) {
    getChargersForWorld(charger.getworld()).remove(charger.getLocation());
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
    final Collection<BlockPos> chargers = getChargersForWorld(world);
    for (Iterator<BlockPos> iterator = chargers.iterator(); iterator.hasNext();) {
      BlockPos chargerPos = iterator.next();
      IWirelessCharger charger = chargerPos != null ? BlockEnder.getAnyTileEntitySafe(world, chargerPos, IWirelessCharger.class) : null;
      if (charger != null) {
        if (charger.getRange().contains(pos)) {
          res.add(charger);
        }
      } else {
        iterator.remove();
        changeCount++;
      }
    }
  }

  public void chargePlayersItems(@Nonnull EntityPlayer player) {
    List<IWirelessCharger> chargers = new ArrayList<>();
    getChargers(player.world, new BlockPos(player), chargers);
    if (chargers.isEmpty()) {
      return;
    }
    BlockPos pos = BlockCoord.get(player);
    for (IWirelessCharger charger : chargers) {
      if (charger.isActive() && charger.getRange().contains(pos)) {
        boolean done = chargeFromCapBank(player, charger);
        if (done) {
          return;
        }
      }
    }
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

  private @Nonnull Collection<BlockPos> getChargersForWorld(@Nonnull World world) {
    Collection<BlockPos> res = perWorldChargers.get(world.provider.getDimension());
    if (res == null) {
      res = new HashSet<BlockPos>();
      perWorldChargers.put(world.provider.getDimension(), res);
    }
    return res;
  }

}
