package crazypants.enderio.integration.forestry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;

public class ForestryItemStacks {

  // Forge will force-load this class because of the itemStack holders. So this cannot be in any class that depends on Forestry...

  @ItemStackHolder("forestry:sapling")
  public static final ItemStack FORESTRY_SAPLING = null;

  @ItemStackHolder("forestry:fertilizer_compound")
  public static final ItemStack FORESTRY_FERTILIZER = null;

}
