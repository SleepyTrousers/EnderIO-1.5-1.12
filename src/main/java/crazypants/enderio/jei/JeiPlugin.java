package crazypants.enderio.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class JeiPlugin extends BlankModPlugin {

  @Override
  public void register(@Nonnull IModRegistry registry) {

    IJeiHelpers jeiHelpers = registry.getJeiHelpers();    
    IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        
    AlloyRecipeCategory.register(registry,guiHelper);
    SagMillRecipeCategory.register(registry,guiHelper);
    EnchanterRecipeCategory.register(registry,guiHelper);
    SliceAndSpliceRecipeCategory.register(registry,guiHelper);
    SoulBinderRecipeCategory.register(registry, guiHelper);
           
  }


}
