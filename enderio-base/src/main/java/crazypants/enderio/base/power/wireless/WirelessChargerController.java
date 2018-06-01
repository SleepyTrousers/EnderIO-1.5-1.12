package crazypants.enderio.base.power.wireless;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

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
public class WirelessChargerController {

  public static @Nonnull WirelessChargerController instance = new WirelessChargerController();

  private final @Nonnull Map<Integer, List<IWirelessCharger>> perWorldChargers = new HashMap<Integer, List<IWirelessCharger>>();
  private int changeCount;

  private WirelessChargerController() {
  }

  public void registerCharger(@Nonnull IWirelessCharger charger) {
    List<IWirelessCharger> chargers = getChargersForWorld(charger.getworld());
    chargers.add(charger);
    changeCount++;
  }

  public void deregisterCharger(@Nonnull IWirelessCharger charger) {
    List<IWirelessCharger> chargers = getChargersForWorld(charger.getworld());
    chargers.remove(charger);
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
    List<IWirelessCharger> chargers = getChargersForWorld(world);
    for (IWirelessCharger charger : chargers) {
      if (charger.getRange().contains(pos)) {
        res.add(charger);
      }
    }
  }

  public void chargePlayersItems(@Nonnull EntityPlayer player) {
    List<IWirelessCharger> chargers = getChargersForWorld(player.world);
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

  private @Nonnull List<IWirelessCharger> getChargersForWorld(@Nonnull World world) {
    List<IWirelessCharger> res = perWorldChargers.get(world.provider.getDimension());
    if (res == null) {
      res = new ArrayList<IWirelessCharger>();
      perWorldChargers.put(world.provider.getDimension(), res);
    }
    return res;
  }

}
