package crazypants.enderio.machine.transceiver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.PoweredTask;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.PowerDistributor;
import crazypants.enderio.rail.EnderRailController;
import crazypants.util.FluidUtil;
import crazypants.util.ItemUtil;
import crazypants.vecmath.VecmathUtil;

public class TileTransceiver extends AbstractPoweredTaskEntity implements IFluidHandler {

  //Power will only be sent to other transceivers is the buffer is higher than this amount
  private static final float MIN_POWER_TO_SEND = 0.5f;

  private final EnumMap<ChannelType, List<Channel>> sendChannels = new EnumMap<ChannelType, List<Channel>>(ChannelType.class);
  private final EnumMap<ChannelType, List<Channel>> recieveChannels = new EnumMap<ChannelType, List<Channel>>(ChannelType.class);

  private ICapacitor capacitor = new BasicCapacitor(Config.transceiverMaxIoRF * 2, 500000, Config.transceiverMaxIoRF);
  private boolean sendChannelsDirty = false;
  private boolean recieveChannelsDirty = false;
  private boolean registered = false;

  private Map<ForgeDirection, IFluidHandler> neighbourFluidHandlers = null;

  private PowerDistributor powerDistributor;
  
  private final EnderRailController railController;

  public TileTransceiver() {
    super(new SlotDefinition(8, 8, 0));
    for (ChannelType type : ChannelType.values()) {
      sendChannels.put(type, new ArrayList<Channel>());
      recieveChannels.put(type, new ArrayList<Channel>());
    }
    currentTask = new ContinuousTask(Config.transceiverUpkeepCostRF);
    railController = new EnderRailController(this);
  }  
  
  public EnderRailController getRailController() {
    return railController;
  }

  public boolean isRedstoneChecksPassed() {
    return redstoneCheckPassed;
  }

  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {
    boolean res = super.processTasks(redstoneChecksPassed);
    if(!redstoneChecksPassed) {
      return res;
    }

    //NB: Fluid done synchronously
    processPower();
    processItems();
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
      railController.doTick();
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
    railController.readFromNBT(nbtRoot);
    currentTask = new ContinuousTask(Config.transceiverUpkeepCostRF);
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    railController.writeToNBT(nbtRoot);
  }
  
  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);    
    readChannels(nbtRoot, sendChannels, "sendChannels");
    readChannels(nbtRoot, recieveChannels, "recieveChannels");
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
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    
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
    for (ChannelType type : ChannelType.values()) {
      sendChannels.get(type).clear();
      sendChannels.get(type).addAll(channels.get(type));
    }
  }

  void setRecieveChannels(EnumMap<ChannelType, List<Channel>> channels) {
    for (ChannelType type : ChannelType.values()) {
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

  //---------------- Power Handling

  private void processPower() {
    List<Channel> sendTo = getSendChannels(ChannelType.POWER);
    int canSend = getMaxSendableEnergy();
    if(canSend > 0 && !sendTo.isEmpty()) {
      for (int i = 0; i < sendTo.size() && canSend > 0; i++) {
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
    return getEnergyStored() - (int) (MIN_POWER_TO_SEND * getMaxEnergyStored());
  }

  private float getEnergyStoredRatio() {
    return (float) getEnergyStored() / getMaxEnergyStored();
  }

  @Override
  public void onNeighborBlockChange(Block blockId) {
    super.onNeighborBlockChange(blockId);
    if(powerDistributor != null) {
      powerDistributor.neighboursChanged();
    }
    neighbourFluidHandlers = null;
  }

  private boolean hasRecieveChannel(List<Channel> channels, ChannelType type) {
    boolean hasChannel = false;
    for (Channel chan : channels) {
      if(getRecieveChannels(type).contains(chan)) {
        hasChannel = true;
        break;
      }
    }
    return hasChannel;
  }

  //----------------  Fluid Handling

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    if(getSendChannels(ChannelType.FLUID).isEmpty()) {
      return false;
    }
    return ServerChannelRegister.instance.canFill(this, getSendChannels(ChannelType.FLUID), fluid);
  }

  public boolean canReceive(List<Channel> channels, Fluid fluid) {
    if(!hasRecieveChannel(channels, ChannelType.FLUID)) {
      return false;
    }
    Map<ForgeDirection, IFluidHandler> handlers = getNeighbouringFluidHandlers();
    for (Entry<ForgeDirection, IFluidHandler> entry : handlers.entrySet()) {
      if(entry.getValue().canFill(entry.getKey().getOpposite(), fluid)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(getSendChannels(ChannelType.FLUID).isEmpty() || !redstoneCheckPassed || !getIoMode(from).canRecieveInput()) {
      return 0;
    }
    return ServerChannelRegister.instance.fill(this, getSendChannels(ChannelType.FLUID), resource, doFill);
  }

  public int recieveFluid(List<Channel> channels, FluidStack resource, boolean doFill) {
    if(!hasRecieveChannel(channels, ChannelType.FLUID) || !redstoneCheckPassed) {
      return 0;
    }
    Map<ForgeDirection, IFluidHandler> handlers = getNeighbouringFluidHandlers();
    for (Entry<ForgeDirection, IFluidHandler> entry : handlers.entrySet()) {
      IoMode mode = getIoMode(entry.getKey());
      if(mode.canOutput()) {
        int res = entry.getValue().fill(entry.getKey().getOpposite(), resource, doFill);
        if(res > 0) {
          return res;
        }
      }
    }
    return 0;
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    return ServerChannelRegister.instance.getTankInfoForChannels(this, getSendChannels(ChannelType.FLUID));
  }

  public void getRecieveTankInfo(List<FluidTankInfo> infos, List<Channel> channels) {
    if(!hasRecieveChannel(channels, ChannelType.FLUID)) {
      return;
    }
    Map<ForgeDirection, IFluidHandler> fluidHandlers = getNeighbouringFluidHandlers();
    for (Entry<ForgeDirection, IFluidHandler> entry : fluidHandlers.entrySet()) {
      FluidTankInfo[] tanks = entry.getValue().getTankInfo(entry.getKey().getOpposite());
      if(tanks != null) {
        for (FluidTankInfo info : tanks) {
          infos.add(info);
        }
      }
    }
  }

  Map<ForgeDirection, IFluidHandler> getNeighbouringFluidHandlers() {
    if(neighbourFluidHandlers == null) {
      neighbourFluidHandlers = FluidUtil.getNeighbouringFluidHandlers(worldObj, getLocation());
    }
    return neighbourFluidHandlers;
  }

  //Pulling liquids not supported
  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    return null;
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    return null;
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    return false;
  }

  //---------------- item handling
  private void processItems() {
    List<Channel> sendItemChannels = getSendChannels(ChannelType.ITEM);
    if(!sendItemChannels.isEmpty()) {
      for (int i = slotDefinition.minInputSlot; i <= slotDefinition.maxInputSlot; i++) {
        ItemStack toSend = getStackInSlot(i);
        if(toSend != null) {
          ServerChannelRegister.instance.sendItem(this, sendItemChannels, i, toSend);
        }
      }
    }
  }

  @Override
  public boolean canInsertItem(int slot, ItemStack itemstack, int j) {
    if(itemstack == null) {
      return false;
    }

    //only allow 1 stack per type
    if(slotDefinition.isInputSlot(slot)) {
      for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
        if(i != slot) {
          if(ItemUtil.areStackTypesEqual(itemstack, getStackInSlot(i))) {
            return false;
          }
        }
      }
    } else if(slotDefinition.isOutputSlot(slot)) {
      for (int i = slotDefinition.getMinOutputSlot(); i <= slotDefinition.getMaxOutputSlot(); i++) {
        if(i != slot) {
          if(ItemUtil.areStackTypesEqual(itemstack, getStackInSlot(i))) {
            return false;
          }
        }
      }
    }    
    return super.canInsertItem(slot, itemstack, j);
  }

}
