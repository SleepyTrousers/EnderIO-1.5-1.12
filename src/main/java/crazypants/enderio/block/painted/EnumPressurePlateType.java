package crazypants.enderio.block.painted;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import com.google.common.base.Predicate;

import crazypants.util.CapturedMob;

public enum EnumPressurePlateType implements IStringSerializable {

  WOOD(Entity.class),
  STONE(EntityLivingBase.class),
  IRON(CountingMode.ENTITIES, Entity.class),
  GOLD(CountingMode.ITEMS, EntityItem.class),
  DARKSTEEL(EntityPlayer.class),
  @SuppressWarnings("unchecked")
  SOULARIUM(EntityLiving.class, EntitySlime.class, EntityGhast.class, EntityMob.class),
  TUNED(EntityLivingBase.class) {
    @Override
    public Predicate<Entity> getPredicate(final CapturedMob capturedMob) {
      return new Predicate<Entity>() {
        @Override
        public boolean apply(@Nullable Entity entity) {
          if (capturedMob == null || entity == null || !entity.isEntityAlive() || entity.doesEntityNotTriggerPressurePlate()
              || ((entity instanceof EntityPlayer) && ((EntityPlayer) entity).isSpectator())) {
            return false;
          }
          return capturedMob.isSameType(entity);
        }

        @Override
        public int hashCode() {
          return super.hashCode();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
          return super.equals(obj);
        }
      };
    }

  };

  public static enum CountingMode {
    BINARY {
      @Override
      public int count(List<Entity> list) {
        return list.isEmpty() ? 0 : 15;
      }
    },
    ENTITIES {
      @Override
      public int count(List<Entity> list) {
        return Math.min(15, list.size());
      }
    },
    ITEMS {
      @Override
      public int count(List<Entity> list) {
        int result = 0;
        for (Entity entity : list) {
          if (entity instanceof EntityItem) {
            ItemStack stack = ((EntityItem) entity).getEntityItem();
            result += stack.stackSize;
            if (result >= 15) {
              return 15;
            }
          }
        }
        return Math.min(15, result);
      }
    };

    public abstract int count(List<Entity> list);
  }

  private final CountingMode countingMode;
  private final Class<? extends Entity> searchClass;
  private final List<Class<? extends Entity>> whiteClasses;

  private EnumPressurePlateType(Class<? extends Entity> searchClass) {
    this.countingMode = CountingMode.BINARY;
    this.searchClass = searchClass;
    this.whiteClasses = Collections.emptyList();
  }

  private EnumPressurePlateType(Class<? extends Entity> searchClass, Class<? extends Entity>... whiteClasses) {
    this.countingMode = CountingMode.BINARY;
    this.searchClass = searchClass;
    this.whiteClasses = Arrays.asList(whiteClasses);
  }

  private EnumPressurePlateType(CountingMode countingMode, Class<? extends Entity> searchClass) {
    this.countingMode = countingMode;
    this.searchClass = searchClass;
    this.whiteClasses = Collections.emptyList();
  }

  private EnumPressurePlateType(CountingMode countingMode, Class<? extends Entity> searchClass, Class<? extends Entity>... whiteClasses) {
    this.countingMode = countingMode;
    this.searchClass = searchClass;
    this.whiteClasses = Arrays.asList(whiteClasses);
  }

  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }
  
  public String getUnlocName(Item me) {
    return me.getUnlocalizedName() + "." + getName();
  }
  
  public static EnumPressurePlateType getTypeFromMeta(int meta) {
    int meta1 = meta >> 1;
    return values()[meta1 >= 0 && meta1 < values().length ? meta1 : 0];
  }

  public static boolean getSilentFromMeta(int meta) {
    return (meta & 1) != 0;
  }

  public static int getMetaFromType(EnumPressurePlateType value, Boolean isSilent) {
    return (value.ordinal() << 1) | (isSilent ? 1 : 0);
  }

  public int getMetaFromType(Boolean isSilent) {
    return getMetaFromType(this, isSilent);
  }

  public int getMetaFromType() {
    return getMetaFromType(this, false);
  }

  protected CountingMode getCountingMode() {
    return countingMode;
  }

  public Class<? extends Entity> getSearchClass() {
    return searchClass;
  }

  public Predicate<Entity> getPredicate(CapturedMob capturedMob) {
    return new Predicate<Entity>() {
      @Override
      public boolean apply(@Nullable Entity entity) {
        if (entity == null || !entity.isEntityAlive() || entity.doesEntityNotTriggerPressurePlate()
            || ((entity instanceof EntityPlayer) && ((EntityPlayer) entity).isSpectator())) {
          return false;
        }
        if (searchClass.isInstance(entity) && whiteClasses.isEmpty()) {
          return true;
        }
        for (Class<? extends Entity> clazz : whiteClasses) {
          if (clazz.isInstance(entity)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public int hashCode() {
        return super.hashCode();
      }

      @Override
      public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
      }
    };
  }

}
