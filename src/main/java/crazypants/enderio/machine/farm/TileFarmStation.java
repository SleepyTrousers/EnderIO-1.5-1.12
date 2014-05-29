package crazypants.enderio.machine.farm;

import java.util.List;

import buildcraft.api.power.PowerHandler.Type;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.render.BoundingBox;
import crazypants.util.BlockCoord;
import crazypants.util.ItemUtil;
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

  private static final DummyTask TASK = new DummyTask();
  
  private ICapacitor cap = new BasicCapacitor(200,25000);

  public TileFarmStation() {
    super(new SlotDefinition(6, 4, 0));
    currentTask = TASK;
    powerHandler = PowerHandlerUtil.createHandler(cap, this, Type.MACHINE);
  }

  public int getFarmSize() {
    return farmSize;
  }

  public void setFarmSize(int farmSize) {
    this.farmSize = farmSize;
  }

  public void actionPerformed() {
    usePower(Config.farmActionEnergyUse);
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

  public void damageMaxLootingItem() {
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
      damageTool(toDamage.getItem().getClass(), 1);
    }
  }

  public boolean hasHoe() {
    return hasTool(ItemHoe.class);
  }
    
  public boolean hasAxe() {
    return hasTool(ItemAxe.class);
  }
  
  public boolean hasHarvestTool() {    
    return hasAxe() || hasHoe();
  }

  public int getAxeLootingValue() {
    ItemStack tool = getTool(ItemAxe.class);
    if(tool == null) {
      return 0;
    }
    return getLooting(tool);
  }

  
  public void damageAxe() {
    damageTool(ItemAxe.class, 1);
  }

  public void damageHoe(int i) {
    damageTool(ItemHoe.class, i);
  }

  private boolean hasTool(Class<? extends Item> class1) {
    return getTool(class1) != null;
  }

  private ItemStack getTool(Class<? extends Item> class1) {
    for (int i = minToolSlot; i <= maxToolSlot; i++) {
      if(Util.isType(inventory[i], class1)) {
        return inventory[i];
      }
    }
    return null;
  }

  private void destroyTool(Class<? extends Item> class1) {
    for (int i = minToolSlot; i <= maxToolSlot; i++) {
      if(Util.isType(inventory[i], class1)) {
        inventory[i] = null;
        markDirty();
        return;
      }
    }

  }

  private void damageTool(Class<? extends Item> class1, int damage) {
    ItemStack tool = getTool(class1);
    if(tool != null) {
      tool.damageItem(damage, farmerJoe);
      if(tool.getItemDamage() >= tool.getMaxDamage()) {
        destroyTool(class1);
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

  public int getBlockMeta(BlockCoord bc) {
    return worldObj.getBlockMetadata(bc.x, bc.y, bc.z);
  }
  
  public boolean isAir(BlockCoord bc) {
    return worldObj.isAirBlock(bc.x, bc.y, bc.z);
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack stack) {
    if(stack == null) {
      return false;
    }
    if(i < 2) {
      return 
          (Util.isType(stack, ItemHoe.class) && !hasHoe()) || 
          (Util.isType(stack, ItemAxe.class) && !hasAxe()) || 
          (getTool(stack.getItem().getClass()) == null && getLooting(stack) > 0);
    }
    return FarmersComune.instance.canPlant(stack);
  }

  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    return super.checkProgress(redstoneChecksPassed) || doTick(redstoneChecksPassed);
  }

  protected boolean doTick(boolean redstoneCheckPassed) {

    if(!redstoneCheckPassed || !hasPower()) {
      return false;
    }
    if(worldObj.getWorldTime() % 2 != 0) {
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

    
    if(isAir(bc)) {
      FarmersComune.instance.prepareBlock(this, bc, block, meta);
      block = worldObj.getBlock(bc.x, bc.y, bc.z);
    }
    if(!isAir(bc) && hasPower()) {
      IHarvestResult harvest = FarmersComune.instance.harvestBlock(this, bc, block, meta);
      if(harvest != null) {
        if(harvest.getDrops() != null) {
          PacketFarmAction pkt = new PacketFarmAction(harvest.getHarvestedBlocks());
          EnderIO.packetPipeline.sendToAllAround(pkt, new TargetPoint(worldObj.provider.dimensionId, bc.x, bc.y, bc.z, 64));
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

  public boolean hasSeed(ItemStack seeds, BlockCoord bc) {
    int slot = getSupplySlotForCoord(bc);
    ItemStack inv = inventory[slot];
    if(inv != null && inv.isItemEqual(seeds)) {
      return true;
    }
    return false;
  }

  public ItemStack getSeedFromSupplies(ItemStack stack, BlockCoord forBlock) {
    return getSeedFromSupplies(stack, forBlock, true);
  }

  public ItemStack getSeedFromSupplies(ItemStack stack, BlockCoord forBlock, boolean matchMetadata) {
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

    BlockCoord loc = getLocation();
    if(lastScanned == null) {
      lastScanned = new BlockCoord(loc.x - farmSize, loc.y, loc.z - farmSize);
      return lastScanned;
    }

    int nextX = lastScanned.x + 1;
    int nextZ = lastScanned.z;
    if(nextX > loc.x + farmSize) {
      nextX = loc.x - farmSize;
      nextZ += 1;
      if(nextZ > loc.z + farmSize) {
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
    //no support for capacitor upgrades
//    powerHandler = PowerHandlerUtil.createHandler(cap, this, Type.MACHINE);
//    powerHandler.setEnergy(storedEnergy);    
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
  }

}
