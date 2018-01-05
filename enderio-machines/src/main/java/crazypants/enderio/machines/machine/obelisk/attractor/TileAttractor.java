package crazypants.enderio.machines.machine.obelisk.attractor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.mojang.authlib.GameProfile;

import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.fakeplayer.FakePlayerEIO;
import crazypants.enderio.base.machine.modes.EntityAction;
import crazypants.enderio.base.network.PacketSpawnParticles;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.obelisk.spawn.AbstractMobObelisk;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.TargetContext;

import static crazypants.enderio.base.config.Config.maxMobsAttracted;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ATTRACTOR_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ATTRACTOR_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ATTRACTOR_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ATTRACTOR_RANGE;

@Storable
public class TileAttractor extends AbstractMobObelisk {

  private FakePlayerEIO target;

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
    return MachineObject.block_attractor_obelisk.getUnlocalisedName();
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
      } else if (next.getKey().world.rand.nextInt(4) == 0) {
        PacketSpawnParticles.create(next.getKey(), EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL, EnumParticleTypes.VILLAGER_HAPPY);
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
    for (EntityLiving entity : world.getEntitiesWithinAABB(EntityLiving.class, getBounds())) {
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
        PacketSpawnParticles.create(ent, EnumParticleTypes.HEART, EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL,
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

  @Nonnull
  FakePlayer getTarget() {
    return target != null ? target : (target = new Target(getWorld()).setOwner(getOwner()));
  }

  public boolean canAttract(EntityLiving mob) {
    return isMobInFilter(mob) && getBounds().intersectsWith(mob.getEntityBoundingBox());
  }

  @Override
  public @Nonnull EntityAction getEntityAction() {
    return EntityAction.ATTRACT;
  }

  private class Target extends FakePlayerEIO {

    public Target(World world) {
      super(world, getLocation(), new GameProfile(null, MachineObject.block_attractor_obelisk.getUnlocalisedName() + ":" + getLocation()));
      posY += 1;
    }
  }

}
