package crazypants.enderio.conduits.me;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.util.NNList;

import appeng.api.networking.IGridHost;
import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import crazypants.enderio.conduits.me.init.ConduitAppliedEnergisticsObject;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = EnderIOConduitsAppliedEnergistics.MODID, name = EnderIOConduitsAppliedEnergistics.MOD_NAME, version = EnderIOConduitsAppliedEnergistics.VERSION, dependencies = EnderIOConduitsAppliedEnergistics.DEPENDENCIES)
@EventBusSubscriber
public class EnderIOConduitsAppliedEnergistics implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderioconduitsappliedenergistics";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Conduits Applied Energistics";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

  @EventHandler
  public static void init(@Nonnull FMLPreInitializationEvent event) {
    if (MEUtil.isMEEnabled()) {
      Log.warn("Applied Energistics conduits loaded. Let your networks connect!");
    } else {
      Log.warn("Applied Energistics conduits NOT loaded. Applied Energistics is not installed");
    }
  }

  @EventHandler
  public static void init(FMLInitializationEvent event) {
    if (MEUtil.isMEEnabled()) {
      // Sanity checking
      System.out.println("Mixin successful? " + IGridHost.class.isAssignableFrom(TileConduitBundle.class));
    }
  }

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
    if (MEUtil.isMEEnabled()) {
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void registerConduits(@Nonnull RegistryEvent.Register<Block> event) {
    if (MEUtil.isMEEnabled()) {
      ConduitAppliedEnergisticsObject.registerBlocksEarly(event);
    }
  }

  @Override
  @Nonnull
  public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
    if (MEUtil.isMEEnabled()) {
      return new NNList<>(Triple.of(2, null, "conduits-applied-energistics"));
    }
    return NNList.emptyList();
  }

}
