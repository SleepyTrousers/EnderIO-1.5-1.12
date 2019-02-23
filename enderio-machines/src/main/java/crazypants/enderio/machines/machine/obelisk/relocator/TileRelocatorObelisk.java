package crazypants.enderio.machines.machine.obelisk.relocator;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.modes.EntityAction;
import crazypants.enderio.base.network.PacketSpawnParticles;
import crazypants.enderio.base.render.ranged.RangeParticle;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.obelisk.base.AbstractRangedObeliskEntity;
import crazypants.enderio.machines.machine.obelisk.base.AbstractSpawningObeliskEntity;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.capacitor.CapacitorKey.RELOCATOR_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.RELOCATOR_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.RELOCATOR_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.RELOCATOR_RANGE;

@Storable
public class TileRelocatorObelisk extends AbstractSpawningObeliskEntity {

  private final Map<EntityLivingBase, Integer> relocationQueue = new WeakHashMap<EntityLivingBase, Integer>();
  private final Random rand = new Random();

  public TileRelocatorObelisk() {
    super(new SlotDefinition(12, 0), RELOCATOR_POWER_INTAKE, RELOCATOR_POWER_BUFFER, RELOCATOR_POWER_USE);
  }

  @Override
  public float getRange() {
    return RELOCATOR_RANGE.get(getCapacitorData());
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.block_relocator_obelisk.getUnlocalisedName();
  }

  @Override
  public @Nonnull Result isSpawnPrevented(EntityLivingBase mob) {
    if (redstoneCheckPassed && hasPower() && isMobInRange(mob) && isMobInFilter(mob)) {
      relocationQueue.put(mob, null);
      return Result.DONE;
    } else {
      return Result.NEXT;
    }
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (!relocationQueue.isEmpty()) {
      AxisAlignedBB targetBB = new AxisAlignedBB(getPos(), getPos().add(1, 1, 1)).expand(4, 1, 4);
      Iterator<EntityLivingBase> iterator = relocationQueue.keySet().iterator();
      while (iterator.hasNext()) {
        EntityLivingBase mob = iterator.next();
        if (mob == null || mob.isDead || world.getEntityByID(mob.getEntityId()) == null || mob.ticksExisted > 2 * 60 * 20 || relocationQueue.size() > 100) {
          iterator.remove();
        } else if (hasPower() && rand.nextFloat() < .025f) {
          AxisAlignedBB mobbb = mob.getEntityBoundingBox();
          if (targetBB.intersects(mobbb)) {
            iterator.remove();
          } else {
            double x = getPos().getX() + .5 + Math.random() * 8d - 4.0;
            double y = getPos().getY() + .5 + Math.random() * 3d - 1.5;
            double z = getPos().getZ() + .5 + Math.random() * 8d - 4.0;
            double dx = mobbb.maxX - mobbb.minX;
            double dy = mobbb.maxY - mobbb.minY;
            double dz = mobbb.maxZ - mobbb.minZ;
            AxisAlignedBB bb = new AxisAlignedBB(x - dx / 2, y, z - dz / 2, x + dx / 2, y + dy, z + dz / 2);

            boolean spaceClear = world.checkNoEntityCollision(bb, mob) && world.getCollisionBoxes(mob, bb).isEmpty()
                && (world.containsAnyLiquid(bb) == mob.isCreatureType(EnumCreatureType.WATER_CREATURE, false));

            if (spaceClear) {
              PacketSpawnParticles.create(mob, EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL);
              mob.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
              mob.setPositionAndUpdate(x - dx / 2, y + .05, z - dz / 2);
              mob.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
              PacketSpawnParticles.create(mob, EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL);
              iterator.remove();
            }
          }
        }
      }
    }
    return super.processTasks(redstoneCheck);
  }

  @Override
  public @Nonnull EntityAction getEntityAction() {
    return EntityAction.RELOCATE;
  }

  private final static @Nonnull Vector4f color = new Vector4f(.94f, .11f, .11f, .4f);

  @Override
  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if (showingRange == showRange) {
      return;
    }
    super.setShowRange(showRange);
    if (showingRange) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new RangeParticle<AbstractRangedObeliskEntity>(this, color) {
        @Override
        protected @Nonnull BoundingBox getBounds() {
          return new BoundingBox(getPos()).expand(4, 1d, 4);
        }
      });
    }
  }

  @Override
  protected @Nonnull String getDocumentationPage() {
    return EnderIO.DOMAIN + ":relocator_obelisk";
  }

}
