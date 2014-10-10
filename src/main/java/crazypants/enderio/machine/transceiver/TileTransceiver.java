package crazypants.enderio.machine.transceiver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.PoweredTask;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.PowerDistributor;
import crazypants.vecmath.VecmathUtil;

public class TileTransceiver extends AbstractPoweredTaskEntity {

  //Power will only be sent to other transceivers is the buffer is higher than this amount
  private static final float MIN_POWER_TO_SEND = 0.5f;
  
  private final EnumMap<ChannelType, List<Channel>> sendChannels = new EnumMap<ChannelType, List<Channel>>(ChannelType.class);
  private final EnumMap<ChannelType, List<Channel>> recieveChannels = new EnumMap<ChannelType, List<Channel>>(ChannelType.class);

  private ICapacitor capacitor = new BasicCapacitor(Config.transceiverMaxIoRF * 2, 100000, Config.transceiverMaxIoRF);
  private boolean sendChannelsDirty = false;
  private boolean recieveChannelsDirty = false;
  private boolean registered = false;

  private PowerDistributor powerDistributor;
  
  public TileTransceiver() {
    super(new SlotDefinition(0, 0, 0));
    for (ChannelType type : ChannelType.values()) {
      sendChannels.put(type, new ArrayList<Channel>());
      recieveChannels.put(type, new ArrayList<Channel>());
    }
    currentTask = new ContinuousTask(Config.transceiverUpkeepCostRF);  
  }
  
  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {
    boolean res = super.processTasks(redstoneChecksPassed);
    if(!redstoneChecksPassed) {      
      return res;
    }  
    
    processPower();    
    return res;
  }

  @Override
  public void updateEntity() {
    if(!registered && worldObj != null && !worldObj.isRemote) {
      ServerChannelRegister.instance.register(this);
      registered = true;
      removeUnregsiteredChannels(sendChannels);
      removeUnregsiteredChannels(recieveChannels);
    }
    super.updateEntity();

    if(worldObj != null && !worldObj.isRemote) {
      if(sendChannelsDirty) {
        PacketHandler.sendToAllAround(new PacketSendRecieveChannelList(this, true), this, 256);
        sendChannelsDirty = false;
      }
      if(recieveChannelsDirty) {
        PacketHandler.sendToAllAround(new PacketSendRecieveChannelList(this, false), this, 256);
        recieveChannelsDirty = false;
      }
    }
  }
  
  @Override
  public void invalidate() {
    super.invalidate();
    if(registered && worldObj != null && !worldObj.isRemote) {
      ServerChannelRegister.instance.dergister(this);
      registered = false;
    }
  }

  @Override
  public void onChunkUnload() {
    super.onChunkUnload();
    if(registered && worldObj != null && !worldObj.isRemote) {
      ServerChannelRegister.instance.dergister(this);
      registered = false;
    }
  }

  private void removeUnregsiteredChannels(EnumMap<ChannelType, List<Channel>> channels) {
    List<Channel> toRemove = new ArrayList<Channel>();
    for (List<Channel> chans : channels.values()) {
      for (Channel chan : chans) {
        if(!ServerChannelRegister.instance.getChannelsForType(chan.getType()).contains(chan)) {
          toRemove.add(chan);
        }
      }
    }
    for (Channel chan : toRemove) {
      removeChannel(chan, channels);
    }
  }
  
  @Override
  public String getMachineName() {
    return ModObject.blockTransceiver.unlocalisedName;
  }
    
  @Override
  public boolean isActive() {
    return hasPower();    
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return true;
  }

  @Override
  public ICapacitor getCapacitor() {
    return capacitor;
  }
  
  public int getPowerUsePerTick() {
    return Config.transceiverUpkeepCostRF;
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
      if(channels == sendChannels) {
        sendChannelsDirty = true;
      } else {
        recieveChannelsDirty = true;
      }
    }

  }

  private void removeChannel(Channel channel, EnumMap<ChannelType, List<Channel>> channels) {
    if(channel == null) {
      return;
    }
    List<Channel> chans = channels.get(channel.getType());
    if(chans.contains(channel)) {
      chans.remove(channel);
      if(channels == sendChannels) {
        sendChannelsDirty = true;
      } else {
        recieveChannelsDirty = true;
      }
    }
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);

    readChannels(nbtRoot, sendChannels, "sendChannels");
    readChannels(nbtRoot, recieveChannels, "recieveChannels");
    
    currentTask = new ContinuousTask(Config.transceiverUpkeepCostRF);

  }
    
  static void readChannels(NBTTagCompound nbtRoot, EnumMap<ChannelType, List<Channel>> readInto, String key) {
    
    for (ChannelType type : ChannelType.values()) {
      readInto.get(type).clear();
    }

    if(!nbtRoot.hasKey(key)) {
      return;
    }
    NBTTagList tags = (NBTTagList) nbtRoot.getTag(key);
    for (int i = 0; i < tags.tagCount(); i++) {
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

  static NBTTagList createTagList(EnumMap<ChannelType, List<Channel>> chans) {
    NBTTagList res = new NBTTagList();
    for (List<Channel> chanList : chans.values()) {
      for (Channel channel : chanList) {
        NBTTagCompound chanTag = new NBTTagCompound();
        channel.writeToNBT(chanTag);
        res.appendTag(chanTag);
      }
    }
    return res;
  }

  void setSendChannels(EnumMap<ChannelType, List<Channel>> channels) {
    for(ChannelType type : ChannelType.values()) {
      sendChannels.get(type).clear();
      sendChannels.get(type).addAll(channels.get(type));
    }   
  }

  void setRecieveChannels(EnumMap<ChannelType, List<Channel>> channels) {
    for(ChannelType type : ChannelType.values()) {
      recieveChannels.get(type).clear();
      recieveChannels.get(type).addAll(channels.get(type));
    }
  }
  
  EnumMap<ChannelType, List<Channel>> getSendChannels() {    
    return sendChannels;
  }
  
  EnumMap<ChannelType, List<Channel>> getReceiveChannels() {    
    return recieveChannels;
  }
  
  //Power Handling
  
  private void processPower() {
    List<Channel> sendTo = getSendChannels(ChannelType.POWER);    
    int canSend = getMaxSendableEnergy();
    if(canSend > 0 && !sendTo.isEmpty()) {      
      for(int i=0;i<sendTo.size() && canSend > 0;i++) {
        ServerChannelRegister.instance.sendPower(this, canSend, sendTo.get(i));
        canSend = getMaxSendableEnergy();
      }
    }    
    canSend = getMaxSendableEnergy();
    if(canSend > 0 && !getRecieveChannels(ChannelType.POWER).isEmpty()) {
      if(powerDistributor == null) {
        powerDistributor = new PowerDistributor(getLocation());
      }
      int used = powerDistributor.transmitEnergy(worldObj, canSend);
      usePower(used);
    }
  }

  private int getMaxSendableEnergy() {
    return getEnergyStored() - (int)(MIN_POWER_TO_SEND * getMaxEnergyStored());
  }
  
  private float getEnergyStoredRatio() {
    return (float)getEnergyStored() / getMaxEnergyStored();
  }
  
  @Override
  public void onNeighborBlockChange(Block blockId) {    
    super.onNeighborBlockChange(blockId);
    if(powerDistributor != null) {
      powerDistributor.neighboursChanged();
    }
  }
  
}
