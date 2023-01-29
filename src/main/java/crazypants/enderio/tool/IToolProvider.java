package crazypants.enderio.tool;

import net.minecraft.item.ItemStack;

import crazypants.enderio.api.tool.ITool;

public interface IToolProvider {

    ITool getTool(ItemStack stack);
}
