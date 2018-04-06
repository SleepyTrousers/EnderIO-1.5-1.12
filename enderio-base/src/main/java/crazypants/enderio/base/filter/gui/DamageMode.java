package crazypants.enderio.base.filter.gui;

import javax.annotation.Nonnull;

import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;

public enum DamageMode {
  DISABLED() {
    @Override
    public boolean passesFilter(float percentDamaged) {
      return true;
    }

    @Override
    public boolean passesFilter(@Nonnull ItemStack stack) {
      return Prep.isValid(stack);
    }
  },
  DAMAGE_00_25() {
    @Override
    protected boolean passesFilter(float percentDamaged) {
      return percentDamaged <= .25f;
    }
  },
  DAMAGE_25_00() {
    @Override
    protected boolean passesFilter(float percentDamaged) {
      return percentDamaged > .25f;
    }
  },
  DAMAGE_00_50() {
    @Override
    protected boolean passesFilter(float percentDamaged) {
      return percentDamaged <= .5f;
    }
  },
  DAMAGE_50_00() {
    @Override
    protected boolean passesFilter(float percentDamaged) {
      return percentDamaged > .5f;
    }
  },
  DAMAGE_00_75 {
    @Override
    protected boolean passesFilter(float percentDamaged) {
      return percentDamaged <= .75f;
    }
  },
  DAMAGE_75_00 {
    @Override
    protected boolean passesFilter(float percentDamaged) {
      return percentDamaged > .75f;
    }
  },
  DAMAGE_00_00 { // must be undamaged (undamageable items are always undamaged)
    @Override
    public boolean passesFilter(float percentDamaged) {
      return percentDamaged <= 0f;
    }
  },
  DAMAGE_01_00 { // must be damaged (undamageable items can never be damaged)
    @Override
    public boolean passesFilter(float percentDamaged) {
      return percentDamaged > 0f;
    }
  },
  DAMAGE_YES { // must be damageable
    @Override
    public boolean passesFilter(float percentDamaged) {
      return percentDamaged >= 0f;
    }
  },
  DAMAGE_NOT { // must not be damageable
    @Override
    public boolean passesFilter(float percentDamaged) {
      return percentDamaged < 0f;
    }
  };

  abstract boolean passesFilter(float percentDamaged);

  public boolean passesFilter(@Nonnull ItemStack stack) {
    return Prep.isValid(stack) && passesFilter(stack.isItemStackDamageable() ? (float) stack.getItemDamage() / (float) stack.getMaxDamage() : -1f);
  }

}
