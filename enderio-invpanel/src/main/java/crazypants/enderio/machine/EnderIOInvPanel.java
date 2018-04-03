package crazypants.enderio.machine;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.machine.invpanel.config.ConfigHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static crazypants.enderio.machine.EnderIOInvPanel.MODID;
import static crazypants.enderio.machine.EnderIOInvPanel.MOD_NAME;
import static crazypants.enderio.machine.EnderIOInvPanel.VERSION;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION)
public class EnderIOInvPanel implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderioinvpanel";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Inventory Panel";
  public static final @Nonnull String VERSION = "@VERSION@";

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    ConfigHandler.init(event);
  }

  @Override
  @Nonnull
  public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
    return new NNList<>(Triple.of(2, null, "invpanel"));
  }
}
