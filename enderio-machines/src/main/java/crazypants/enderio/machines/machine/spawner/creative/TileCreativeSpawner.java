package crazypants.enderio.machines.machine.spawner.creative;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.Filters.PredicateItemStack;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.capability.Filters;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityMachineEntity;
import crazypants.enderio.base.machine.modes.EntityAction;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.machines.config.config.SpawnerConfig;
import crazypants.enderio.machines.machine.spawner.SpawnerLogic;
import crazypants.enderio.machines.machine.spawner.SpawnerLogic.ISpawnerCallback;
import crazypants.enderio.machines.machine.spawner.SpawnerNotification;
import crazypants.enderio.util.CapturedMob;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Storable
public class TileCreativeSpawner extends AbstractCapabilityMachineEntity
    implements IPaintable.IPaintableTileEntity, ISpawnerCallback, EntityAction.Implementer {

  private final @Nonnull SpawnerLogic logic = new SpawnerLogic(this);

  static enum SLOT {
    VIAL,
    PATTERN,
    OFFERING;
  }

  public TileCreativeSpawner() {
    super();
    getInventory().add(Type.INPUT, SLOT.VIAL, new InventorySlot(Filters.WITH_MOB_SOUL, 1));
    getInventory().add(Type.INPUT, SLOT.PATTERN, new InventorySlot());
    getInventory().add(Type.INPUT, SLOT.OFFERING, new InventorySlot(new PredicateItemStack() {

      @Override
      public boolean doApply(@Nonnull ItemStack input) {
        InventorySlot slot = getInventory().getSlot(SLOT.PATTERN);
        return !slot.isEmpty() && ItemUtil.areStacksEqual(slot.get(), input);
      }
    }, -1));
  }

  @Override
  protected void processTasks(boolean redstoneCheck) {
    if (!redstoneCheck || getInventory().getSlot(SLOT.VIAL).isEmpty()) {
      return;
    }
    if (!shouldDoWorkThisTick(20)) {
      return;
    }
    InventorySlot slot = getInventory().getSlot(SLOT.PATTERN);
    if (slot.isEmpty()) {
      if (!shouldDoWorkThisTick(60 * 20)) {
        return;
      }
      if (SpawnerConfig.homeZoneEnabled.get()
          && !logic.isAreaClear(SpawnerConfig.homeZoneSize.get(), SpawnerConfig.homeZoneSize.get(), SpawnerConfig.homeZoneLimit.get())) {
        return;
      }
    } else {
      InventorySlot slot2 = getInventory().getSlot(SLOT.OFFERING);
      if (slot2.get().getCount() < slot.get().getCount()) {
        return;
      }
      if (!logic.isAreaClear()) {
        return;
      }
      if (SpawnerConfig.homeZoneEnabled.get()
          && !logic.isAreaClear(SpawnerConfig.homeZoneSize.get(), SpawnerConfig.homeZoneSize.get(), SpawnerConfig.homeZoneLimit.get())) {
        return;
      }
      slot2.get().shrink(slot.get().getCount());
      markDirty();
    }
    for (int i = 0; i < SpawnerConfig.poweredSpawnerSpawnCount.get(); ++i) {
      logic.trySpawnEntity();
    }
  }

  @Override
  @Nonnull
  public NNList<CapturedMob> getEntities() {
    CapturedMob capturedMob = getEntity();
    if (capturedMob != null) {
      return new NNList<>(capturedMob);
    }
    return NNList.emptyList();
  }

  @Override
  @Nonnull
  public EntityAction getEntityAction() {
    return EntityAction.SPAWN;
  }

  @Override
  @Nonnull
  public World getSpawnerWorld() {
    return world;
  }

  @Override
  @Nonnull
  public BlockPos getSpawnerPos() {
    return pos;
  }

  @Override
  public int getRange() {
    return SpawnerConfig.poweredSpawnerSpawnRange.get();
  }

  @Override
  public void setNotification(@Nonnull SpawnerNotification note) {
  }

  @Override
  public void removeNotification(@Nonnull SpawnerNotification note) {
  }

  @Override
  public boolean isActive() {
    return getInventory().getSlot(SLOT.PATTERN).isEmpty();
  }

  @Override
  @Nullable
  public CapturedMob getEntity() {
    return CapturedMob.create(getInventory().getSlot(SLOT.VIAL).get());
  }

  @Override
  public void setHome(@Nonnull EntityCreature entity) {
    if (SpawnerConfig.homeZoneEnabled.get()) {
      entity.setHomePosAndDistance(pos, SpawnerConfig.homeZoneSize.get());
    }
  }

  @Override
  public void resetCapturedMob() {
  }

}
