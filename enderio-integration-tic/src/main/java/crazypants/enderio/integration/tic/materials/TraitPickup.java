package crazypants.enderio.integration.tic.materials;

import java.util.Iterator;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.traits.AbstractTraitLeveled;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitPickup extends AbstractTraitLeveled {

  // the "variant" allows multiple traits of the same level to be added
  public TraitPickup(int level, int variant) {
    super("enderpickup", "-" + variant, 0x8F2ccdb1, 3, level);
  }

  @Override
  public void blockHarvestDrops(ItemStack tool, HarvestDropsEvent event) {
    if (ToolHelper.isToolEffective2(tool, event.getState())) {
      ModifierNBT data = new ModifierNBT(TinkerUtil.getModifierTag(tool, name));
      for (Iterator<ItemStack> iterator = event.getDrops().iterator(); iterator.hasNext();) {
        ItemStack next = iterator.next().copy();
        if (random.nextFloat() <= event.getDropChance()) {
          System.out.println(data.level);
          if (random.nextFloat() < (data.level * .55f)) {
            if (event.getHarvester().inventory.addItemStackToInventory(next)) {
              iterator.remove();
            }
          }
        } else {
          iterator.remove();
        }
      }
      event.setDropChance(1); // we already implemented the drop chance
    }
  }

}
