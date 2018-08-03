package crazypants.enderio.machines.machine.spawner;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.NBTAction;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.interfaces.INotifier;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.machine.modes.EntityAction;
import crazypants.enderio.base.machine.task.PoweredTask;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.spawner.DummyRecipe;
import crazypants.enderio.base.recipe.spawner.PoweredSpawnerRecipeRegistry;
import crazypants.enderio.base.render.ranged.IRanged;
import crazypants.enderio.base.render.ranged.RangeParticle;
import crazypants.enderio.machines.config.config.SpawnerConfig;
import crazypants.enderio.machines.machine.spawner.SpawnerLogic.ISpawnerCallback;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.CapturedMob;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.capacitor.CapacitorKey.SPAWNER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SPAWNER_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SPAWNER_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SPAWNER_SPEEDUP;

@Storable
public class TilePoweredSpawner extends AbstractPoweredTaskEntity
    implements IPaintable.IPaintableTileEntity, IRanged, EntityAction.Implementer, INotifier, ISpawnerCallback {

  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  private CapturedMob capturedMob = null;
  @Store
  private boolean isSpawnMode = true;

  private final @Nonnull Set<SpawnerNotification> notification = EnumSet.noneOf(SpawnerNotification.class);
  private boolean sendNotification = false;

  private final @Nonnull SpawnerLogic logic = new SpawnerLogic(this);

  public TilePoweredSpawner() {
    super(new SlotDefinition(1, 1, 1), SPAWNER_POWER_INTAKE, SPAWNER_POWER_BUFFER, SPAWNER_POWER_USE);
  }

  public boolean isSpawnMode() {
    return isSpawnMode;
  }

  public void setSpawnMode(boolean isSpawnMode) {
    if (isSpawnMode != this.isSpawnMode) {
      currentTask = null;
    }
    this.isSpawnMode = isSpawnMode;
  }

  @Override
  protected void taskComplete() {
    super.taskComplete();
    if (hasEntity()) {
      if (isSpawnMode) {
        boolean spawnedOne = false;
        for (int i = 0; i < SpawnerConfig.poweredSpawnerSpawnCount.get(); ++i) {
          if (logic.trySpawnEntity()) {
            spawnedOne = true;
          }
        }
        if (spawnedOne) {
          clearNotification();
        }
      } else {
        clearNotification();
        if (Prep.isInvalid(getStackInSlot(0)) || Prep.isValid(getStackInSlot(1)) || !hasEntity()) {
          return;
        }
        ItemStack res = capturedMob.toGenericStack(ModObject.itemSoulVial.getItemNN(), 1, 1);
        getStackInSlot(0).shrink(1);
        setInventorySlotContents(1, res);
      }
    } else {
      this.world.destroyBlock(getPos(), true);
    }

    if (sendNotification) {
      if (hasNotification(SpawnerNotification.NO_LOCATION_FOUND)) {
        logic.anyLocationInRange();
      }
      sendNotification();
    }
  }

  @Override
  public int getMaxUsage() {
    return getPowerUsePerTick();
  }

  @Override
  public int getPowerUsePerTick() {
    final ResourceLocation entityName = getEntityName();
    return (int) (super.getPowerUsePerTick() * (entityName == null ? 0 : PoweredSpawnerRecipeRegistry.getInstance().getCostMultiplierFor(entityName)));
  }

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    final ResourceLocation entityName = getEntityName();
    return (int) (super.getMaxEnergyRecieved(dir) * (entityName == null ? 0 : PoweredSpawnerRecipeRegistry.getInstance().getCostMultiplierFor(entityName)));
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.SPAWNER;
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    if (itemstack.isEmpty() || isSpawnMode) {
      return false;
    }
    if (slotDefinition.isInputSlot(i)) {
      return itemstack.getItem() == ModObject.itemSoulVial.getItem() && !CapturedMob.containsSoul(itemstack);
    }
    return false;
  }

  @Override
  protected IMachineRecipe canStartNextTask(long nextSeed) {
    if (!hasEntity()) {
      this.world.destroyBlock(getPos(), true);
      return null;
    }
    if (isSpawnMode) {
      if (SpawnerConfig.poweredSpawnerMaxPlayerDistance.get() > 0) {
        BlockPos p = getPos();
        if (world.getClosestPlayer(p.getX() + 0.5, p.getX() + 0.5, p.getX() + 0.5, SpawnerConfig.poweredSpawnerMaxPlayerDistance.get(), false) == null) {
          setNotification(SpawnerNotification.NO_PLAYER);
          return null;
        }
      }
      removeNotification(SpawnerNotification.NO_PLAYER);
    } else {
      clearNotification();
      if (Prep.isInvalid(getStackInSlot(0)) || Prep.isValid(getStackInSlot(1))) {
        return null;
      }
    }
    return new DummyRecipe();
  }

  @Override
  protected boolean hasInputStacks() {
    return true;
  }

  @Override
  protected boolean canInsertResult(long nextSeed, @Nonnull IMachineRecipe nextRecipe) {
    return true;
  }

  @Override
  public void writeCustomNBT(@Nonnull ItemStack stack) {
    super.writeCustomNBT(stack);
    // save mob the same way as the soul binder adds it to the item
    if (hasEntity()) {
      if (!stack.hasTagCompound()) {
        stack.setTagCompound(new NBTTagCompound());
      }
      capturedMob.toNbt(stack.getTagCompound());
    }
  }

  private double mobRotation;
  private double prevMobRotation;
  private Entity cachedEntity;

  double getMobRotation() {
    return mobRotation;
  }

  double getPrevMobRotation() {
    return prevMobRotation;
  }

  Entity getCachedEntity() {
    return cachedEntity;
  }

  @Override
  protected void updateEntityClient() {
    if (isActive()) {
      double x = getPos().getX() + world.rand.nextFloat();
      double y = getPos().getY() + world.rand.nextFloat();
      double z = getPos().getZ() + world.rand.nextFloat();
      world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
      world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);

      this.prevMobRotation = this.mobRotation;
      this.mobRotation = (this.mobRotation + 1000.0F / ((1F - getProgress()) * 800F + 200.0F)) % 360.0D;
      if (cachedEntity == null && hasEntity()) {
        cachedEntity = capturedMob.getEntity(world, pos, null, false);
        if (cachedEntity != null) {
          cachedEntity.setDead();
        }
      }
    }
    super.updateEntityClient();
  }

  @Override
  protected IPoweredTask createTask(@Nonnull IMachineRecipe nextRecipe, long nextSeed) {
    PoweredTask res = new PoweredTask(nextRecipe, nextSeed, getRecipeInputs());
    int ticksDelay;
    if (isSpawnMode) {
      ticksDelay = SpawnerConfig.poweredSpawnerMinDelayTicks.get()
          + (int) Math.round((SpawnerConfig.poweredSpawnerMaxDelayTicks.get() - SpawnerConfig.poweredSpawnerMinDelayTicks.get()) * Math.random());
    } else {
      ticksDelay = SpawnerConfig.poweredSpawnerMaxDelayTicks.get()
          - ((SpawnerConfig.poweredSpawnerMaxDelayTicks.get() - SpawnerConfig.poweredSpawnerMinDelayTicks.get()) / 2);
    }
    ticksDelay /= SPAWNER_SPEEDUP.getFloat(getCapacitorData());
    int powerPerTick = getPowerUsePerTick();
    res.setRequiredEnergy(powerPerTick * ticksDelay);
    return res;
  }

  public ResourceLocation getEntityName() {
    return capturedMob != null ? capturedMob.getEntityName() : null;
  }

  @Override
  public CapturedMob getEntity() {
    return capturedMob;
  }

  public boolean hasEntity() {
    return capturedMob != null;
  }

  @Override
  public void readCustomNBT(@Nonnull ItemStack stack) {
    super.readCustomNBT(stack);
    capturedMob = CapturedMob.create(stack);
  }

  // RANGE

  private BoundingBox bounds;
  private boolean showingRange;

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isShowingRange() {
    return showingRange;
  }

  private final static @Nonnull Vector4f color = new Vector4f(.94f, .11f, .11f, .4f);

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if (showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if (showingRange) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new RangeParticle<TilePoweredSpawner>(this, color));
    }
  }

  @Override
  public void onCapacitorDataChange() {
    super.onCapacitorDataChange();
    bounds = null;
  }

  @Override
  public @Nonnull BoundingBox getBounds() {
    if (isSpawnMode) {
      if (bounds != null) {
        return bounds;
      }
      bounds = ISpawnerCallback.super.getBounds();
      if (capturedMob != null) {
        Entity ent = capturedMob.getEntity(world, false);
        if (ent != null) {
          int height = Math.max((int) Math.ceil(ent.height) - 1, 0);
          return bounds = bounds.setMaxY(bounds.maxY + height);
        }
      }
    }
    return new BoundingBox(getPos());
  }

  @Override
  public int getRange() {
    return SpawnerConfig.poweredSpawnerSpawnRange.get();
  }

  // RANGE END

  // NOTIFICATION

  @Override
  public void setNotification(SpawnerNotification note) {
    if (!notification.contains(note)) {
      notification.add(note);
      sendNotification = true;
    }
  }

  @Override
  public void removeNotification(SpawnerNotification note) {
    if (getNotification().remove(note)) {
      sendNotification = true;
    }
  }

  public void clearNotification() {
    if (hasNotification()) {
      getNotification().clear();
      sendNotification = true;
    }
  }

  public void replaceNotification(Set<SpawnerNotification> notes) {
    getNotification().clear();
    for (SpawnerNotification note : notes) {
      getNotification().add(note);
    }
  }

  public boolean hasNotification() {
    return !getNotification().isEmpty();
  }

  public boolean hasNotification(SpawnerNotification note) {
    return getNotification().contains(note);
  }

  @Override
  public @Nonnull Set<SpawnerNotification> getNotification() {
    return notification;
  }

  private void sendNotification() {
    sendNotification = false;
    PacketHandler.INSTANCE.sendToAll(new PacketSpawnerUpdateNotification(this, getNotification()));
  }

  // NOTIFICATION END

  @Override
  @Nonnull
  public NNList<CapturedMob> getEntities() {
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

}
