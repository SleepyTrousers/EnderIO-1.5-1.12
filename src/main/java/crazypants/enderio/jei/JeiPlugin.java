package crazypants.enderio.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.INbtIgnoreList;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.Item;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.painter.BlockPainter;

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
           
    INbtIgnoreList nbtIgnoreList = registry.getJeiHelpers().getNbtIgnoreList();
    final Item ppplate = Item.getItemFromBlock(EnderIO.blockPaintedPressurePlate);
    if (ppplate != null) {
      nbtIgnoreList.ignoreNbtTagNames(ppplate, "mobType", BlockPainter.KEY_SOURCE_BLOCK_ID, BlockPainter.KEY_SOURCE_BLOCK_META);
    }
  }


}
