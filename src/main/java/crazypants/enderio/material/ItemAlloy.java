package crazypants.enderio.material;

import java.util.List;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.IHaveRenderers;
import crazypants.enderio.ModObject;
import crazypants.util.ClientUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAlloy extends Item implements IHaveRenderers {

  static final boolean useNuggets = false;

  private final int numItems;

  public static ItemAlloy create() {
    ItemAlloy alloy = new ItemAlloy();
    alloy.init();
    return alloy;
  }

  private ItemAlloy() {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemAlloy.getUnlocalisedName());
    setRegistryName(ModObject.itemAlloy.getUnlocalisedName());

    numItems = Alloy.values().length;
    if (useNuggets) {
      numItems = numItems * 2;
    }
  }

  private void init() {
    GameRegistry.register(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    int numAlloys = Alloy.values().length;
    for (int i = 0; i < numAlloys; i++) {
      ClientUtil.regRenderer(this, i, Alloy.values()[i].baseName);
    }
    if (useNuggets) {
      for (int i = 0; i < numAlloys; i++) {
        ClientUtil.regRenderer(this, numAlloys + i, Alloy.values()[i].baseName + "Nugget");
      }
    }
  }


  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, numItems - 1);
    if (i < Alloy.values().length) {
      return Alloy.values()[i].unlocalisedName;
    } else {
      return Alloy.values()[i - Alloy.values().length].unlocalisedName + "Nugget";
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (int j = 0; j < numItems; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }
}
