package crazypants.enderio.machine.obelisk.attractor;

import info.loenwind.autosave.annotations.Storable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.WalkNodeProcessor;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.vecmath.Vector3d;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.FakePlayerEIO;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.ranged.IRanged;
import crazypants.enderio.machine.ranged.RangeEntity;
import crazypants.util.CapturedMob;

import static crazypants.enderio.capacitor.CapacitorKey.ATTRACTOR_POWER_BUFFER;
import static crazypants.enderio.capacitor.CapacitorKey.ATTRACTOR_POWER_INTAKE;
import static crazypants.enderio.capacitor.CapacitorKey.ATTRACTOR_POWER_USE;
import static crazypants.enderio.capacitor.CapacitorKey.ATTRACTOR_RANGE;

@Storable
public class TileAttractor extends AbstractPowerConsumerEntity implements IRanged {

  private AxisAlignedBB attractorBounds;
  private FakePlayer target;
  private Set<EntityLiving> tracking = new HashSet<EntityLiving>();
  private int tickCounter = 0;
  private int maxMobsAttracted = 20;

  private boolean showingRange;

  public TileAttractor() {
    super(new SlotDefinition(12, 0), ATTRACTOR_POWER_INTAKE, ATTRACTOR_POWER_BUFFER, ATTRACTOR_POWER_USE);
  }

  @Override
  public float getRange() {
    return ATTRACTOR_RANGE.get(getCapacitorData());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isShowingRange() {
    return showingRange;
  }

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if (showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if (showingRange) {
      worldObj.spawnEntityInWorld(new RangeEntity(this));
    }
  }

  @Override
  public World getRangeWorldObj() {
    return getWorld();
  }

  @Override
  public void onCapacitorDataChange() {
    super.onCapacitorDataChange();
    attractorBounds = null;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockAttractor.getUnlocalisedName();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if (!slotDefinition.isInputSlot(i)) {
      return false;
    }
    return CapturedMob.containsSoul(itemstack);
  }

  @Override
  public boolean isActive() {
    return hasPower();
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (redstoneCheck && hasPower()) {
      usePower();
    } else {
      return false;
    }
    tickCounter++;
    if (tickCounter < 10) {
      for (EntityLiving ent : tracking) {
        onEntityTick(ent);
      }
      return false;
    }
    tickCounter = 0;

    if (attractorBounds == null) {
      BoundingBox bb = new BoundingBox(new BlockCoord(this));
      bb = bb.scale(getRange(), getRange(), getRange());
      attractorBounds = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }
    Set<EntityLiving> trackingThisTick = new HashSet<EntityLiving>();
    List<EntityLiving> entsInBounds = worldObj.getEntitiesWithinAABB(EntityLiving.class, attractorBounds);
    
    for (EntityLiving ent : entsInBounds) {
      if (!ent.isDead && isMobInFilter(ent)) {    
        if (tracking.contains(ent)) {
          trackingThisTick.add(ent);
          onEntityTick(ent);
        } else if (tracking.size() < maxMobsAttracted && trackMob(ent)) {
          trackingThisTick.add(ent);
          onTracked(ent);
        }
      }
    }
    for (EntityLiving e : tracking) {
      if (!trackingThisTick.contains(e)) {
        onUntracked(e);
      }
    }
    tracking.clear();
    tracking = trackingThisTick;
    return false;
  }

  private void onUntracked(EntityLiving e) {
    if (e instanceof EntityEnderman) {
      e.getEntityData().setBoolean("EIO:tracked", false);
    }
  }

  private void onTracked(EntityLiving e) {
    if (e instanceof EntityEnderman) {
      e.getEntityData().setBoolean("EIO:tracked", true);
    }
  }

  @Override
  public void invalidate() {
    super.invalidate();
    for (EntityLiving e : tracking) {
      onUntracked(e);
    }
    tracking.clear();
  }

  protected double usePower() {
    return usePower(getPowerUsePerTick());
  }

  protected int usePower(int wantToUse) {
    int used = Math.min(getEnergyStored(), wantToUse);
    setEnergyStored(Math.max(0, getEnergyStored() - used));
    return used;
  }

  FakePlayer getTarget() {
    if (target == null) {
      target = new Target();
    }
    return target;
  }

  public boolean canAttract(EntityLiving mob) {
    return redstoneCheckPassed && hasPower() && isMobInFilter(mob) && isMobInRange(mob);
  }

  private boolean isMobInRange(EntityLiving mob) {
    return isMobInRange(mob, (int) (getRange() * getRange()));
  }

  private boolean isMobInRange(EntityLiving mob, int rangeIn) {
    if (mob == null) {
      return false;
    }
    return new Vector3d(mob.posX, mob.posY, mob.posZ).distanceSquared(new Vector3d(getPos())) <= rangeIn;
  }

  private boolean isMobInFilter(EntityLiving entity) {
    for (int i = slotDefinition.minInputSlot; i <= slotDefinition.maxInputSlot; i++) {
      CapturedMob mob = CapturedMob.create(inventory[i]);
      if (mob != null && mob.isSameType(entity)) {
        return true;
      }
    }
    return false;
  }

  private boolean trackMob(EntityLiving ent) {
    if (useSetTarget(ent)) {
      ((EntityMob) ent).setAttackTarget(getTarget());
      return true;
    } else if (useSpecialCase(ent)) {
      return applySpecialCase(ent);
    } else {
      return attractyUsingAITask(ent);
    }
  }

  private boolean attractyUsingAITask(EntityLiving ent) {
    tracking.add(ent);
    List<EntityAITaskEntry> entries = ent.tasks.taskEntries;
    // boolean hasTask = false;
    EntityAIBase remove = null;
    // boolean isTracked;
    for (EntityAITaskEntry entry : entries) {
      if (entry.action instanceof AttractTask) {
        AttractTask at = (AttractTask) entry.action;
        if (at.coord.equals(new BlockCoord(this)) || !at.continueExecuting()) {
          remove = entry.action;
        } else {
          return false;
        }
      }
    }
    if (remove != null) {
      ent.tasks.removeTask(remove);
    }
    cancelCurrentTasks(ent);
    ent.tasks.addTask(0, new AttractTask(ent, getTarget(), new BlockCoord(this)));

    return true;
  }

  private void cancelCurrentTasks(EntityLiving ent) {
    Iterator<EntityAITaskEntry> iterator = ent.tasks.taskEntries.iterator();

    List<EntityAITasks.EntityAITaskEntry> currentTasks = new ArrayList<EntityAITasks.EntityAITaskEntry>();
    while (iterator.hasNext()) {
      EntityAITaskEntry entityaitaskentry = iterator.next();
      if (entityaitaskentry != null) {
        currentTasks.add(entityaitaskentry);
      }
    }
    // Only available way to stop current execution is to remove all current
    // tasks, then re-add them
    for (EntityAITaskEntry task : currentTasks) {
      ent.tasks.removeTask(task.action);
      ent.tasks.addTask(task.priority, task.action);
    }
  }

  private boolean applySpecialCase(EntityLiving ent) {
    if (ent instanceof EntitySlime) {
      ent.faceEntity(getTarget(), 10.0F, 20.0F);
      return true;
    } else if (ent instanceof EntitySilverfish) {
      EntitySilverfish es = (EntitySilverfish) ent;
      PathEntity pathentity = getPathEntityToEntity(ent, getTarget(), getRange());
      es.getNavigator().setPath(pathentity, es.getAIMoveSpeed());
      return true;
    } else if (ent instanceof EntityBlaze) {
      return true;
    }
    return false;
  }

  private boolean useSpecialCase(EntityLiving ent) {
    return ent instanceof EntitySlime || ent instanceof EntitySilverfish || ent instanceof EntityBlaze;
  }

  private void onEntityTick(EntityLiving ent) {
    if (ent instanceof EntitySlime) {
      ent.faceEntity(getTarget(), 10.0F, 20.0F);
    } else if (ent instanceof EntitySilverfish) {
      if (tickCounter < 10) {
        return;
      }
      EntitySilverfish sf = (EntitySilverfish) ent;
      PathEntity pathentity = getPathEntityToEntity(ent, getTarget(), getRange());
      sf.getNavigator().setPath(pathentity, sf.getAIMoveSpeed());
    } else if (ent instanceof EntityBlaze) {
      
      double x = (getPos().getX() + 0.5D - ent.posX);
      double y = (getPos().getX() + 1D - ent.posY);
      double z = (getPos().getX() + 0.5D - ent.posZ);
      double distance = Math.sqrt(x * x + y * y + z * z);
      if (distance > 1.25) {
        double speed = 0.01;
        ent.motionX += x / distance * speed;
        if (y > 0) {
          ent.motionY += (0.30000001192092896D - ent.motionY) * 0.30000001192092896D;
        }
        ent.motionZ += z / distance * speed;
      }
    } else if (ent instanceof EntityPigZombie || ent instanceof EntitySpider) {
      forceMove(ent);
    } else if (ent instanceof EntityEnderman) {
      ((EntityEnderman) ent).setAttackTarget(getTarget());
    }
  }

  private void forceMove(EntityLiving ent) {
    double x = (getPos().getX() + 0.5D - ent.posX);
    double y = (getPos().getY() + 1D - ent.posY);
    double z = (getPos().getZ() + 0.5D - ent.posZ);
    double distance = Math.sqrt(x * x + y * y + z * z);
    if (distance > 2) {
      EntityMob mod = (EntityMob) ent;
      mod.faceEntity(getTarget(), 180, 0);
      mod.moveEntityWithHeading(0, 1);
      if (mod.posY < getPos().getY()) {
        mod.setJumping(true);
      } else {
        mod.setJumping(false);
      }
    }
  }

  private boolean useSetTarget(EntityLiving ent) {
    return ent instanceof EntityPigZombie || ent instanceof EntitySpider || ent instanceof EntitySilverfish;
  }

  public PathEntity getPathEntityToEntity(Entity entity, Entity targetEntity, float range) {

    int targX = MathHelper.floor_double(targetEntity.posX);
    int targY = MathHelper.floor_double(targetEntity.posY + 1.0D);
    int targZ = MathHelper.floor_double(targetEntity.posZ);

    PathFinder pf = new PathFinder(new WalkNodeProcessor());
    return pf.createEntityPathTo(worldObj, entity, new BlockPos(targX, targY, targZ), range);

  }

  private class Target extends FakePlayerEIO {

    public Target() {
      super(getWorld(), getLocation(), new GameProfile(null, ModObject.blockAttractor.getUnlocalisedName() + ":" + getLocation()));
      posY += 1;
    }
  }

  private static class AttractTask extends EntityAIBase {

    private EntityLiving mob;
    private BlockCoord coord;
    private FakePlayer target;
    private int updatesSincePathing;

    private boolean started = false;

    private AttractTask(EntityLiving mob, FakePlayer target, BlockCoord coord) {
      this.mob = mob;
      this.coord = coord;
      this.target = target;
    }

    @Override
    public boolean shouldExecute() {
      return continueExecuting();
    }

    @Override
    public void resetTask() {
      started = false;
      updatesSincePathing = 0;
    }

    @Override
    public boolean continueExecuting() {
      boolean res = false;
      TileEntity te = mob.worldObj.getTileEntity(coord.getBlockPos());
      if (te instanceof TileAttractor) {
        TileAttractor attractor = (TileAttractor) te;
        res = attractor.canAttract(mob);
      }
      return res;
    }

    @Override
    public boolean isInterruptible() {
      return true;
    }

    @Override
    public void updateTask() {
      if (!started || updatesSincePathing > 20) {
        started = true;
        int speed = 1;
//        mob.getNavigator().setAvoidsWater(false);
        boolean res = mob.getNavigator().tryMoveToEntityLiving(target, speed);
        if (!res) {
          mob.getNavigator().tryMoveToXYZ(target.posX, target.posY + 1, target.posZ, speed);
        }
        updatesSincePathing = 0;
      } else {
        updatesSincePathing++;
      }
    }

  }

}
