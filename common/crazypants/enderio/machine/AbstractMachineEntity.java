package crazypants.enderio.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.vecmath.VecmathUtil;

public abstract class AbstractMachineEntity extends TileEntity implements IInventory, IInternalPowerReceptor, IMachine {

  public short facing;

  // Client sync monitoring
  protected int ticksSinceSync = -1;
  protected boolean forceClientUpdate = true;
  protected boolean lastActive;
  protected float lastSyncPowerStored = -1;

  // Power
  protected Capacitors capacitorType;

  // Used on the client as the power provided isn't sinked
  private float storedEnergy;

  protected ItemStack[] inventory;
  protected final SlotDefinition slotDefinition;

  protected PowerHandler powerHandler;

  protected RedstoneControlMode redstoneControlMode;

  protected boolean redstoneCheckPassed;

  public AbstractMachineEntity(SlotDefinition slotDefinition, Type powerType) {
    this.slotDefinition = slotDefinition; // plus one for capacitor
    facing = 3;
    capacitorType = Capacitors.BASIC_CAPACITOR;
    powerHandler = PowerHandlerUtil.createHandler(capacitorType.capacitor, this, powerType);

    inventory = new ItemStack[slotDefinition.getNumSlots()];

    redstoneControlMode = RedstoneControlMode.IGNORE;
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
      return itemstack.itemID == ModObject.itemBasicCapacitor.actualId && itemstack.getItemDamage() > 0;
    }
    return isMachineItemValidForSlot(i, itemstack);
  }

  protected abstract boolean isMachineItemValidForSlot(int i, ItemStack itemstack);

  public AbstractMachineEntity(SlotDefinition slotDefinition) {
    this(slotDefinition, Type.MACHINE);
  }

  public RedstoneControlMode getRedstoneControlMode() {
    return redstoneControlMode;
  }

  public void setRedstoneControlMode(RedstoneControlMode redstoneControlMode) {
    this.redstoneControlMode = redstoneControlMode;
  }

  @Override
  public PowerHandler getPowerHandler() {
    return powerHandler;
  }

  @Override
  public void applyPerdition() {
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
    return powerHandler.getPowerReceiver();
  }

  @Override
  public World getWorld() {
    return worldObj;
  }

  protected float getPowerUsePerTick() {
    return capacitorType.capacitor.getMaxEnergyExtracted();
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
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
      }
      lastActive = isActive();
      return;

    } // else is server, do all logic only on the server

    float stored = powerHandler.getEnergyStored();
    powerHandler.update();
    powerHandler.setEnergy(stored);
    storedEnergy = stored;

    boolean requiresClientSync = false;
    if(forceClientUpdate) {
      // First update, send state to client
      forceClientUpdate = false;
      requiresClientSync = true;
    }

    boolean prevRedCheck = redstoneCheckPassed;
    redstoneCheckPassed = RedstoneControlMode.isConditionMet(redstoneControlMode, this);
    requiresClientSync |= prevRedCheck != redstoneCheckPassed;

    requiresClientSync |= processTasks(redstoneCheckPassed);

    requiresClientSync |= lastSyncPowerStored != powerHandler.getEnergyStored() && worldObj.getTotalWorldTime() % 16 == 0;

    if(requiresClientSync) {
      lastSyncPowerStored = powerHandler.getEnergyStored();
      // this will cause 'getPacketDescription()' to be called and its result
      // will be sent to the PacketHandler on the other end of
      // client/server connection
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      // And this will make sure our current tile entity state is saved
      onInventoryChanged();
    }

  }

  protected abstract boolean processTasks(boolean redstoneCheckPassed);

  // ---- Tile Entity
  // ------------------------------------------------------------------------------

  @Override
  public Packet getDescriptionPacket() {
    return PacketHandler.getPacket(this);
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);

    facing = nbtRoot.getShort("facing");

    setCapacitor(Capacitors.values()[nbtRoot.getShort("capacitorType")]);

    float storedEnergy = nbtRoot.getFloat("storedEnergy");
    powerHandler.setEnergy(storedEnergy);
    // For the client as provider is not saved to NBT
    this.storedEnergy = storedEnergy;

    redstoneCheckPassed = nbtRoot.getBoolean("redstoneCheckPassed");

    // read in the inventories contents
    inventory = new ItemStack[slotDefinition.getNumSlots()];
    NBTTagList itemList = nbtRoot.getTagList("Items");

    for (int i = 0; i < itemList.tagCount(); i++) {
      NBTTagCompound itemStack = (NBTTagCompound) itemList.tagAt(i);
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

  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setShort("facing", facing);
    nbtRoot.setFloat("storedEnergy", powerHandler.getEnergyStored());
    nbtRoot.setShort("capacitorType", (short) capacitorType.ordinal());
    nbtRoot.setBoolean("redstoneCheckPassed", redstoneCheckPassed);

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
  }

  // ---- Inventory
  // ------------------------------------------------------------------------------

  @Override
  public boolean isInvNameLocalized() {
    return false;
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer player) {
    if(worldObj == null) {
      return true;
    }
    if(worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this) {
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
    ItemStack result = new ItemStack(fromStack.itemID, amount, fromStack.getItemDamage());
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
    ItemStack contents = inventory[slotDefinition.minUpgradeSlot];
    if(contents == null || contents.itemID != ModObject.itemBasicCapacitor.actualId) {
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
  public void openChest() {
  }

  @Override
  public void closeChest() {
  }

  public void onNeighborBlockChange(int blockId) {
  }

}
