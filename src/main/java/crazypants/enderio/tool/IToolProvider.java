package crazypants.enderio.tool;

import crazypants.enderio.api.tool.ITool;
import net.minecraft.item.ItemStack;

public interface IToolProvider {

    ITool getTool(ItemStack stack);
}
