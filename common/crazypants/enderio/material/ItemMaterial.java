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

public class ItemMaterial extends Item {

  private final Icon[] icons;

  public static ItemMaterial create() {
    ItemMaterial mp = new ItemMaterial();
    mp.init();
    return mp;
  }

  private ItemMaterial() {
    super(ModObject.itemMaterial.id);
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemMaterial.unlocalisedName);

    icons = new Icon[Material.values().length];
  }

  private void init() {
    GameRegistry.registerItem(this, ModObject.itemMaterial.unlocalisedName);
  }

  @Override
  public Icon getIconFromDamage(int damage) {
    damage = MathHelper.clamp_int(damage, 0, Material.values().length - 1);
    return icons[damage];
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    int numParts = Material.values().length;
    for (int i = 0; i < numParts; i++) {
      icons[i] = iconRegister.registerIcon(Material.values()[i].iconKey);
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, Material.values().length - 1);
    return Material.values()[i].unlocalisedName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < Material.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  public boolean hasEffect(ItemStack par1ItemStack, int pass) {
    if(par1ItemStack == null) {
      return false;
    }
    int damage = par1ItemStack.getItemDamage();
    if(damage == Material.VIBRANT_CYSTAL.ordinal()) {
      return true;
    }
    return false;
  }

}
