package crazypants.enderio.machine.farm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
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
import net.minecraftforge.oredict.OreDictionary;
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
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.util.BlockCoord;
import crazypants.util.Lang;

public class TileFarmStation extends AbstractPoweredTaskEntity {

  public enum ToolType {
    HOE { 
      boolean match(ItemStack item) {
        for (ItemStack stack : Config.farmHoes) {
          if (stack != null && OreDictionary.itemMatches(item, stack, false)) {
            return true;
          }
        }
        return false;
      }
    },
    
    AXE     { boolean match(ItemStack item) { return item.getItem().getHarvestLevel(item, "axe") > 0;         }},
    TREETAP { boolean match(ItemStack item) { return item.getItem().getClass() == RubberTreeFarmerIC2.treeTap;}};
    
    public final boolean itemMatches(ItemStack item) {
      if (item == null) {
        return false;
      }
      
      return match(item);
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
  }
  
  public static final String NOTIFICATION_NO_HOE = "noHoe";
  public static final String NOTIFICATION_NO_AXE = "noAxe";
  public static final String NOTIFICATION_NO_SEEDS = "noSeeds";
  
  private BlockCoord lastScanned;
  private EntityPlayerMP farmerJoe;

  private int farmSize = Config.farmDefaultSize;

  private int minToolSlot = 0;
  private int maxToolSlot = 1;

  int minSupSlot = maxToolSlot + 1;
  int maxSupSlot = minSupSlot + 4;
  
  List<Integer> lockedSlots = new ArrayList<Integer>();

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
    return hasTool(ToolType.HOE);
  }

  public boolean hasAxe() {
    return hasTool(ToolType.AXE);
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

  public boolean hasTool(ToolType type){
    return getTool(type) != null;
  }

  private ItemStack getTool(ToolType type) {
    for (int i = minToolSlot; i <= maxToolSlot; i++) {
      if(type.itemMatches(inventory[i])) {
        return inventory[i];
      }
    }
    return null;
  }

  public void damageTool(ToolType type, Block blk, BlockCoord bc, int damage) {

    float rand = worldObj.rand.nextFloat();
    if(rand >= Config.farmToolTakeDamageChance) {
      return;
    }

    ItemStack tool = getTool(type);
    if(tool == null) {
      return;
    }
    boolean canDamage = canDamage(tool);
    if(tool.getItem() instanceof ItemAxe) {            
      tool.getItem().onBlockDestroyed(tool, worldObj, blk, bc.x, bc.y, bc.z, farmerJoe);      
    } else if(tool.getItem() instanceof ItemHoe) {
      int origDamage = tool.getItemDamage();
      tool.getItem().onItemUse(tool, farmerJoe, worldObj, bc.x, bc.y, bc.z, 1, 0.5f, 0.5f, 0.5f);
      if(origDamage == tool.getItemDamage() && canDamage) {
        tool.damageItem(1, farmerJoe);
      }      
    } else if(canDamage) {
      tool.damageItem(1, farmerJoe);
    }

    if(canDamage && tool.getItemDamage() >= tool.getMaxDamage()) {
      destroyTool(type);
    }
  }
  
  private boolean canDamage(ItemStack stack) {
    return stack != null && stack.isItemStackDamageable() && stack.getItem().isDamageable();  
  }

  private void destroyTool(ToolType type) {
    for (int i = minToolSlot; i <= maxToolSlot; i++) {
      if(type.itemMatches(inventory[i])) {
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
    if(i <= maxToolSlot) {
        return ToolType.isTool(stack) || getLooting(stack) > 0;          
    }
    return (inventory[i] != null || !isSlotLocked(i)) && FarmersCommune.instance.canPlant(stack);
  }

  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if(canTick(redstoneChecksPassed) && redstoneChecksPassed) {
      doTick();
    }
    return false;
  }
  
  protected boolean canTick(boolean redstoneChecksPassed) {
    if(worldObj.getTotalWorldTime() % 2 != 0) {
      return false;
    }    
    if(getEnergyStored() < getPowerUsePerTick()) {
      setNotification("noPower");
      return false;
    }    
    if(redstoneChecksPassed) {
      usePower();
    }   
    int curScaled = getProgressScaled(16);
    if(curScaled != lastProgressScaled) {
      sendTaskProgressPacket();
      lastProgressScaled = curScaled;
    }    
    return true;
  }

  protected void doTick() {

    if (sendNotification && worldObj.getTotalWorldTime() % 20 == 0) {
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

    BlockCoord bc = getNextCoord();
    if(bc != null && bc.equals(getLocation())) { //don't try and harvest ourselves
      bc = getNextCoord();
    }
    if(bc == null) {
      return;
    }
    lastScanned = bc;

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
    return;
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
    if(inv != null && (inv.stackSize > 1 || !isSlotLocked(slot)) && inv.isItemEqual(seeds)) {
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
        } else if(curStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(curStack,stack)) {
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

  @Override
  protected int getNumCanMerge(ItemStack itemStack, ItemStack result) {
    if(!itemStack.isItemEqual(result) || !ItemStack.areItemStackTagsEqual(itemStack,result)) {
      return 0;
    }
    return Math.min(itemStack.getMaxStackSize() - itemStack.stackSize, result.stackSize);
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
  
  public void toggleLockedState(int buttonID) {
    if (worldObj.isRemote) {
      PacketHandler.INSTANCE.sendToServer(new PacketFarmLockedSlot(this, buttonID));
    }
    buttonID += minSupSlot;
    if (lockedSlots.contains(buttonID)) {
      lockedSlots.remove((Integer) buttonID); // hard cast otherwise it removes by index
    } else {
      lockedSlots.add(buttonID);
    }
  }
  public boolean isSlotLocked(int slot) {
    return lockedSlots.contains(slot);
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
      cap = new BasicCapacitor(ppt * 40, 500000);
      break;
    case 2:
      cap = new BasicCapacitor(ppt * 40, 1000000);
      break;
    default:
      cap = new BasicCapacitor(ppt * 40, 250000);
      break;
    }
    if(getEnergyStored() > getMaxEnergyStored()) {
      setEnergyStored(getMaxEnergyStored());
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
    for (int i : nbtRoot.getIntArray("lockedSlots")) {
      lockedSlots.add(i);
    }
  }
  
  IPoweredTask createTask() {
    return new ContinuousTask(getPowerUsePerTick());
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    nbtRoot.setBoolean("isActive", isActive());
    int[] locked = new int[lockedSlots.size()];
    for (int i = 0; i < lockedSlots.size(); i++) {
      locked[i] = lockedSlots.get(i);
    }
    nbtRoot.setIntArray("lockedSlots", locked);
  }

}
