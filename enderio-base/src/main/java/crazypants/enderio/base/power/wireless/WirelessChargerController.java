package crazypants.enderio.base.power.wireless;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
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
import crazypants.enderio.base.power.wireless.IWirelessCharger.SelfDestructionException;
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

  private final static @Nonnull Map<Integer, Collection<BlockPos>> perWorldChargers = new HashMap<>();

  public static void registerCharger(@Nonnull IWirelessCharger charger) {
    getChargers(charger.getworld()).add(charger.getLocation().toImmutable());
  }

  public static void deregisterCharger(@Nonnull IWirelessCharger charger) {
    getChargers(charger.getworld()).remove(charger.getLocation());
  }

  @SubscribeEvent
  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.side == Side.CLIENT || event.phase != TickEvent.Phase.END || event.player.isSpectator()) {
      return;
    }
    chargePlayersItems(NullHelper.notnullF(event.player, "TickEvent.PlayerTickEvent without player"));
  }

  public static @Nonnull List<IWirelessCharger> getChargers(@Nonnull World world, @Nonnull BlockPos pos) {
    final Collection<BlockPos> chargers = getChargers(world);
    if (chargers.isEmpty()) {
      return Collections.emptyList();
    }
    final List<IWirelessCharger> res = new ArrayList<>();
    for (Iterator<BlockPos> iterator = chargers.iterator(); iterator.hasNext();) {
      BlockPos chargerPos = iterator.next();
      IWirelessCharger charger = chargerPos != null ? BlockEnder.getAnyTileEntitySafe(world, chargerPos, IWirelessCharger.class) : null;
      if (charger != null) {
        try {
          if (charger.isActive() && charger.getRange().contains(pos)) {
            res.add(charger);
          }
        } catch (SelfDestructionException e) {
          try {
            iterator.remove();
          } catch (ConcurrentModificationException e1) {
            // NOP
          }
          return getChargers(world, pos);
        }
      } else {
        iterator.remove();
      }
    }
    return res;
  }

  private static void chargePlayersItems(@Nonnull EntityPlayer player) {
    for (IWirelessCharger charger : getChargers(player.world, BlockCoord.get(player))) {
      if (charger != null && chargePlayersItems(player, charger) && charger.forceSingle()) {
        return;
      }
    }
  }

  private static boolean chargePlayersItems(@Nonnull EntityPlayer player, @Nonnull IWirelessCharger charger) {
    boolean res = charger.chargeItems(player.inventory.armorInventory);
    res |= charger.chargeItems(player.inventory.mainInventory);
    res |= charger.chargeItems(player.inventory.offHandInventory);
    if (res) {
      player.inventoryContainer.detectAndSendChanges();
    }
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
    return res;
  }

  private static @Nonnull Collection<BlockPos> getChargers(@Nonnull World world) {
    return NullHelper.notnull(perWorldChargers.computeIfAbsent(world.provider.getDimension(), dim -> new HashSet<BlockPos>()), "computeIfAbsent()");
  }

}
