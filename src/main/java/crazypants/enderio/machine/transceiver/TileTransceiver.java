package crazypants.enderio.machine.transceiver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.ItemUtil;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.item.FilterRegister;
import crazypants.enderio.conduit.item.filter.ItemFilter;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IItemBuffer;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.IInternalPowerHandler;
import crazypants.enderio.power.PowerDistributor;
import crazypants.enderio.rail.EnderRailController;

public class TileTransceiver extends AbstractPoweredTaskEntity implements IFluidHandler, IItemBuffer, IInternalPowerHandler {

  //Power will only be sent to other transceivers is the buffer is higher than this amount
  private static final float MIN_POWER_TO_SEND = 0.5f;

  private final SetMultimap<ChannelType, Channel> sendChannels = MultimapBuilder.enumKeys(ChannelType.class).hashSetValues().build();
  private final SetMultimap<ChannelType, Channel> recieveChannels = MultimapBuilder.enumKeys(ChannelType.class).hashSetValues().build();

  private final ICapacitor capacitor = new BasicCapacitor(0, Config.transceiverMaxIoRF * 2, 500000, Config.transceiverMaxIoRF);
  private boolean sendChannelsDirty = false;
  private boolean recieveChannelsDirty = false;
  private boolean registered = false;

  private Map<ForgeDirection, IFluidHandler> neighbourFluidHandlers = null;

  private PowerDistributor powerDistributor;

  private final EnderRailController railController;

  private boolean inFluidFill = false;
  private boolean inGetTankInfo = false;

  private ItemFilter sendItemFilter;
  private ItemFilter recieveItemFilter;

  private boolean bufferStacks = true;

  public TileTransceiver() {
    super(new SlotDefinition(8, 8, 0));
    currentTask = new ContinuousTask(Config.transceiverUpkeepCostRF);
    railController = new EnderRailController(this);

    sendItemFilter = new ItemFilter(true);
    recieveItemFilter = new ItemFilter(true);
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
  public void doUpdate() {
    if (!registered && !worldObj.isRemote) {
      ServerChannelRegister.instance.register(this);
      registered = true;
      removeUnregsiteredChannels(sendChannels);
      removeUnregsiteredChannels(recieveChannels);
    }
    super.doUpdate();

    if (!worldObj.isRemote) {
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

  private void removeUnregsiteredChannels(SetMultimap<ChannelType, Channel> chans) {
    List<Channel> toRemove = new ArrayList<Channel>();
    for (Channel chan : chans.values()) {
      if (!ServerChannelRegister.instance.getChannelsForType(chan.getType()).contains(chan)) {
        toRemove.add(chan);
      }
    }
    for (Channel chan : toRemove) {
      removeChannel(chan, chans);
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
  public ICapacitor getCapacitor() {
    return capacitor;
  }

  @Override
  public int getPowerUsePerTick() {
    return Config.transceiverUpkeepCostRF;
  }

  public Set<Channel> getSendChannels(ChannelType type) {
    return sendChannels.get(type);
  }

  public Set<Channel> getRecieveChannels(ChannelType type) {
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

  private void addChannel(Channel channel, SetMultimap<ChannelType, Channel> channels) {
    if (channel == null) {
      return;
    }
    Collection<Channel> chans = channels.get(channel.getType());
    if (chans.add(channel)) {
      if (channels == sendChannels) {
        sendChannelsDirty = true;
      } else {
        recieveChannelsDirty = true;
      }
    }
  }

  private void removeChannel(Channel channel, SetMultimap<ChannelType, Channel> channnels) {
    if(channel == null) {
      return;
    }
    Set<Channel> chans = channnels.get(channel.getType());
    if (chans.remove(channel)) {
      if (channnels == sendChannels) {
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

    if(nbtRoot.hasKey("sendItemFilter")) {
      NBTTagCompound itemRoot = nbtRoot.getCompoundTag("sendItemFilter");
      sendItemFilter.copyFrom((ItemFilter) FilterRegister.loadFilterFromNbt(itemRoot));
    }
    if(nbtRoot.hasKey("recieveItemFilter")) {
      NBTTagCompound itemRoot = nbtRoot.getCompoundTag("recieveItemFilter");
      recieveItemFilter.copyFrom((ItemFilter) FilterRegister.loadFilterFromNbt(itemRoot));
    }

    if(nbtRoot.hasKey("bufferStacks")) {
      bufferStacks = nbtRoot.getBoolean("bufferStacks");
    } else {
      bufferStacks = true;
    }
  }

  static void readChannels(NBTTagCompound nbtRoot, SetMultimap<ChannelType, Channel> channels, String key) {
    channels.clear();

    if(!nbtRoot.hasKey(key)) {
      return;
    }
    NBTTagList tags = (NBTTagList) nbtRoot.getTag(key);
    for (int i = 0; i < tags.tagCount(); i++) {
      NBTTagCompound chanelTag = tags.getCompoundTagAt(i);
      Channel channel = Channel.readFromNBT(chanelTag);
      if(channel != null) {
        channels.put(channel.getType(), channel);
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

    if(sendItemFilter != null) {
      NBTTagCompound itemRoot = new NBTTagCompound();
      FilterRegister.writeFilterToNbt(sendItemFilter, itemRoot);
      nbtRoot.setTag("sendItemFilter", itemRoot);
    }
    if(recieveItemFilter != null) {
      NBTTagCompound itemRoot = new NBTTagCompound();
      FilterRegister.writeFilterToNbt(recieveItemFilter, itemRoot);
      nbtRoot.setTag("recieveItemFilter", itemRoot);
    }

    nbtRoot.setBoolean("bufferStacks", bufferStacks);
  }

  static NBTTagList createTagList(SetMultimap<ChannelType, Channel> channels) {
    NBTTagList res = new NBTTagList();
    for (Channel channel : channels.values()) {
      NBTTagCompound chanTag = new NBTTagCompound();
      channel.writeToNBT(chanTag);
      res.appendTag(chanTag);
    }
    return res;
  }

  void setSendChannels(Multimap<? extends ChannelType, ? extends Channel> channels) {
    sendChannels.clear();
    sendChannels.putAll(channels);
  }

  void setRecieveChannels(Multimap<? extends ChannelType, ? extends Channel> channels) {
    recieveChannels.clear();
    recieveChannels.putAll(channels);
  }

  SetMultimap<ChannelType, Channel> getSendChannels() {
    return sendChannels;
  }

  SetMultimap<ChannelType, Channel> getReceiveChannels() {
    return recieveChannels;
  }

  //---------------- Power Handling

  @Override
  public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
    return 0;
  }

  private void processPower() {
    Set<Channel> sendTo = getSendChannels(ChannelType.POWER);
    int canSend = getMaxSendableEnergy();
    if(canSend > 0 && !sendTo.isEmpty()) {
      Iterator<Channel> iter = sendTo.iterator();
      while (canSend > 0 && iter.hasNext()) {
        ServerChannelRegister.instance.sendPower(this, canSend, iter.next());
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

  private boolean hasRecieveChannel(Set<Channel> channels, ChannelType type) {
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
    if(inFluidFill) {
      return false;
    }
    try {
      inFluidFill = true;
      if(getSendChannels(ChannelType.FLUID).isEmpty()) {
        return false;
      }
      return ServerChannelRegister.instance.canFill(this, getSendChannels(ChannelType.FLUID), fluid);
    } finally {
      inFluidFill = false;
    }
  }

  public boolean canReceive(Set<Channel> channels, Fluid fluid) {
    if(inFluidFill) {
      return false;
    }

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
    if(inFluidFill) {
      return 0;
    }
    try {
      inFluidFill = true;
      if(getSendChannels(ChannelType.FLUID).isEmpty() || !redstoneCheckPassed || !getIoMode(from).canRecieveInput()) {
        return 0;
      }
      return ServerChannelRegister.instance.fill(this, getSendChannels(ChannelType.FLUID), resource, doFill);
    } finally {
      inFluidFill = false;
    }
  }

  public int recieveFluid(Set<Channel> channels, FluidStack resource, boolean doFill) {
    if(inFluidFill) {
      return 0;
    }
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
    if(inGetTankInfo) {
      return new FluidTankInfo[0];
    }
    try {
      inGetTankInfo = true;
      return ServerChannelRegister.instance.getTankInfoForChannels(this, getSendChannels(ChannelType.FLUID));
    } finally {
      inGetTankInfo = false;
    }
  }

  public void getRecieveTankInfo(List<FluidTankInfo> infos, Set<Channel> channels) {
    if(inGetTankInfo) {
      return;
    }
    try {
      inGetTankInfo = true;
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
    } finally {
      inGetTankInfo = false;
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

  @Override
  public int getInventoryStackLimit() {
    return bufferStacks ? 64 : 1;
  }

  @Override
  public boolean isBufferStacks() {
    return bufferStacks;
  }

  @Override
  public void setBufferStacks(boolean bufferStacks) {
    this.bufferStacks = bufferStacks;
  }

  private void processItems() {
    Set<Channel> sendItemChannels = getSendChannels(ChannelType.ITEM);
    if(!sendItemChannels.isEmpty()) {
      for (int i = slotDefinition.minInputSlot; i <= slotDefinition.maxInputSlot; i++) {
        ItemStack toSend = getStackInSlot(i);
        if(toSend != null) {
          ServerChannelRegister.instance.sendItem(this, sendItemChannels, i, toSend);
        }
      }
    }
  }

  public ItemFilter getSendItemFilter() {
    return sendItemFilter;
  }

  public ItemFilter getReceiveItemFilter() {
    return recieveItemFilter;
  }

  public ItemFilter getRecieveItemFilter() {
    return recieveItemFilter;
  }

  public void setRecieveItemFilter(ItemFilter recieveItemFilter) {
    this.recieveItemFilter = recieveItemFilter;
  }

  public void setSendItemFilter(ItemFilter sendItemFilter) {
    this.sendItemFilter = sendItemFilter;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int slot, ItemStack itemstack) {
    if(itemstack == null) {
      return false;
    }
    if(slotDefinition.isInputSlot(slot)) {
      if(!getSendItemFilter().doesItemPassFilter(null, itemstack)) {
        return false;
      }
    } else if(slotDefinition.isOutputSlot(slot)) {
      if(!getReceiveItemFilter().doesItemPassFilter(null, itemstack)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean canInsertItem(int slot, ItemStack itemstack, int j) {
    if(itemstack == null) {
      return false;
    }

    //only allow 1 stack per type
    if(slotDefinition.isInputSlot(slot)) {

      Set<Channel> chans = getSendChannels().get(ChannelType.ITEM);
      if(chans == null || chans.size() == 0) {
        return false;
      }
      if(!getSendItemFilter().doesItemPassFilter(null, itemstack)) {
        return false;
      }

      for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
        if(i != slot) {
          if(ItemUtil.areStacksEqual(itemstack, getStackInSlot(i))) {
            return false;
          }
        }
      }
    } else if(slotDefinition.isOutputSlot(slot)) {

      if(!getRecieveItemFilter().doesItemPassFilter(null, itemstack)) {
        return false;
      }
      for (int i = slotDefinition.getMinOutputSlot(); i <= slotDefinition.getMaxOutputSlot(); i++) {
        if(i != slot) {
          if(ItemUtil.areStacksEqual(itemstack, getStackInSlot(i))) {
            return false;
          }
        }
      }
    }
    return super.canInsertItem(slot, itemstack, j);
  }

}
