package crazypants.enderio.machine;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;
import crazypants.util.InventoryWrapper;
import crazypants.util.ItemUtil;
import crazypants.util.Lang;
import crazypants.vecmath.VecmathUtil;

public abstract class AbstractMachineEntity extends TileEntityEio implements ISidedInventory, IInternalPowerReceptor, IMachine, IRedstoneModeControlable, IIoConfigurable {

  public short facing;

  // Client sync monitoring
  protected int ticksSinceSync = -1;
  protected boolean forceClientUpdate = true;
  protected boolean lastActive;
  protected int ticksSinceActiveChanged = 0;
  protected float lastSyncPowerStored = -1;

  // Power
  protected Capacitors capacitorType;

  protected float storedEnergy;

  protected ItemStack[] inventory;
  protected final SlotDefinition slotDefinition;

  protected PowerHandler powerHandler;

  protected RedstoneControlMode redstoneControlMode;

  protected boolean redstoneCheckPassed;

  private boolean redstoneStateDirty = true;

  protected Map<ForgeDirection, IoMode> faceModes;

  private int[] allSlots;

  protected boolean notifyNeighbours = false;

  public AbstractMachineEntity(SlotDefinition slotDefinition, Type powerType) {
    this.slotDefinition = slotDefinition;
    facing = 3;
    capacitorType = Capacitors.BASIC_CAPACITOR;
    powerHandler = PowerHandlerUtil.createHandler(capacitorType.capacitor, this, powerType);
    inventory = new ItemStack[slotDefinition.getNumSlots()];
    redstoneControlMode = RedstoneControlMode.IGNORE;

    allSlots = new int[slotDefinition.getNumSlots()];
    for(int i=0;i<allSlots.length;i++) {
      allSlots[i] = i;
    }
  }

  @Override
  public IoMode toggleIoModeForFace(ForgeDirection faceHit) {
    IoMode curMode = getIoMode(faceHit);
    IoMode mode = curMode.next();
    while(!supportsMode(faceHit, mode)) {
      mode = mode.next();
    }
    setIoMode(faceHit, mode);
    return mode;
  }

  @Override
  public boolean supportsMode(ForgeDirection faceHit, IoMode mode) {
    return true;
  }

  @Override
  public void setIoMode(ForgeDirection faceHit, IoMode mode) {
    if(mode == IoMode.NONE && faceModes == null) {
      return;
    }
    if(faceModes == null) {
      faceModes = new EnumMap<ForgeDirection, IoMode>(ForgeDirection.class);
    }
    faceModes.put(faceHit, mode);
    forceClientUpdate = true;
    notifyNeighbours = true;
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

  @Override
  public BlockCoord getLocation() {
    return new BlockCoord(this);
  }

  public SlotDefinition getSlotDefinition() {
    return slotDefinition;
  }

  public boolean isValidUpgrade(ItemStack itemstack) {
    for (int i = slotDefinition.getMinUpgradeSlot(); i <= slotDefinition.getMaxUpgradeSlot(); i++) {
      if(isItemValidForSlot(i, itemstack)) {
        return true;
      }
    }
    return false;
  }

  public boolean isValidInput(ItemStack itemstack) {
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
      if(isItemValidForSlot(i, itemstack)) {
        return true;
      }
    }
    return false;
  }

  public boolean isValidOutput(ItemStack itemstack) {
    for (int i = slotDefinition.getMinOutputSlot(); i <= slotDefinition.getMaxOutputSlot(); i++) {
      if(isItemValidForSlot(i, itemstack)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public final boolean isItemValidForSlot(int i, ItemStack itemstack) {
    if(slotDefinition.isUpgradeSlot(i)) {
      return itemstack != null && itemstack.getItem() == EnderIO.itemBasicCapacitor && itemstack.getItemDamage() > 0;
    }
    return isMachineItemValidForSlot(i, itemstack);
  }

  protected abstract boolean isMachineItemValidForSlot(int i, ItemStack itemstack);

  public AbstractMachineEntity(SlotDefinition slotDefinition) {
    this(slotDefinition, Type.MACHINE);
  }

  @Override
  public RedstoneControlMode getRedstoneControlMode() {
    return redstoneControlMode;
  }

  @Override
  public void setRedstoneControlMode(RedstoneControlMode redstoneControlMode) {
    this.redstoneControlMode = redstoneControlMode;
    redstoneStateDirty = true;
  }

  public short getFacing() {
    return facing;
  }

  public void setFacing(short facing) {
    this.facing = facing;
  }

  public abstract boolean isActive();

  public abstract float getProgress();

  public int getProgressScaled(int scale) {
    int result = (int) (getProgress() * scale);
    return result;
  }

  // --- Power
  // --------------------------------------------------------------------------------------

  public boolean hasPower() {
    return storedEnergy > 0;
  }

  public ICapacitor getCapacitor() {
    return capacitorType.capacitor;
  }

  public int getEnergyStoredScaled(int scale) {
    // NB: called on the client so can't use the power provider
    return VecmathUtil.clamp(Math.round(scale * (storedEnergy / capacitorType.capacitor.getMaxEnergyStored())), 0, scale);
  }

  public float getEnergyStored() {
    return storedEnergy;
  }

  public void setCapacitor(Capacitors capacitorType) {
    this.capacitorType = capacitorType;
    PowerHandlerUtil.configure(powerHandler, capacitorType.capacitor);
    forceClientUpdate = true;
  }

  @Override
  public void doWork(PowerHandler workProvider) {
  }

  @Override
  public PowerReceiver getPowerReceiver(ForgeDirection side) {
    if(isSideDisabled(side.ordinal())) {
      return null;
    }
    return powerHandler.getPowerReceiver();
  }

  @Override
  public World getWorld() {
    return worldObj;
  }

  public float getPowerUsePerTick() {
    return capacitorType.capacitor.getMaxEnergyExtracted();
  }

  // RF Power

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    if(isSideDisabled(from.ordinal())) {
      return 0;
    }
    return PowerHandlerUtil.recieveRedstoneFlux(from, powerHandler, maxReceive, simulate);
  }

  @Override
  public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    return !isSideDisabled(from.ordinal());
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return (int) (powerHandler.getEnergyStored() * 10);
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    return (int) (powerHandler.getMaxEnergyStored() * 10);
  }

  public int getMaxEnergyStoredMJ() {
    return (int)powerHandler.getMaxEnergyStored();
  }

  // --- Process Loop
  // --------------------------------------------------------------------------

  @Override
  public void updateEntity() {

    if(worldObj == null) { // sanity check
      return;
    }

    if(worldObj.isRemote) {
      // check if the block on the client needs to update its texture
      if(isActive() != lastActive) {
        ticksSinceActiveChanged++;
        if(ticksSinceActiveChanged > 20 || isActive()) {
          ticksSinceActiveChanged = 0;
          lastActive = isActive();
          forceClientUpdate = false;
          worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
      }

      if(forceClientUpdate) {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        forceClientUpdate = false;
      }
      return;


    } // else is server, do all logic only on the server

    updateStoredEnergyFromPowerHandler();

    boolean requiresClientSync = forceClientUpdate;
    boolean prevRedCheck = redstoneCheckPassed;
    if(redstoneStateDirty) {
      redstoneCheckPassed = RedstoneControlMode.isConditionMet(redstoneControlMode, this);
      redstoneStateDirty = false;
    }

    
    if(worldObj.getTotalWorldTime() % 5 == 0) {
      requiresClientSync |= doSideIo();
    }

    requiresClientSync |= prevRedCheck != redstoneCheckPassed;

    requiresClientSync |= processTasks(redstoneCheckPassed);
        
    boolean powerChanged = (lastSyncPowerStored != storedEnergy && worldObj.getTotalWorldTime() % 5 == 0);

    if(requiresClientSync) {
      lastSyncPowerStored = storedEnergy;
      // this will cause 'getPacketDescription()' to be called and its result
      // will be sent to the PacketHandler on the other end of
      // client/server connection
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      // And this will make sure our current tile entity state is saved
      markDirty();
    } else if(powerChanged) {
      lastSyncPowerStored = storedEnergy;
      PacketHandler.sendToAllAround(new PacketPowerStorage(this), this);       
    }

    if(notifyNeighbours) {
      worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType);
      notifyNeighbours = false;
    }

  }

  protected boolean doSideIo() {
    if(faceModes == null) {
      return false;
    }
    
    boolean res = false;
    Set<Entry<ForgeDirection, IoMode>> ents = faceModes.entrySet();
    for(Entry<ForgeDirection, IoMode> ent : ents) {
      IoMode mode = ent.getValue();
      if(mode.pulls()) {
        res = res | doPull(ent.getKey());
      }
      if(mode.pushes()) {
        res = res | doPush(ent.getKey());
      }
    }
    return res;
  }

  protected boolean doPush(ForgeDirection dir) {

    if(slotDefinition.getNumOutputSlots() <= 0) {
      return false;
    }
    if(worldObj.getTotalWorldTime() % 20 != 0) {
      return false;
    }

    BlockCoord loc = getLocation().getLocation(dir);
    TileEntity te = worldObj.getTileEntity(loc.x, loc.y, loc.z);
    if(te == null) {
      return false;
    }    
    for(int i=slotDefinition.minOutputSlot; i<= slotDefinition.maxOutputSlot;i++) {
      ItemStack item = inventory[i];
      if(item != null) {
        int num = ItemUtil.doInsertItem(te, item, dir.getOpposite());
        if(num > 0) {
          item.stackSize -= num;
          if(item.stackSize <= 0) {
            item = null;
          }
          inventory[i] = item;
          markDirty();    
        }
      }
    }
    return false;
  }

  protected boolean doPull(ForgeDirection dir) {

    if(slotDefinition.getNumInputSlots() <= 0) {
      return false;
    }
    if(worldObj.getTotalWorldTime() % 20 != 0) {
      return false;
    }

    boolean hasSpace = false;
    for(int slot=slotDefinition.minInputSlot; slot <= slotDefinition.maxInputSlot && !hasSpace;slot++) {
      hasSpace = inventory[slot] == null ? true : inventory[slot].stackSize < inventory[slot].getMaxStackSize();
    }
    if(!hasSpace) {
      return false;
    }

    BlockCoord loc = getLocation().getLocation(dir);
    TileEntity te = worldObj.getTileEntity(loc.x, loc.y, loc.z);
    if(te == null) {
      return false;
    }
    if( !(te instanceof IInventory)) {
      return false;
    }
    ISidedInventory target;
    if(te instanceof ISidedInventory) {
      target = (ISidedInventory)te;
    } else {
      target = new InventoryWrapper((IInventory)te);
    }

    int[] targetSlots = target.getAccessibleSlotsFromSide(dir.getOpposite().ordinal());
    if(targetSlots == null) {
      return false;
    }

    for(int inputSlot=slotDefinition.minInputSlot; inputSlot <= slotDefinition.maxInputSlot;inputSlot++) {
      if(doPull(inputSlot, target, targetSlots, dir)) {
        return false;
      }
    }
    return false;
  }

  protected boolean doPull(int inputSlot, ISidedInventory target, int[] targetSlots, ForgeDirection side) {
    ItemStack curStack = inventory[inputSlot];
    for(int i=0;i<targetSlots.length;i++) {
      int tSlot = targetSlots[i];
      ItemStack targetStack = target.getStackInSlot(tSlot);
      if(targetStack != null && target.canExtractItem(i, targetStack, side.getOpposite().ordinal())) {
        int res = ItemUtil.doInsertItem(this, targetStack, side.getOpposite());
        if(res > 0) {
          targetStack = targetStack.copy();
          targetStack.stackSize -= res;
          if(targetStack.stackSize <= 0) {
            targetStack = null;
          }
          target.setInventorySlotContents(tSlot, targetStack);
          return true;
        }
      }
    }
    return false;
  }

  protected void updateStoredEnergyFromPowerHandler() {
    storedEnergy = (float) powerHandler.getEnergyStored();
  }

  protected abstract boolean processTasks(boolean redstoneCheckPassed);

  // ---- Tile Entity
  // ------------------------------------------------------------------------------

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {

    facing = nbtRoot.getShort("facing");
    redstoneCheckPassed = nbtRoot.getBoolean("redstoneCheckPassed");
    forceClientUpdate = nbtRoot.getBoolean("forceClientUpdate");
    readCommon(nbtRoot);
  }

  public void readCommon(NBTTagCompound nbtRoot) {

    setCapacitor(Capacitors.values()[nbtRoot.getShort("capacitorType")]);

    float storedEnergy = nbtRoot.getFloat("storedEnergy");
    powerHandler.setEnergy(storedEnergy);
    // For the client as provider is not saved to NBT
    this.storedEnergy = storedEnergy;

    // read in the inventories contents
    inventory = new ItemStack[slotDefinition.getNumSlots()];

    NBTTagList itemList = (NBTTagList) nbtRoot.getTag("Items");
    for (int i = 0; i < itemList.tagCount(); i++) {
      NBTTagCompound itemStack = itemList.getCompoundTagAt(i);
      byte slot = itemStack.getByte("Slot");
      if(slot >= 0 && slot < inventory.length) {
        inventory[slot] = ItemStack.loadItemStackFromNBT(itemStack);
      }
    }

    int rsContr = nbtRoot.getInteger("redstoneControlMode");
    if(rsContr < 0 || rsContr >= RedstoneControlMode.values().length) {
      rsContr = 0;
    }
    redstoneControlMode = RedstoneControlMode.values()[rsContr];

    if(nbtRoot.hasKey("hasFaces")) {
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(nbtRoot.hasKey("face" + dir.ordinal())) {
          setIoMode(dir, IoMode.values()[nbtRoot.getShort("face" + dir.ordinal())]);
        }
      }
    }

  }

  public void readFromItemStack(ItemStack stack) {
    if(stack == null || stack.stackTagCompound == null) {
      return;
    }
    NBTTagCompound root = stack.stackTagCompound ;
    if(!root.hasKey("eio.abstractMachine")) {
      return;
    }
    readCommon(root);
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {

    nbtRoot.setShort("facing", facing);
    nbtRoot.setBoolean("redstoneCheckPassed", redstoneCheckPassed);
    nbtRoot.setBoolean("forceClientUpdate", forceClientUpdate);
    forceClientUpdate = false;

    writeCommon(nbtRoot);
  }

  public void writeCommon(NBTTagCompound nbtRoot) {
    nbtRoot.setFloat("storedEnergy", storedEnergy);
    nbtRoot.setShort("capacitorType", (short) capacitorType.ordinal());

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

    nbtRoot.setInteger("redstoneControlMode", redstoneControlMode.ordinal());

    //face modes
    if(faceModes != null) {
      nbtRoot.setByte("hasFaces", (byte) 1);
      for (Entry<ForgeDirection, IoMode> e : faceModes.entrySet()) {
        nbtRoot.setShort("face" + e.getKey().ordinal(), (short) e.getValue().ordinal());
      }
    }
  }

  public void writeToItemStack(ItemStack stack) {
    if(stack == null) {
      return;
    }
    if(stack.stackTagCompound == null) {
      stack.stackTagCompound = new NBTTagCompound();
    }

    NBTTagCompound root = stack.stackTagCompound;
    root.setBoolean("eio.abstractMachine", true);
    writeCommon(root);

    String name;
    if(stack.hasDisplayName()) {
      name = stack.getDisplayName();
    } else {
      name = Lang.localize(stack.getUnlocalizedName() + ".name", false);
    }
    name += " " + Lang.localize("machine.tooltip.configured");
    stack.setStackDisplayName(name);
  }



  // ---- Inventory
  // ------------------------------------------------------------------------------

  @Override
  public boolean isUseableByPlayer(EntityPlayer player) {
    if(worldObj == null) {
      return true;
    }
    if(worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
      return false;
    }
    return player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
  }

  @Override
  public int getSizeInventory() {
    return slotDefinition.getNumSlots();
  }

  @Override
  public int getInventoryStackLimit() {
    return 64;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if(slot < 0 || slot >= inventory.length) {
      return null;
    }
    return inventory[slot];
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack fromStack = inventory[fromSlot];
    if(fromStack == null) {
      return null;
    }
    if(fromStack.stackSize <= amount) {
      inventory[fromSlot] = null;
      updateCapacitorFromSlot();
      return fromStack;
    }
    ItemStack result = new ItemStack(fromStack.getItem(), amount, fromStack.getItemDamage());
    if(fromStack.stackTagCompound != null) {
      result.stackTagCompound = (NBTTagCompound) fromStack.stackTagCompound.copy();
    }
    fromStack.stackSize -= amount;
    return result;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack contents) {
    if(contents == null) {
      inventory[slot] = contents;
    } else {
      inventory[slot] = contents.copy();
    }

    if(contents != null && contents.stackSize > getInventoryStackLimit()) {
      contents.stackSize = getInventoryStackLimit();
    }

    if(slotDefinition.isUpgradeSlot(slot)) {
      updateCapacitorFromSlot();
    }
  }

  private void updateCapacitorFromSlot() {
    if(slotDefinition.getNumUpgradeSlots() <= 0) {
      setCapacitor(Capacitors.BASIC_CAPACITOR);
      return;
    }
    ItemStack contents = inventory[slotDefinition.minUpgradeSlot];
    if(contents == null || contents.getItem() != EnderIO.itemBasicCapacitor) {
      setCapacitor(Capacitors.BASIC_CAPACITOR);
    } else {
      setCapacitor(Capacitors.values()[contents.getItemDamage()]);
    }
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int i) {
    return null;
  }

  @Override
  public void openInventory() {
  }

  @Override
  public void closeInventory() {
  }

  @Override
  public String getInventoryName() {
    return getMachineName();
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public int[] getAccessibleSlotsFromSide(int var1) {
    if(isSideDisabled(var1)) {
      return new int[0];
    }
    return allSlots;
  }

  @Override
  public boolean canInsertItem(int slot, ItemStack var2, int side) {
    if(isSideDisabled(side)) {
      return false;
    }
    return slotDefinition.isInputSlot(slot) && isMachineItemValidForSlot(slot, var2);
  }

  @Override
  public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
    if(isSideDisabled(side)) {
      return false;
    }
    if(!slotDefinition.isOutputSlot(slot)) {
      return false;
    }
    if(inventory[slot] == null || inventory[slot].stackSize < itemstack.stackSize) {
      return false;
    }
    return itemstack.getItem() == inventory[slot].getItem();
  }

  public boolean isSideDisabled(int var1) {
    ForgeDirection dir = ForgeDirection.getOrientation(var1);
    IoMode mode = getIoMode(dir);
    if(mode == IoMode.DISABLED) {
      return true;
    }
    return false;
  }

  public void onNeighborBlockChange(Block blockId) {
    redstoneStateDirty = true;
  }

}
