package crazypants.enderio.machine.invpanel.client;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

public class CraftingHelperNEI extends CraftingHelper {

  public Object overlayRenderer;

  public CraftingHelperNEI(ItemStack[][] ingredients) {
    super(ingredients);
  }

  @Override
  @Optional.Method(modid = "NotEnoughItems")
  public void install() {
    super.install();
    //TODO: Mod NEI
//    if(overlayRenderer != null) {
//      LayoutManager.overlayRenderer = (IRecipeOverlayRenderer) overlayRenderer;
//    } else {
//      List<PositionedStack> list = new ArrayList<PositionedStack>();
//      for(int y = 0; y < 3; y++) {
//        for(int x = 0; x < 3; x++) {
//          ItemStack[] ingredient = ingredients[y*3+x];
//          if(ingredient != null) {
//            list.add(new PositionedStack(ingredient,
//                    InventoryPanelNEIOverlayHandler.NEI_OFFSET_X + x*18,
//                    InventoryPanelNEIOverlayHandler.NEI_OFFSET_Y + y*18));
//          }
//        }
//      }
//      LayoutManager.overlayRenderer = new DefaultOverlayRenderer(list, InventoryPanelNEIOverlayHandler.positioner);
//    }
  }

  @Override
  @Optional.Method(modid = "NotEnoughItems")
  public void remove() {
    super.remove();
//    LayoutManager.overlayRenderer = null;
  }
}
