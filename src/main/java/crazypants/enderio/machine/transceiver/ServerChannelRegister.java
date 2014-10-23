package crazypants.enderio.machine.transceiver;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.util.ItemUtil;
import crazypants.util.RoundRobinIterator;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public class ServerChannelRegister extends ChannelRegister {

  public static ServerChannelRegister instance = new ServerChannelRegister();

  public static void load() {
    instance.reset();
    File dataFile = getDataFile();
    if(!dataFile.exists()) {
      return;
    }

    try {
      JsonReader reader = new JsonReader(new FileReader(getDataFile()));
      reader.beginArray();
      while (reader.hasNext()) {
        reader.beginObject();
        reader.nextName();
        String name = reader.nextString();
        String key = reader.nextName();
        String user = null;
        if("user".equals(key)) {
          user = reader.nextString();
          reader.nextName();
        }
        int oridinal = reader.nextInt();

        Channel chan = new Channel(name, user, ChannelType.values()[oridinal]);
        instance.addChannel(chan);

        reader.endObject();
      }
      reader.endArray();
      reader.close();
    } catch (Exception e) {
      Log.error("Could not read Dimensional trasciever channels from " + getDataFile().getAbsolutePath() + " : " + e);
    }
  }

  public static void store() {
    try {
      File dataFile = getDataFile();
      dataFile.getParentFile().mkdirs();
      JsonWriter writer = new JsonWriter(new FileWriter(dataFile, false));
      writer.setIndent("  ");
      writer.beginArray();
      for (List<Channel> chanList : instance.channels.values()) {
        for (Channel chan : chanList) {
          writer.beginObject();
          writer.name("name").value(chan.getName());
          if(chan.getUser() != null) {
            writer.name("user").value(chan.getUser());
          }
          writer.name("type").value(chan.getType().ordinal());
          writer.endObject();
        }
      }
      writer.endArray();
      writer.close();
    } catch (Exception e) {
      Log.error("Could not write Dimensional trasciever channels to " + getDataFile().getAbsolutePath() + " : " + e);
    }
  }

  private static File getDataFile() {
    return new File(DimensionManager.getCurrentSaveRootDirectory(), "enderio/dimensionalTransceiver.json");
  }

  //-----------------------------------------------------------------------------------------------

  private final List<TileTransceiver> transceivers = new ArrayList<TileTransceiver>();
  private Map<Channel, RoundRobinIterator<TileTransceiver>> iterators = new HashMap<Channel, RoundRobinIterator<TileTransceiver>>();

  private ServerChannelRegister() {
  }

  public void register(TileTransceiver transceiver) {
    transceivers.add(transceiver);
  }

  public void dergister(TileTransceiver transceiver) {
    transceivers.remove(transceiver);
  }

  @Override
  public void removeChannel(Channel channel) {
    super.removeChannel(channel);
    for (TileTransceiver trans : transceivers) {
      trans.removeRecieveChanel(channel);
      trans.removeSendChanel(channel);
    }
    iterators.remove(channel);
  }

  public RoundRobinIterator<TileTransceiver> getIterator(Channel channel) {
    RoundRobinIterator<TileTransceiver> res = iterators.get(channel);
    if(res == null) {
      res = new RoundRobinIterator<TileTransceiver>(transceivers);
      iterators.put(channel, res);
    }
    return res;
  }

  //Power

  public void sendPower(TileTransceiver sender, int canSend, Channel channel) {
    RoundRobinIterator<TileTransceiver> iter = getIterator(channel);
    for (TileTransceiver trans : iter) {
      if(trans != sender && trans.getRecieveChannels(ChannelType.POWER).contains(channel)) {
        double invLoss = 1 - Config.transceiverEnergyLoss;
        int canSendWithLoss = (int) Math.round(canSend * invLoss);
        int recieved = trans.receiveEnergy(ForgeDirection.UNKNOWN, canSendWithLoss, false);
        if(recieved > 0) {
          int recievedPlusLoss = (int) Math.round(recieved / invLoss);
          sender.usePower(recievedPlusLoss);
        }
      }
    }
  }  

  //Fluid

  public FluidTankInfo[] getTankInfoForChannels(TileTransceiver tileTransceiver, List<Channel> channels) {
    List<FluidTankInfo> infos = new ArrayList<FluidTankInfo>();
    for (TileTransceiver tran : transceivers) {
      if(tran != tileTransceiver) {
        tran.getRecieveTankInfo(infos, channels);
      }
    }
    return infos.toArray(new FluidTankInfo[infos.size()]);
  }

  public boolean canFill(TileTransceiver tileTransceiver, List<Channel> channels, Fluid fluid) {
    for (TileTransceiver tran : transceivers) {
      if(tran != tileTransceiver) {
        if(tran.canReceive(channels, fluid)) {
          return true;
        }
      }
    }
    return false;
  }

  public int fill(TileTransceiver from, List<Channel> list, FluidStack resource, boolean doFill) {
    if(resource == null || !from.hasPower()) {
      return 0;
    }
    for (Channel channel : list) {
      RoundRobinIterator<TileTransceiver> iter = getIterator(channel);
      for (TileTransceiver trans : iter) {
        if(trans != from) {
          int val = trans.recieveFluid(list, resource, doFill);
          if(val > 0) {
            if(doFill && Config.transceiverBucketTransmissionCostRF > 0) {
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

  //Item 

  public void sendItem(TileTransceiver from, List<Channel> channels, int slot, ItemStack contents) {
    if(!from.hasPower()) {
      return;
    }
    for (Channel channel : channels) {
      RoundRobinIterator<TileTransceiver> iter = getIterator(channel);
      for (TileTransceiver trans : iter) {
        if(trans != from && trans.getRecieveChannels(ChannelType.ITEM).contains(channel) && trans.getRedstoneChecksPassed()) {
          contents = sendItem(from, slot, contents, trans);
          if(contents == null) {
            return;
          }
        }
      }
    }
  }

  private ItemStack sendItem(TileTransceiver from, int slot, ItemStack contents, TileTransceiver to) {
    SlotDefinition sd = to.getSlotDefinition();
    //try merging into existing stacks

    boolean recieverHasItem = false;     // Only allow 1 stack per item type
    for (int i = sd.minOutputSlot; i <= sd.maxOutputSlot && !recieverHasItem; i++) {
      ItemStack existing = to.getStackInSlot(i);
      if(ItemUtil.areStackTypesEqual(existing, contents)) {
        recieverHasItem = true;
        int numCanMerge = existing.getMaxStackSize() - existing.stackSize;
        numCanMerge = Math.min(numCanMerge, contents.stackSize);
        ItemStack remaining;
        if(numCanMerge >= contents.stackSize) {
          remaining = null;
        } else {
          remaining = contents.copy();
          remaining.stackSize -= numCanMerge;
        }
        ItemStack destStack = existing.copy();
        destStack.stackSize += numCanMerge;
        to.setInventorySlotContents(i, destStack);
        from.setInventorySlotContents(slot, remaining);
        if(remaining == null) {
          return null;
        } else {
          contents = remaining.copy();
        }
      }
    }
    if(!recieverHasItem) {
      //then fill empty stack
      for (int i = sd.minOutputSlot; i <= sd.maxOutputSlot; i++) {
        ItemStack existing = to.getStackInSlot(i);
        if(existing == null) {
          to.setInventorySlotContents(i, contents.copy());
          from.setInventorySlotContents(slot, null);
          return null;
        }
      }
    }
    return contents;
  }

}
