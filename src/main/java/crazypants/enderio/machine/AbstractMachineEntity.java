package crazypants.enderio.machine;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.InventoryWrapper;
import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.api.redstone.IRedstoneConnectable;
import crazypants.enderio.config.Config;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.YetaUtil;

public abstract class AbstractMachineEntity extends TileEntityEio
    implements ISidedInventory, IMachine, IRedstoneModeControlable, IRedstoneConnectable, IIoConfigurable {

  public EnumFacing facing;

  // Client sync monitoring
  protected int ticksSinceSync = -1;
  protected boolean forceClientUpdate = true;
  protected boolean lastActive;
  protected int ticksSinceActiveChanged = 0;

  protected ItemStack[] inventory;
  protected final SlotDefinition slotDefinition;

  protected RedstoneControlMode redstoneControlMode;

  protected boolean redstoneCheckPassed;

  private boolean redstoneStateDirty = true;

  protected Map<EnumFacing, IoMode> faceModes;

  private final int[] allSlots;

  protected boolean notifyNeighbours = false;

  @SideOnly(Side.CLIENT)
  private MachineSound sound;

  private final ResourceLocation soundRes;

  public static ResourceLocation getSoundFor(String sound) {
    return sound == null ? null : new ResourceLocation(EnderIO.DOMAIN + ":" + sound);
  }

  public AbstractMachineEntity(SlotDefinition slotDefinition) {
    this.slotDefinition = slotDefinition;
    facing = EnumFacing.SOUTH;

    inventory = new ItemStack[slotDefinition.getNumSlots()];
    redstoneControlMode = RedstoneControlMode.IGNORE;
    soundRes = getSoundFor(getSoundName());

    allSlots = new int[slotDefinition.getNumSlots()];
    for (int i = 0; i < allSlots.length; i++) {
      allSlots[i] = i;
    }
  }

  @Override
  public IoMode toggleIoModeForFace(EnumFacing faceHit) {
    IoMode curMode = getIoMode(faceHit);
    IoMode mode = curMode.next();
    while (!supportsMode(faceHit, mode)) {
      mode = mode.next();
    }
    setIoMode(faceHit, mode);
    return mode;
  }

  @Override
  public boolean supportsMode(EnumFacing faceHit, IoMode mode) {
    return true;
  }

  @Override
  public void setIoMode(EnumFacing faceHit, IoMode mode) {
    if (mode == IoMode.NONE && faceModes == null) {
      return;
    }
    if (faceModes == null) {
      faceModes = new EnumMap<EnumFacing, IoMode>(EnumFacing.class);
    }
    faceModes.put(faceHit, mode);
    forceClientUpdate = true;
    notifyNeighbours = true;

    updateBlock();
  }

  @Override
  public void clearAllIoModes() {
    if (faceModes != null) {
      faceModes = null;
      forceClientUpdate = true;
      notifyNeighbours = true;
      updateBlock();
    }
  }

  @Override
  public IoMode getIoMode(EnumFacing face) {
    if (faceModes == null) {
      return IoMode.NONE;
    }
    IoMode res = faceModes.get(face);
    if (res == null) {
      return IoMode.NONE;
    }
    return res;
  }

  public SlotDefinition getSlotDefinition() {
    return slotDefinition;
  }

  public boolean isValidUpgrade(ItemStack itemstack) {
    for (int i = slotDefinition.getMinUpgradeSlot(); i <= slotDefinition.getMaxUpgradeSlot(); i++) {
      if (isItemValidForSlot(i, itemstack)) {
        return true;
      }
    }
    return false;
  }

  public boolean isValidInput(ItemStack itemstack) {
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
      if (isItemValidForSlot(i, itemstack)) {
        return true;
      }
    }
    return false;
  }

  public boolean isValidOutput(ItemStack itemstack) {
    for (int i = slotDefinition.getMinOutputSlot(); i <= slotDefinition.getMaxOutputSlot(); i++) {
      if (isItemValidForSlot(i, itemstack)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public final boolean isItemValidForSlot(int i, ItemStack itemstack) {
    if (slotDefinition.isUpgradeSlot(i)) {
      return itemstack != null && itemstack.getItem() == EnderIO.itemBasicCapacitor && itemstack.getItemDamage() > 0;
    }
    return isMachineItemValidForSlot(i, itemstack);
  }

  protected abstract boolean isMachineItemValidForSlot(int i, ItemStack itemstack);

  @Override
  public RedstoneControlMode getRedstoneControlMode() {
    return redstoneControlMode;
  }

  @Override
  public void setRedstoneControlMode(RedstoneControlMode redstoneControlMode) {
    this.redstoneControlMode = redstoneControlMode;
    redstoneStateDirty = true;
    updateBlock();
  }

  public EnumFacing getFacing() {
    return facing;
  }

  public EnumFacing getFacingDir() {
    return facing;
  }

  public void setFacing(EnumFacing facing) {
    this.facing = facing;
  }

  public abstract boolean isActive();

  public String getSoundName() {
    return null;
  }

  public boolean hasSound() {
    return getSoundName() != null;
  }

  public float getVolume() {
    return Config.machineSoundVolume;
  }

  public float getPitch() {
    return 1.0f;
  }

  protected boolean shouldPlaySound() {
    return isActive() && !isInvalid();
  }

  @SideOnly(Side.CLIENT)
  private void updateSound() {
    if (Config.machineSoundsEnabled && hasSound()) {
      if (shouldPlaySound()) {
        if (sound == null) {
          sound = new MachineSound(soundRes, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, getVolume(), getPitch());
          FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
        }
      } else if (sound != null) {
        sound.endPlaying();
        sound = null;
      }
    }
  }

  // --- Process Loop
  // --------------------------------------------------------------------------

  @Override
  public void doUpdate() {
    if (worldObj.isRemote) {
      updateEntityClient();
      return;
    } // else is server, do all logic only on the server

    boolean requiresClientSync = forceClientUpdate;
    boolean prevRedCheck = redstoneCheckPassed;
    if (redstoneStateDirty) {
      redstoneCheckPassed = RedstoneControlMode.isConditionMet(redstoneControlMode, this);
      redstoneStateDirty = false;
    }

    if (shouldDoWorkThisTick(5)) {
      requiresClientSync |= doSideIo();
    }

    requiresClientSync |= prevRedCheck != redstoneCheckPassed;

    requiresClientSync |= processTasks(redstoneCheckPassed);

    if (requiresClientSync) {

      // this will cause 'getPacketDescription()' to be called and its result
      // will be sent to the PacketHandler on the other end of
      // client/server connection
      worldObj.markBlockForUpdate(pos);
      // And this will make sure our current tile entity state is saved
      markDirty();
    }

    if (notifyNeighbours) {
      worldObj.notifyBlockOfStateChange(pos, getBlockType());
      notifyNeighbours = false;
    }

  }

  protected void updateEntityClient() {
    // check if the block on the client needs to update its texture
    if (isActive() != lastActive) {
      ticksSinceActiveChanged++;
      if (ticksSinceActiveChanged > 20 || isActive()) {
        ticksSinceActiveChanged = 0;
        lastActive = isActive();
        forceClientUpdate = true;
      }
    }

    if (hasSound()) {
      updateSound();
    }

    if (forceClientUpdate) {
      worldObj.markBlockForUpdate(pos);
      forceClientUpdate = false;
    } else {
      YetaUtil.refresh(this);
    }
  }

  protected boolean doSideIo() {
    if (faceModes == null) {
      return false;
    }

    boolean res = false;
    Set<Entry<EnumFacing, IoMode>> ents = faceModes.entrySet();
    for (Entry<EnumFacing, IoMode> ent : ents) {
      IoMode mode = ent.getValue();
      if (mode.pulls()) {
        res = res | doPull(ent.getKey());
      }
      if (mode.pushes()) {
        res = res | doPush(ent.getKey());
      }
    }
    return res;
  }

  protected boolean doPush(EnumFacing dir) {

    if (slotDefinition.getNumOutputSlots() <= 0) {
      return false;
    }
    if (!shouldDoWorkThisTick(20)) {
      return false;
    }

    BlockCoord loc = getLocation().getLocation(dir);
    TileEntity te = worldObj.getTileEntity(loc.getBlockPos());

    return doPush(dir, te, slotDefinition.minOutputSlot, slotDefinition.maxOutputSlot);
  }

  protected boolean doPush(EnumFacing dir, TileEntity te, int minSlot, int maxSlot) {
    if (te == null) {
      return false;
    }
    for (int i = minSlot; i <= maxSlot; i++) {
      ItemStack item = inventory[i];
      if (item != null) {
        int num = ItemUtil.doInsertItem(te, item, dir.getOpposite());
        if (num > 0) {
          item.stackSize -= num;
          if (item.stackSize <= 0) {
            item = null;
          }
          inventory[i] = item;
          markDirty();
        }
      }
    }
    return false;
  }

  protected boolean doPull(EnumFacing dir) {

    if (slotDefinition.getNumInputSlots() <= 0) {
      return false;
    }
    if (!shouldDoWorkThisTick(20)) {
      return false;
    }

    boolean hasSpace = false;
    for (int slot = slotDefinition.minInputSlot; slot <= slotDefinition.maxInputSlot && !hasSpace; slot++) {
      hasSpace = inventory[slot] == null ? true : inventory[slot].stackSize < Math.min(inventory[slot].getMaxStackSize(), getInventoryStackLimit(slot));
    }
    if (!hasSpace) {
      return false;
    }

    BlockCoord loc = getLocation().getLocation(dir);
    TileEntity te = worldObj.getTileEntity(loc.getBlockPos());
    if (te == null) {
      return false;
    }
    if (!(te instanceof IInventory)) {
      return false;
    }
    ISidedInventory target;
    if (te instanceof ISidedInventory) {
      target = (ISidedInventory) te;
    } else {
      target = new InventoryWrapper((IInventory) te);
    }

    int[] targetSlots = target.getSlotsForFace(dir.getOpposite());
    if (targetSlots == null) {
      return false;
    }

    for (int inputSlot = slotDefinition.minInputSlot; inputSlot <= slotDefinition.maxInputSlot; inputSlot++) {
      if (doPull(inputSlot, target, targetSlots, dir)) {
        return false;
      }
    }
    return false;
  }

  protected boolean doPull(int inputSlot, ISidedInventory target, int[] targetSlots, EnumFacing side) {
    for (int i = 0; i < targetSlots.length; i++) {
      int tSlot = targetSlots[i];
      ItemStack targetStack = target.getStackInSlot(tSlot);
      if (targetStack != null && target.canExtractItem(i, targetStack, side.getOpposite())) {
        int res = ItemUtil.doInsertItem(this, targetStack, side);
        if (res > 0) {
          targetStack = targetStack.copy();
          targetStack.stackSize -= res;
          if (targetStack.stackSize <= 0) {
            targetStack = null;
          }
          target.setInventorySlotContents(tSlot, targetStack);
          return true;
        }
      }
    }
    return false;
  }

  protected abstract boolean processTasks(boolean redstoneCheck);

  // ---- Tile Entity
  // ------------------------------------------------------------------------------

  @Override
  public void invalidate() {
    super.invalidate();
    if (worldObj.isRemote) {
      updateSound();
    }
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {

    setFacing(EnumFacing.VALUES[nbtRoot.getShort("facing")]);
    redstoneCheckPassed = nbtRoot.getBoolean("redstoneCheckPassed");
    forceClientUpdate = nbtRoot.getBoolean("forceClientUpdate");
    readCommon(nbtRoot);
  }

  /**
   * Read state common to both block and item
   */
  public void readCommon(NBTTagCompound nbtRoot) {

    // read in the inventories contents
    inventory = new ItemStack[slotDefinition.getNumSlots()];

    NBTTagList itemList = (NBTTagList) nbtRoot.getTag("Items");
    if (itemList != null) {
      for (int i = 0; i < itemList.tagCount(); i++) {
        NBTTagCompound itemStack = itemList.getCompoundTagAt(i);
        byte slot = itemStack.getByte("Slot");
        if (slot >= 0 && slot < inventory.length) {
          inventory[slot] = ItemStack.loadItemStackFromNBT(itemStack);
        }
      }
    }

    int rsContr = nbtRoot.getInteger("redstoneControlMode");
    if (rsContr < 0 || rsContr >= RedstoneControlMode.values().length) {
      rsContr = 0;
    }
    redstoneControlMode = RedstoneControlMode.values()[rsContr];

    if (nbtRoot.hasKey("hasFaces")) {
      for (EnumFacing dir : EnumFacing.VALUES) {
        if (nbtRoot.hasKey("face" + dir.ordinal())) {
          setIoMode(dir, IoMode.values()[nbtRoot.getShort("face" + dir.ordinal())]);
        }
      }
    }

    if (this instanceof IPaintable.IPaintableTileEntity) {
      paintSource = PainterUtil2.readNbt(nbtRoot);
    }

  }

  public void readFromItemStack(ItemStack stack) {
    if (stack == null || stack.getTagCompound() == null) {
      return;
    }
    NBTTagCompound root = stack.getTagCompound();
    if (root.hasKey("eio.abstractMachine")) {
      readCommon(root);
    } else if (this instanceof IPaintable.IPaintableTileEntity) {
      paintSource = PainterUtil2.readNbt(root);
    }
    return;
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {

    nbtRoot.setShort("facing", (short) facing.ordinal());
    nbtRoot.setBoolean("redstoneCheckPassed", redstoneCheckPassed);
    nbtRoot.setBoolean("forceClientUpdate", forceClientUpdate);
    forceClientUpdate = false;

    writeCommon(nbtRoot);
  }

  /**
   * Write state common to both block and item
   */
  public void writeCommon(NBTTagCompound nbtRoot) {

    // write inventory list
    NBTTagList itemList = new NBTTagList();
    for (int i = 0; i < inventory.length; i++) {
      if (inventory[i] != null) {
        NBTTagCompound itemStackNBT = new NBTTagCompound();
        itemStackNBT.setByte("Slot", (byte) i);
        inventory[i].writeToNBT(itemStackNBT);
        itemList.appendTag(itemStackNBT);
      }
    }
    nbtRoot.setTag("Items", itemList);

    nbtRoot.setInteger("redstoneControlMode", redstoneControlMode.ordinal());

    // face modes
    if (faceModes != null) {
      nbtRoot.setByte("hasFaces", (byte) 1);
      for (Entry<EnumFacing, IoMode> e : faceModes.entrySet()) {
        nbtRoot.setShort("face" + e.getKey().ordinal(), (short) e.getValue().ordinal());
      }
    }

    if (this instanceof IPaintable.IPaintableTileEntity) {
      PainterUtil2.writeNbt(nbtRoot, paintSource);
    }

  }

  public void writeToItemStack(ItemStack stack) {
    if (stack == null) {
      return;
    }
    if (!stack.hasTagCompound()) {
      stack.setTagCompound(new NBTTagCompound());
    }

    NBTTagCompound root = stack.getTagCompound();
    root.setBoolean("eio.abstractMachine", true);
    writeCommon(root);

    String name;
    if (stack.hasDisplayName()) {
      name = stack.getDisplayName();
    } else {
      name = EnderIO.lang.localizeExact(stack.getUnlocalizedName() + ".name");
    }
    name += " " + EnderIO.lang.localize("machine.tooltip.configured");
    stack.setStackDisplayName(name);
  }

  // ---- Inventory
  // ------------------------------------------------------------------------------

  @Override
  public boolean isUseableByPlayer(EntityPlayer player) {
    return canPlayerAccess(player);
  }

  @Override
  public int getSizeInventory() {
    return slotDefinition.getNumSlots();
  }

  public int getInventoryStackLimit(int slot) {
    return getInventoryStackLimit();
  }

  @Override
  public int getInventoryStackLimit() {
    return 64;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if (slot < 0 || slot >= inventory.length) {
      return null;
    }
    return inventory[slot];
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack fromStack = inventory[fromSlot];
    if (fromStack == null) {
      return null;
    }
    if (fromStack.stackSize <= amount) {
      inventory[fromSlot] = null;
      return fromStack;
    }
    ItemStack result = new ItemStack(fromStack.getItem(), amount, fromStack.getItemDamage());
    if (fromStack.getTagCompound() != null) {
      result.setTagCompound((NBTTagCompound) fromStack.getTagCompound().copy());
    }
    fromStack.stackSize -= amount;
    return result;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack contents) {
    if (contents == null) {
      inventory[slot] = contents;
    } else {
      inventory[slot] = contents.copy();
    }
    if (contents != null && contents.stackSize > getInventoryStackLimit(slot)) {
      contents.stackSize = getInventoryStackLimit(slot);
    }
  }

  @Override
  public void clear() {
    for (int i = 0; i < inventory.length; ++i) {
      inventory[i] = null;
    }
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    ItemStack res = inventory[index];
    inventory[index] = null;
    return res;
  }

  @Override
  public int getField(int id) {
    return 0;
  }

  @Override
  public void setField(int id, int value) {
  }

  @Override
  public int getFieldCount() {
    return 0;
  }

  @Override
  public void openInventory(EntityPlayer player) {
  }

  @Override
  public void closeInventory(EntityPlayer player) {
  }

  @Override
  public String getName() {
    return getMachineName();
  }

  @Override
  public boolean hasCustomName() {
    return false;
  }

  @Override
  public IChatComponent getDisplayName() {
    return hasCustomName() ? new ChatComponentText(getName()) : new ChatComponentTranslation(getName(), new Object[0]);
  }

  @Override
  public int[] getSlotsForFace(EnumFacing var1) {
    if (isSideDisabled(var1)) {
      return new int[0];
    }
    return allSlots;
  }

  @Override
  public boolean canInsertItem(int slot, ItemStack itemstack, EnumFacing side) {
    if (isSideDisabled(side) || !slotDefinition.isInputSlot(slot)) {
      return false;
    }
    ItemStack existing = inventory[slot];
    if (existing != null) {
      // no point in checking the recipes if an item is already in the slot
      // worst case we get more of the wrong item - but that doesn't change
      // anything
      return existing.isStackable() && existing.isItemEqual(itemstack);
    }
    // no need to call isItemValidForSlot as upgrade slots are not input slots
    return isMachineItemValidForSlot(slot, itemstack);
  }

  @Override
  public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing side) {
    if (isSideDisabled(side)) {
      return false;
    }
    if (!slotDefinition.isOutputSlot(slot)) {
      return false;
    }
    return canExtractItem(slot, itemstack);
  }

  protected boolean canExtractItem(int slot, ItemStack itemstack) {
    if (inventory[slot] == null || inventory[slot].stackSize < itemstack.stackSize) {
      return false;
    }
    return itemstack.getItem() == inventory[slot].getItem();
  }

  public boolean isSideDisabled(EnumFacing dir) {
    IoMode mode = getIoMode(dir);
    if (mode == IoMode.DISABLED) {
      return true;
    }
    return false;
  }

  public void onNeighborBlockChange(Block blockId) {
    redstoneStateDirty = true;
  }

  /* IRedstoneConnectable */

  @Override
  public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, EnumFacing from) {
    return true;
  }
  
  @Override
  public BlockCoord getLocation() {    
    return new BlockCoord(pos);
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT START
  // ///////////////////////////////////////////////////////////////////////

  private IBlockState paintSource = null;

  public void setPaintSource(IBlockState paintSource) {
    this.paintSource = paintSource;
    markDirty();
    updateBlock();
  }

  public IBlockState getPaintSource() {
    return paintSource;
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT END
  // ///////////////////////////////////////////////////////////////////////

}
