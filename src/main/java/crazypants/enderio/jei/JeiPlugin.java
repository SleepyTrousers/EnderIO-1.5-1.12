package crazypants.enderio.jei;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.INbtIgnoreList;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.Item;

import static crazypants.util.CapturedMob.CUSTOM_NAME_KEY;
import static crazypants.util.CapturedMob.ENTITY_KEY;
import static crazypants.util.CapturedMob.IS_STUB_KEY;
import static crazypants.util.CapturedMob.VARIANT_KEY;
import static crazypants.util.NbtValue.SOURCE_BLOCK;
import static crazypants.util.NbtValue.SOURCE_META;

@JEIPlugin
public class JeiPlugin extends BlankModPlugin {

  private static IJeiRuntime jeiRuntime = null;

  @Override
  public void register(@Nonnull IModRegistry registry) {

    IJeiHelpers jeiHelpers = registry.getJeiHelpers();    
    IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        
    AlloyRecipeCategory.register(registry,guiHelper);
    SagMillRecipeCategory.register(registry,guiHelper);
    EnchanterRecipeCategory.register(registry,guiHelper);
    SliceAndSpliceRecipeCategory.register(registry,guiHelper);
    SoulBinderRecipeCategory.register(registry, guiHelper);
    PainterRecipeCategory.register(registry, guiHelper);
    VatRecipeCategory.register(registry, guiHelper);
    DarkSteelUpgradeRecipeCategory.register(registry, guiHelper);
    TankRecipeCategory.register(registry, guiHelper);
    CombustionRecipeCategory.register(registry, guiHelper);

    INbtIgnoreList nbtIgnoreList = registry.getJeiHelpers().getNbtIgnoreList();
    final Item ppplate = Item.getItemFromBlock(EnderIO.blockPaintedPressurePlate);
    if (ppplate != null) {
      nbtIgnoreList.ignoreNbtTagNames(ppplate, ENTITY_KEY, CUSTOM_NAME_KEY, IS_STUB_KEY, VARIANT_KEY, SOURCE_BLOCK.getKey(), SOURCE_META.getKey());
    }

    CrafterRecipeTransferHandler.register(registry);
    InventoryPanelRecipeTransferHandler.register(registry);

    registry.addAdvancedGuiHandlers(new AdvancedGuiHandlerEnderIO());
  }

  @Override
  public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
    this.jeiRuntime = jeiRuntime;
    JeiAccessor.jeiRuntimeAvailable = true;
  }

  public static void setFilterText(@Nonnull String filterText) {
    jeiRuntime.getItemListOverlay().setFilterText(filterText);
  }

  public static @Nonnull String getFilterText() {
    return jeiRuntime.getItemListOverlay().getFilterText();
  }

}
