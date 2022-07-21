package crazypants.enderio.machine.obelisk.aversion;

import com.enderio.core.client.render.BoundingBox;
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
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class TileAversionObelisk extends AbstractPowerConsumerEntity implements IRanged {

    private int powerPerTick;
    private int range;
    private boolean registered = false;
    private AxisAlignedBB bounds;

    private boolean showingRange;

    public TileAversionObelisk() {
        super(new SlotDefinition(12, 0));
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
        return getWorldObj();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        AversionObeliskController.instance.deregisterGuard(this);
        registered = false;
    }

    @Override
    public float getRange() {
        return range;
    }

    @Override
    public void onCapacitorTypeChange() {
        switch (getCapacitor().getTier()) {
            case 1:
                range = Config.spawnGuardRangeLevelOne;
                powerPerTick = Config.spawnGuardPowerPerTickLevelOne;
                break;
            case 2:
                range = Config.spawnGuardRangeLevelTwo;
                powerPerTick = Config.spawnGuardPowerPerTickLevelTwo;
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                range = Config.spawnGuardRangeLevelThree;
                powerPerTick = Config.spawnGuardPowerPerTickLevelThree;
                break;
        }
        setCapacitor(new BasicCapacitor(powerPerTick * 8, getCapacitor().getMaxEnergyStored(), powerPerTick));

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
            if (!registered) {
                AversionObeliskController.instance.registerGuard(this);
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
        if (mob == null) {
            return false;
        }
        // return new Vector3d(mob.posX, mob.posY, mob.posZ).distanceSquared(new Vector3d(xCoord, yCoord, zCoord)) <=
        // rangeSqu;
        return bounds.isVecInside(Vec3.createVectorHelper(mob.posX, mob.posY, mob.posZ));
    }

    private boolean isMobInFilter(EntityLivingBase ent) {
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
}
