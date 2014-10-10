package crazypants.enderio.machine.transceiver;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import crazypants.enderio.Log;
import crazypants.enderio.config.Config;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

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

  private List<TileTransceiver> transceivers = new ArrayList<TileTransceiver>();
  
  public void register(TileTransceiver transceiver) {
    transceivers.add(transceiver);    
  }
  
  public void dergister(TileTransceiver transceiver) {
    transceivers.remove(transceiver);    
  }

  @Override
  public void removeChannel(Channel channel) {    
    super.removeChannel(channel);
    for(TileTransceiver trans : transceivers) {
      trans.removeRecieveChanel(channel);
      trans.removeSendChanel(channel);
    }
  }

  public void sendPower(TileTransceiver sender, int canSend, Channel channel) {
    //TODO: Round robin somehow
    for(TileTransceiver trans : transceivers) {
      if(trans != sender && trans.getRecieveChannels(ChannelType.POWER).contains(channel)) {
        double invLoss = 1 - Config.transceiverEnergyLoss;
        int canSendWithLoss = (int)Math.round(canSend * invLoss);
        int recieved = trans.receiveEnergy(ForgeDirection.UNKNOWN, canSendWithLoss, false);
        if(recieved > 0) {
          int recievedPlusLoss = (int)Math.round(recieved / invLoss);
          sender.usePower(recievedPlusLoss);
        }
      }
    }    
  }
  
}
