package crazypants.enderio.machine.farm;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.farm.farmers.FarmersCommune;
import crazypants.enderio.machine.farm.farmers.IHarvestResult;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.util.BlockCoord;
import crazypants.util.Lang;
import crazypants.util.Util;

public class TileFarmStation extends AbstractPoweredTaskEntity {

  public static final String NOTIFICATION_NO_HOE = "noHoe";
  public static final String NOTIFICATION_NO_AXE = "noAxe";
  public static final String NOTIFICATION_NO_SEEDS = "noSeeds";
  
  private BlockCoord lastScanned;
  private EntityPlayerMP farmerJoe;

  private int farmSize = Config.farmDefaultSize;

  private int minToolSlot = 0;
  private int maxToolSlot = 1;

  private int minSupSlot = maxToolSlot + 1;
  private int maxSupSlot = minSupSlot + 4;

  private final int upgradeBonusSize = 2;

  private ICapacitor cap = new BasicCapacitor(200, 25000);

  public int tier = 1;

  public String notification = "";
  public boolean sendNotification = false;

  public TileFarmStation() {
    super(new SlotDefinition(6, 4, 1));
    setCapacitor(Capacitors.BASIC_CAPACITOR);    
  }

  public int getFarmSize() {
    return farmSize + getUpgradeDist();
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
      return upgradeBonusSize * inventory[upg].getItemDamage();
    }
  }

  public boolean hasHoe() {
    return hasTool(ItemHoe.class);
  }

  public boolean hasAxe() {
    return hasTool(ItemAxe.class);
  }

  public int getAxeLootingValue() {
    ItemStack tool = getTool(ItemAxe.class);
    if(tool == null) {
      return 0;
    }
    return getLooting(tool);
  }

  public void damageAxe(Block blk, BlockCoord bc) {
    damageTool(ItemAxe.class, blk, bc, 1);
  }

  public void damageHoe(int i, BlockCoord bc) {
    damageTool(ItemHoe.class, null, bc, i);
  }

  public boolean hasTool(Class<?> class1) {
    return getTool(class1) != null;
  }

  private ItemStack getTool(Class<?> class1) {
    for (int i = minToolSlot; i <= maxToolSlot; i++) {
      if(Util.isType(inventory[i], class1)) {
        return inventory[i];
      }
    }
    return null;
  }

  public void damageTool(Class<?> class1, Block blk, BlockCoord bc, int damage) {

    float rand = worldObj.rand.nextFloat();
    if(rand >= Config.farmToolTakeDamageChance)
      return;

    ItemStack tool = getTool(class1);
    if(tool == null) {
      return;
    }
    if(tool.getItem() instanceof ItemAxe) {            
      tool.getItem().onBlockDestroyed(tool, worldObj, blk, bc.x, bc.y, bc.z, farmerJoe);      
    } else if(tool.getItem() instanceof ItemHoe) {
      int origDamage = tool.getItemDamage();
      tool.getItem().onItemUse(tool, farmerJoe, worldObj, bc.x, bc.y, bc.z, 1, 0.5f, 0.5f, 0.5f);
      if(origDamage == tool.getItemDamage() && tool.isItemStackDamageable()) {
        tool.damageItem(1, farmerJoe);
      }      
    } else if(tool.isItemStackDamageable()) {
      tool.damageItem(1, farmerJoe);
    }

    if(tool.isItemStackDamageable() && tool.getItemDamage() >= tool.getMaxDamage()) {
      destroyTool(class1);
    }
  }

  private boolean isLeaves(Block blk) {
    return (blk instanceof BlockOldLeaf || blk instanceof BlockNewLeaf);    
  }

  private void destroyTool(Class<?> class1) {
    for (int i = minToolSlot; i <= maxToolSlot; i++) {
      if(Util.isType(inventory[i], class1)) {
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
    String newNote = Lang.localize("farm.note." + unloc);
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
    if(i < 2) {
      for (Class<?> toolType : FarmersCommune.instance.getToolTypes()) {
        if(getTool(stack.getItem().getClass()) == null && (Util.isType(stack, toolType) || getLooting(stack) > 0)) {
          return true;
        }
      }
      return false;
    }
    return FarmersCommune.instance.canPlant(stack);
  }

  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    return super.checkProgress(redstoneChecksPassed) || doTick(redstoneChecksPassed);
  }

  protected boolean doTick(boolean redstoneCheckPassed) {

    if(worldObj.getTotalWorldTime() % 2 != 0) {
      return false;
    }
    
    if (sendNotification) {
      sendNotification = false;
      sendNotification();
    }       
    if(!redstoneCheckPassed) {
      return false;
    }
    if(!hasPower()) {
      setNotification("noPower");
      return false;
    }
    if("noPower".equals(notification)) {
      clearNotification();
    }

    BlockCoord bc = getNextCoord();
    if(bc != null && bc.equals(getLocation())) { //don't try and harvest ourselves
      bc = getNextCoord();
    }
    if(bc == null) {
      return false;
    }
    lastScanned = bc;

    Block block = worldObj.getBlock(bc.x, bc.y, bc.z);
    if(block == null) {
      return false;
    }
    int meta = worldObj.getBlockMetadata(bc.x, bc.y, bc.z);
    if(farmerJoe == null) {
      farmerJoe = new FakeFarmPlayer(MinecraftServer.getServer().worldServerForDimension(worldObj.provider.dimensionId));
    }

//    clearNotification();
    
    if(isOpen(bc)) {
      FarmersCommune.instance.prepareBlock(this, bc, block, meta);
      block = worldObj.getBlock(bc.x, bc.y, bc.z);
    }

    if(isOutputFull()) {
      setNotification("outputFull");
      return false;
    }
    
    if(!hasPower()) {
      setNotification("noPower");
      return false;
    }

    if(!isOpen(bc)) {
      IHarvestResult harvest = FarmersCommune.instance.harvestBlock(this, bc, block, meta);
      if(harvest != null) {
        if(harvest.getDrops() != null) {
          PacketFarmAction pkt = new PacketFarmAction(harvest.getHarvestedBlocks());
          PacketHandler.INSTANCE.sendToAllAround(pkt, new TargetPoint(worldObj.provider.dimensionId, bc.x, bc.y, bc.z, 64));
          for (EntityItem ei : harvest.getDrops()) {
            if(ei != null) {
              insertHarvestDrop(ei);
              if(!ei.isDead) {
                worldObj.spawnEntityInWorld(ei);
              }
            }
          }
        }
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
    if(inv != null && inv.isItemEqual(seeds)) {
      return true;
    }
    return false;
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
    if(inv != null) {
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

  private void insertHarvestDrop(Entity entity) {
    if(!worldObj.isRemote) {
      if(entity instanceof EntityItem && !entity.isDead) {
        EntityItem item = (EntityItem) entity;
        ItemStack stack = item.getEntityItem().copy();

        int numInserted = insertResult(stack);
        stack.stackSize -= numInserted;
        item.setEntityItemStack(stack);
        if(stack.stackSize == 0) {
          item.setDead();
        }
      }
    }

  }

  private int insertResult(ItemStack stack) {

    int origSize = stack.stackSize;
    stack = stack.copy();

    int inserted = 0;
    for (int i = slotDefinition.minInputSlot; i <= slotDefinition.maxInputSlot && inserted < stack.stackSize; i++) {
      ItemStack curStack = inventory[i];
      if(isItemValidForSlot(i, stack) && (curStack == null || curStack.stackSize < 16)) {
        if(curStack == null) {
          inventory[i] = stack.copy();
          inserted = stack.stackSize;
        } else if(curStack.isItemEqual(stack)) {
          inserted = Math.min(16 - curStack.stackSize, stack.stackSize);
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

  private BlockCoord getNextCoord() {

    int size = getFarmSize();

    BlockCoord loc = getLocation();
    if(lastScanned == null) {
      lastScanned = new BlockCoord(loc.x - size, loc.y, loc.z - size);
      return lastScanned;
    }

    int nextX = lastScanned.x + 1;
    int nextZ = lastScanned.z;
    if(nextX > loc.x + size) {
      nextX = loc.x - size;
      nextZ += 1;
      if(nextZ > loc.z + size) {
        lastScanned = null;
        return getNextCoord();
      }
    }
    return new BlockCoord(nextX, lastScanned.y, nextZ);
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
  public void setCapacitor(Capacitors capacitorType) {
    this.capacitorType = capacitorType;    
    tier = capacitorType.ordinal();
    currentTask = createTask();
    
    int ppt = getPowerUsePerTick();
    switch (capacitorType.ordinal()) {
    case 1:
      cap = new BasicCapacitor(ppt * 4, 500000);
      break;
    case 2:
      cap = new BasicCapacitor(ppt * 4, 1000000);
      break;
    default:
      cap = new BasicCapacitor(ppt * 4, 250000);
      break;
    }
    
  }

  @Override
  public ICapacitor getCapacitor() {
    return cap;
  }

  @Override
  public int getPowerUsePerTick() {
    return Math.round(Config.farmContinuousEnergyUseRF * (getFarmSize()/(float)Config.farmDefaultSize ));
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    currentTask = createTask();
    
  }
  
  IPoweredTask createTask() {
    return new ContinuousTask(getPowerUsePerTick());
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    nbtRoot.setBoolean("isActive", isActive());
  }

}
