package crazypants.enderio.base.autosave;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.autosave.endercore.HandleEnderInventory;
import crazypants.enderio.base.autosave.endercore.HandleSmartTank;
import crazypants.enderio.base.autosave.endercore.HandleThings;
import crazypants.enderio.base.autosave.endercore.HandleUserIdent;
import crazypants.enderio.base.autosave.enderio.HandleCapturedMob;
import crazypants.enderio.base.autosave.enderio.HandleExperienceContainer;
import crazypants.enderio.base.autosave.enderio.HandleGrindingMultiplier;
import crazypants.enderio.base.autosave.enderio.HandleIFilter;
import crazypants.enderio.base.autosave.enderio.HandleIMachineRecipe;
import crazypants.enderio.base.autosave.enderio.HandlePoweredTask;
import crazypants.enderio.base.autosave.enderio.HandleTelepadTarget;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.handlers.java.util.HandleSimpleCollection;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static info.loenwind.autosave.Registry.GLOBAL_REGISTRY;

@EventBusSubscriber(modid = EnderIO.MODID)
public class BaseHandlers {
  
  public static final @Nonnull Registry REGISTRY = new Registry();

  @SubscribeEvent
  public static void register(EnderIOLifecycleEvent.PreInit event) {
    // EnderCore Object Handlers, leave these global for other mods
    GLOBAL_REGISTRY.register(new HandleEnderInventory());
    GLOBAL_REGISTRY.register(new HandleSimpleCollection<>(NNList.class));
    GLOBAL_REGISTRY.register(new HandleSmartTank());
    GLOBAL_REGISTRY.register(new HandleThings());
    GLOBAL_REGISTRY.register(new HandleUserIdent());

    // EnderIO Object Handlers
    REGISTRY.register(new HandleCapturedMob());
    REGISTRY.register(new HandleExperienceContainer());
    REGISTRY.register(new HandleIMachineRecipe());
    REGISTRY.register(new HandlePoweredTask());
    REGISTRY.register(new HandleIFilter());
    REGISTRY.register(new HandleGrindingMultiplier());
    REGISTRY.register(new HandleTelepadTarget());
  }

}
