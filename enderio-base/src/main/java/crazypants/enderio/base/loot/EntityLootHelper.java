package crazypants.enderio.base.loot;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class EntityLootHelper {

  static ResourceLocation getDeathLootTable(EntityLiving entity) {
    return ReflectionHelper.getPrivateValue(EntityLiving.class, entity, "field_184659_bA", "deathLootTable");
  }

  static long getDeathLootTableSeed(EntityLiving entity) {
    return ReflectionHelper.getPrivateValue(EntityLiving.class, entity, "field_184653_bB", "deathLootTableSeed");
  }

  static final @Nonnull Random rand = new Random();

  /**
   * Drops loot like {@link EntityLiving#dropLoot(boolean, int, DamageSource)} but using the looting enchantment for the luck value. This allows us to use the
   * loot pool's bonus rolls instead of having to add a function to each single loot table entry. That way we can match the old behavior of dropping additional
   * rolls on the whole table.
   * <p>
   * Note; Don't forget to also call <code>dropEquipment(wasRecentlyHit, lootingModifier)</code>.
   * 
   * @param entity
   *          'this'
   * @param lootTable
   *          'getLootTable()'
   * @param source
   */
  // don't forget to also call dropEquipment(wasRecentlyHit, lootingModifier)
  static public void dropLoot(@Nonnull EntityLiving entity, ResourceLocation lootTable, @Nonnull DamageSource source) {
    ResourceLocation resourcelocation = getDeathLootTable(entity);

    if (resourcelocation == null) {
      resourcelocation = lootTable;
    }

    if (resourcelocation != null) {
      LootTable loottable = entity.world.getLootTableManager().getLootTableFromLocation(resourcelocation);
      LootContext.Builder builder = (new LootContext.Builder((WorldServer) entity.world)).withLootedEntity(entity).withDamageSource(source);

      Entity trueSource = source.getTrueSource();
      if (trueSource instanceof EntityPlayer) {
        builder = builder.withPlayer((EntityPlayer) trueSource).withLuck(ForgeHooks.getLootingLevel(entity, trueSource, source));
      }

      long deathLootTableSeed = getDeathLootTableSeed(entity);
      for (ItemStack itemstack : loottable.generateLootForPools(deathLootTableSeed == 0L ? rand : new Random(deathLootTableSeed), builder.build())) {
        if (itemstack != null) {
          entity.entityDropItem(itemstack, 0.0F);
        }
      }

    }
  }

}
