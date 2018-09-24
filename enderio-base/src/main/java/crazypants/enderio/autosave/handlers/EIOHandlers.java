package crazypants.enderio.autosave.handlers;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.autosave.handlers.endercore.HandleEnderInventory;
import crazypants.enderio.autosave.handlers.endercore.HandleSmartTank;
import crazypants.enderio.autosave.handlers.endercore.HandleThings;
import crazypants.enderio.autosave.handlers.endercore.HandleUserIdent;
import crazypants.enderio.autosave.handlers.enderio.HandleCapturedMob;
import crazypants.enderio.autosave.handlers.enderio.HandleExperienceContainer;
import crazypants.enderio.autosave.handlers.enderio.HandleIMachineRecipe;
import crazypants.enderio.autosave.handlers.enderio.HandlePoweredTask;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.java.util.HandleSimpleCollection;

import static info.loenwind.autosave.Registry.GLOBAL_REGISTRY;

public class EIOHandlers {
  
  public static final @Nonnull Registry REGISTRY = new Registry();

  public static void register() {
    try {
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

    } catch (NoHandlerFoundException ignored) {} // impossible
  }

}
