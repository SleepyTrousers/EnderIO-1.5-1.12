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
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.power.PowerHandler.Type;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;
import crazypants.util.Util;

public class TileFarmStation extends AbstractPoweredTaskEntity /*implements IEntitySelector*/ {

  private static final float ENERGY_PER_TICK = Config.farmContinuousEnergyUse;

  private BlockCoord lastScanned;
  private EntityPlayerMP farmerJoe;

  private int farmSize = Config.farmDefaultSize;

  private int minToolSlot = 0;
  private int maxToolSlot = 1;

  private int minSupSlot = maxToolSlot + 1;
  private int maxSupSlot = minSupSlot + 4;
  
  private final int upgradeBonusSize = 2;

  private static final DummyTask TASK = new DummyTask();
  
  private ICapacitor cap = new BasicCapacitor(200,25000);
  
  public int tier = 1;

  public TileFarmStation() {
    super(new SlotDefinition(6, 4, 1));
    currentTask = TASK;
    powerHandler = PowerHandlerUtil.createHandler(cap, this, Type.MACHINE);
    
  }

  public int getFarmSize() {
    return farmSize + getUpgradeDist();
  }

  public void setFarmSize(int farmSize) {
    this.farmSize = farmSize;
  }

  public void actionPerformed() {
    usePower(Config.farmActionEnergyUse * (getUpgradeDist() * upgradeBonusSize));
  }
  
  public boolean tillBlock(BlockCoord plantingLocation) {    
    BlockCoord dirtLoc = plantingLocation.getLocation(ForgeDirection.DOWN);
    Block dirtBlock = getBlock(dirtLoc);
    if((dirtBlock == Blocks.dirt || dirtBlock == Blocks.grass) && hasHoe()) {
      damageHoe(1, dirtLoc);
      worldObj.setBlock(dirtLoc.x, dirtLoc.y, dirtLoc.z, Blocks.farmland);
      worldObj.playSoundEffect(dirtLoc.x + 0.5F, dirtLoc.y + 0.5F, dirtLoc.z + 0.5F, Blocks.farmland.stepSound.getStepResourcePath(),
          (Blocks.farmland.stepSound.getVolume() + 1.0F) / 2.0F, Blocks.farmland.stepSound.getPitch() * 0.8F);
      actionPerformed();
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
    if (inventory[upg] == null) {
      return 0;
    } else { 
      return upgradeBonusSize * inventory[upg].getItemDamage();
    }
  }

  public void damageMaxLootingItem(int damage, BlockCoord bc, Block block) {
    int maxLooting = -1;
    ItemStack toDamage = null;
    for (int i = minToolSlot; i <= maxToolSlot; i++) {
      if(inventory[i] != null) {
        int level = getLooting(inventory[i]);
        if(level > maxLooting) {
          maxLooting = level;
          toDamage = inventory[i];
        }
      }
    }
    if(toDamage != null) {
      damageTool(toDamage.getItem().getClass(), block, bc, damage);
    }
  }

  public boolean hasHoe() {
    return hasTool(ItemHoe.class);
  }
    
  public boolean hasAxe() {
    return hasTool(ItemAxe.class);
  }
  
  public boolean hasDefaultHarvestTool() {    
    return hasAxe() || hasHoe();
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
    if (rand >= Config.farmToolTakeDamageChance)
      return;
    
    ItemStack tool = getTool(class1);
    if(tool == null) {
      return;
    }
    
    if(tool.getItem() instanceof ItemAxe) {
      tool.getItem().onBlockDestroyed(tool, worldObj, blk, bc.x, bc.y, bc.z, farmerJoe);
    } else if(tool.getItem() instanceof ItemHoe) {
      tool.getItem().onItemUse(tool, farmerJoe, worldObj, bc.x, bc.y, bc.z, 1, 0.5f, 0.5f, 0.5f);
    } else if(tool.isItemStackDamageable()) {
      tool.damageItem(1, farmerJoe);
    }

    if(tool.isItemStackDamageable() && tool.getItemDamage() >= tool.getMaxDamage()) {
      destroyTool(class1);
    }
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

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack stack) {
    if(stack == null) {
      return false;
    }
    if(i < 2) {
      for(Class<?> toolType : FarmersCommune.instance.getToolTypes()) {
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

    if(!redstoneCheckPassed || !hasPower()) {
      return false;
    }
    if(worldObj.getTotalWorldTime() % 2 != 0) {
      return false;
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

    
    if(isOpen(bc)) {
      FarmersCommune.instance.prepareBlock(this, bc, block, meta);
      block = worldObj.getBlock(bc.x, bc.y, bc.z);
    }
    
    if(isOutputFull()) {
      return false;
    }
    
    if(!isOpen(bc) && hasPower()) {
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
    switch(capacitorType.ordinal()) {
    case 1:
      cap = new BasicCapacitor(400,50000);
      break;
    case 2:
      cap = new BasicCapacitor(1000,250000,20);
      break;
    default:
      cap = new BasicCapacitor(200,25000,50);
      break;
    }
    tier = capacitorType.ordinal();
    powerHandler.configure(cap.getMinEnergyReceived(), cap.getMaxEnergyReceived(), cap.getMinActivationEnergy(), cap.getMaxEnergyStored());
  }
  
  @Override
  public ICapacitor getCapacitor() {  
    return cap;
  }

  @Override
  public float getPowerUsePerTick() {
    return ENERGY_PER_TICK;
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    currentTask = TASK;
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    nbtRoot.setBoolean("isActive", isActive());
  }

  private static class DummyTask implements IPoweredTask {
    @Override
    public void writeToNBT(NBTTagCompound nbtRoot) {
    }

    @Override
    public void update(float availableEnergy) {
    }

    @Override
    public boolean isComplete() {
      return false;
    }

    @Override
    public float getRequiredEnergy() {
      return ENERGY_PER_TICK;
    }

    @Override
    public IMachineRecipe getRecipe() {
      return null;
    }

    @Override
    public float getProgress() {
      return 0.5f;
    }

    @Override
    public ResultStack[] getCompletedResult() {
      return new ResultStack[0];
    }

    @Override
    public float getChance() {
      return 1;
    }

    @Override
    public MachineRecipeInput[] getInputs() {
      return new MachineRecipeInput[0];
    }
  }

}
