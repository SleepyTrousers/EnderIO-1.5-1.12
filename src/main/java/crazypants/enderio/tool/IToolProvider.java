package crazypants.enderio.tool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.tool.ITool;
import net.minecraft.item.ItemStack;

public interface IToolProvider {

  @Nullable
  ITool getTool(@Nonnull ItemStack stack);

}
