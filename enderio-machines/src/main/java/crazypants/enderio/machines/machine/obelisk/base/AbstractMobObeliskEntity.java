package crazypants.enderio.machines.machine.obelisk.base;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.ILocalizable;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.interfaces.INotifier;
import crazypants.enderio.base.machine.modes.EntityAction;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.util.CapturedMob;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

@Storable
public abstract class AbstractMobObeliskEntity extends AbstractRangedObeliskEntity implements EntityAction.Implementer, INotifier {

  public AbstractMobObeliskEntity(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    if (!slotDefinition.isInputSlot(i)) {
      return false;
    }
    return CapturedMob.containsSoul(itemstack);
  }

  @Override
  public boolean isActive() {
    return redstoneCheckPassed && hasPower();
  }

  protected boolean isMobInRange(EntityLivingBase mob) {
    return mob != null && getBounds().isVecInside(new Vec3d(mob.posX, mob.posY, mob.posZ));
  }

  @Override
  public boolean canWork() {
    for (int i = slotDefinition.minInputSlot; i <= slotDefinition.maxInputSlot; i++) {
      if (Prep.isValid(getStackInSlot(i))) {
        return true;
      }
    }
    return false;
  }

  protected boolean isMobInFilter(EntityLivingBase entity) {
    for (int i = slotDefinition.minInputSlot; i <= slotDefinition.maxInputSlot; i++) {
      CapturedMob mob = CapturedMob.create(getStackInSlot(i));
      if (mob != null && mob.isSameType(entity)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public @Nonnull NNList<CapturedMob> getEntities() {
    NNList<CapturedMob> result = new NNList<>();
    for (int i = slotDefinition.minInputSlot; i <= slotDefinition.maxInputSlot; i++) {
      CapturedMob mob = CapturedMob.create(getStackInSlot(i));
      if (mob != null) {
        result.add(mob);
      }
    }
    return result;
  }

  @Override
  @Nonnull
  public Set<? extends ILocalizable> getNotification() {
    return canWork() ? Collections.emptySet() : Collections.singleton(new ILocalizable() {
      @Override
      @Nonnull
      public String getUnlocalizedName() {
        return Lang.GUI_OBELISK_NO_VIALS.getKey();
      }
    });
  }

}