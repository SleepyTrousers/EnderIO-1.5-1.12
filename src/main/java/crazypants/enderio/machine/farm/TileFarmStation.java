package crazypants.enderio.machine.farm;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.vecmath.Vector4f;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.FakePlayerEIO;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.farm.farmers.FarmersCommune;
import crazypants.enderio.machine.farm.farmers.IHarvestResult;
import crazypants.enderio.machine.ranged.IRanged;
import crazypants.enderio.machine.ranged.RangeParticle;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.NullHelper;
import crazypants.util.Prep;
import crazypants.util.Things;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;

import static crazypants.enderio.capacitor.CapacitorKey.FARM_BASE_SIZE;
import static crazypants.enderio.capacitor.CapacitorKey.FARM_BONUS_SIZE;
import static crazypants.enderio.capacitor.CapacitorKey.FARM_POWER_BUFFER;
import static crazypants.enderio.capacitor.CapacitorKey.FARM_POWER_INTAKE;
import static crazypants.enderio.capacitor.CapacitorKey.FARM_POWER_USE;
import static crazypants.enderio.capacitor.CapacitorKey.FARM_STACK_LIMIT;
import static crazypants.enderio.capacitor.DefaultCapacitorData.BASIC_CAPACITOR;
import static crazypants.enderio.config.Config.farmEvictEmptyRFTools;
import static crazypants.enderio.config.Config.farmStopOnNoOutputSlots;

@Storable
public class TileFarmStation extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity, IRanged {

  private static final int TICKS_PER_WORK = 20;

  public static Things TREETAPS = new Things();

  public enum ToolType {
    HOE {
      @Override
      boolean match(ItemStack item) {
        return Config.farmHoes.contains(item);
      }
    },

    AXE {
      @Override
      boolean match(ItemStack item) {
        return item.getItem().getHarvestLevel(item, "axe", null, null) >= 0;
      }
    },
    TREETAP {
      @Override
      boolean match(ItemStack item) {
        return TREETAPS.contains(item);
      }
    },
    SHEARS {
      @Override
      boolean match(ItemStack item) {
        return item.getItem() instanceof ItemShears;
      }
    },
    NONE {
      @Override
      boolean match(ItemStack item) {
        return false;
      }
    };

    public final boolean itemMatches(ItemStack item) {
      return Prep.isValid(item) && match(item) && !isBrokenTinkerTool(item);
    }

    @SuppressWarnings("null")
    private static boolean isBrokenTinkerTool(ItemStack item) {
      return Prep.isValid(item) && item.hasTagCompound() && item.getTagCompound().hasKey("Stats")
          && item.getTagCompound().getCompoundTag("Stats").getBoolean("Broken");
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

  private BlockPos lastScanned;
  private FakePlayerEIO farmerJoe;
  private static GameProfile FARMER_PROFILE = new GameProfile(UUID.fromString("c1ddfd7f-120a-4437-8b64-38660d3ec62d"), "[EioFarmer]");

  public static final int NUM_TOOL_SLOTS = 3;

  private static final int minToolSlot = 0;
  private static final int maxToolSlot = -1 + NUM_TOOL_SLOTS;

  public static final int NUM_FERTILIZER_SLOTS = 2;

  public static final int minFirtSlot = maxToolSlot + 1;
  public static final int maxFirtSlot = maxToolSlot + NUM_FERTILIZER_SLOTS;

  public static final int NUM_SUPPLY_SLOTS = 4;

  public static final int minSupSlot = maxFirtSlot + 1;
  public static final int maxSupSlot = maxFirtSlot + NUM_SUPPLY_SLOTS;

  @Store
  private int lockedSlots = 0x00;

  public Set<FarmNotification> notification = EnumSet.noneOf(FarmNotification.class);
  public boolean sendNotification = false;

  private boolean wasActive;

  public TileFarmStation() {
    super(new SlotDefinition(9, 6, 1), FARM_POWER_INTAKE, FARM_POWER_BUFFER, FARM_POWER_USE);
  }

  public int getFarmSize() {
    return (int) (FARM_BASE_SIZE.getFloat(getCapacitorData()) + FARM_BONUS_SIZE.getFloat(getCapacitorData()));
  }

  public int getFarmBaseSize() {
    return (int) (FARM_BASE_SIZE.getFloat(BASIC_CAPACITOR) + FARM_BONUS_SIZE.getFloat(BASIC_CAPACITOR));
  }

  public void actionPerformed(boolean isAxe) {
    if (isAxe) {
      usePower(Config.farmAxeActionEnergyUseRF);
    } else {
      usePower(Config.farmActionEnergyUseRF);
    }
    clearNotification();
  }

  public boolean tillBlock(BlockPos plantingLocation) {
    BlockPos dirtLoc = plantingLocation.down();
    Block dirtBlock = getBlock(dirtLoc);
    if (dirtBlock == Blocks.FARMLAND) {
      return true;
    } else {
      ItemStack tool = getTool(ToolType.HOE);
      if (Prep.isInvalid(tool)) {
        if (dirtBlock == Blocks.DIRT || dirtBlock == Blocks.GRASS) {
          setNotification(FarmNotification.NO_HOE);
        }
        // else we don't know if the ground can even be tilled, so no notification
        return false;
      }


      boolean doDamage = worldObj.rand.nextFloat() < Config.farmToolTakeDamageChance && canDamage(tool);
      if (!doDamage) {
        tool = tool.copy();
      }

      int origDamage = tool.getItemDamage();
      EnumActionResult itemUse = tool.getItem().onItemUse(tool, farmerJoe, worldObj, dirtLoc, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);

      if (itemUse != EnumActionResult.SUCCESS) {
        return false;
      }

      if (doDamage) {
        if (origDamage == tool.getItemDamage()) {
          tool.damageItem(1, farmerJoe);
        }

        if (Prep.isInvalid(tool) || tool.stackSize == 0 || tool.getItemDamage() >= tool.getMaxDamage()) { // TODO 1.11
          destroyTool(ToolType.HOE);
          markDirty();
        }
      }

      worldObj.playSound(dirtLoc.getX() + 0.5F, dirtLoc.getY() + 0.5F, dirtLoc.getZ() + 0.5F, SoundEvents.BLOCK_GRASS_STEP, SoundCategory.BLOCKS,
          (Blocks.FARMLAND.getSoundType().getVolume() + 1.0F) / 2.0F, Blocks.FARMLAND.getSoundType().getPitch() * 0.8F, false);
      actionPerformed(false);
      return true;
    }
  }

  public int getMaxLootingValue() {
    int result = 0;
    for (int i = minToolSlot; i <= maxToolSlot; i++) {
      if (Prep.isValid(inventory[i])) {
        int level = getLooting(inventory[i]);
        if (level > result) {
          result = level;
        }
      }
    }
    return result;
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
    if (Prep.isInvalid(tool)) {
      return 0;
    }
    return getLooting(tool);
  }

  public void damageAxe(Block blk, BlockPos bc) {
    damageTool(ToolType.AXE, blk, bc, 1);
  }

  public void damageHoe(int i, BlockPos bc) {
    damageTool(ToolType.HOE, null, bc, i);
  }

  public void damageShears(Block blk, BlockPos bc) {
    damageTool(ToolType.SHEARS, blk, bc, 1);
  }

  public boolean hasTool(ToolType type) {
    return getTool(type) != null;
  }

  private boolean isDryRfTool(ItemStack stack) {
    if (!farmEvictEmptyRFTools || Prep.isInvalid(stack)) {
      return false;
    }
    IEnergyStorage cap = PowerHandlerUtil.getCapability(stack, null);
    if (cap == null) {
      return false;
    }
    return cap.getMaxEnergyStored() > 0 && cap.getEnergyStored() <= 0;
  }

  public ItemStack getTool(ToolType type) {
    for (int i = minToolSlot; i <= maxToolSlot; i++) {
      if (ToolType.isBrokenTinkerTool(inventory[i]) || isDryRfTool(inventory[i])) {
        for (int j = slotDefinition.minOutputSlot; j <= slotDefinition.maxOutputSlot; j++) {
          if (Prep.isInvalid(inventory[j])) {
            inventory[j] = inventory[i];
            inventory[i] = Prep.getEmpty();
            markDirty();
            break;
          }
        }
      } else if (type.itemMatches(inventory[i]) && inventory[i].stackSize > 0) {
        switch (type) {
        case AXE:
          removeNotification(FarmNotification.NO_AXE);
          break;
        case HOE:
          removeNotification(FarmNotification.NO_HOE);
          break;
        case TREETAP:
          removeNotification(FarmNotification.NO_TREETAP);
          break;
        default:
          break;
        }
        return inventory[i];
      }
    }
    return Prep.getEmpty();
  }

  public void damageTool(ToolType type, Block blk, BlockPos bc, int damage) {
    ItemStack tool = getTool(type);
    if (Prep.isInvalid(tool)) {
      return;
    }

    float rand = worldObj.rand.nextFloat();
    if (rand >= Config.farmToolTakeDamageChance) {
      return;
    }

    IBlockState bs = getBlockState(bc);

    boolean canDamage = canDamage(tool);
    if (type == ToolType.AXE) {
      tool.getItem().onBlockDestroyed(tool, worldObj, bs, bc, farmerJoe);
    } else if (type == ToolType.HOE) {
      int origDamage = tool.getItemDamage();
      tool.getItem().onItemUse(tool, farmerJoe, worldObj, bc, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
      if (origDamage == tool.getItemDamage() && canDamage) {
        tool.damageItem(1, farmerJoe);
      }
    } else if (canDamage) {
      tool.damageItem(1, farmerJoe);
    }

    if (Prep.isInvalid(tool) || tool.stackSize == 0 || (canDamage && tool.getItemDamage() >= tool.getMaxDamage())) {
      destroyTool(type);
      markDirty();
    }
  }

  private boolean canDamage(ItemStack stack) {
    return Prep.isValid(stack) && stack.isItemStackDamageable() && stack.getItem().isDamageable();
  }

  // TODO 1.11 clean up
  private void destroyTool(ToolType type) {
    for (int i = minToolSlot; i <= maxToolSlot; i++) {
      if (Prep.isValid(inventory[i]) && type.itemMatches(inventory[i]) && inventory[i].stackSize == 0) {
        inventory[i] = Prep.getEmpty();
        return;
      }
    }
  }

  private int getLooting(ItemStack stack) {
    return Math.max(EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, stack), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
  }

  public EntityPlayerMP getFakePlayer() {
    return farmerJoe;
  }

  public Block getBlock(BlockPos posIn) {
    return getBlockState(posIn).getBlock();
  }

  public IBlockState getBlockState(BlockPos posIn) {
    return worldObj.getBlockState(posIn);
  }

  public boolean isOpen(BlockPos bc) {
    Block block = getBlock(bc);
    IBlockState bs = getBlockState(bc);
    return block.isAir(bs, worldObj, bc) || block.isReplaceable(worldObj, bc);
  }

  public void setNotification(FarmNotification note) {
    if (!notification.contains(note)) {
      notification.add(note);
      sendNotification = true;
    }
  }

  public void removeNotification(FarmNotification note) {
    if (notification.remove(note)) {
      sendNotification = true;
    }
  }

  public void clearNotification() {
    if (hasNotification()) {
      notification.clear();
      sendNotification = true;
    }
  }

  public boolean hasNotification() {
    return !notification.isEmpty();
  }

  private void sendNotification() {
    PacketHandler.INSTANCE.sendToAll(new PacketUpdateNotification(this, notification));
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack stack) {
    if (Prep.isInvalid(stack)) {
      return false;
    }
    if (i <= maxToolSlot) {
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
      return (Prep.isValid(inventory[i]) || !isSlotLocked(i)) && FarmersCommune.instance.canPlant(stack);
    } else {
      return false;
    }
  }

  @Override
  public void doUpdate() {
    super.doUpdate();
    if (isActive() != wasActive) {
      wasActive = isActive();
      worldObj.checkLightFor(EnumSkyBlock.BLOCK, pos);
    }
  }

  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if (shouldDoWorkThisTick(6 * 60 * 20)) {
      clearNotification();
    }
    if (redstoneChecksPassed) {
      usePower();
      if (canTick(redstoneChecksPassed)) {
        doTick();
      }
    }
    return false;
  }

  protected boolean canTick(boolean redstoneChecksPassed) {
    if (!shouldDoWorkThisTick(2)) {
      return false;
    }
    if (getEnergyStored() < getPowerUsePerTick()) {
      setNotification(FarmNotification.NO_POWER);
      return false;
    }
    return true;
  }

  protected void doTick() {
    if (sendNotification && shouldDoWorkThisTick(TICKS_PER_WORK)) {
      sendNotification = false;
      sendNotification();
    }

    if (!hasPower() && Config.farmActionEnergyUseRF > 0 && Config.farmAxeActionEnergyUseRF > 0) {
      setNotification(FarmNotification.NO_POWER);
      return;
    }
    removeNotification(FarmNotification.NO_POWER);

    if (farmerJoe == null) {
      farmerJoe = new FakePlayerEIO(worldObj, getLocation(), FARMER_PROFILE);
      farmerJoe.setOwner(getOwner());
      farmerJoe.worldObj = new PickupWorld(worldObj, farmerJoe);
    }

    BlockPos bc = null;
    IBlockState bs = null;
    int infiniteLoop = 20;
    while (bc == null || bc.equals(getPos()) || !worldObj.isBlockLoaded(bc)
        || !PermissionAPI.hasPermission(getOwner().getAsGameProfile(), BlockFarmStation.permissionFarming, new BlockPosContext(farmerJoe, bc, bs, null))) {
      if (infiniteLoop-- <= 0) {
        return;
      }
      bc = getNextCoord();
      bs = getBlockState(bc);
    }

    Block block = bs.getBlock();

    if (isOpen(bc)) {
      FarmersCommune.instance.prepareBlock(this, bc, block, bs);
      bs = getBlockState(bc);
      block = bs.getBlock();
    }

    if (isOutputFull()) {
      setNotification(FarmNotification.OUTPUT_FULL);
      return;
    }
    removeNotification(FarmNotification.OUTPUT_FULL);

    if (!hasPower() && Config.farmActionEnergyUseRF > 0 && Config.farmAxeActionEnergyUseRF > 0) {
      setNotification(FarmNotification.NO_POWER);
      return;
    }

    if (!isOpen(bc)) {
      IHarvestResult harvest = FarmersCommune.instance.harvestBlock(this, bc, block, bs);
      if (harvest != null && harvest.getDrops() != null && !harvest.getDrops().isEmpty()) {
        PacketFarmAction pkt = new PacketFarmAction(harvest.getHarvestedBlocks());
        PacketHandler.INSTANCE.sendToAllAround(pkt, new TargetPoint(worldObj.provider.getDimension(), bc.getX(), bc.getY(), bc.getZ(), 64));
        for (EntityItem ei : harvest.getDrops()) {
          if (ei != null) {
            insertHarvestDrop(ei, bc);
            if (!ei.isDead) {
              worldObj.spawnEntityInWorld(ei);
            }
          }
        }
        return;
      }
    }

    if (!hasPower() && (Config.farmBonemealActionEnergyUseRF > 0 || Config.farmBonemealTryEnergyUseRF > 0)) {
      setNotification(FarmNotification.NO_POWER);
      return;
    }

    if (hasBonemeal() && bonemealCooldown-- <= 0 && random.nextFloat() <= .75f) {
      Fertilizer fertilizer = Fertilizer.getInstance(inventory[minFirtSlot]);
      if ((fertilizer.applyOnPlant() != isOpen(bc)) || (fertilizer.applyOnAir() == worldObj.isAirBlock(bc))) {
        farmerJoe.inventory.mainInventory[0] = inventory[minFirtSlot];
        farmerJoe.inventory.currentItem = 0;
        if (fertilizer.apply(inventory[minFirtSlot], farmerJoe, worldObj, bc)) {
          inventory[minFirtSlot] = farmerJoe.inventory.mainInventory[0];
          PacketHandler.INSTANCE.sendToAllAround(new PacketFarmAction(bc),
              new TargetPoint(worldObj.provider.getDimension(), bc.getX(), bc.getY(), bc.getZ(), 64));
          if (Prep.isValid(inventory[minFirtSlot]) && inventory[minFirtSlot].stackSize == 0) {
            inventory[minFirtSlot] = Prep.getEmpty(); // TODO 1.11 remove
          }
          usePower(Config.farmBonemealActionEnergyUseRF);
          bonemealCooldown = 16;
        } else {
          usePower(Config.farmBonemealTryEnergyUseRF);
          bonemealCooldown = 4;
        }
        farmerJoe.inventory.mainInventory[0] = Prep.getEmpty();
      }
    }
  }

  private int bonemealCooldown = 4; // no need to persist this

  private boolean hasBonemeal() {
    if (Prep.isValid(inventory[minFirtSlot])) {
      return true;
    }
    for (int i = minFirtSlot + 1; i <= maxFirtSlot; i++) {
      if (Prep.isValid(inventory[i])) {
        inventory[minFirtSlot] = inventory[i];
        inventory[i] = Prep.getEmpty();
        return true;
      }
    }
    return false;
  }

  private boolean isOutputFull() {
    for (int i = slotDefinition.minOutputSlot; i <= slotDefinition.maxOutputSlot; i++) {
      ItemStack curStack = inventory[i];
      if (Prep.isInvalid(curStack) || (!farmStopOnNoOutputSlots && curStack.stackSize < curStack.getMaxStackSize())) {
        return false;
      }
    }
    return true;
  }

  public boolean hasSeed(ItemStack seeds, BlockPos bc) {
    int slot = getSupplySlotForCoord(bc);
    ItemStack inv = inventory[slot];
    return Prep.isValid(inv) && (inv.stackSize > 1 || !isSlotLocked(slot)) && inv.isItemEqual(seeds);
  }

  /*
   * Returns a fuzzy boolean:
   * 
   * <=0 - break no leaves for saplings 50 - break half the leaves for saplings 90 - break 90% of the leaves for saplings
   */
  public int isLowOnSaplings(BlockPos bc) {
    int slot = getSupplySlotForCoord(bc);
    ItemStack inv = inventory[slot];

    return 90 * (Config.farmSaplingReserveAmount - (Prep.isInvalid(inv) ? 0 : inv.stackSize)) / Config.farmSaplingReserveAmount; // TODO 1.11 clean up
  }

  public ItemStack takeSeedFromSupplies(ItemStack stack, BlockPos forBlock) {
    return takeSeedFromSupplies(stack, forBlock, true);
  }

  public ItemStack takeSeedFromSupplies(ItemStack stack, BlockPos forBlock, boolean matchMetadata) {
    if (Prep.isInvalid(stack) || forBlock == null) {
      return null;
    }
    int slot = getSupplySlotForCoord(forBlock);
    ItemStack inv = inventory[slot];
    if (Prep.isValid(inv)) {
      if (matchMetadata ? inv.isItemEqual(stack) : inv.getItem() == stack.getItem()) {
        if (inv.stackSize <= 1 && isSlotLocked(slot)) {
          return null;
        }

        ItemStack result = inv.copy();
        result.stackSize = 1;

        inv = inv.copy();
        inv.stackSize--;
        if (inv.stackSize == 0) {
          inv = Prep.getEmpty(); // TODO 1.11 clean up
        }
        setInventorySlotContents(slot, inv);
        return result;
      }
    }
    return null;
  }

  public ItemStack takeSeedFromSupplies(BlockPos bc) {
    return takeSeedFromSupplies(getSeedTypeInSuppliesFor(bc), bc);
  }

  public ItemStack getSeedTypeInSuppliesFor(BlockPos bc) {
    int slot = getSupplySlotForCoord(bc);
    return getSeedTypeInSuppliesFor(slot);
  }

  public ItemStack getSeedTypeInSuppliesFor(int slot) {
    ItemStack inv = inventory[slot];
    if (Prep.isValid(inv) && (inv.stackSize > 1 || !isSlotLocked(slot))) {
      return inv.copy();
    }
    return Prep.getEmpty();
  }

  public int getSupplySlotForCoord(BlockPos forBlock) {
    int xCoord = getPos().getX();
    int zCoord = getPos().getZ();
    if (forBlock.getX() <= xCoord && forBlock.getZ() > zCoord) {
      return minSupSlot;
    } else if (forBlock.getX() > xCoord && forBlock.getZ() > zCoord - 1) {
      return minSupSlot + 1;
    } else if (forBlock.getX() < xCoord && forBlock.getZ() <= zCoord) {
      return minSupSlot + 2;
    }
    return minSupSlot + 3;
  }

  private void insertHarvestDrop(Entity entity, BlockPos bc) {
    if (!worldObj.isRemote) {
      if (entity instanceof EntityItem && !entity.isDead) {
        EntityItem item = (EntityItem) entity;
        ItemStack stack = item.getEntityItem();
        if (Prep.isValid(stack)) {
          stack = stack.copy();
          int numInserted = insertResult(stack, bc);
          stack.stackSize -= numInserted;
          item.setEntityItemStack(stack);
          if (stack.stackSize == 0) {
            item.setDead();
          }
        } else {
          item.setDead();
        }
      }
    }
  }

  private int insertResult(ItemStack stack, BlockPos bc) {
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
      if (isItemValidForSlot(i, stack)) {
        if (Prep.isInvalid(curStack)) {
          if (stack.stackSize < inventoryStackLimit) {
            inventory[i] = stack.copy();
            inserted = stack.stackSize;
          } else {
            inventory[i] = stack.copy();
            inserted = inventoryStackLimit;
            inventory[i].stackSize = inserted;
          }
        } else if (curStack.stackSize < inventoryStackLimit && curStack.isItemEqual(stack)) {
          inserted = Math.min(inventoryStackLimit - curStack.stackSize, stack.stackSize);
          inventory[i].stackSize += inserted;
        }
      }
    }

    stack.stackSize -= inserted;
    if (inserted >= origSize) {
      return origSize;
    }

    ResultStack[] in = new ResultStack[] { new ResultStack(stack) };
    mergeResults(in);
    return origSize - (Prep.isInvalid(in[0].item) ? 0 : in[0].item.stackSize);
  }

  private @Nonnull BlockPos getNextCoord() {
    int size = getFarmSize();

    BlockPos loc = getPos();
    if (lastScanned == null) {
      return lastScanned = NullHelper.notnullM(loc.add(-size, 0, -size), "BlockPos.add()");
    }

    int nextX = lastScanned.getX() + 1;
    int nextZ = lastScanned.getZ();
    if (nextX > loc.getX() + size) {
      nextX = loc.getX() - size;
      nextZ += 1;
      if (nextZ > loc.getZ() + size) {
        nextX = loc.getX() - size;
        nextZ = loc.getZ() - size;
      }
    }
    return lastScanned = new BlockPos(nextX, lastScanned.getY(), nextZ);
  }

  public void toggleLockedState(int slot) {
    if (worldObj.isRemote) {
      PacketHandler.INSTANCE.sendToServer(new PacketFarmLockedSlot(this, slot));
    }
    setSlotLocked(slot, !isSlotLocked(slot));
  }

  public boolean isSlotLocked(int slot) {
    return (lockedSlots & (1 << slot)) != 0;
  }

  private void setSlotLocked(int slot, boolean value) {
    if (value) {
      lockedSlots = lockedSlots | (1 << slot);
    } else {
      lockedSlots = lockedSlots & ~(1 << slot);
    }
  }

  @Override
  public boolean hasCustomName() {
    return false;
  }

  @Override
  public @Nonnull String getMachineName() {
    return ModObject.blockFarmStation.getUnlocalisedName();
  }

  @Override
  public float getProgress() {
    return 0.5f;
  }

  @Override
  public void onCapacitorDataChange() {
    super.onCapacitorDataChange();
    currentTask = createTask(null, 0f);
  }

  @Override
  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
    return new ContinuousTask(getPowerUsePerTick());
  }

  @Override
  public int getInventoryStackLimit(int slot) {
    if (slot >= minSupSlot && slot <= maxSupSlot) {
      return Math.min(FARM_STACK_LIMIT.get(getCapacitorData()), 64);
    }
    return 64;
  }

  @Override
  public int getInventoryStackLimit() {
    // We return the (lowered) input slot limit here, so others who insert into us
    // will behave nicely.
    return getInventoryStackLimit(minSupSlot);
  }

  @Override
  public boolean shouldRenderInPass(int pass) {
    return pass == 1;
  }

  // RANGE

  private boolean showingRange;

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isShowingRange() {
    return showingRange;
  }

  private final static Vector4f color = new Vector4f(145f / 255f, 82f / 255f, 21f / 255f, .4f);

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if (showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if (showingRange) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new RangeParticle<TileFarmStation>(this, color));
    }
  }

  @Override
  public BoundingBox getBounds() {
    return new BoundingBox(getPos()).expand(getRange(), 0, getRange());
  }

  public float getRange() {
    return getFarmSize();
  }

  // RANGE END

}
