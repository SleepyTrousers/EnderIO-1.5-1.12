package crazypants.enderio.integration.tic.traits;

import java.util.Iterator;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitPickup extends ModifierTrait {

  public static TraitPickup instance = new TraitPickup();

  public TraitPickup() {
    super("enderpickup", 0x8F2ccdb1, 1, 1);
  }

  @Override
  public void blockHarvestDrops(ItemStack tool, HarvestDropsEvent event) {
    if (ToolHelper.isToolEffective2(tool, event.getState())) {
      for (Iterator<ItemStack> iterator = event.getDrops().iterator(); iterator.hasNext();) {
        ItemStack next = iterator.next().copy();
        if (random.nextFloat() <= event.getDropChance()) {
          if (event.getHarvester().inventory.addItemStackToInventory(next)) {
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
    return !(otherModifier instanceof TraitPickup);
  }

  // @Override (newer API version)
  int getPriority() {
    return 10;
  }

}
