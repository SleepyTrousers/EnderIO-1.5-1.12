package crazypants.enderio.machines.machine.transceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.RoundRobinIterator;

import crazypants.enderio.config.Config;
import crazypants.enderio.machine.baselegacy.SlotDefinition;
import crazypants.enderio.transceiver.Channel;
import crazypants.enderio.transceiver.ChannelType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public enum TransceiverRegistry {
  
  INSTANCE;

  private final List<TileTransceiver> transceivers = new ArrayList<TileTransceiver>();
  private Map<Channel, RoundRobinIterator<TileTransceiver>> iterators = new HashMap<Channel, RoundRobinIterator<TileTransceiver>>();

  private TransceiverRegistry() {
  }

  public void register(TileTransceiver transceiver) {
    transceivers.add(transceiver);
  }

  public void dergister(TileTransceiver transceiver) {
    transceivers.remove(transceiver);
  }

  public RoundRobinIterator<TileTransceiver> getIterator(Channel channel) {
    RoundRobinIterator<TileTransceiver> res = iterators.get(channel);
    if (res == null) {
      res = new RoundRobinIterator<TileTransceiver>(transceivers);
      iterators.put(channel, res);
    }
    return res;
  }

  // Power

  public void sendPower(TileTransceiver sender, int canSend, Channel channel) {
    RoundRobinIterator<TileTransceiver> iter = getIterator(channel);
    for (TileTransceiver trans : iter) {
      if (trans != sender && trans.getRecieveChannels(ChannelType.POWER).contains(channel)) {
        double invLoss = 1 - Config.transceiverEnergyLoss;
        int canSendWithLoss = (int) Math.round(canSend * invLoss);
        int recieved = trans.receiveEnergy(null, canSendWithLoss, false);
        if (recieved > 0) {
          int recievedPlusLoss = (int) Math.round(recieved / invLoss);
          sender.usePower(recievedPlusLoss);
        }
      }
    }
  }

  // Fluid

  public IFluidTankProperties[] getTankInfoForChannels(TileTransceiver tileTransceiver, Set<Channel> channelsIn) {
    List<IFluidTankProperties> infos = new ArrayList<IFluidTankProperties>();
    for (TileTransceiver tran : transceivers) {
      if (tran != tileTransceiver) {
        tran.getRecieveTankInfo(infos, channelsIn);
      }
    }
    return infos.toArray(new IFluidTankProperties[infos.size()]);
  }

  public boolean canFill(TileTransceiver tileTransceiver, Set<Channel> set, Fluid fluid) {
    for (TileTransceiver tran : transceivers) {
      if (tran != tileTransceiver) {
        if (tran.canReceive(set, fluid)) {
          return true;
        }
      }
    }
    return false;
  }

  public int fill(TileTransceiver from, Set<Channel> list, FluidStack resource, boolean doFill) {
    if (resource == null || !from.hasPower()) {
      return 0;
    }
    for (Channel channel : list) {
      RoundRobinIterator<TileTransceiver> iter = getIterator(channel);
      for (TileTransceiver trans : iter) {
        if (trans != from) {
          int val = trans.recieveFluid(list, resource, doFill);
          if (val > 0) {
            if (doFill && Config.transceiverBucketTransmissionCostRF > 0) {
              int powerUsed = (int) Math.max(1, Config.transceiverBucketTransmissionCostRF * val / 1000d);
              from.usePower(powerUsed);
            }
            return val;
          }
        }
      }
    }
    return 0;
  }

  // Item

  public void sendItem(TileTransceiver from, Set<Channel> channelsIn, int slot, @Nonnull ItemStack contents) {
    if (!from.hasPower()) {
      return;
    }
    if (!from.getSendItemFilter().doesItemPassFilter(null, contents)) {
      return;
    }
    for (Channel channel : channelsIn) {
      RoundRobinIterator<TileTransceiver> iter = getIterator(channel);
      for (TileTransceiver trans : iter) {
        if (trans != from && trans.getRecieveChannels(ChannelType.ITEM).contains(channel) && trans.getRedstoneChecksPassed()) {
          contents = sendItem(from, slot, contents, trans);
          if (contents.isEmpty()) {
            return;
          }
        }
      }
    }
  }

  @Nonnull
  private ItemStack sendItem(TileTransceiver from, int slot, @Nonnull ItemStack contents, TileTransceiver to) {
    SlotDefinition sd = to.getSlotDefinition();
    if (!to.getReceiveItemFilter().doesItemPassFilter(null, contents)) {
      return contents;
    }
    // try merging into existing stacks

    boolean sendComplete = false; // Only allow 1 stack per item type
    for (int i = sd.minOutputSlot; i <= sd.maxOutputSlot && !sendComplete; i++) {
      ItemStack existing = to.getStackInSlot(i);
      if (ItemUtil.areStacksEqual(existing, contents)) {
        sendComplete = true;
        if (existing.getCount() < to.getInventoryStackLimit()) {
          int numCanMerge = existing.getMaxStackSize() - existing.getCount();
          numCanMerge = Math.min(numCanMerge, contents.getCount());
          ItemStack remaining;
          if (numCanMerge >= contents.getCount()) {
            remaining = ItemStack.EMPTY;
          } else {
            remaining = contents.copy();
            remaining.shrink(numCanMerge);
          }
          ItemStack destStack = existing.copy();
          destStack.grow(numCanMerge);
          to.setInventorySlotContents(i, destStack);
          from.setInventorySlotContents(slot, remaining);
          if (remaining.isEmpty()) {
            return ItemStack.EMPTY;
          } else {
            contents = remaining.copy();
          }
        }
      }
    }
    if (!sendComplete) {
      // then fill empty stack
      for (int i = sd.minOutputSlot; i <= sd.maxOutputSlot; i++) {
        ItemStack existing = to.getStackInSlot(i);
        if (existing.isEmpty()) {
          int numCanMerge = Math.min(contents.getCount(), to.getInventoryStackLimit());
          if (numCanMerge > 0) {
            ItemStack destStack = contents.copy();
            destStack.setCount(numCanMerge);
            to.setInventorySlotContents(i, destStack);
            ItemStack remaining = contents.copy();
            remaining.shrink(numCanMerge);
            if (remaining.getCount() == 0) {
              remaining = ItemStack.EMPTY;
            }
            from.setInventorySlotContents(slot, remaining);
            return ItemStack.EMPTY;
          }
        }
      }
    }
    return contents;
  }
}
