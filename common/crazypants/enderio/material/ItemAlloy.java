package crazypants.enderio.material;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class ItemAlloy extends Item {

  private final Icon[] icons;

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

    icons = new Icon[Alloy.values().length * 2];
  }

  private void init() {
    LanguageRegistry.addName(this, ModObject.itemAlloy.name);
    GameRegistry.registerItem(this, ModObject.itemAlloy.unlocalisedName);
    for (int i = 0; i < Alloy.values().length; i++) {
      LanguageRegistry.instance().addStringLocalization(getUnlocalizedName() + "." + Alloy.values()[i].unlocalisedName + ".name", Alloy.values()[i].uiName);
    }
    for (int i = 0; i < Alloy.values().length; i++) {
      LanguageRegistry.instance().addStringLocalization(getUnlocalizedName() + "." + Alloy.values()[i].unlocalisedName + "Nugget" + ".name",
          Alloy.values()[i].uiName + " Nugget");
    }
  }

  @Override
  public Icon getIconFromDamage(int damage) {
    damage = MathHelper.clamp_int(damage, 0, Alloy.values().length * 2);
    return icons[damage];
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    int numAlloys = Alloy.values().length;
    for (int i = 0; i < numAlloys; i++) {
      icons[i] = iconRegister.registerIcon(Alloy.values()[i].iconKey);
    }
    for (int i = 0; i < numAlloys; i++) {
      icons[i + numAlloys] = iconRegister.registerIcon(Alloy.values()[i].iconKey + "Nugget");
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, Alloy.values().length * 2);
    if (i < Alloy.values().length) {
      return super.getUnlocalizedName() + "." + Alloy.values()[i].unlocalisedName;
    } else {
      return super.getUnlocalizedName() + "." + Alloy.values()[i - Alloy.values().length].unlocalisedName + "Nugget";
    }
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < Alloy.values().length * 2; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

}
