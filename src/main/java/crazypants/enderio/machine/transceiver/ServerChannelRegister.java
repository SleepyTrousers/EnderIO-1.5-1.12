package crazypants.enderio.machine.transceiver;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import crazypants.util.PlayerUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.apache.commons.io.FileUtils;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.util.ItemUtil;
import crazypants.util.RoundRobinIterator;

public class ServerChannelRegister extends ChannelRegister {

  public static ServerChannelRegister instance = new ServerChannelRegister();

  private static final ExecutorService saveExecutor = Executors.newSingleThreadExecutor();

  public static void load() {
    instance.reset();
    File dataFile = getDataFile();
    if(!dataFile.exists()) {
      dataFile = getFallbackDataFile();
      if(!dataFile.exists()) {
        return;
      } else {
        Log.warn("ServerChannelRegister: Using fallback save location " + dataFile.getAbsolutePath());
      }
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

        Channel chan = new Channel(name, PlayerUtil.getPlayerUIDUnstable(user), ChannelType.values()[oridinal]);
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
    Future<?> future = saveExecutor.submit(new SaveRunnable(copyChannels()));
    try {
      //wait up to 5 seconds for it to finish
      future.get(5, TimeUnit.SECONDS);
    } catch (Exception e) {
      Log.warn("Failed to write Transciever Channels on exit: " + e);
      future.cancel(false);
    }
  }

  private static void queueStore() {
    saveExecutor.execute(new SaveRunnable(copyChannels()));
  }

  private static void doStore(EnumMap<ChannelType, List<Channel>> channels) {
    File dataFile = getDataFile();
    if(!createFolderAndWriteFile(channels, dataFile)) {
      dataFile = getFallbackDataFile();
      Log.error("ServerChannelRegister: Attempting to write Dimensional Transceiver data to fallback location: " + dataFile.getAbsolutePath());
      try {
        writeFile(copyChannels(), dataFile);
      } catch (Exception e) {
        Log.error("ServerChannelRegister: Could not write Dimensional Transceiver data fallback location " + dataFile.getAbsolutePath()
            + " channels not saved: " + e.getMessage());
        return;
      }
    }
    Log.info("ServerChannelRegister: Dimensional Transceiver data saved to " + dataFile.getAbsolutePath());
  }

  private static boolean createFolderAndWriteFile(EnumMap<ChannelType, List<Channel>> data, File dataFile) {
    try {
      File parentFolder = dataFile.getParentFile();
      FileUtils.forceMkdir(parentFolder);
      writeFile(data, dataFile);
      return true;
    } catch (Exception e) {
      Log.error("ServerChannelRegister: Could not write Dimensional Transceiver channels to " + dataFile.getAbsolutePath() + " : " + e);
      return false;
    }
  }

  protected static void writeFile(EnumMap<ChannelType, List<Channel>> chans, File dataFile) throws IOException {
    if(dataFile.exists()) {
      File tmpFile = new File(dataFile.getAbsolutePath() + ".tmp");
      doWriteFile(chans, tmpFile);
      if(FileUtils.deleteQuietly(dataFile)) {
        tmpFile.renameTo(dataFile);
      }

    } else {
      doWriteFile(chans, dataFile);
    }

  }

  protected static void doWriteFile(EnumMap<ChannelType, List<Channel>> chans, File dataFile) throws IOException {
    JsonWriter writer = new JsonWriter(new FileWriter(dataFile, false));
    writer.setIndent("  ");
    writer.beginArray();
    for (List<Channel> chanList : chans.values()) {
      for (Channel chan : chanList) {
        writer.beginObject();
        writer.name("name").value(chan.getName());
        if(chan.getUser() != null) {
          writer.name("user").value(chan.getUser().toString());
        }
        writer.name("type").value(chan.getType().ordinal());
        writer.endObject();
      }
    }
    writer.endArray();
    writer.close();
  }

  private static File getDataFile() {
    return new File(DimensionManager.getCurrentSaveRootDirectory(), "enderio/dimensionalTransceiver.json");
  }

  private static File getFallbackDataFile() {
    return new File(DimensionManager.getCurrentSaveRootDirectory(), "dimensionalTransceiver.json");
  }

  private static EnumMap<ChannelType, List<Channel>> copyChannels() {
    //NB: deep copy not needed as all types are immuatble
    EnumMap<ChannelType, List<Channel>> copy = new EnumMap<ChannelType, List<Channel>>(ChannelType.class);
    for (Entry<ChannelType, List<Channel>> entry : instance.channels.entrySet()) {
      copy.put(entry.getKey(), new ArrayList<Channel>(entry.getValue()));
    }
    return copy;
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
  public void reset() {
    super.reset();
    transceivers.clear();
    iterators.clear();
  }

  @Override
  public void removeChannel(Channel channel) {
    super.removeChannel(channel);
    for (TileTransceiver trans : transceivers) {
      trans.removeRecieveChanel(channel);
      trans.removeSendChanel(channel);
    }
    iterators.remove(channel);
    queueStore();
  }

  @Override
  public void addChannel(Channel channel) {
    super.addChannel(channel);
    queueStore();
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
    if(!from.getSendItemFilter().doesItemPassFilter(null, contents)) {
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
    if(!to.getReceiveItemFilter().doesItemPassFilter(null, contents)) {
      return contents;
    }
    //try merging into existing stacks    

    boolean sendComplete = false; // Only allow 1 stack per item type
    for (int i = sd.minOutputSlot; i <= sd.maxOutputSlot && !sendComplete; i++) {
      ItemStack existing = to.getStackInSlot(i);
      if(ItemUtil.areStacksEqual(existing, contents)) {
        sendComplete = true;
        if(existing.stackSize < to.getInventoryStackLimit()) {
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
    }
    if(!sendComplete) {
      //then fill empty stack
      for (int i = sd.minOutputSlot; i <= sd.maxOutputSlot; i++) {
        ItemStack existing = to.getStackInSlot(i);
        if(existing == null) {
          int numCanMerge = Math.min(contents.stackSize, to.getInventoryStackLimit());
          if(numCanMerge > 0) {
            ItemStack destStack = contents.copy();
            destStack.stackSize = numCanMerge;
            to.setInventorySlotContents(i, destStack);
            ItemStack remaining = contents.copy();
            remaining.stackSize -= numCanMerge;
            if(remaining.stackSize == 0) {
              remaining = null;
            }
            from.setInventorySlotContents(slot, remaining);
            return null;
          }
        }
      }
    }
    return contents;
  }

  private static class SaveRunnable implements Runnable {

    private EnumMap<ChannelType, List<Channel>> chans;

    public SaveRunnable(EnumMap<ChannelType, List<Channel>> chans) {
      this.chans = chans;
    }

    @Override
    public void run() {
      doStore(chans);
    }

  }

}
