package crazypants.enderio.material;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class ItemAlloy extends Item {

  static final boolean useNuggets = false;

  private final Icon[] icons;
  private final int numItems;

  public static ItemAlloy create() {
    ItemAlloy alloy = new ItemAlloy();
    alloy.init();
    return alloy;
  }

  private ItemAlloy() {
    super(ModObject.itemAlloy.id);
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemAlloy.unlocalisedName);

    numItems = Alloy.values().length;
    if(useNuggets) {
      numItems = numItems * 2;
    }
    icons = new Icon[numItems];
  }

  private void init() {
    GameRegistry.registerItem(this, ModObject.itemAlloy.unlocalisedName);
  }

  @Override
  public Icon getIconFromDamage(int damage) {
    damage = MathHelper.clamp_int(damage, 0, numItems - 1);
    return icons[damage];
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    int numAlloys = Alloy.values().length;
    for (int i = 0; i < numAlloys; i++) {
      icons[i] = iconRegister.registerIcon(Alloy.values()[i].iconKey);
    }
    if(useNuggets) {
      for (int i = 0; i < numAlloys; i++) {
        icons[i + numAlloys] = iconRegister.registerIcon(Alloy.values()[i].iconKey + "Nugget");
      }
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, numItems - 1);
    if(i < Alloy.values().length) {
      return Alloy.values()[i].unlocalisedName;
    } else {
      return Alloy.values()[i - Alloy.values().length].unlocalisedName + "Nugget";
    }
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < numItems; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

}
