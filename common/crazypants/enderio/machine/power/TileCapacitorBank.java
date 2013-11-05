package crazypants.enderio.machine.power;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import thermalexpansion.api.item.IChargeableItem;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;
import crazypants.util.Util;
import crazypants.vecmath.VecmathUtil;

public class TileCapacitorBank extends TileEntity implements IInternalPowerReceptor, IInventory {

  static final BasicCapacitor BASE_CAP = new BasicCapacitor(100, 250000);

  BlockCoord[] multiblock = null;

  private PowerHandler powerHandler;

  private float lastSyncPowerStored;

  private float storedEnergy;

  private int maxStoredEnergy;

  private int maxIO;

  private int maxInput;

  private int maxOutput;

  private boolean multiblockDirty = false;

  private RedstoneControlMode inputControlMode;

  private RedstoneControlMode outputControlMode;

  private boolean outputEnabled;

  private boolean inputEnabled;

  private final List<Receptor> receptors = new ArrayList<Receptor>();
  private ListIterator<Receptor> receptorIterator = receptors.listIterator();
  private boolean receptorsDirty = true;

  private PowerHandler disabledPowerHandler;

  private final ItemStack[] inventory;

  public TileCapacitorBank() {
    inventory = new ItemStack[4];
    storedEnergy = 0;
    inputControlMode = RedstoneControlMode.IGNORE;
    outputControlMode = RedstoneControlMode.IGNORE;
    maxStoredEnergy = BASE_CAP.getMaxEnergyStored();
    maxIO = BASE_CAP.getMaxEnergyExtracted();
    maxInput = maxIO;
    maxOutput = maxIO;
    updatePowerHandler();
  }

  @Override
  public void updateEntity() {

    if(worldObj == null) { // sanity check
      return;
    }

    if(worldObj.isRemote) {
      return;
    } // else is server, do all logic only on the server

    if(multiblockDirty) {
      formMultiblock();
      multiblockDirty = false;
    }

    if(!isContoller()) {
      return;
    }

    // do the required tick to keep BC API happy
    float stored = powerHandler.getEnergyStored();
    powerHandler.update();
    // do a dummy recieve of power to force the updating of what is an isn't a
    // power source as we rely on this
    // to make sure we dont both send and recieve to the same source
    powerHandler.getPowerReceiver().receiveEnergy(Type.STORAGE, 1, null);
    powerHandler.setEnergy(stored);

    boolean requiresClientSync = false;
    requiresClientSync = chargeItems(stored);

    boolean hasSignal = isRecievingRedstoneSignal();
    if(inputControlMode == RedstoneControlMode.IGNORE) {
      inputEnabled = true;
    } else if(inputControlMode == RedstoneControlMode.NEVER) {
      inputEnabled = false;
    } else {
      inputEnabled = (inputControlMode == RedstoneControlMode.ON && hasSignal) || (inputControlMode == RedstoneControlMode.OFF && !hasSignal);
    }
    if(outputControlMode == RedstoneControlMode.IGNORE) {
      outputEnabled = true;
    } else if(outputControlMode == RedstoneControlMode.NEVER) {
      outputEnabled = false;
    } else {
      outputEnabled = (outputControlMode == RedstoneControlMode.ON && hasSignal) || (outputControlMode == RedstoneControlMode.OFF && !hasSignal);
    }

    if(outputEnabled) {
      transmitEnergy();
    }

    storedEnergy = powerHandler.getEnergyStored();

    // Update if our power has changed by more than 0.5%
    requiresClientSync |= lastSyncPowerStored != storedEnergy && worldObj.getTotalWorldTime() % 21 == 0;

    if(requiresClientSync) {
      lastSyncPowerStored = storedEnergy;

      // this will cause 'getPacketDescription()' to be called and its result
      // will be sent to the PacketHandler on the other end of
      // client/server connection
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      onInventoryChanged();
    }

  }

  private boolean chargeItems(float stored) {
    boolean chargedItem = false;
    float available = Math.min(maxIO, stored);
    for (ItemStack item : inventory) {
      if(item != null && item.getItem() instanceof IChargeableItem && available > 0) {
        IChargeableItem chargable = (IChargeableItem) item.getItem();
        float max = chargable.getMaxEnergyStored(item);
        float cur = chargable.getEnergyStored(item);
        float canUse = Math.min(available, max - cur);

        if(cur < max) {

          float used = 0;
          if(chargable.getClass().getName().startsWith("appeng") || "appeng.common.base.AppEngMultiChargeable".equals(chargable.getClass().getName())) {
            //256 max limit          
            canUse = Math.min(canUse, 256);
            NBTTagCompound tc = item.getTagCompound();
            if(tc == null) {
              item.setTagCompound(tc = new NBTTagCompound());
            }
            double newVal = (cur + canUse) * 5.0;
            tc.setDouble("powerLevel", newVal);
            used = canUse;

          } else {
            used = chargable.receiveEnergy(item, canUse, true);
          }
          if(used > 0) {
            powerHandler.setEnergy(stored - used);
            chargedItem = true;
            available -= used;
          }
        }
      }
    }
    return chargedItem;
  }

  public boolean isOutputEnabled() {
    return getController().outputEnabled;
  }

  public boolean isInputEnabled() {
    return getController().inputEnabled;
  }

  private boolean transmitEnergy() {

    if(powerHandler.getEnergyStored() <= 0) {
      return false;
    }
    float canTransmit = Math.min(storedEnergy, maxOutput);
    float transmitted = 0;

    checkReceptors();

    if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
      receptorIterator = receptors.listIterator();
    }

    int appliedCount = 0;
    int numReceptors = receptors.size();
    while (receptorIterator.hasNext() && canTransmit > 0 && appliedCount < numReceptors) {

      Receptor receptor = receptorIterator.next();
      PowerReceiver pp = receptor.receptor.getPowerReceiver(receptor.fromDir.getOpposite());
      if(pp != null && pp.getMinEnergyReceived() <= canTransmit && pp.getType() != Type.ENGINE && !powerHandler.isPowerSource(receptor.fromDir)) {
        float used;
        if(receptor.receptor instanceof IInternalPowerReceptor) {
          if(!(receptor.receptor instanceof IConduitBundle)) { //power conduits manage the exchange between them an the cap bank
            used = PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) receptor.receptor, pp, canTransmit, Type.STORAGE, receptor.fromDir.getOpposite());
          } else {
            IConduitBundle bundle = (IConduitBundle) receptor.receptor;
            IPowerConduit conduit = bundle.getConduit(IPowerConduit.class);
            if(conduit != null && conduit.getConectionMode(receptor.fromDir.getOpposite()) == ConnectionMode.INPUT) {
              used = PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) receptor.receptor, pp, canTransmit, Type.STORAGE,
                  receptor.fromDir.getOpposite());
            } else {
              used = 0;
            }
          }
        } else {
          used = pp.receiveEnergy(Type.STORAGE, canTransmit, receptor.fromDir.getOpposite());
        }
        transmitted += used;
        canTransmit -= used;
      }
      if(canTransmit <= 0) {
        break;
      }

      if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
      }
      appliedCount++;
    }
    powerHandler.setEnergy(powerHandler.getEnergyStored() - transmitted);

    return transmitted > 0;

  }

  private void checkReceptors() {
    if(!receptorsDirty) {
      return;
    }
    receptors.clear();

    BlockCoord[] coords;
    if(isMultiblock()) {
      coords = multiblock;
    } else {
      coords = new BlockCoord[] { new BlockCoord(this) };
    }

    for (BlockCoord bc : coords) {
      // BlockCoord bc = new BlockCoord(xCoord, yCoord, zCoord);
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        BlockCoord checkLoc = bc.getLocation(dir);
        TileEntity te = worldObj.getBlockTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
        if(te instanceof IPowerReceptor) {
          IPowerReceptor rec = (IPowerReceptor) te;
          if(!(te instanceof TileCapacitorBank) && PowerHandlerUtil.canConnectRecievePower(rec)) {
            receptors.add(new Receptor((IPowerReceptor) te, dir));
          }

        }
      }
    }
    receptorIterator = receptors.listIterator();
    receptorsDirty = false;
  }

  // ------------ Multiblock overrides

  public int getEnergyStoredScaled(int scale) {
    return getController().doGetEnergyStoredScaled(scale);
  }

  public int getMaxInput() {
    return maxInput;
  }

  public void setMaxInput(int maxInput) {
    getController().doSetMaxInput(maxInput);
  }

  public int getMaxOutput() {
    return maxOutput;
  }

  public void setMaxOutput(int maxOutput) {
    getController().doSetMaxOutput(maxOutput);
  }

  public float getEnergyStored() {
    return getController().doGetEnergyStored();
  }

  public float getEnergyStoredRatio() {
    return getController().doGetEnergyStoredRatio();
  }

  public int getMaxEnergyStored() {
    return getController().doGetMaxEnergyStored();
  }

  public int getMaxIO() {
    return getController().doGetMaxIO();
  }

  @Override
  public PowerHandler getPowerHandler() {
    return getController().doGetPowerHandler();
  }

  @Override
  public PowerReceiver getPowerReceiver(ForgeDirection side) {
    return getPowerHandler().getPowerReceiver();
  }

  public void addEnergy(float add) {
    getController().doAddEnergy(add);
  }

  private boolean isRecievingRedstoneSignal() {
    if(!isMultiblock()) {
      return worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord) > 0;
    }
    for (BlockCoord bc : multiblock) {
      if(worldObj.getStrongestIndirectPower(bc.x, bc.y, bc.z) > 0) {
        return true;
      }
    }
    return false;
  }

  public RedstoneControlMode getInputControlMode() {
    return inputControlMode;
  }

  public void setInputControlMode(RedstoneControlMode inputControlMode) {
    if(!isMultiblock()) {
      this.inputControlMode = inputControlMode;
    } else {
      for (BlockCoord bc : multiblock) {
        TileCapacitorBank cp = getCapBank(bc);
        if(cp != null) {
          cp.inputControlMode = inputControlMode;
        }
      }
    }
  }

  public RedstoneControlMode getOutputControlMode() {
    return outputControlMode;
  }

  public void setOutputControlMode(RedstoneControlMode outputControlMode) {
    if(!isMultiblock()) {
      this.outputControlMode = outputControlMode;
    } else {
      for (BlockCoord bc : multiblock) {
        TileCapacitorBank cp = getCapBank(bc);
        if(cp != null) {
          cp.outputControlMode = outputControlMode;
        }
      }
    }
  }

  // ------------ Multiblock implementations

  int doGetMaxIO() {
    return maxIO;
  }

  int doGetMaxEnergyStored() {
    return maxStoredEnergy;
  }

  PowerHandler doGetPowerHandler() {
    if(inputEnabled) {
      return powerHandler;
    }
    return getDisabledPowerHandler();
  }

  private PowerHandler getDisabledPowerHandler() {
    if(disabledPowerHandler == null) {
      disabledPowerHandler = PowerHandlerUtil.createHandler(new BasicCapacitor(0, 0), this, Type.STORAGE);
    }
    return disabledPowerHandler;
  }

  int doGetEnergyStoredScaled(int scale) {
    // NB: called on the client so can't use the power provider
    return VecmathUtil.clamp(Math.round(scale * (storedEnergy / maxStoredEnergy)), 0, scale);
  }

  float doGetEnergyStored() {
    return storedEnergy;
  }

  float doGetEnergyStoredRatio() {
    return storedEnergy / maxStoredEnergy;
  }

  void doAddEnergy(float add) {
    storedEnergy = Math.min(maxStoredEnergy, storedEnergy + add);
    powerHandler.setEnergy(storedEnergy);
  }

  void doSetMaxInput(int in) {
    maxInput = Math.min(in, maxIO);
    maxInput = Math.max(0, maxInput);
    if(isMultiblock()) {
      for (BlockCoord bc : multiblock) {
        TileCapacitorBank cp = getCapBank(bc);
        if(cp != null) {
          cp.maxInput = maxInput;
        }
      }
    }
    updatePowerHandler();
  }

  void doSetMaxOutput(int out) {
    maxOutput = Math.min(out, maxIO);
    maxOutput = Math.max(0, maxOutput);
    if(isMultiblock()) {
      for (BlockCoord bc : multiblock) {
        TileCapacitorBank cp = getCapBank(bc);
        if(cp != null) {
          cp.maxOutput = maxOutput;
        }
      }
    }
    updatePowerHandler();
  }

  // ------------ Common power functions

  @Override
  public void doWork(PowerHandler workProvider) {
  }

  @Override
  public World getWorld() {
    return worldObj;
  }

  @Override
  public void applyPerdition() {
  }

  private void updatePowerHandler() {

    powerHandler = PowerHandlerUtil.createHandler(new BasicCapacitor(maxInput, maxStoredEnergy, maxOutput), this, Type.STORAGE);
    if(storedEnergy > maxStoredEnergy) {
      storedEnergy = maxStoredEnergy;
    }
    powerHandler.setEnergy(storedEnergy);
  }

  // ------------ Multiblock management

  public void onBlockAdded() {
    // formMultiblock();
    multiblockDirty = true;
  }

  public void onNeighborBlockChange(int blockId) {
    if(blockId != ModObject.blockCapacitorBank.actualId) {
      getController().receptorsDirty = true;
    }
  }

  public void onBreakBlock() {
    TileCapacitorBank controller = getController();
    controller.clearCurrentMultiblock();
  }

  private void clearCurrentMultiblock() {
    if(multiblock == null) {
      return;
    }
    for (BlockCoord bc : multiblock) {
      TileCapacitorBank res = getCapBank(bc);
      if(res != null) {
        res.setMultiblock(null);

      }
    }
    multiblock = null;
  }

  private void formMultiblock() {
    List<TileCapacitorBank> blocks = new ArrayList<TileCapacitorBank>();
    blocks.add(this);
    findNighbouringBanks(this, blocks);

    if(blocks.size() < 2) {
      return;
    }
    for (TileCapacitorBank cb : blocks) {
      cb.clearCurrentMultiblock();
    }

    BlockCoord[] mb = new BlockCoord[blocks.size()];
    for (int i = 0; i < blocks.size(); i++) {
      mb[i] = new BlockCoord(blocks.get(i));
    }

    for (TileCapacitorBank cb : blocks) {
      cb.setMultiblock(mb);
    }

  }

  private void findNighbouringBanks(TileCapacitorBank tileCapacitorBank, List<TileCapacitorBank> blocks) {
    BlockCoord bc = new BlockCoord(tileCapacitorBank);
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      TileCapacitorBank cb = getCapBank(bc.getLocation(dir));
      if(cb != null && !blocks.contains(cb)) {
        blocks.add(cb);
        findNighbouringBanks(cb, blocks);
      }
    }
  }

  private void setMultiblock(BlockCoord[] mb) {

    if(multiblock != null && isMaster()) {
      // split up current multiblock and reconfigure all the internal capacitors
      float powerPerBlock = storedEnergy / multiblock.length;
      for (BlockCoord bc : multiblock) {
        TileCapacitorBank cb = getCapBank(bc);
        if(cb != null) {
          cb.maxStoredEnergy = BASE_CAP.getMaxEnergyStored();
          cb.maxIO = BASE_CAP.getMaxEnergyExtracted();
          cb.maxInput = cb.maxIO;
          cb.maxOutput = cb.maxIO;
          cb.storedEnergy = powerPerBlock;
          cb.updatePowerHandler();
          cb.multiblockDirty = true;
        }
      }

    }
    multiblock = mb;
    if(isMaster()) {

      List<ItemStack> invItems = new ArrayList<ItemStack>();

      float totalStored = 0;
      int totalCap = multiblock.length * BASE_CAP.getMaxEnergyStored();
      int totalIO = multiblock.length * BASE_CAP.getMaxEnergyExtracted();
      for (BlockCoord bc : multiblock) {
        TileCapacitorBank cb = getCapBank(bc);
        if(cb != null) {
          totalStored += cb.storedEnergy;
        }
        ItemStack[] inv = cb.inventory;
        for (int i = 0; i < inv.length; i++) {
          if(inv[i] != null) {
            invItems.add(inv[i]);
            inv[i] = null;
          }
        }
        cb.multiblockDirty = false;
      }
      storedEnergy = totalStored;
      maxStoredEnergy = totalCap;
      maxIO = totalIO;
      maxInput = maxIO;
      maxOutput = maxIO;

      if(invItems.size() > inventory.length) {
        for (int i = inventory.length; i < invItems.size(); i++) {
          Util.dropItems(worldObj, invItems.get(i), xCoord, yCoord, zCoord, true);
        }
      }
      for (int i = 0; i < inventory.length && i < invItems.size(); i++) {
        inventory[i] = invItems.get(i);
      }

      updatePowerHandler();
    }
    receptorsDirty = true;

    // Forces an update
    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, isMultiblock() ? 1 : 0, 2);
  }

  TileCapacitorBank getController() {
    if(isMaster() || !isMultiblock()) {
      return this;
    }
    TileCapacitorBank res = getCapBank(multiblock[0]);
    return res != null ? res : this;
  }

  boolean isContoller() {
    return multiblock == null ? true : isMaster();
  }

  boolean isMaster() {
    if(multiblock != null) {
      return multiblock[0].equals(xCoord, yCoord, zCoord);
    }
    return false;
  }

  public boolean isMultiblock() {
    return multiblock != null;
  }

  private boolean isCurrentMultiblockValid() {
    if(multiblock == null) {
      return false;
    }
    for (BlockCoord bc : multiblock) {
      TileCapacitorBank res = getCapBank(bc);
      if(res == null || !res.isMultiblock()) {
        return false;
      }
    }
    return true;
  }

  private TileCapacitorBank getCapBank(BlockCoord bc) {
    return getCapBank(bc.x, bc.y, bc.z);
  }

  private TileCapacitorBank getCapBank(int x, int y, int z) {
    TileEntity te = worldObj.getBlockTileEntity(x, y, z);
    if(te instanceof TileCapacitorBank) {
      return (TileCapacitorBank) te;
    }
    return null;
  }

  //------------- Inventory

  @Override
  public int getSizeInventory() {
    return inventory.length;
  }

  @Override
  public ItemStack getStackInSlot(int i) {
    if(i < 0 || i >= inventory.length) {
      return null;
    }
    return inventory[i];
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    if(fromSlot < 0 || fromSlot >= inventory.length) {
      return null;
    }
    ItemStack item = inventory[fromSlot];
    if(item == null) {
      return null;
    }
    if(item.stackSize <= amount) {
      ItemStack result = item.copy();
      inventory[fromSlot] = null;
      return result;
    }
    item.stackSize -= amount;
    return item.copy();
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int i) {
    return null;
  }

  @Override
  public void setInventorySlotContents(int i, ItemStack itemstack) {
    if(i < 0 || i >= inventory.length) {
      return;
    }
    inventory[i] = itemstack;
  }

  @Override
  public String getInvName() {
    return ModObject.blockCapacitorBank.name();
  }

  @Override
  public boolean isInvNameLocalized() {
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 1;
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer entityplayer) {
    return true;
  }

  @Override
  public void openChest() {
  }

  @Override
  public void closeChest() {
  }

  @Override
  public boolean isItemValidForSlot(int i, ItemStack itemstack) {
    if(itemstack == null) {
      return false;
    }
    return itemstack.getItem() instanceof IChargeableItem;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);

    storedEnergy = nbtRoot.getFloat("storedEnergy");
    maxStoredEnergy = nbtRoot.getInteger("maxStoredEnergy");
    maxIO = nbtRoot.getInteger("maxIO");
    if(nbtRoot.hasKey("maxInput")) {
      maxInput = nbtRoot.getInteger("maxInput");
      maxOutput = nbtRoot.getInteger("maxOutput");
    } else {
      maxInput = maxIO;
      maxOutput = maxIO;
    }

    inputControlMode = RedstoneControlMode.values()[nbtRoot.getShort("inputControlMode")];
    outputControlMode = RedstoneControlMode.values()[nbtRoot.getShort("outputControlMode")];

    updatePowerHandler();

    boolean wasMulti = isMultiblock();
    if(nbtRoot.getBoolean("isMultiblock")) {
      int[] coords = nbtRoot.getIntArray("multiblock");
      multiblock = new BlockCoord[coords.length / 3];
      int c = 0;
      for (int i = 0; i < multiblock.length; i++) {
        multiblock[i] = new BlockCoord(coords[c++], coords[c++], coords[c++]);
      }

    } else {
      multiblock = null;
    }

    for (int i = 0; i < inventory.length; i++) {
      inventory[i] = null;
    }

    NBTTagList itemList = nbtRoot.getTagList("Items");
    for (int i = 0; i < itemList.tagCount(); i++) {
      NBTTagCompound itemStack = (NBTTagCompound) itemList.tagAt(i);
      byte slot = itemStack.getByte("Slot");
      if(slot >= 0 && slot < inventory.length) {
        inventory[slot] = ItemStack.loadItemStackFromNBT(itemStack);
      }
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    nbtRoot.setFloat("storedEnergy", storedEnergy);
    nbtRoot.setInteger("maxStoredEnergy", maxStoredEnergy);
    nbtRoot.setInteger("maxIO", maxIO);
    nbtRoot.setInteger("maxInput", maxInput);
    nbtRoot.setInteger("maxOutput", maxOutput);
    nbtRoot.setShort("inputControlMode", (short) inputControlMode.ordinal());
    nbtRoot.setShort("outputControlMode", (short) outputControlMode.ordinal());

    nbtRoot.setBoolean("isMultiblock", isMultiblock());
    if(isMultiblock()) {
      int[] vals = new int[multiblock.length * 3];
      int i = 0;
      for (BlockCoord bc : multiblock) {
        vals[i++] = bc.x;
        vals[i++] = bc.y;
        vals[i++] = bc.z;
      }
      nbtRoot.setIntArray("multiblock", vals);
    }

    // write inventory list
    NBTTagList itemList = new NBTTagList();
    for (int i = 0; i < inventory.length; i++) {
      if(inventory[i] != null) {
        NBTTagCompound itemStackNBT = new NBTTagCompound();
        itemStackNBT.setByte("Slot", (byte) i);
        inventory[i].writeToNBT(itemStackNBT);
        itemList.appendTag(itemStackNBT);
      }
    }
    nbtRoot.setTag("Items", itemList);
  }

  @Override
  public Packet getDescriptionPacket() {
    return PacketHandler.getPacket(this);
  }

  static class Receptor {
    IPowerReceptor receptor;
    ForgeDirection fromDir;

    private Receptor(IPowerReceptor rec, ForgeDirection fromDir) {
      super();
      this.receptor = rec;
      this.fromDir = fromDir;
    }
  }

}
