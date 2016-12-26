package crazypants.enderio.machine.farm;

import java.util.BitSet;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.farm.farmers.FarmersCommune;
import crazypants.enderio.machine.farm.farmers.IHarvestResult;
import crazypants.enderio.machine.farm.farmers.RubberTreeFarmerIC2;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.tool.ArrayMappingTool;

public class TileFarmStation extends AbstractPoweredTaskEntity {

  private static final int TICKS_PER_WORK = 20;

  public enum ToolType {
    HOE {
      @Override
      boolean match(ItemStack item) {
        for (ItemStack stack : Config.farmHoes) {
          if (stack.getItem() == item.getItem()) {
            return true;
          }
        }
        return false;
      }
    },

    AXE     {
      @Override
      boolean match(ItemStack item) {
        return item.getItem().getHarvestLevel(item, "axe") >= 0;
      }
    },
    TREETAP {
      @Override
      boolean match(ItemStack item) {
        return item.getItem().getClass() == RubberTreeFarmerIC2.treeTap;
      }
    },
    SHEARS  {
      @Override
      boolean match(ItemStack item) {
        return item.getItem() instanceof ItemShears;
      }
    },
    NONE  {
      @Override
      boolean match(ItemStack item) {
        return false;
      }
    };

    public final boolean itemMatches(ItemStack item) {
      if (item == null) {
        return false;
      }
      return match(item) && !isBrokenTinkerTool(item);
    }

    private boolean isBrokenTinkerTool(ItemStack item)
    {
      return item.hasTagCompound() && item.getTagCompound().hasKey("InfiTool") && item.getTagCompound().getCompoundTag("InfiTool").getBoolean("Broken");
    }

    abstract boolean match(ItemStack item);

    public static boolean isTool(ItemStack stack) {
      for (ToolType type : values()) {
        if (type.itemMatches(stack)) {
          return true;
        }
      }
      return false;
    }

    public static ToolType getToolType(ItemStack stack) {
      for (ToolType type : values()) {
        if (type.itemMatches(stack)) {
          return type;
        }
      }
      return NONE;
    }
  }

  public static final String NOTIFICATION_NO_HOE = "noHoe";
  public static final String NOTIFICATION_NO_AXE = "noAxe";
  public static final String NOTIFICATION_NO_SEEDS = "noSeeds";

  private BlockCoord lastScanned;
  private EntityPlayerMP farmerJoe;

  public static final int NUM_TOOL_SLOTS = 3;

  private static final int minToolSlot = 0;
  private static final int maxToolSlot = -1 + NUM_TOOL_SLOTS;

  public static final int NUM_FERTILIZER_SLOTS = 2;

  public static final int minFirtSlot = maxToolSlot + 1;
  public static final int maxFirtSlot = maxToolSlot + NUM_FERTILIZER_SLOTS;

  public static final int NUM_SUPPLY_SLOTS = 4;
  
  public static final int minSupSlot = maxFirtSlot + 1;
  public static final int maxSupSlot = maxFirtSlot + NUM_SUPPLY_SLOTS;

  private final BitSet lockedSlots = new BitSet();

  public String notification = "";
  public boolean sendNotification = false;

  private boolean wasActive;

  public TileFarmStation() {
    super(new SlotDefinition(9, 6, 1));
  }

  public int getFarmSize() {
    return Config.farmDefaultSize + getUpgradeDist();
  }

  public void actionPerformed(boolean isAxe) {
    if(isAxe) {
      usePower(Config.farmAxeActionEnergyUseRF);
    } else {
      usePower(Config.farmActionEnergyUseRF);
    }
    clearNotification();
  }

  public boolean tillBlock(BlockCoord plantingLocation) {
    BlockCoord dirtLoc = plantingLocation.getLocation(ForgeDirection.DOWN);
    Block dirtBlock = getBlock(dirtLoc);
    if((dirtBlock == Blocks.dirt || dirtBlock == Blocks.grass)) {
      if(!hasHoe()) {
        setNotification(NOTIFICATION_NO_HOE);
        return false;
      }
      damageHoe(1, dirtLoc);
      worldObj.setBlock(dirtLoc.x, dirtLoc.y, dirtLoc.z, Blocks.farmland);
      worldObj.playSoundEffect(dirtLoc.x + 0.5F, dirtLoc.y + 0.5F, dirtLoc.z + 0.5F, Blocks.farmland.stepSound.getStepResourcePath(),
          (Blocks.farmland.stepSound.getVolume() + 1.0F) / 2.0F, Blocks.farmland.stepSound.getPitch() * 0.8F);
      actionPerformed(false);
      return true;
    } else if(dirtBlock == Blocks.farmland) {
      return true;
    }
    return false;
  }

  public int getMaxLootingValue() {
    int result = 0;
    for (int i = minToolSlot; i <= maxToolSlot; i++) {
      if(inventory[i] != null) {
        int level = getLooting(inventory[i]);
        if(level > result) {
          result = level;
        }
      }
    }
    return result;
  }

  private int getUpgradeDist() {
    int upg = slotDefinition.getMaxUpgradeSlot();
    if(inventory[upg] == null) {
      return 0;
    } else {
      return Config.farmBonusSize * inventory[upg].getItemDamage();
    }
  }

  public boolean hasHoe() {
    return hasTool(ToolType.HOE);
  }

  public boolean hasAxe() {
    return hasTool(ToolType.AXE);
  }

  public boolean hasShears() {
    return hasTool(ToolType.SHEARS);
  }

  public int getAxeLootingValue() {
    ItemStack tool = getTool(ToolType.AXE);
    if(tool == null) {
      return 0;
    }
    return getLooting(tool);
  }

  public void damageAxe(Block blk, BlockCoord bc) {
    damageTool(ToolType.AXE, blk, bc, 1);
  }

  public void damageHoe(int i, BlockCoord bc) {
    damageTool(ToolType.HOE, null, bc, i);
  }

  public void damageShears(Block blk, BlockCoord bc) {
    damageTool(ToolType.SHEARS, blk, bc, 1);
  }

  public boolean hasTool(ToolType type){
    return getTool(type) != null;
  }

  private ItemStack getTool(ToolType type) {
    for (int i = minToolSlot; i <= maxToolSlot; i++) {
      if(type.itemMatches(inventory[i]) && inventory[i].stackSize>0) {
        return inventory[i];
      }
    }
    return null;
  }

  public void damageTool(ToolType type, Block blk, BlockCoord bc, int damage) {

    ItemStack tool = getTool(type);
    if(tool == null) {
      return;
    }

    float rand = worldObj.rand.nextFloat();
    if(rand >= Config.farmToolTakeDamageChance) {
      return;
    }

    boolean canDamage = canDamage(tool);
    if(type == ToolType.AXE) {
      tool.getItem().onBlockDestroyed(tool, worldObj, blk, bc.x, bc.y, bc.z, farmerJoe);
    } else if(type == ToolType.HOE) {
      int origDamage = tool.getItemDamage();
      tool.getItem().onItemUse(tool, farmerJoe, worldObj, bc.x, bc.y, bc.z, 1, 0.5f, 0.5f, 0.5f);
      if(origDamage == tool.getItemDamage() && canDamage) {
        tool.damageItem(1, farmerJoe);
      }
    } else if(canDamage) {
      tool.damageItem(1, farmerJoe);
    }

    if(tool.stackSize == 0 || (canDamage && tool.getItemDamage() >= tool.getMaxDamage())) {
      destroyTool(type);
    }
  }

  private boolean canDamage(ItemStack stack) {
    return stack != null && stack.isItemStackDamageable() && stack.getItem().isDamageable();
  }

  private void destroyTool(ToolType type) {
    for (int i = minToolSlot; i <= maxToolSlot; i++) {
      if(type.itemMatches(inventory[i]) && inventory[i].stackSize==0) {
        inventory[i] = null;
        markDirty();
        return;
      }
    }
  }

  private int getLooting(ItemStack stack) {
    return Math.max(
        EnchantmentHelper.getEnchantmentLevel(Enchantment.looting.effectId, stack),
        EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));
  }

  @Override
  public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
    if(super.canExtractItem(slot, itemstack, side)) {
      return true;
    }
    //allow almost completely broken tools from Tinkers Construct to be extracted to get them repaired/recharged
    if(slot >= minToolSlot && slot <= maxToolSlot && itemstack.getItemDamage() >= 95) {
      Class<?> c = itemstack.getItem().getClass();
      do {
        if(c.getName().equals("tconstruct.library.tools.HarvestTool"))
          return true;
        c = c.getSuperclass();
      } while(c != null);
    }
    return false;
  }

  public EntityPlayerMP getFakePlayer() {
    return farmerJoe;
  }

  public Block getBlock(BlockCoord bc) {
    return worldObj.getBlock(bc.x, bc.y, bc.z);
  }

  public Block getBlock(int x, int y, int z) {
    return worldObj.getBlock(x, y, z);
  }

  public int getBlockMeta(BlockCoord bc) {
    return worldObj.getBlockMetadata(bc.x, bc.y, bc.z);
  }

  public boolean isOpen(BlockCoord bc) {
    Block block = worldObj.getBlock(bc.x, bc.y, bc.z);
    return block.isAir(worldObj, bc.x, bc.y, bc.z) || block.isReplaceable(worldObj, bc.x, bc.y, bc.z);
  }

  public void setNotification(String unloc) {
    String newNote = EnderIO.lang.localize("farm.note." + unloc);
    if(!newNote.equals(notification)) {
      notification = newNote;
      sendNotification = true;
    }
  }

  public void clearNotification() {
    if(hasNotification()) {
      notification = "";
      sendNotification = true;
    }
  }

  public boolean hasNotification() {
    return !"".equals(notification);
  }

  private void sendNotification() {
    PacketHandler.INSTANCE.sendToAll(new PacketUpdateNotification(this, notification));
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack stack) {
    if(stack == null) {
      return false;
    }
    if(i <= maxToolSlot) {
      if (ToolType.isTool(stack)) {
        for (int j = minToolSlot; j <= maxToolSlot; j++) {
          if (ToolType.getToolType(stack).itemMatches(inventory[j])) {
            return false;
          }
        }
        return true;
      }
      return false;
    } else if (i <= maxFirtSlot) {
      return Fertilizer.isFertilizer(stack);
    } else if (i <= maxSupSlot) {
      return (inventory[i] != null || !isSlotLocked(i)) && FarmersCommune.instance.canPlant(stack);
    } else {
      return false;
    }
  }

  @Override
  public void doUpdate() {
    super.doUpdate();
    if(isActive() != wasActive) {
      wasActive = isActive();
      worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
    }
  }

  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if(redstoneChecksPassed) {
      usePower();
      if(canTick(redstoneChecksPassed)) {
        doTick();
      }
    }
    return false;
  }

  protected boolean canTick(boolean redstoneChecksPassed) {
    if(!shouldDoWorkThisTick(2)) {
      return false;
    }
    if(getEnergyStored() < getPowerUsePerTick()) {
      setNotification("noPower");
      return false;
    }
    return true;
  }

  protected void doTick() {

    if (sendNotification && shouldDoWorkThisTick(TICKS_PER_WORK)) {
      sendNotification = false;
      sendNotification();
    }

    if(!hasPower() && Config.farmActionEnergyUseRF > 0 && Config.farmAxeActionEnergyUseRF > 0) {
      setNotification("noPower");
      return;
    }
    if("noPower".equals(notification)) {
      clearNotification();
    }

    BlockCoord bc = null;
    int infiniteLoop = 20;
    while (bc == null || bc.equals(getLocation()) || !worldObj.getChunkProvider().chunkExists(bc.x >> 4, bc.z >> 4)) {
      if (infiniteLoop-- <= 0) {
        return;
      }
      bc = getNextCoord();
    }

    Block block = worldObj.getBlock(bc.x, bc.y, bc.z);
    if(block == null) {
      return;
    }
    int meta = worldObj.getBlockMetadata(bc.x, bc.y, bc.z);
    if(farmerJoe == null) {
      farmerJoe = new FakeFarmPlayer(MinecraftServer.getServer().worldServerForDimension(worldObj.provider.dimensionId));
    }

    if(isOpen(bc)) {
      FarmersCommune.instance.prepareBlock(this, bc, block, meta);
      block = worldObj.getBlock(bc.x, bc.y, bc.z);
    }

    if(isOutputFull()) {
      setNotification("outputFull");
      return;
    }

    if(!hasPower() && Config.farmActionEnergyUseRF > 0 && Config.farmAxeActionEnergyUseRF > 0) {
      setNotification("noPower");
      return;
    }

    if(!isOpen(bc)) {
      IHarvestResult harvest = FarmersCommune.instance.harvestBlock(this, bc, block, meta);
      if(harvest != null && harvest.getDrops() != null) {
        PacketFarmAction pkt = new PacketFarmAction(harvest.getHarvestedBlocks());
        PacketHandler.INSTANCE.sendToAllAround(pkt, new TargetPoint(worldObj.provider.dimensionId, bc.x, bc.y, bc.z, 64));
        for (EntityItem ei : harvest.getDrops()) {
          if(ei != null) {
            insertHarvestDrop(ei, bc);
            if(!ei.isDead) {
              worldObj.spawnEntityInWorld(ei);
            }
          }
        }
        return;
      }
    }

    if(!hasPower() && (Config.farmBonemealActionEnergyUseRF > 0 || Config.farmBonemealTryEnergyUseRF > 0)) {
      setNotification("noPower");
      return;
    }

    if (hasBonemeal() && bonemealCooldown-- <= 0) {
      Fertilizer fertilizer = Fertilizer.getInstance(inventory[minFirtSlot]);
      if ((fertilizer.applyOnPlant() != isOpen(bc)) || (fertilizer.applyOnAir() == worldObj.isAirBlock(bc.x, bc.y, bc.z))) {
        farmerJoe.inventory.mainInventory[0] = inventory[minFirtSlot];
        farmerJoe.inventory.currentItem = 0;
        if (fertilizer.apply(inventory[minFirtSlot], farmerJoe, worldObj, bc)) {
          inventory[minFirtSlot] = farmerJoe.inventory.mainInventory[0];
          PacketHandler.INSTANCE.sendToAllAround(new PacketFarmAction(bc), new TargetPoint(worldObj.provider.dimensionId, bc.x, bc.y, bc.z, 64));
          if (inventory[minFirtSlot] != null && inventory[minFirtSlot].stackSize == 0) {
            inventory[minFirtSlot] = null;
          }
          usePower(Config.farmBonemealActionEnergyUseRF);
          bonemealCooldown = 20;
        } else {
          usePower(Config.farmBonemealTryEnergyUseRF);
          bonemealCooldown = 5;
        }
        farmerJoe.inventory.mainInventory[0] = null;
      }
    }
  }

  private int bonemealCooldown = 5; // no need to persist this
  
  private boolean hasBonemeal() {
    if (inventory[minFirtSlot] != null) {
      return true;
    }
    for (int i = minFirtSlot + 1; i <= maxFirtSlot; i++) {
      if (inventory[i] != null) {
        inventory[minFirtSlot] = inventory[i];
        inventory[i] = null;
        return true;
      }
    }
    return false;
  }

  private boolean isOutputFull() {
    for (int i = slotDefinition.minOutputSlot; i <= slotDefinition.maxOutputSlot; i++) {
      ItemStack curStack = inventory[i];
      if(curStack == null || curStack.stackSize < curStack.getMaxStackSize()) {
        return false;
      }
    }
    return true;
  }

  public boolean hasSeed(ItemStack seeds, BlockCoord bc) {
    int slot = getSupplySlotForCoord(bc);
    ItemStack inv = inventory[slot];
    return inv != null && (inv.stackSize > 1 || !isSlotLocked(slot)) && inv.isItemEqual(seeds);
  }

  /*
   * Returns a fuzzy boolean:
   * 
   * <=0 - break no leaves for saplings
   *  50 - break half the leaves for saplings
   *  90 - break 90% of the leaves for saplings
   */
  public int isLowOnSaplings(BlockCoord bc) {
    int slot = getSupplySlotForCoord(bc);
    ItemStack inv = inventory[slot];
    
    return 90 * (Config.farmSaplingReserveAmount - (inv == null ? 0 : inv.stackSize)) / Config.farmSaplingReserveAmount;
  }

  public ItemStack takeSeedFromSupplies(ItemStack stack, BlockCoord forBlock) {
    return takeSeedFromSupplies(stack, forBlock, true);
  }

  public ItemStack takeSeedFromSupplies(ItemStack stack, BlockCoord forBlock, boolean matchMetadata) {
    if(stack == null || forBlock == null) {
      return null;
    }
    int slot = getSupplySlotForCoord(forBlock);
    ItemStack inv = inventory[slot];
    if(inv != null) {
      if(matchMetadata ? inv.isItemEqual(stack) : inv.getItem() == stack.getItem()) {
        if (inv.stackSize <= 1 && isSlotLocked(slot)) {
          return null;
        }

        ItemStack result = inv.copy();
        result.stackSize = 1;

        inv = inv.copy();
        inv.stackSize--;
        if(inv.stackSize == 0) {
          inv = null;
        }
        setInventorySlotContents(slot, inv);
        return result;
      }
    }
    return null;
  }

  public ItemStack takeSeedFromSupplies(BlockCoord bc) {
    return takeSeedFromSupplies(getSeedTypeInSuppliesFor(bc), bc);
  }

  public ItemStack getSeedTypeInSuppliesFor(BlockCoord bc) {
    int slot = getSupplySlotForCoord(bc);
    ItemStack inv = inventory[slot];
    if(inv != null && (inv.stackSize > 1 || !isSlotLocked(slot))) {
      return inv.copy();
    }
    return null;
  }

  protected int getSupplySlotForCoord(BlockCoord forBlock) {

    if(forBlock.x <= xCoord && forBlock.z > zCoord) {
      return minSupSlot;
    } else if(forBlock.x > xCoord && forBlock.z > zCoord - 1) {
      return minSupSlot + 1;
    } else if(forBlock.x < xCoord && forBlock.z <= zCoord) {
      return minSupSlot + 2;
    }
    return minSupSlot + 3;
  }

  private void insertHarvestDrop(Entity entity, BlockCoord bc) {
    if(!worldObj.isRemote) {
      if(entity instanceof EntityItem && !entity.isDead) {
        EntityItem item = (EntityItem) entity;
        ItemStack stack = item.getEntityItem().copy();
        int numInserted = insertResult(stack, bc);
        stack.stackSize -= numInserted;
        item.setEntityItemStack(stack);
        if(stack.stackSize == 0) {
          item.setDead();
        }
      }
    }
  }


  private int insertResult(ItemStack stack, BlockCoord bc) {

    int slot = bc != null ? getSupplySlotForCoord(bc) : minSupSlot;
    int[] slots = new int[NUM_SUPPLY_SLOTS];
    int k = 0;
    for (int j = slot; j <= maxSupSlot; j++) {
      slots[k++] = j;
    }
    for (int j = minSupSlot; j < slot; j++) {
      slots[k++] = j;
    }
    
    int origSize = stack.stackSize;
    stack = stack.copy();

    int inserted = 0;
    for (int j = 0; j < slots.length && inserted < stack.stackSize; j++) {
      int i = slots[j];
      ItemStack curStack = inventory[i];
      int inventoryStackLimit = getInventoryStackLimit(i);
      if(isItemValidForSlot(i, stack) && (curStack == null || curStack.stackSize < inventoryStackLimit)) {
        if(curStack == null) {
          if (stack.stackSize < inventoryStackLimit) {
          inventory[i] = stack.copy();
          inserted = stack.stackSize;
          } else {
            inventory[i] = stack.copy();
            inserted = inventoryStackLimit;
            inventory[i].stackSize = inserted;
          }
        } else if(curStack.isItemEqual(stack)) {
          inserted = Math.min(inventoryStackLimit - curStack.stackSize, stack.stackSize);
          inventory[i].stackSize += inserted;
        }
      }
    }

    stack.stackSize -= inserted;
    if(inserted >= origSize) {
      return origSize;
    }

    ResultStack[] in = new ResultStack[] { new ResultStack(stack) };
    mergeResults(in);
    return origSize - (in[0].item == null ? 0 : in[0].item.stackSize);

  }

  private @Nonnull BlockCoord getNextCoord() {

    int size = getFarmSize();

    BlockCoord loc = getLocation();
    if(lastScanned == null) {
      return lastScanned = new BlockCoord(loc.x - size, loc.y, loc.z - size);
    }

    int nextX = lastScanned.x + 1;
    int nextZ = lastScanned.z;
    if(nextX > loc.x + size) {
      nextX = loc.x - size;
      nextZ += 1;
      if(nextZ > loc.z + size) {
        nextX = loc.x - size;
        nextZ = loc.z - size;
      }
    }
    return lastScanned = new BlockCoord(nextX, lastScanned.y, nextZ);
  }

  public void toggleLockedState(int slot) {
    if (worldObj.isRemote) {
      PacketHandler.INSTANCE.sendToServer(new PacketFarmLockedSlot(this, slot));
    }
    lockedSlots.flip(slot);
  }

  public boolean isSlotLocked(int slot) {
    return lockedSlots.get(slot);
  }

  @Override
  public String getInventoryName() {
    return EnderIO.blockFarmStation.getLocalizedName();
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockFarmStation.unlocalisedName;
  }

  @Override
  public float getProgress() {
    return 0.5f;
  }

  @Override
  public void onCapacitorTypeChange() {
    int ppt = calcPowerUsePerTick();
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      setCapacitor(new BasicCapacitor(ppt * 40, 250000, ppt));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(ppt * 40, 500000, ppt));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(ppt * 40, 1000000, ppt));
      break;
    }
    currentTask = createTask();
  }

  private int calcPowerUsePerTick() {
    return Math.round(Config.farmContinuousEnergyUseRF * (getFarmSize()/(float)Config.farmDefaultSize ));
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    currentTask = createTask();
  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    lockedSlots.clear();
    for (int i : nbtRoot.getIntArray("lockedSlots")) {
      lockedSlots.set(i);
    }
    int slotLayoutVersion = nbtRoot.getInteger("slotLayoutVersion");
    if (slotLayoutVersion == 0) {
      inventory = (new ArrayMappingTool<ItemStack>("TTSSSSOOOOC", "TTTBBSSSSOOOOOOC")).map(inventory);
    } else if (slotLayoutVersion == 1) {
      inventory = (new ArrayMappingTool<ItemStack>("TTTSSSSOOOOC", "TTTBBSSSSOOOOOOC")).map(inventory);
    } else if (slotLayoutVersion == 2) {
      inventory = (new ArrayMappingTool<ItemStack>("TTTSSSSOOOOOOC", "TTTBBSSSSOOOOOOC")).map(inventory);
    }
  }

  IPoweredTask createTask() {
    return new ContinuousTask(getPowerUsePerTick());
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    nbtRoot.setBoolean("isActive", isActive());
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    if(!lockedSlots.isEmpty()) {
      int[] locked = new int[lockedSlots.cardinality()];
      for (int i=0,bit=-1; (bit=lockedSlots.nextSetBit(bit+1)) >= 0; i++) {
        locked[i] = bit;
      }
      nbtRoot.setIntArray("lockedSlots", locked);
    }
    nbtRoot.setInteger("slotLayoutVersion", 3);
  }

  @Override
  public int getInventoryStackLimit(int slot) {
    if (slot >= minSupSlot && slot <= maxSupSlot) {
      switch (getCapacitorType()) {
      case BASIC_CAPACITOR:
        return 16;
      case ACTIVATED_CAPACITOR:
        return 32;
      case ENDER_CAPACITOR:
        return 64;
      }
    }
    return 64;
  }

  @Override
  public int getInventoryStackLimit() {
    // We return the (lowered) input slot limit here, so others who insert into us
    // will behave nicely.
    return getInventoryStackLimit(minSupSlot);
  }

}
