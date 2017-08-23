package crazypants.enderio.machine.capbank;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyContainerItem;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.network.CapBankClientNetwork;
import crazypants.enderio.machine.capbank.network.ClientNetworkManager;
import crazypants.enderio.machine.capbank.network.EnergyReceptor;
import crazypants.enderio.machine.capbank.network.ICapBankNetwork;
import crazypants.enderio.machine.capbank.network.InventoryImpl;
import crazypants.enderio.machine.capbank.network.NetworkUtil;
import crazypants.enderio.machine.capbank.packet.PacketNetworkIdRequest;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.IPowerStorage;
import crazypants.enderio.power.PowerHandlerUtil;

public class TileCapBank extends TileEntityEio implements IInternalPowerReceiver, IInventory, IIoConfigurable, IPowerStorage {

  private Map<ForgeDirection, IoMode> faceModes;
  private Map<ForgeDirection, InfoDisplayType> faceDisplayTypes;

  private CapBankType type;

  private int energyStored;
  private int maxInput = -1;
  private int maxOutput = -1;

  private RedstoneControlMode inputControlMode = RedstoneControlMode.IGNORE;
  private RedstoneControlMode outputControlMode = RedstoneControlMode.IGNORE;

  private boolean redstoneStateDirty = true;
  private boolean isRecievingRedstoneSignal;

  private final List<EnergyReceptor> receptors = new ArrayList<EnergyReceptor>();
  private boolean receptorsDirty = true;

  private ICapBankNetwork network;

  private final ItemStack[] inventory;

  public TileCapBank() {
    inventory = new ItemStack[4];
  }

  //Client side refernce to look up network state
  private int networkId = -1;
  private int idRequestTimer = 0;

  private boolean dropItems;
  private boolean displayTypesDirty;
  private boolean revalidateDisplayTypes;
  private int lastComparatorState;

  public CapBankType getType() {
    if(type == null) {
      type = CapBankType.getTypeFromMeta(getBlockMetadata());
    }
    return type;
  }

  public void onNeighborBlockChange(Block blockId) {
    redstoneStateDirty = true;
    revalidateDisplayTypes = true;
    // directly call updateReceptors() to work around issue #1433
    updateReceptors();
  }

  //---------- Multiblock

  @SideOnly(Side.CLIENT)
  public void setNetworkId(int networkId) {
    this.networkId = networkId;
    if(networkId != -1) {
      ClientNetworkManager.getInstance().addToNetwork(networkId, this);
    }
  }

  @SideOnly(Side.CLIENT)
  public int getNetworkId() {
    return networkId;
  }

  public ICapBankNetwork getNetwork() {
    return network;
  }

  public boolean setNetwork(ICapBankNetwork network) {
    this.network = network;
    return true;
  }

  public boolean canConnectTo(TileCapBank cap) {
    CapBankType t = getType();
    return t.isMultiblock() && t.getUid().equals(cap.getType().getUid());
  }

  @Override
  public void onChunkUnload() {
    if(network != null) {
      network.destroyNetwork();
    }
  }

  @Override
  public void invalidate() {
    super.invalidate();
    if(network != null) {
      network.destroyNetwork();
    }
  }

  public void moveInventoryToNetwork() {
    if(network == null) {
      return;
    }
    if(network.getInventory().getCapBank() == this && !InventoryImpl.isInventoryEmtpy(inventory)) {
      for (TileCapBank cb : network.getMembers()) {
        if(cb != this) {
          for (int i = 0; i < inventory.length; i++) {
            cb.inventory[i] = inventory[i];
            inventory[i] = null;
          }
          network.getInventory().setCapBank(cb);
          break;
        }
      }
    }
  }

  public void onBreakBlock() {
    //If we are holding the networks inventory when we care broken, tranfer it to another member of the network
    moveInventoryToNetwork();
  }

  @Override
  public void doUpdate() {
    if(worldObj.isRemote) {
      if(networkId == -1) {
        if(idRequestTimer <= 0) {
          PacketHandler.INSTANCE.sendToServer(new PacketNetworkIdRequest(this));
          idRequestTimer = 5;
        } else {
          --idRequestTimer;
        }
      }
      return;
    }
    updateNetwork(worldObj);
    if(network == null) {
      return;
    }

    if(redstoneStateDirty) {
      int sig = worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord);
      boolean recievingSignal = sig > 0;
      network.updateRedstoneSignal(this, recievingSignal);
      redstoneStateDirty = false;
    }

    if(receptorsDirty) {
      updateReceptors();
    }
    if(revalidateDisplayTypes) {
      validateDisplayTypes();
      revalidateDisplayTypes = false;
    }
    if(displayTypesDirty) {
      displayTypesDirty = false;
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    // update any comparators, since they don't check themselves
    int comparatorState = getComparatorOutput();
    if(lastComparatorState != comparatorState) {
      worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
      lastComparatorState = comparatorState;
    }

    doDropItems();
  }

  private void updateNetwork(World world) {
    if(getNetwork() == null) {
      NetworkUtil.ensureValidNetwork(this);
    }
    if(getNetwork() != null) {
      getNetwork().onUpdateEntity(this);
    }

  }

  //---------- IO

  @Override
  public IoMode toggleIoModeForFace(ForgeDirection faceHit) {
    IPowerInterface rec = getReceptorForFace(faceHit);
    IoMode curMode = getIoMode(faceHit);
    if(curMode == IoMode.PULL) {
      setIoMode(faceHit, IoMode.PUSH, true);
      return IoMode.PUSH;
    }
    if(curMode == IoMode.PUSH) {
      setIoMode(faceHit, IoMode.DISABLED, true);
      return IoMode.DISABLED;
    }
    if(curMode == IoMode.DISABLED) {
      if(rec == null || rec.getDelegate() instanceof IConduitBundle) {
        setIoMode(faceHit, IoMode.NONE, true);
        return IoMode.NONE;
      }
    }
    setIoMode(faceHit, IoMode.PULL, true);
    return IoMode.PULL;
  }

  @Override
  public boolean supportsMode(ForgeDirection faceHit, IoMode mode) {
    IPowerInterface rec = getReceptorForFace(faceHit);
    if(mode == IoMode.NONE) {
      return rec == null || rec.getDelegate() instanceof IConduitBundle;
    }
    return true;
  }

  @Override
  public void setIoMode(ForgeDirection faceHit, IoMode mode) {
    setIoMode(faceHit, mode, true);
  }

  public void setIoMode(ForgeDirection faceHit, IoMode mode, boolean updateReceptors) {
    if(mode == IoMode.NONE) {
      if(faceModes == null) {
        return;
      }
      faceModes.remove(faceHit);
      if(faceModes.isEmpty()) {
        faceModes = null;
      }
    } else {
      if(faceModes == null) {
        faceModes = new EnumMap<ForgeDirection, IoMode>(ForgeDirection.class);
      }
      faceModes.put(faceHit, mode);
    }
    if(updateReceptors) {
      validateModeForReceptor(faceHit);
      receptorsDirty = true;
    }
    if(worldObj != null) {
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
    }
  }

  public void setDefaultIoMode(ForgeDirection faceHit) {
    EnergyReceptor er = getEnergyReceptorForFace(faceHit);
    if(er == null || er.getConduit() != null) {
      setIoMode(faceHit, IoMode.NONE);
    } else if(er.getReceptor().isInputOnly()) {
      setIoMode(faceHit, IoMode.PUSH);
    } else if(er.getReceptor().isOutputOnly()) {
      setIoMode(faceHit, IoMode.PULL);
    } else {
      setIoMode(faceHit, IoMode.PUSH);
    }
  }

  @Override
  public void clearAllIoModes() {
    if(network != null) {
      for(TileCapBank cb : network.getMembers()) {
        cb.doClearAllIoModes();
      }
    } else {
      doClearAllIoModes();
    }
  }

  private void doClearAllIoModes() {
    for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      setDefaultIoMode(dir);
    }
  }

  @Override
  public IoMode getIoMode(ForgeDirection face) {
    if(faceModes == null) {
      return IoMode.NONE;
    }
    IoMode res = faceModes.get(face);
    if(res == null) {
      return IoMode.NONE;
    }
    return res;
  }

  //----- Info Display

  public boolean hasDisplayTypes() {
    return faceDisplayTypes != null;
  }

  public InfoDisplayType getDisplayType(ForgeDirection face) {
    if(faceDisplayTypes == null) {
      return InfoDisplayType.NONE;
    }
    InfoDisplayType res = faceDisplayTypes.get(face);
    return res == null ? InfoDisplayType.NONE : res;
  }

  public void setDisplayType(ForgeDirection face, InfoDisplayType type) {
    setDisplayType(face, type, true);
  }

  public void setDisplayType(ForgeDirection face, InfoDisplayType type, boolean markDirty) {
    if(type == null) {
      type = InfoDisplayType.NONE;
    }
    if(faceDisplayTypes == null && type == InfoDisplayType.NONE) {
      return;
    }
    InfoDisplayType cur = getDisplayType(face);
    if(cur == type) {
      return;
    }

    if(faceDisplayTypes == null) {
      faceDisplayTypes = new EnumMap<ForgeDirection, InfoDisplayType>(ForgeDirection.class);
    }

    if(type == InfoDisplayType.NONE) {
      faceDisplayTypes.remove(face);
    } else {
      faceDisplayTypes.put(face, type);
    }

    if(faceDisplayTypes.isEmpty()) {
      faceDisplayTypes = null;
    }
    displayTypesDirty = markDirty;
    invalidateDisplayInfoCache();
  }

  public void validateDisplayTypes() {
    if(faceDisplayTypes == null) {
      return;
    }
    List<ForgeDirection> reset = new ArrayList<ForgeDirection>();
    for (Entry<ForgeDirection, InfoDisplayType> entry : faceDisplayTypes.entrySet()) {
      BlockCoord bc = getLocation().getLocation(entry.getKey());
      Block block = worldObj.getBlock(bc.x, bc.y, bc.z);
      if(block != null && (block.isOpaqueCube() || block == EnderIO.blockCapBank)) {
        reset.add(entry.getKey());
      }
    }
    for (ForgeDirection dir : reset) {
      setDisplayType(dir, InfoDisplayType.NONE);
      setDefaultIoMode(dir);
    }
  }

  private void invalidateDisplayInfoCache() {
    if(network != null) {
      network.invalidateDisplayInfoCache();
    }
  }

  //----------- rendering

  @Override
  public boolean shouldRenderInPass(int pass) {
    if(faceDisplayTypes == null) {
      return false;
    }
    return pass == 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getRenderBoundingBox() {
    if(!type.isMultiblock() || !(network instanceof CapBankClientNetwork)) {
      return super.getRenderBoundingBox();
    }

    int minX = xCoord;
    int minY = yCoord;
    int minZ = zCoord;
    int maxX = xCoord+1;
    int maxY = yCoord+1;
    int maxZ = zCoord+1;

    if(faceDisplayTypes != null) {
      CapBankClientNetwork cn = (CapBankClientNetwork)network;
      if(faceDisplayTypes.get(ForgeDirection.NORTH) == InfoDisplayType.IO) {
        CapBankClientNetwork.IOInfo info = cn.getIODisplayInfo(xCoord, yCoord, zCoord, ForgeDirection.NORTH);
        maxX = Math.max(maxX, xCoord +     info.width);
        minY = Math.min(minY, yCoord + 1 - info.height);
      }
      if(faceDisplayTypes.get(ForgeDirection.SOUTH) == InfoDisplayType.IO) {
        CapBankClientNetwork.IOInfo info = cn.getIODisplayInfo(xCoord, yCoord, zCoord, ForgeDirection.SOUTH);
        minX = Math.min(minX, xCoord + 1 - info.width);
        minY = Math.min(minY, yCoord + 1 - info.height);
      }
      if(faceDisplayTypes.get(ForgeDirection.EAST) == InfoDisplayType.IO) {
        CapBankClientNetwork.IOInfo info = cn.getIODisplayInfo(xCoord, yCoord, zCoord, ForgeDirection.EAST);
        maxZ = Math.max(maxZ, zCoord +     info.width);
        minY = Math.min(minY, yCoord + 1 - info.height);
      }
      if(faceDisplayTypes.get(ForgeDirection.WEST) == InfoDisplayType.IO) {
        CapBankClientNetwork.IOInfo info = cn.getIODisplayInfo(xCoord, yCoord, zCoord, ForgeDirection.WEST);
        minZ = Math.min(minZ, zCoord + 1 - info.width);
        minY = Math.min(minY, yCoord + 1 - info.height);
      }
    }

    return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
  }

  //----------- Redstone

  public RedstoneControlMode getInputControlMode() {
    return inputControlMode;
  }

  public void setInputControlMode(RedstoneControlMode inputControlMode) {
    this.inputControlMode = inputControlMode;
  }

  public RedstoneControlMode getOutputControlMode() {
    return outputControlMode;
  }

  public void setOutputControlMode(RedstoneControlMode outputControlMode) {
    this.outputControlMode = outputControlMode;
  }

  //----------- Power

  @Override
  public IPowerStorage getController() {
    return network;
  }

  @Override
  public long getEnergyStoredL() {
    if(network == null) {
      return getEnergyStored();
    }
    return network.getEnergyStoredL();
  }

  @Override
  public long getMaxEnergyStoredL() {
    if (network == null) {
      return getMaxEnergyStored();
    }
    return network.getMaxEnergyStoredL();
  }

  @Override
  public boolean isOutputEnabled(ForgeDirection direction) {
    IoMode mode = getIoMode(direction);
    return mode == IoMode.PUSH || mode == IoMode.NONE && isOutputEnabled();
  }

  private boolean isOutputEnabled() {
    if(network == null) {
      return true;
    }
    return network.isOutputEnabled();
  }

  @Override
  public boolean isInputEnabled(ForgeDirection direction) {
    IoMode mode = getIoMode(direction);
    return mode == IoMode.PULL || mode == IoMode.NONE && isInputEnabled();
  }

  private boolean isInputEnabled() {
    if(network == null) {
      return true;
    }
    return network.isInputEnabled();
  }

  @Override
  public boolean isNetworkControlledIo(ForgeDirection direction) {
    IoMode mode = getIoMode(direction);
    return mode == IoMode.NONE || mode == IoMode.PULL;
  }

  @Override
  public boolean isCreative() {
    return getType().isCreative();
  }

  public List<EnergyReceptor> getReceptors() {
    if(receptorsDirty) {
      updateReceptors();
    }
    return receptors;
  }

  private void updateReceptors() {

    if(network == null) {
      return;
    }
    network.removeReceptors(receptors);

    receptors.clear();
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      IPowerInterface pi = getReceptorForFace(dir);
      if(pi != null) {
        EnergyReceptor er = new EnergyReceptor(this, pi, dir);
        validateModeForReceptor(er);
        IoMode ioMode = getIoMode(dir);
        if(ioMode != IoMode.DISABLED && ioMode != IoMode.PULL) {
          receptors.add(er);
        }
      }
    }
    network.addReceptors(receptors);

    receptorsDirty = false;
  }

  private IPowerInterface getReceptorForFace(ForgeDirection faceHit) {
    BlockCoord checkLoc = new BlockCoord(this).getLocation(faceHit);
    TileEntity te = worldObj.getTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
    if(!(te instanceof TileCapBank)) {
      return PowerHandlerUtil.create(te);
    } else {
      TileCapBank other = (TileCapBank) te;
      if(other.getType() != getType()) {
        return PowerHandlerUtil.create(te);
      }
    }
    return null;
  }

  private EnergyReceptor getEnergyReceptorForFace(ForgeDirection dir) {
    IPowerInterface pi = getReceptorForFace(dir);
    if(pi == null || pi.getDelegate() instanceof TileCapBank) {
      return null;
    }
    return new EnergyReceptor(this, pi, dir);
  }

  private void validateModeForReceptor(ForgeDirection dir) {
    validateModeForReceptor(getEnergyReceptorForFace(dir));
  }
  
  private void validateModeForReceptor(EnergyReceptor er) {
    if (er == null) return;
    IoMode ioMode = getIoMode(er.getDir());
    if((ioMode == IoMode.PUSH_PULL || ioMode == IoMode.NONE) && er.getConduit() == null) {
      if(er.getReceptor().isOutputOnly()) {
        setIoMode(er.getDir(), IoMode.PULL, false);
      } else if (er.getReceptor().isInputOnly()) {
        setIoMode(er.getDir(), IoMode.PUSH, false);
      }
    }
    if(ioMode == IoMode.PULL && er.getReceptor().isInputOnly()) {
      setIoMode(er.getDir(), IoMode.PUSH, false);
    } else if(ioMode == IoMode.PUSH && er.getReceptor().isOutputOnly()) {
      setIoMode(er.getDir(), IoMode.DISABLED, false);
    }
  }

  @Override
  public void addEnergy(int energy) {
    if(network == null) {
      setEnergyStored(getEnergyStored() + energy);
    } else {
      network.addEnergy(energy);
    }
  }

  @Override
  public void setEnergyStored(int stored) {
    energyStored = MathHelper.clamp_int(stored, 0, getMaxEnergyStored());
  }

  @Override
  public int getEnergyStored() {
    return energyStored;
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return getType().getMaxEnergyStored();
  }

  @Override
  public int getMaxEnergyRecieved(ForgeDirection dir) {
    return getMaxInput();
  }

  @Override
  public int getMaxInput() {
    if(network == null) {
      return getType().getMaxIO();
    }
    return network.getMaxInput();
  }

  public void setMaxInput(int maxInput) {
    this.maxInput = maxInput;
  }

  public int getMaxInputOverride() {
    return maxInput;
  }

  @Override
  public int getMaxOutput() {
    if(network == null) {
      return getType().getMaxIO();
    }
    return network.getMaxOutput();
  }

  public void setMaxOutput(int maxOutput) {
    this.maxOutput = maxOutput;
  }

  public int getMaxOutputOverride() {
    return maxOutput;
  }

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    if(network == null) {
      return 0;
    }
    IoMode mode = getIoMode(from);
    if(mode == IoMode.DISABLED || mode == IoMode.PUSH) {
      return 0;
    }
    return network.receiveEnergy(maxReceive, simulate);
  }

//  @Override
//  public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
//    return 0;
//  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    return getType().getMaxEnergyStored();
  }

  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    return getIoMode(from) != IoMode.DISABLED;
  }

  public int getComparatorOutput() {
    double stored = getEnergyStored();
    return stored == 0 ? 0 : (int) (1 + stored / getMaxEnergyStored() * 14);
  }

  @Override
  public boolean displayPower() {
    return true;
  }

  //------------------- Inventory

  @Override
  public boolean isUseableByPlayer(EntityPlayer player) {
    return canPlayerAccess(player);
  }
  
  @Override
  public ItemStack getStackInSlot(int slot) {
    if(network == null) {
      return null;
    }
    return network.getInventory().getStackInSlot(slot);
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    if(network == null) {
      return null;
    }
    return network.getInventory().decrStackSize(fromSlot, amount);
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemstack) {
    if(network == null) {
      return;
    }
    network.getInventory().setInventorySlotContents(slot, itemstack);
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
    return null;
  }

  @Override
  public int getSizeInventory() {
    return 4;
  }

  @Override
  public String getInventoryName() {
    return EnderIO.blockCapacitorBank.getUnlocalizedName() + ".name";
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 1;
  }
  
  @Override
  public void openInventory() {
  }

  @Override
  public void closeInventory() {
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
    if(itemstack == null) {
      return false;
    }
    return itemstack.getItem() instanceof IEnergyContainerItem;
  }

  public ItemStack[] getInventory() {
    return inventory;
  }

  public void dropItems() {
    dropItems = true;
  }

  public void doDropItems() {
    if(!dropItems) {
      return;
    }
    Vector3d dropLocation;
    EntityPlayer player = worldObj.getClosestPlayer(xCoord, yCoord, zCoord, 32);
    if(player != null) {
      dropLocation = EntityUtil.getEntityPosition(player);
    } else {
      dropLocation = new Vector3d(xCoord, yCoord, zCoord);
    }
    Util.dropItems(worldObj, inventory, (int) dropLocation.x, (int) dropLocation.y, (int) dropLocation.z, false);
    for (int i = 0; i < inventory.length; i++) {
      inventory[i] = null;
    }
    dropItems = false;
  }

  //---------------- NBT

  @Override
  protected void writeCustomNBT(NBTTagCompound nbtRoot) {
    writeCommonNBT(nbtRoot);
  }

  //Values common to both item and block form
  public void writeCommonNBT(NBTTagCompound nbtRoot) {
    getType().writeTypeToNBT(nbtRoot);
    nbtRoot.setInteger(PowerHandlerUtil.STORED_ENERGY_NBT_KEY, energyStored);

    if(maxInput != -1) {
      nbtRoot.setInteger("maxInput", maxInput);
    }
    if(maxOutput != -1) {
      nbtRoot.setInteger("maxOutput", maxOutput);
    }
    if(inputControlMode != RedstoneControlMode.IGNORE) {
      nbtRoot.setShort("inputControlMode", (short) inputControlMode.ordinal());
    }
    if(outputControlMode != RedstoneControlMode.IGNORE) {
      nbtRoot.setShort("outputControlMode", (short) outputControlMode.ordinal());
    }

    //face modes
    if(faceModes != null) {
      nbtRoot.setByte("hasFaces", (byte) 1);
      for (Entry<ForgeDirection, IoMode> e : faceModes.entrySet()) {
        nbtRoot.setShort("face" + e.getKey().ordinal(), (short) e.getValue().ordinal());
      }
    }

    //display type
    if(faceDisplayTypes != null) {
      nbtRoot.setByte("hasDisplayTypes", (byte) 1);
      for (Entry<ForgeDirection, InfoDisplayType> e : faceDisplayTypes.entrySet()) {
        if(e.getValue() != InfoDisplayType.NONE) {
          nbtRoot.setShort("faceDisplay" + e.getKey().ordinal(), (short) e.getValue().ordinal());
        }
      }
    }

    boolean hasItems = false;
    NBTTagList itemList = new NBTTagList();
    for (int i = 0; i < inventory.length; i++) {
      if(inventory[i] != null) {
        hasItems = true;
        NBTTagCompound itemStackNBT = new NBTTagCompound();
        itemStackNBT.setByte("Slot", (byte) i);
        inventory[i].writeToNBT(itemStackNBT);
        itemList.appendTag(itemStackNBT);
      }
    }
    if(hasItems) {
      nbtRoot.setTag("Items", itemList);
    }
  }

  @Override
  protected void readCustomNBT(NBTTagCompound nbtRoot) {
    readCommonNBT(nbtRoot);
  }

  //Values common to both item and block form
  public void readCommonNBT(NBTTagCompound nbtRoot) {
    type = CapBankType.readTypeFromNBT(nbtRoot);
    energyStored = nbtRoot.getInteger(PowerHandlerUtil.STORED_ENERGY_NBT_KEY);
    setEnergyStored(energyStored); //Call this to clamp values in case config changed

    if(nbtRoot.hasKey("maxInput")) {
      maxInput = nbtRoot.getInteger("maxInput");
    } else {
      maxInput = -1;
    }
    if(nbtRoot.hasKey("maxOutput")) {
      maxOutput = nbtRoot.getInteger("maxOutput");
    } else {
      maxOutput = -1;
    }

    if(nbtRoot.hasKey("inputControlMode")) {
      inputControlMode = RedstoneControlMode.values()[nbtRoot.getShort("inputControlMode")];
    } else {
      inputControlMode = RedstoneControlMode.IGNORE;
    }
    if(nbtRoot.hasKey("outputControlMode")) {
      outputControlMode = RedstoneControlMode.values()[nbtRoot.getShort("outputControlMode")];
    } else {
      outputControlMode = RedstoneControlMode.IGNORE;
    }

    if(nbtRoot.hasKey("hasFaces")) {
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        String key = "face" + dir.ordinal();
        if(nbtRoot.hasKey(key)) {
          setIoMode(dir, IoMode.values()[nbtRoot.getShort(key)], false);
        }
      }
    } else {
      faceModes = null;
    }

    if(nbtRoot.hasKey("hasDisplayTypes")) {
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        String key = "faceDisplay" + dir.ordinal();
        if(nbtRoot.hasKey(key)) {
          setDisplayType(dir, InfoDisplayType.values()[nbtRoot.getShort(key)], false);
        }
      }
    } else {
      faceDisplayTypes = null;
    }

    for (int i = 0; i < inventory.length; i++) {
      inventory[i] = null;
    }

    if(nbtRoot.hasKey("Items")) {
      NBTTagList itemList = (NBTTagList) nbtRoot.getTag("Items");
      for (int i = 0; i < itemList.tagCount(); i++) {
        NBTTagCompound itemStack = itemList.getCompoundTagAt(i);
        byte slot = itemStack.getByte("Slot");
        if(slot >= 0 && slot < inventory.length) {
          inventory[slot] = ItemStack.loadItemStackFromNBT(itemStack);
        }
      }
    }
  }

}
