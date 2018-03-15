package crazypants.enderio.integration.tic.traits;

import java.util.Iterator;

import javax.annotation.Nonnull;

import crazypants.enderio.base.teleport.RandomTeleportUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.traits.AbstractTraitLeveled;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitTeleport extends AbstractTraitLeveled {

  // MUST create them in ascending order of levels, otherwise TiC will throw itself out the window
  public static final @Nonnull TraitTeleport instance1 = new TraitTeleport(1, 1);
  public static final @Nonnull TraitTeleport instance2 = new TraitTeleport(1, 2);
  public static final @Nonnull TraitTeleport instance3 = new TraitTeleport(1, 3);
  public static final @Nonnull TraitTeleport instance4 = new TraitTeleport(2, 4);
  public static final @Nonnull TraitTeleport instance0 = new TraitTeleport(3, 0);

  // the "variant" allows multiple traits of the same level to be added
  public TraitTeleport(int level, int variant) {
    super("enderport", "-" + variant, 0x8F032620, 5, level);
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    if (wasHit) {
      ModifierNBT data = new ModifierNBT(TinkerUtil.getModifierTag(tool, name));
      if (random.nextFloat() < (data.level * .21f)) {
        RandomTeleportUtil.teleportEntity(target.world, target, true);
      }
    }
  }

  @Override
  public void blockHarvestDrops(ItemStack tool, HarvestDropsEvent event) {
    if (ToolHelper.isToolEffective2(tool, event.getState())) {
      ModifierNBT data = new ModifierNBT(TinkerUtil.getModifierTag(tool, name));
      BlockPos pos = event.getPos();
      final World world = event.getWorld();
      for (Iterator<ItemStack> iterator = event.getDrops().iterator(); iterator.hasNext();) {
        ItemStack next = iterator.next().copy();
        if (world != null && random.nextFloat() <= event.getDropChance()) {
          if (random.nextFloat() < (data.level * .21f)) {
            RandomTeleportUtil.teleportSpawnItem(world, pos, next);
            iterator.remove();
          }
        } else {
          iterator.remove();
        }
      }
      event.setDropChance(1); // we already implemented the drop chance
    }
  }

  @Override
  public boolean canApplyTogether(IToolMod otherModifier) {
    return !(otherModifier instanceof TraitTeleport) && !(otherModifier instanceof TraitPickup)
        && otherModifier != TinkerRegistry.getModifier("exnihilo_smashing");
  }

}
