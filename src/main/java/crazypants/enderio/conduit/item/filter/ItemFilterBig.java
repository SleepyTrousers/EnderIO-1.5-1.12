package crazypants.enderio.conduit.item.filter;

import java.util.List;

import com.enderio.core.client.gui.widget.GhostSlot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.gui.item.BasicItemFilterGui;
import crazypants.enderio.conduit.gui.item.BigItemFilterGui;
import crazypants.enderio.conduit.gui.item.IItemFilterGui;
import crazypants.enderio.conduit.gui.item.ItemConduitFilterContainer;
import crazypants.enderio.conduit.item.IItemConduit;

public class ItemFilterBig extends ItemFilter {

	public ItemFilterBig() {
		this(24,false);
	}

  public ItemFilterBig(int numItems, boolean isAdvanced) {
	  super(numItems, isAdvanced);
  }

  @Override
  public String getInventoryName() {
    return "Big Item Filter";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemFilterGui getGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
    ItemConduitFilterContainer cont = new ItemConduitFilterContainer(itemConduit, gui.getDir(), isInput);
    BigItemFilterGui bigItemFilterGui = new BigItemFilterGui(gui, cont, !isInput, isInput);
    bigItemFilterGui.createFilterSlots();
    return bigItemFilterGui;
  }

  @Override
  public void createGhostSlots(List<GhostSlot> slots, int xOffset, int yOffset, Runnable cb) {
    int topY = yOffset;
    int leftX = xOffset;
    int index = 0;
    int numRows = (int) Math.ceil(items.length/8);
    for (int row = 0; row < numRows; ++row) {
      for (int col = 0; col < 8; ++col) {
        int x = leftX + col * 18;
        int y = topY + row * 18;
        slots.add(new ItemFilterGhostSlot(index, x, y, cb));
        index++;
      }
    }
  }

  @Override
  public int getSlotCount() {
    return getSizeInventory();
  }


  @Override
  public String toString() {
//    return "ItemFilter [isBlacklist=" + isBlacklist + ", matchMeta=" + matchMeta + ", matchNBT=" + matchNBT + ", useOreDict=" + useOreDict + ", sticky="
//        + sticky + ", items=" + Arrays.toString(items) + ", oreIds=" + Arrays.toString(oreIds) + ", isAdvanced=" + isAdvanced + "]";
    return "Big"+super.toString();
  }

  @Override
  public String getUnlocalizedName() {
    return isAdvanced() ? "gui.big_advanced_item_filter" : "gui.big_item_filter";
  }
}
