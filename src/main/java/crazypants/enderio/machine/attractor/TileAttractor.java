package crazypants.enderio.machine.attractor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.FakePlayer;

import com.mojang.authlib.GameProfile;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.Capacitors;
import crazypants.render.BoundingBox;
import crazypants.util.BlockCoord;
import crazypants.vecmath.Vector3d;

public class TileAttractor extends AbstractMachineEntity {

  private AxisAlignedBB attractorBounds;
  private FakePlayer target;
  private int rangeSqu;
  private int range;
  private int powerPerTick;
  private final Set<EntityLiving> tracking = new HashSet<EntityLiving>();
  private int tickCounter = 0;

  public TileAttractor() {
    super(new SlotDefinition(12, 0));
    updateRange();
  }

  private void updateRange() {
    //  //TODO: Config
    switch (capacitorType) {
    case ACTIVATED_CAPACITOR:
      range = 32;
      powerPerTick = 60;
      break;
    case ENDER_CAPACITOR:
      range = 64;
      powerPerTick = 180;
      break;
    case BASIC_CAPACITOR:
    default:
      range = 16;
      powerPerTick = 20;
      break;
    }
    rangeSqu = range * range;

    BoundingBox bb = new BoundingBox(new BlockCoord(this));
    bb = bb.scale(range, range, range);
    attractorBounds = AxisAlignedBB.getBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
  }

  @Override
  public String getMachineName() {
    return ModObject.blockAttrator.unlocalisedName;
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
      usePower();
    } else {
      return false;
    }
    tickCounter++;
    if(tickCounter < 10) {
      return false;
    }
    tickCounter = 0;

    List<EntityLiving> entsInBounds = worldObj.getEntitiesWithinAABB(EntityLiving.class, attractorBounds);
    if(!entsInBounds.isEmpty()) {
      for (EntityLiving ent : entsInBounds) {
        if(!ent.isDead && !tracking.contains(ent) && isMobInFilter(ent)) {
          trackMob(ent);
        }
      }
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

  @Override
  public void setCapacitor(Capacitors capacitorType) {
    super.setCapacitor(capacitorType);
    attractorBounds = null;
    updateRange();
  }

  FakePlayer getTarget() {
    if(target == null) {
      target = new Target();
    }
    return target;
  }

  public boolean canAttract(String entityId, EntityLiving mob) {
    return redstoneCheckPassed && hasPower() && isMobInFilter(entityId) && isMobInRange(mob);
  }

  private boolean isMobInRange(EntityLiving mob) {
    return isMobInRange(mob, rangeSqu);
  }
  
  private boolean isMobInRange(EntityLiving mob, int range) {
    if(mob == null) {
      return false;
    }
    return new Vector3d(mob.posX, mob.posY, mob.posZ).distanceSquared(new Vector3d(xCoord, yCoord, zCoord)) <= range;
  }
  

  private boolean isMobInFilter(EntityLiving ent) {
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
  
  private void trackMob(EntityLiving ent) {               
    if(ent instanceof EntityEnderman) {      
      ((EntityEnderman) ent).setTarget(getTarget());
    } else {
      tracking.add(ent);
      ent.tasks.addTask(0, new AttractTask(ent, getTarget(), new BlockCoord(this)));
    }    
  }

  private class Target extends FakePlayer {

    ItemStack prevWeapon;

    public Target() {
      super(MinecraftServer.getServer().worldServerForDimension(getWorldObj().provider.dimensionId), new GameProfile(null,
          ModObject.blockAttrator.unlocalisedName + ":" + getLocation()));
      posX = xCoord + 0.5;
      posY = yCoord + 0.5;
      posZ = zCoord + 0.5;
    }
  }

  private static class AttractTask extends EntityAIBase {

    private EntityLiving mob;
    private BlockCoord coord;
    private FakePlayer target;
    private String entityId;

    private boolean keepGoing = true;
    
    private AttractTask(EntityLiving mob, FakePlayer target, BlockCoord coord) {
      this.mob = mob;
      this.coord = coord;
      this.target = target;
      entityId = EntityList.getEntityString(mob);
    }

    @Override
    public boolean shouldExecute() {
      return continueExecuting();
    }

    @Override
    public boolean continueExecuting() {
      boolean res = false;
      TileEntity te = mob.worldObj.getTileEntity(coord.x, coord.y, coord.z);
      if(te instanceof TileAttractor) {
        TileAttractor attractor = (TileAttractor) te;
        res = attractor.canAttract(entityId, mob);    
        if(attractor.isMobInRange(mob, 2)) {          
          res = false;
        }
      }            
      return res;
    }

    @Override
    public boolean isInterruptible() {
      return true;
    }

    @Override
    public void updateTask() {
      int speed = 1;
      mob.getNavigator().setAvoidsWater(false);
      mob.getNavigator().tryMoveToEntityLiving(target, speed);      
    }

  }

}
