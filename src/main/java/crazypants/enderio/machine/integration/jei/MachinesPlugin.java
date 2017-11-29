package crazypants.enderio.machine.integration.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class MachinesPlugin extends BlankModPlugin {
  
  
  @Override
  public void register(IModRegistry registry) {
    super.register(registry);
    
    IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
    
    AlloyRecipeCategory.register(registry, guiHelper);
    CombustionRecipeCategory.register(registry, guiHelper);
    EnchanterRecipeCategory.register(registry, guiHelper);
    PainterRecipeCategory.register(registry, registry.getJeiHelpers());
    SagMillRecipeCategory.register(registry, guiHelper);
    SliceAndSpliceRecipeCategory.register(registry, guiHelper);
    SoulBinderRecipeCategory.register(registry, guiHelper);
    TankRecipeCategory.register(registry, guiHelper);
    VatRecipeCategory.register(registry, guiHelper);
  }

}
