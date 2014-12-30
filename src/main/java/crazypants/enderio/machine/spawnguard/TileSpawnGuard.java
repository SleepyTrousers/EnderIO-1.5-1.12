package crazypants.enderio.machine.spawnguard;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.ranged.IRanged;
import crazypants.enderio.machine.ranged.RangeEntity;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.render.BoundingBox;

public class TileSpawnGuard extends AbstractPowerConsumerEntity implements IRanged {

  private ICapacitor capacitor;
  private int powerPerTick;
  private int range;
  private int rangeSqu;
  private boolean registered = false;
  private AxisAlignedBB bounds;
  
  private boolean showingRange;
  
  public TileSpawnGuard() {
    super(new SlotDefinition(12, 0));
    setUpdrade(Capacitors.BASIC_CAPACITOR);    
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public boolean isShowingRange() {
    return showingRange;
  }
  
  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if(showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if(showingRange) {
      worldObj.spawnEntityInWorld(new RangeEntity(this));
    }
  }
  
  @Override
  public World getWorld() {
    return getWorldObj();
  }

  @Override
  public void invalidate() {
    super.invalidate();    
    SpawnGuardController.instance.deregisterGuard(this);
    registered = false;
  }
  
  @Override
  public float getRange() {
    return range;    
  }

  @Override
  public void setCapacitor(Capacitors capacitorType) {
    setUpdrade(capacitorType);
    super.setCapacitor(capacitorType);
  }

  @Override
  public ICapacitor getCapacitor() {
    return capacitor;
  }

  private void setUpdrade(Capacitors capacitorType) {    
    switch (capacitorType) {
    case ACTIVATED_CAPACITOR:
      range = Config.spawnGuardRangeLevelTwo;
      powerPerTick = Config.spawnGuardPowerPerTickLevelTwo;
      break;
    case ENDER_CAPACITOR:
      range = Config.spawnGuardRangeLevelThree;
      powerPerTick = Config.spawnGuardPowerPerTickLevelThree;
      break;
    case BASIC_CAPACITOR:
    default:
      range = Config.spawnGuardRangeLevelOne;
      powerPerTick = Config.spawnGuardPowerPerTickLevelOne;
      break;
    }
    rangeSqu = range * range;    
    capacitor = new BasicCapacitor(powerPerTick * 8, capacitorType.capacitor.getMaxEnergyStored(), powerPerTick);
    
    BoundingBox bb = new BoundingBox(getLocation());
    bb = bb.scale(range + 0.5f, range + 0.5f, range + 0.5f).translate(0.5f, 0.5f, 0.5f);    
    bounds = AxisAlignedBB.getBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
  }

  @Override
  public String getMachineName() {
    return ModObject.blockSpawnGuard.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if(!slotDefinition.isInputSlot(i)) {
      return false;
    }
    String mob = EnderIO.itemSoulVessel.getMobTypeFromStack(itemstack);
    if(mob == null) {
      return false;
    }
    Class<?> cl = (Class<?>) EntityList.stringToClassMapping.get(mob);
    if(cl == null) {
      return false;
    }
    return EntityLiving.class.isAssignableFrom(cl);
  }

  @Override
  public boolean isActive() {
    return hasPower();
  }

  @Override
  public float getProgress() {
    return 0;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    if(redstoneCheckPassed && hasPower()) {
      if(!registered) {
        SpawnGuardController.instance.registerGuard(this);
        registered = true;
      }
      usePower();
    } 
    return false;    
  }

  protected double usePower() {
    return usePower(getPowerUsePerTick());
  }

  protected int usePower(int wantToUse) {
    int used = Math.min(getEnergyStored(), wantToUse);
    setEnergyStored(Math.max(0, getEnergyStored() - used));
    return used;
  }

  @Override
  public int getPowerUsePerTick() {
    return powerPerTick;
  }

  public boolean isSpawnPrevented(EntityLivingBase mob) {
    return redstoneCheckPassed && hasPower() && isMobInRange(mob) && isMobInFilter(mob);
  }
  
  private boolean isMobInRange(EntityLivingBase mob) {
    if(mob == null) {
      return false;
    }    
    //return new Vector3d(mob.posX, mob.posY, mob.posZ).distanceSquared(new Vector3d(xCoord, yCoord, zCoord)) <= rangeSqu;
    return bounds.isVecInside(Vec3.createVectorHelper(mob.posX, mob.posY, mob.posZ));
  }

  private boolean isMobInFilter(EntityLivingBase ent) {
    return isMobInFilter(EntityList.getEntityString(ent));
  }

  private boolean isMobInFilter(String entityId) {
    for (int i = slotDefinition.minInputSlot; i <= slotDefinition.maxInputSlot; i++) {
      if(inventory[i] != null) {
        String mob = EnderIO.itemSoulVessel.getMobTypeFromStack(inventory[i]);
        if(mob != null && mob.equals(entityId)) {
          return true;
        }
      }
    }
    return false;
  }

}
