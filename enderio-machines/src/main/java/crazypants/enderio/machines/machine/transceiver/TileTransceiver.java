package crazypants.enderio.machines.machine.transceiver;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.IFluidWrapper;
import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.machine.task.ContinuousTask;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.power.PowerDistributor;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.transceiver.Channel;
import crazypants.enderio.base.transceiver.ChannelList;
import crazypants.enderio.base.transceiver.ChannelType;
import crazypants.enderio.base.transceiver.IChanneledMachine;
import crazypants.enderio.base.transceiver.ServerChannelRegister;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import static crazypants.enderio.machines.capacitor.CapacitorKey.TRANSCEIVER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.TRANSCEIVER_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.TRANSCEIVER_POWER_USE;

public class TileTransceiver extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity, IChanneledMachine {

  @Store
  private final ChannelList sendChannels = new ChannelList();
  @Store
  private final ChannelList recieveChannels = new ChannelList();

  private boolean sendChannelsDirty = false;
  private boolean recieveChannelsDirty = false;
  private boolean registered = false;

  private EnumMap<EnumFacing, IFluidHandler> neighbourFluidHandlers = null;

  private PowerDistributor powerDistributor;

  private boolean inFluidFill = false;
  private boolean inGetTankInfo = false;

  // @Store(handler = FilterHandler.class)
  // private IItemFilter sendItemFilter;
  //
  // @Store(handler = FilterHandler.class)
  // private IItemFilter recieveItemFilter;

  @Store
  private boolean bufferStacks = true;

  public TileTransceiver() {
    super(new SlotDefinition(8, 8, 1), TRANSCEIVER_POWER_INTAKE, TRANSCEIVER_POWER_BUFFER, TRANSCEIVER_POWER_USE);

    currentTask = new ContinuousTask(getPowerUsePerTick());

    // sendItemFilter = new ItemFilter(BasicFilterTypes.filterUpgradeAdvanced);
    // recieveItemFilter = new ItemFilter(BasicFilterTypes.filterUpgradeAdvanced);
    addICap(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facingIn -> new FluidCap(facingIn));
  }

  public boolean isRedstoneChecksPassed() {
    return redstoneCheckPassed;
  }

  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {
    boolean res = super.processTasks(redstoneChecksPassed);
    if (!redstoneChecksPassed) {
      return res;
    }

    // NB: Fluid done synchronously
    processPower();
    processItems();
    return res;
  }

  @Override
  public boolean shouldRenderInPass(int pass) {
    return pass == 1;
  }

  @Override
  public void doUpdate() {
    if (!registered && !world.isRemote) {
      TransceiverRegistry.INSTANCE.register(this);
      registered = true;
      removeUnregsiteredChannels(sendChannels);
      removeUnregsiteredChannels(recieveChannels);
    }
    super.doUpdate();

    if (!world.isRemote) {
      if (sendChannelsDirty) {
        PacketHandler.sendToAllAround(new PacketSendRecieveChannelList(this, true), this);
        sendChannelsDirty = false;
      }
      if (recieveChannelsDirty) {
        PacketHandler.sendToAllAround(new PacketSendRecieveChannelList(this, false), this);
        recieveChannelsDirty = false;
      }
    }
  }

  @Override
  public void invalidate() {
    super.invalidate();
    if (registered && !world.isRemote) {
      TransceiverRegistry.INSTANCE.dergister(this);
      registered = false;
    }
  }

  @Override
  public void onChunkUnload() {
    super.onChunkUnload();
    if (registered && !world.isRemote) {
      TransceiverRegistry.INSTANCE.dergister(this);
      registered = false;
    }
  }

  private void removeUnregsiteredChannels(ChannelList chans) {
    List<Channel> toRemove = new ArrayList<Channel>();
    for (Set<Channel> chan : chans.values()) {
      for (Channel channel : chan) {
        if (!ServerChannelRegister.instance.getChannelsForType(channel.getType()).contains(channel)) {
          toRemove.add(channel);
        }
      }
    }
    for (Channel chan : toRemove) {
      removeChannel(chan, chans);
    }
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.TRANSCEIVER;
  }

  @Override
  public boolean isActive() {
    return hasPower();
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull Set<Channel> getSendChannels(@Nonnull ChannelType type) {
    return sendChannels.get(type);
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull Set<Channel> getRecieveChannels(@Nonnull ChannelType type) {
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

  private void addChannel(Channel channel, ChannelList channels) {
    if (channels.add(channel)) {
      if (channels == sendChannels) {
        sendChannelsDirty = true;
      } else {
        recieveChannelsDirty = true;
      }
    }
  }

  private void removeChannel(Channel channel, ChannelList channnels) {
    if (channel == null) {
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
  protected void onAfterNbtRead() {
    super.onAfterNbtRead();
    currentTask = new ContinuousTask(getPowerUsePerTick());
  }

  static void readChannels(NBTTagCompound nbtRoot, ChannelList channels, @Nonnull String key) {
    channels.clear();
    ;

    if (!nbtRoot.hasKey(key)) {
      return;
    }
    NBTTagList tags = (NBTTagList) nbtRoot.getTag(key);
    for (int i = 0; i < tags.tagCount(); i++) {
      channels.add(Channel.readFromNBT(tags.getCompoundTagAt(i)));
    }
  }

  static @Nonnull NBTTagList createTagList(ChannelList channels) {
    NBTTagList res = new NBTTagList();
    for (Set<Channel> chan : channels.values()) {
      for (Channel channel : chan) {
        NBTTagCompound chanTag = new NBTTagCompound();
        channel.writeToNBT(chanTag);
        res.appendTag(chanTag);
      }
    }
    return res;
  }

  void setSendChannels(ChannelList channels) {
    sendChannels.clear();
    sendChannels.putAll(channels);
  }

  void setRecieveChannels(ChannelList channels) {
    recieveChannels.clear();
    recieveChannels.putAll(channels);
  }

  ChannelList getSendChannels() {
    return sendChannels;
  }

  ChannelList getReceiveChannels() {
    return recieveChannels;
  }

  // ---------------- Power Handling

  private void processPower() {
    Set<Channel> sendTo = getSendChannels(ChannelType.POWER);
    int canSend = getMaxSendableEnergy();
    if (canSend > 0 && !sendTo.isEmpty()) {
      Iterator<Channel> iter = sendTo.iterator();
      while (canSend > 0 && iter.hasNext()) {
        final Channel next = iter.next();
        if (next != null) {
          TransceiverRegistry.INSTANCE.sendPower(this, canSend, next);
          canSend = getMaxSendableEnergy();
        }
      }
    }
    canSend = getMaxSendableEnergy();
    if (canSend > 0 && !getRecieveChannels(ChannelType.POWER).isEmpty()) {
      if (powerDistributor == null) {
        powerDistributor = new PowerDistributor(getLocation());
      }
      int used = powerDistributor.transmitEnergy(world, canSend);
      usePower(used);
    }
  }

  private int getMaxSendableEnergy() {
    return getEnergyStored() - (int) (getInternalBufferRatio() * getMaxEnergyStored());
  }

  public float getInternalBufferRatio() {
    return CapacitorKey.TRANSCEIVER_POWER_BUFFER_RATIO.getFloat(getCapacitorData());
  }

  public float getSendBufferRatio() {
    return 1f - getInternalBufferRatio();
  }

  @Override
  public void onNeighborBlockChange(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos posIn, @Nonnull Block blockIn,
      @Nonnull BlockPos fromPos) {
    super.onNeighborBlockChange(state, worldIn, posIn, blockIn, fromPos);
    if (powerDistributor != null) {
      powerDistributor.neighboursChanged();
    }
    neighbourFluidHandlers = null;
  }

  private boolean hasRecieveChannel(@Nonnull Set<Channel> channels, @Nonnull ChannelType type) {
    boolean hasChannel = false;
    for (Channel chan : channels) {
      if (getRecieveChannels(type).contains(chan)) {
        hasChannel = true;
        break;
      }
    }
    return hasChannel;
  }

  // ---------------- Fluid Handling

  public boolean canReceive(@Nonnull Set<Channel> channels, @Nonnull Fluid fluid) {
    if (inFluidFill) {
      return false;
    }

    if (!hasRecieveChannel(channels, ChannelType.FLUID)) {
      return false;
    }
    FluidStack offer = new FluidStack(fluid, 1);
    Map<EnumFacing, IFluidWrapper> neighbours = FluidWrapper.wrapNeighbours(world, pos);
    for (Entry<EnumFacing, IFluidWrapper> entry : neighbours.entrySet()) {
      IoMode mode = getIoMode(entry.getKey());
      if (mode.canOutput() && entry.getValue().offer(offer) > 0) {
        return true;
      }
    }
    return false;
  }

  public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
    if (inFluidFill) {
      return 0;
    }
    try {
      inFluidFill = true;
      if (getSendChannels(ChannelType.FLUID).isEmpty() || !redstoneCheckPassed || !getIoMode(from).canRecieveInput()) {
        return 0;
      }
      return TransceiverRegistry.INSTANCE.fill(this, getSendChannels(ChannelType.FLUID), resource, doFill);
    } finally {
      inFluidFill = false;
    }
  }

  public int recieveFluid(@Nonnull Set<Channel> channels, @Nonnull FluidStack resource, boolean doFill) {
    if (inFluidFill) {
      return 0;
    }
    if (!hasRecieveChannel(channels, ChannelType.FLUID) || !redstoneCheckPassed) {
      return 0;
    }
    Map<EnumFacing, IFluidWrapper> neighbours = FluidWrapper.wrapNeighbours(world, pos);
    for (Entry<EnumFacing, IFluidWrapper> entry : neighbours.entrySet()) {
      IoMode mode = getIoMode(entry.getKey());
      if (mode.canOutput()) {
        int res = doFill ? entry.getValue().fill(resource) : entry.getValue().offer(resource);
        if (res > 0) {
          return res;
        }
      }
    }
    return 0;
  }

  public void getRecieveTankInfo(@Nonnull List<IFluidTankProperties> infos, @Nonnull Set<Channel> channels) {
    if (inGetTankInfo) {
      return;
    }
    try {
      inGetTankInfo = true;
      if (!hasRecieveChannel(channels, ChannelType.FLUID)) {
        return;
      }
      Map<EnumFacing, IFluidHandler> fluidHandlers = getNeighbouringFluidHandlers();
      for (Entry<EnumFacing, IFluidHandler> entry : fluidHandlers.entrySet()) {
        IFluidTankProperties[] tanks = entry.getValue().getTankProperties();
        if (tanks != null) {
          for (IFluidTankProperties info : tanks) {
            infos.add(info);
          }
        }
      }
    } finally {
      inGetTankInfo = false;
    }
  }

  Map<EnumFacing, IFluidHandler> getNeighbouringFluidHandlers() {
    if (neighbourFluidHandlers == null) {
      neighbourFluidHandlers = FluidUtil.getNeighbouringFluidHandlers(world, getPos());
    }
    return neighbourFluidHandlers;
  }

  private class FluidCap implements IFluidHandler {

    final EnumFacing capFacing;

    FluidCap(EnumFacing facing) {
      this.capFacing = facing;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
      return TileTransceiver.this.fill(capFacing, resource, doFill);
    }

    // Pulling liquids not supported

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
      return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
      return null;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
      if (inGetTankInfo) {
        return new IFluidTankProperties[0];
      }
      try {
        inGetTankInfo = true;
        return TransceiverRegistry.INSTANCE.getTankInfoForChannels(TileTransceiver.this, getSendChannels(ChannelType.FLUID));
      } finally {
        inGetTankInfo = false;
      }
    }
  }

  // ---------------- item handling

  @Override
  public int getInventoryStackLimit() {
    return bufferStacks ? 64 : 1;
  }

  public boolean isBufferStacks() {
    return bufferStacks;
  }

  public void setBufferStacks(boolean bufferStacks) {
    this.bufferStacks = bufferStacks;
  }

  private void processItems() {
    Set<Channel> sendItemChannels = getSendChannels(ChannelType.ITEM);
    if (!sendItemChannels.isEmpty()) {
      for (int i = slotDefinition.minInputSlot; i <= slotDefinition.maxInputSlot; i++) {
        ItemStack toSend = getStackInSlot(i);
        if (Prep.isValid(toSend)) {
          TransceiverRegistry.INSTANCE.sendItem(this, sendItemChannels, i, toSend);
        }
      }
    }
  }

  // public IItemFilter getSendItemFilter() {
  // return sendItemFilter;
  // }
  //
  // public IItemFilter getReceiveItemFilter() {
  // return recieveItemFilter;
  // }
  //
  // public IItemFilter getRecieveItemFilter() {
  // return recieveItemFilter;
  // }
  //
  // public void setRecieveItemFilter(IItemFilter filter) {
  // this.recieveItemFilter = filter;
  // }
  //
  // public void setSendItemFilter(IItemFilter filter) {
  // this.sendItemFilter = filter;
  // }

  @Override
  public boolean isMachineItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
    if (itemstack.isEmpty()) {
      return false;
    }
    // if (slotDefinition.isInputSlot(slot)) {
    // if (!getSendItemFilter().doesItemPassFilter(null, itemstack)) {
    // return false;
    // }
    // } else if (slotDefinition.isOutputSlot(slot)) {
    // if (!getReceiveItemFilter().doesItemPassFilter(null, itemstack)) {
    // return false;
    // }
    // }
    return true;
  }

  // TODO 1.11 we need this in a capability

  // @Override
  // public boolean canInsertItem(int slot, @Nonnull ItemStack itemstack, @Nonnull EnumFacing j) {
  // if (itemstack.isEmpty()) {
  // return false;
  // }
  //
  // // only allow 1 stack per type
  // if (slotDefinition.isInputSlot(slot)) {
  //
  // Set<Channel> chans = getSendChannels().get(ChannelType.ITEM);
  // if (chans == null || chans.size() == 0) {
  // return false;
  // }
  // if (!getSendItemFilter().doesItemPassFilter(null, itemstack)) {
  // return false;
  // }
  //
  // for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
  // if (i != slot) {
  // if (ItemUtil.areStacksEqual(itemstack, getStackInSlot(i))) {
  // return false;
  // }
  // }
  // }
  // } else if (slotDefinition.isOutputSlot(slot)) {
  // if (!getRecieveItemFilter().doesItemPassFilter(null, itemstack)) {
  // return false;
  // }
  // for (int i = slotDefinition.getMinOutputSlot(); i <= slotDefinition.getMaxOutputSlot(); i++) {
  // if (i != slot) {
  // if (ItemUtil.areStacksEqual(itemstack, getStackInSlot(i))) {
  // return false;
  // }
  // }
  // }
  // }
  // return super.canInsertItem(slot, itemstack, j);
  // }

  @Override
  public int usePower(int wantToUse) {
    return super.usePower(wantToUse);
  }

}
