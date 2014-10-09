package crazypants.enderio.machine.transceiver;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.SlotDefinition;

public class TileTransceiver extends AbstractPoweredTaskEntity {

  private final EnumMap<ChannelType, List<Channel>> sendChannels = new EnumMap<ChannelType, List<Channel>>(ChannelType.class);
  private final EnumMap<ChannelType, List<Channel>> recieveChannels = new EnumMap<ChannelType, List<Channel>>(ChannelType.class);
  
  private boolean channelsDirty = false;
  
  public TileTransceiver() {
    super(new SlotDefinition(0, 0, 0));
    for (ChannelType type : ChannelType.values()) {
      sendChannels.put(type, new ArrayList<Channel>());
      recieveChannels.put(type, new ArrayList<Channel>());
    }    
  }

  @Override
  public String getMachineName() {
    return ModObject.blockTransceiver.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return true;
  }
  
  public List<Channel> getSendChannels(ChannelType type) {
    return sendChannels.get(type);
  }
  
  public List<Channel> getRecieveChannels(ChannelType type) {
    return recieveChannels.get(type);
  }
  
  public void addSendChanel(Channel channel) {
    addChannel(channel, sendChannels);
  }
  
  public void addRecieveChanel(Channel channel) {
    addChannel(channel, recieveChannels);
  }
  
  public void removeSendChanel(Channel channel) {
    removeChannel(channel, sendChannels);
  }
  
  public void removeRecieveChanel(Channel channel) {
    removeChannel(channel, recieveChannels);
  }
  
  private void addChannel(Channel channel, EnumMap<ChannelType, List<Channel>> channels) {
    if(channel == null) {
      return;
    }
    List<Channel> chans = channels.get(channel.getType());
    if(!chans.contains(channel)) {
      chans.add(channel);
      channelsDirty = true;
    }
    
  }
  
  private void removeChannel(Channel channel, EnumMap<ChannelType, List<Channel>> channels) {
    if(channel == null) {
      return;
    }
    List<Channel> chans = channels.get(channel.getType());    
    chans.remove(channel);
    channelsDirty = true;    
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);

    readChannels(nbtRoot, sendChannels, "sendChannels");
    readChannels(nbtRoot, recieveChannels, "recieveChannels");
    
  }

  private void readChannels(NBTTagCompound nbtRoot, EnumMap<ChannelType, List<Channel>> readInto, String key) {
    readInto.clear();
    for (ChannelType type : ChannelType.values()) {
      readInto.put(type, new ArrayList<Channel>());      
    } 
    
    if(!nbtRoot.hasKey(key)) {
      return;
    }
    NBTTagList tags = (NBTTagList)nbtRoot.getTag(key);
    for(int i=0;i<tags.tagCount();i++) {
      NBTTagCompound chanelTag = tags.getCompoundTagAt(i);
      Channel channel = Channel.readFromNBT(chanelTag);
      if(channel != null) {
        readInto.get(channel.getType()).add(channel);
      }
    }
    
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {  
    super.writeCustomNBT(nbtRoot);
        
    NBTTagList channelTags = createTagList(sendChannels);
    nbtRoot.setTag("sendChannels", channelTags);
    
    channelTags = createTagList(recieveChannels);
    nbtRoot.setTag("recieveChannels", channelTags);
    
  }

  private NBTTagList createTagList(EnumMap<ChannelType, List<Channel>> chans) {    
    NBTTagList res = new NBTTagList();
    for(List<Channel> chanList : chans.values()) {
      for(Channel channel : chanList) {
        NBTTagCompound chanTag = new NBTTagCompound();
        channel.writeToNBT(chanTag);
        res.appendTag(chanTag);
      }
    }
    return res;
  }
  
  

}
