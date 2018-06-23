package crazypants.enderio.base.item.darksteel.attributes;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.config.Config;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class ToolData {

  public static final @Nonnull ToolMaterial MATERIAL_DARK_STEEL = NullHelper
      .notnull(EnumHelper.addToolMaterial("darkSteel", Config.darkSteelPickMinesTiCArdite ? 5 : 3, 2000, 8, 3.0001f, 25), "failed to add tool material dark steel");
  // 3.0001f = more desirable for mobs (i.e. they'll pick it up even if they already have diamond)

  public static final @Nonnull ToolMaterial MATERIAL_END_STEEL = NullHelper
      .notnull(EnumHelper.addToolMaterial("endSteel", Config.endSteelPickMinesTiCArdite ? 5 : 3, 2000, 10, 5f, 30), "failed to add tool material end steel");

}
