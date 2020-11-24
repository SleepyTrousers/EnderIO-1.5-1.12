package crazypants.enderio.machines.machine.obelisk.attractor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import com.mojang.authlib.GameProfile;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.fakeplayer.FakePlayerEIO;
import crazypants.enderio.base.machine.modes.EntityAction;
import crazypants.enderio.base.network.PacketSpawnParticles;
import crazypants.enderio.machines.config.config.AttractorConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.obelisk.attractor.handlers.AttractionHandlers;
import crazypants.enderio.machines.machine.obelisk.attractor.handlers.IMobAttractionHandler;
import crazypants.enderio.machines.machine.obelisk.attractor.handlers.IMobAttractionHandler.State;
import crazypants.enderio.machines.machine.obelisk.base.AbstractMobObeliskEntity;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.server.FMLServerHandler;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.TargetContext;

import static crazypants.enderio.machines.capacitor.CapacitorKey.ATTRACTOR_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ATTRACTOR_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ATTRACTOR_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ATTRACTOR_RANGE;

@Storable
public class TileAttractor extends AbstractMobObeliskEntity {

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
      if (next.getKey().isDead || !canAttract(next.getKey())) {
        next.getValue().release(this, next.getKey());
        iterator.remove();
      } else if (world.rand.nextInt(4) == 0) {
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
        if (tracking.size() >= AttractorConfig.maxMobsAttracted.get()) {
          return;
        }
      }
    }
  }

  private static final @Nonnull Set<ResourceLocation> FAILED_MOBS = new HashSet<>();

  private void collectEntity(@Nonnull EntityLiving ent) {
    final ResourceLocation key = EntityList.getKey(ent);
    if (!FAILED_MOBS.contains(key)) {
      for (IMobAttractionHandler handler : AttractionHandlers.instance.getRegistry()) {
        State state = handler.canAttract(this, ent);
        if (state == State.CAN_ATTRACT) {
          handler.startAttracting(this, ent);
          tracking.put(ent, handler);
          PacketSpawnParticles.create(ent, EnumParticleTypes.HEART, EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL,
              EnumParticleTypes.PORTAL, EnumParticleTypes.PORTAL, EnumParticleTypes.VILLAGER_HAPPY);
          return;
        } else if (state == State.ALREADY_ATTRACTING) {
          return;
        }
      }
      Log.warn("Attractor Obelisk: Don't know how to attract " + key);
      EntityPlayerMP player = FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(getOwner().getAsGameProfile().getId());
      if (player != null) {
        player.sendMessage(Lang.STATUS_ATTRACTOR_UNKNOWN_MOB.toChatServer(pos, key));
      }
      FAILED_MOBS.add(key);
    }
  }

  @Override
  protected void processTasks(boolean redstoneCheck) {
    if (!redstoneCheck || !hasPower() || !canWork()) {
      untrackAll();
      return;
    }
    usePower();

    if (shouldDoWorkThisTick(60, 5)) {
      cleanTrackedEntities();
    }

    if (shouldDoWorkThisTick(10)) {
      tickTrackedEntities();
    }

    if (tracking.size() < AttractorConfig.maxMobsAttracted.get() && shouldDoWorkThisTick(20, 5)) {
      collectEntities();
    }
  }

  @Override
  public void invalidate() {
    super.invalidate();
    untrackAll();
  }

  @Nonnull
  public FakePlayer getTarget() {
    return target != null ? target : (target = new Target(getWorld()).setOwner(getOwner()));
  }

  public boolean canAttract(EntityLiving mob) {
    return isMobInFilter(mob) && getBounds().intersects(mob.getEntityBoundingBox());
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

  @Override
  protected @Nonnull String getDocumentationPage() {
    return EnderIO.DOMAIN + ":attractor_obelisk";
  }

}
