package crazypants.enderio.zoo.entity;

import javax.annotation.Nonnull;

import crazypants.enderio.zoo.EnderIOZoo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Marker interface for WAILA purposes
 */
public interface IEnderZooMob extends IMob {

  static void register(@Nonnull Register<EntityEntry> event, @Nonnull String name, @Nonnull Class<? extends Entity> clazz, int eggBgCol, int eggFgCol,
      IMobID id) {
    ResourceLocation rl = new ResourceLocation(EnderIOZoo.MODID, name);
    EntityEntry entry = new EntityEntry(clazz, EnderIOZoo.MODID + "." + name);
    entry.setEgg(new EntityEggInfo(rl, eggBgCol, eggFgCol));
    event.getRegistry().register(entry.setRegistryName(rl));

    EntityRegistry.registerModEntity(entry.getRegistryName(), entry.getEntityClass(), entry.getName(), id.getID(), EnderIOZoo.MODID, 64, 3, true);
  }

  public static interface IMobID {

    int getID();

  }

  public static enum MobID implements IMobID {
    CCREEPER,
    DLIME,
    DWOLF,
    EMINIY,
    FKNIGHT,
    FMOUNT,
    OWL,
    WCAT,
    WWITCH;

    @Override
    public int getID() {
      return ordinal();
    }

  }
}
