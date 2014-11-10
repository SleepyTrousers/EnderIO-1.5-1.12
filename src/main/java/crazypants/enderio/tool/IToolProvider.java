package crazypants.enderio.tool;

import net.minecraft.item.ItemStack;

public interface IToolProvider {

  ITool getTool(ItemStack stack);

}
