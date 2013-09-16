package crazypants.enderio.machine.hypercube;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLLog;

public class HyperCubeRegister {

  private static final String CATEGORY_PUBLIC_CHANNELS = "PublicChannels";
  private static final String CATEGORY_PRIVATE_CHANNELS = "PrivateChannels";
  private static final String KEY_PUBLIC_CHANNELS = "names";

  public static HyperCubeRegister instance; 

  private final Map<Channel, List<TileHyperCube>> channelMapping = new HashMap<Channel, List<TileHyperCube>>();
  
  private final List<Channel> publicChannels = new ArrayList<Channel>();
  private final List<Channel> publicChannelsRO = Collections.unmodifiableList(publicChannels);
  
  private final Map<String, List<Channel>> userChannels = new HashMap<String, List<Channel>>();
  
  private Configuration config;
  
  public static void load() {
    instance = new HyperCubeRegister();    
    instance.innerLoad();         
  }
  
  public static void unload() {
    instance = new HyperCubeRegister();    
  }  
  
  private void innerLoad() {
    config = new Configuration(new File(DimensionManager.getCurrentSaveRootDirectory(), "/enderio/hypercubes.cfg"));
    config.load();

    Property pcNamesProp = config.get(CATEGORY_PUBLIC_CHANNELS, "names", new String[] {});
    String[] pcNames = pcNamesProp.getStringList();
    if (pcNames != null) {
      for (String name : pcNames) {
        publicChannels.add(new Channel(name, null));
      }
    }  
    Property userNamesProp = config.get(CATEGORY_PRIVATE_CHANNELS, "users", new String[] {});
    String[] userNames = userNamesProp.getStringList();
    if (userNames != null) {
      for (String user : userNames) {
        Property userChannles = config.get(CATEGORY_PRIVATE_CHANNELS, user + ".channels", new String[] {});
        String[] channelNames = userChannles.getStringList();
        if(channelNames != null && channelNames.length > 0) {
          List<Channel> channels = getChannelsForUser(user);
          for(String chanName : channelNames) {
            channels.add(new Channel(chanName, user));
          }
        }
      }
    }  
  }
  
  public synchronized List<Channel> getChannelsForUser(String user) {    
    List<Channel> result = userChannels.get(user);
    if(result == null) {
      result = new ArrayList<Channel>();
      userChannels.put(user, result);
    }
    return result;
  }

  public synchronized void register(TileHyperCube cube) {
    List<TileHyperCube> cubes = innerGetCubesForChannel(cube.getChannel());
    if (cubes != null && !cubes.contains(cube)) {
      cubes.add(cube);
    }
  }
  
  public synchronized void deregister(TileHyperCube cube, Channel channel) {
    List<TileHyperCube> cubes = innerGetCubesForChannel(channel);
    if(cubes != null) {
      cubes.remove(cube);
    }
  }
  
  public synchronized void deregister(TileHyperCube cube) {
    List<TileHyperCube> cubes = innerGetCubesForChannel(cube.getChannel());
    if(cubes != null) {
      cubes.remove(cube);
    }
  }
  
  public synchronized List<TileHyperCube> getCubesForChannel(Channel channel) {
    List<TileHyperCube> chans = innerGetCubesForChannel(channel);
    if(chans == null) {
      return Collections.emptyList();
    }
    return new ArrayList<TileHyperCube>(chans);
  }
  
  public synchronized void addChannel(Channel channel) {
    if(channel == null || channel.name == null) {
      return;
    }    
    if(channel.user == null) {
      if(!publicChannels.contains(channel)) {
        publicChannels.add(channel);
        updateConfig();
      }
    } else {      
      List<Channel> channels = getChannelsForUser(channel.user);
      if(!channels.contains(channel)) {        
        channels.add(channel);
        updateConfig();
      }
    }
  }
  
  public void removeChannel(Channel channel) {
    if(channel == null || channel.name == null) {
      return;
    }    
    if(channel.user == null) {
      if(publicChannels.contains(channel)) {
        publicChannels.remove(channel);
        updateConfig();
      }
    } else {      
      List<Channel> channels = getChannelsForUser(channel.user);
      if(channels.contains(channel)) {        
        channels.remove(channel);
        updateConfig();
      }
    }
    
  }

  private void updateConfig() {
    if(config == null) {
      FMLLog.warning("HyperCubeRegister.updateConfig: Config was null.");
      return;
    }
    String[] publicNames = new String[publicChannels.size()];
    for(int i=0; i < publicChannels.size(); i++) {
      publicNames[i] = publicChannels.get(i).name;
    }
    Property pcNamesProp = config.get(CATEGORY_PUBLIC_CHANNELS, "names", new String[] {});
    pcNamesProp.set(publicNames);
    
    Set<String> users = userChannels.keySet();    
    Property userNamesProp = config.get(CATEGORY_PRIVATE_CHANNELS, "users", new String[] {});
    userNamesProp.set(users == null ? new String[0] : users.toArray(new String[users.size()]));
    
    for(String user : users) {
      Property userChansProp = config.get(CATEGORY_PRIVATE_CHANNELS, user + ".channels", new String[] {});
      List<Channel> val = userChannels.get(user);
      String[] channelNames;
      if(val == null) {
        channelNames = new String[0];
      } else {
        channelNames = new String[val.size()];
        int i=0;
        for(Channel chan : val) {
          channelNames[i] = chan.name;
          ++i;
        }
      }
      userChansProp.set(channelNames);
      
    }
    
    config.save();    
  }

  private List<TileHyperCube> innerGetCubesForChannel(Channel channel) {
    if(channel == null) {
      return null;
    }
    List<TileHyperCube> result = channelMapping.get(channel);
    if (result == null) {
      result = new ArrayList<TileHyperCube>();
      channelMapping.put(channel, result);
    }
    return result;
  }

  public List<Channel> getPublicChannels() {    
    return publicChannelsRO;
  }

}
