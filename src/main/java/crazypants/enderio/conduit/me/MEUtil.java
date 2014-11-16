package crazypants.enderio.conduit.me;

import java.util.Set;

import net.minecraft.item.ItemStack;
import appeng.api.parts.IPartItem;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.Loader;
import crazypants.enderio.config.Config;

public class MEUtil {

  private static Set<Class<?>> supportedParts;

  static {
    supportedParts = Sets.newHashSet();

    try {
      supportedParts.add(Class.forName("appeng.parts.automation.PartImportBus"));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Could not load ME conduit GUI", e);
    }
  }
  
  private static boolean useCheckPerformed = false;
  private static boolean isMeConduitEnabled = false;

  public static boolean isMEEnabled() {
    if(!useCheckPerformed) {
      isMeConduitEnabled = Loader.isModLoaded("appliedenergistics2") && Config.enableMEConduits;
    }
    return isMeConduitEnabled;
  }
  
  public static boolean isSupportedPart(ItemStack part) {
    return part != null && part.getItem() instanceof IPartItem && supportedParts.contains(((IPartItem)part.getItem()).createPartFromItemStack(part).getClass());
  }
}
