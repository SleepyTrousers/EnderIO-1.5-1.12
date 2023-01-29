package crazypants.enderio.machine.obelisk.attractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.vecmath.Vector3d;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.FakePlayerEIO;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.ranged.IRanged;
import crazypants.enderio.machine.ranged.RangeEntity;
import crazypants.enderio.power.BasicCapacitor;

public class TileAttractor extends AbstractPowerConsumerEntity implements IRanged {

    private AxisAlignedBB attractorBounds;
    private FakePlayer target;
    private int rangeSqu;
    private int range;
    private int powerPerTick;
    private Set<EntityLiving> tracking = new HashSet<EntityLiving>();
    private int tickCounter = 0;
    private int maxMobsAttracted = 20;

    private boolean showingRange;

    public TileAttractor() {
        super(new SlotDefinition(12, 0));
    }

    @Override
    public float getRange() {
        return range;
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
    public World getWorld() {
        return worldObj;
    }

    @Override
    public void onCapacitorTypeChange() {
        switch (getCapacitor().getTier()) {
            case 2:
                range = Config.attractorRangeLevelTwo;
                powerPerTick = Config.attractorPowerPerTickLevelTwo;
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                range = Config.attractorRangeLevelThree;
                powerPerTick = Config.attractorPowerPerTickLevelThree;
                break;
            case 1:
            default:
                range = Config.attractorRangeLevelOne;
                powerPerTick = Config.attractorPowerPerTickLevelOne;
                break;
        }
        rangeSqu = range * range;

        BoundingBox bb = new BoundingBox(new BlockCoord(this));
        bb = bb.scale(range, range, range);
        attractorBounds = AxisAlignedBB.getBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        setCapacitor(new BasicCapacitor(powerPerTick * 8, getCapacitor().getMaxEnergyStored(), powerPerTick));
    }

    @Override
    public String getMachineName() {
        return ModObject.blockAttractor.unlocalisedName;
    }

    @Override
    protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
        if (!slotDefinition.isInputSlot(i)) {
            return false;
        }
        String mob = EnderIO.itemSoulVessel.getMobTypeFromStack(itemstack);
        if (mob == null) {
            return false;
        }
        Class<?> cl = (Class<?>) EntityList.stringToClassMapping.get(mob);
        if (cl == null) {
            return false;
        }
        return EntityLiving.class.isAssignableFrom(cl);
    }

    @Override
    public boolean isActive() {
        return hasPower();
    }

    @Override
    protected boolean processTasks(boolean redstoneCheckPassed) {
        if (redstoneCheckPassed && hasPower()) {
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

        Set<EntityLiving> trackingThisTick = new HashSet<EntityLiving>();
        List<EntityLiving> entsInBounds = worldObj.getEntitiesWithinAABB(EntityLiving.class, attractorBounds);

        int candidates = 0;
        for (EntityLiving ent : entsInBounds) {
            if (!ent.isDead && isMobInFilter(ent)) {
                candidates++;
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

    @Override
    public int getPowerUsePerTick() {
        return powerPerTick;
    }

    FakePlayer getTarget() {
        if (target == null) {
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
        if (mob == null) {
            return false;
        }
        return new Vector3d(mob.posX, mob.posY, mob.posZ).distanceSquared(new Vector3d(xCoord, yCoord, zCoord))
                <= range;
    }

    private boolean isMobInFilter(EntityLiving ent) {
        return isMobInFilter(EntityList.getEntityString(ent));
    }

    private boolean isMobInFilter(String entityId) {
        for (int i = slotDefinition.minInputSlot; i <= slotDefinition.maxInputSlot; i++) {
            if (inventory[i] != null) {
                String mob = EnderIO.itemSoulVessel.getMobTypeFromStack(inventory[i]);
                if (mob != null && mob.equals(entityId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean trackMob(EntityLiving ent) {
        if (useSetTarget(ent)) {
            ((EntityMob) ent).setTarget(getTarget());
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
        boolean hasTask = false;
        EntityAIBase remove = null;
        boolean isTracked;
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
        Iterator iterator = ent.tasks.taskEntries.iterator();

        List<EntityAITasks.EntityAITaskEntry> currentTasks = new ArrayList<EntityAITasks.EntityAITaskEntry>();
        while (iterator.hasNext()) {
            EntityAITaskEntry entityaitaskentry = (EntityAITasks.EntityAITaskEntry) iterator.next();
            if (entityaitaskentry != null) {
                currentTasks.add(entityaitaskentry);
            }
        }
        // Only available way to stop current execution is to remove all current tasks, then re-add them
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
            PathEntity pathentity = worldObj
                    .getPathEntityToEntity(ent, getTarget(), getRange(), true, false, false, true);
            ((EntityCreature) ent).setPathToEntity(pathentity);
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
            PathEntity pathentity = worldObj
                    .getPathEntityToEntity(ent, getTarget(), getRange(), true, false, false, true);
            sf.setPathToEntity(pathentity);
        } else if (ent instanceof EntityBlaze) {
            EntityBlaze mob = (EntityBlaze) ent;

            double x = (xCoord + 0.5D - ent.posX);
            double y = (yCoord + 1D - ent.posY);
            double z = (zCoord + 0.5D - ent.posZ);
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
            ((EntityEnderman) ent).setTarget(getTarget());
        }
    }

    private void forceMove(EntityLiving ent) {
        double x = (xCoord + 0.5D - ent.posX);
        double y = (yCoord + 1D - ent.posY);
        double z = (zCoord + 0.5D - ent.posZ);
        double distance = Math.sqrt(x * x + y * y + z * z);
        if (distance > 2) {
            EntityMob mod = (EntityMob) ent;
            mod.faceEntity(getTarget(), 180, 0);
            mod.moveEntityWithHeading(0, 1);
            if (mod.posY < yCoord) {
                mod.setJumping(true);
            } else {
                mod.setJumping(false);
            }
        }
    }

    private boolean useSetTarget(EntityLiving ent) {
        return ent instanceof EntityPigZombie || ent instanceof EntitySpider || ent instanceof EntitySilverfish;
    }

    private class Target extends FakePlayerEIO {

        public Target() {
            super(
                    getWorldObj(),
                    getLocation(),
                    new GameProfile(null, ModObject.blockAttractor.unlocalisedName + ":" + getLocation()));
            posY += 1;
        }
    }

    private static class AttractTask extends EntityAIBase {

        private EntityLiving mob;
        private BlockCoord coord;
        private FakePlayer target;
        private String entityId;
        private int updatesSincePathing;

        private boolean started = false;

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
        public void resetTask() {
            started = false;
            updatesSincePathing = 0;
        }

        @Override
        public boolean continueExecuting() {
            boolean res = false;
            TileEntity te = mob.worldObj.getTileEntity(coord.x, coord.y, coord.z);
            if (te instanceof TileAttractor) {
                TileAttractor attractor = (TileAttractor) te;
                res = attractor.canAttract(entityId, mob);
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
                mob.getNavigator().setAvoidsWater(false);
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
