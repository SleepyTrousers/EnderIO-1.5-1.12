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

public class ItemMachinePart extends Item {

  private final Icon[] icons;

  public static ItemMachinePart create() {
    ItemMachinePart mp = new ItemMachinePart();
    mp.init();
    return mp;
  }

  private ItemMachinePart() {
    super(ModObject.itemMachinePart.id);
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemMachinePart.unlocalisedName);

    icons = new Icon[MachinePart.values().length];
  }

  private void init() {
    LanguageRegistry.addName(this, ModObject.itemMachinePart.name);
    GameRegistry.registerItem(this, ModObject.itemMachinePart.unlocalisedName);
    for (int i = 0; i < MachinePart.values().length; i++) {
      LanguageRegistry.instance().addStringLocalization(getUnlocalizedName() + "." + MachinePart.values()[i].unlocalisedName + ".name",
          MachinePart.values()[i].uiName);
    }
  }

  @Override
  public Icon getIconFromDamage(int damage) {
    damage = MathHelper.clamp_int(damage, 0, MachinePart.values().length);
    return icons[damage];
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    int numParts = MachinePart.values().length;
    for (int i = 0; i < numParts; i++) {
      icons[i] = iconRegister.registerIcon(MachinePart.values()[i].iconKey);
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, MachinePart.values().length);
    return super.getUnlocalizedName() + "." + MachinePart.values()[i].unlocalisedName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < MachinePart.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

}
