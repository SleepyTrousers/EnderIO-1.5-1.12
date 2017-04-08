package crazypants.enderio.machine.obelisk.attractor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.mojang.authlib.GameProfile;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.FakePlayerEIO;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.obelisk.PacketObeliskFx;
import crazypants.enderio.machine.obelisk.spawn.AbstractMobObelisk;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.TargetContext;

import static crazypants.enderio.capacitor.CapacitorKey.ATTRACTOR_POWER_BUFFER;
import static crazypants.enderio.capacitor.CapacitorKey.ATTRACTOR_POWER_INTAKE;
import static crazypants.enderio.capacitor.CapacitorKey.ATTRACTOR_POWER_USE;
import static crazypants.enderio.capacitor.CapacitorKey.ATTRACTOR_RANGE;

@Storable
public class TileAttractor extends AbstractMobObelisk {

  private Target target;
  private int maxMobsAttracted = 20;

  private Map<EntityLiving, IMobAttractionHandler> tracking = new HashMap<EntityLiving, IMobAttractionHandler>();

  public TileAttractor() {
    super(new SlotDefinition(12, 0), ATTRACTOR_POWER_INTAKE, ATTRACTOR_POWER_BUFFER, ATTRACTOR_POWER_USE);
  }

  @Override
  public float getRange() {
    return ATTRACTOR_RANGE.get(getCapacitorData());
  }

  @Override
  public @Nonnull String getMachineName() {
    return ModObject.blockAttractor.getUnlocalisedName();
  }

  private void untrackAll() {
    for (Entry<EntityLiving, IMobAttractionHandler> tracked : tracking.entrySet()) {
      tracked.getValue().release(this, tracked.getKey());
    }
    tracking.clear();
  }

  private void cleanTrackedEntities() {
    Iterator<Entry<EntityLiving, IMobAttractionHandler>> iterator = tracking.entrySet().iterator();
    while (iterator.hasNext()) {
      Entry<EntityLiving, IMobAttractionHandler> next = iterator.next();
      if (next.getKey().isDead) {
        iterator.remove();
      } else if (!canAttract(next.getKey())) {
        next.getValue().release(this, next.getKey());
        iterator.remove();
      } else if (next.getKey().worldObj.rand.nextInt(4) == 0) {
        PacketObeliskFx.create(next.getKey(), EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL, EnumParticleTypes.VILLAGER_HAPPY);
      }
    }
  }

  private void tickTrackedEntities() {
    for (Entry<EntityLiving, IMobAttractionHandler> tracked : tracking.entrySet()) {
      if (!tracked.getKey().isDead) {
        tracked.getValue().tick(this, tracked.getKey());
      }
    }
  }

  private void collectEntities() {
    for (EntityLiving entity : worldObj.getEntitiesWithinAABB(EntityLiving.class, getBounds())) {
      if (!entity.isDead && !tracking.containsKey(entity) && canAttract(entity)
          && PermissionAPI.hasPermission(getOwner().getAsGameProfile(), BlockAttractor.permissionAttracting, new TargetContext(getTarget(), entity))) {
        collectEntity(entity);
        if (tracking.size() >= maxMobsAttracted) {
          return;
        }
      }
    }
  }

  private void collectEntity(EntityLiving ent) {
    for (IMobAttractionHandler handler : AttractionHandlers.instance.getRegistry()) {
      if (handler.canAttract(this, ent)) {
        handler.startAttracting(this, ent);
        tracking.put(ent, handler);
        PacketObeliskFx.create(ent, EnumParticleTypes.HEART, EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL,
            EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL, EnumParticleTypes.VILLAGER_HAPPY);
        return;
      }
    }
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (!redstoneCheck || !hasPower()) {
      untrackAll();
      return false;
    }
    usePower();

    if (shouldDoWorkThisTick(60, 5)) {
      cleanTrackedEntities();
    }

    if (shouldDoWorkThisTick(10)) {
      tickTrackedEntities();
    }

    if (tracking.size() < maxMobsAttracted && shouldDoWorkThisTick(20, 5)) {
      collectEntities();
    }

    return false;
  }

  @Override
  public void invalidate() {
    super.invalidate();
    untrackAll();
  }

  FakePlayer getTarget() {
    if (target == null) {
      target = new Target();
      target.setOwner(getOwner());
    }
    return target;
  }

  public boolean canAttract(EntityLiving mob) {
    return isMobInFilter(mob) && getBounds().intersectsWith(mob.getEntityBoundingBox());
  }

  @Override
  public SpawnObeliskAction getSpawnObeliskAction() {
    return SpawnObeliskAction.ATTRACT;
  }

  private class Target extends FakePlayerEIO {

    public Target() {
      super(getWorld(), getLocation(), new GameProfile(null, ModObject.blockAttractor.getUnlocalisedName() + ":" + getLocation()));
      posY += 1;
    }
  }

}
