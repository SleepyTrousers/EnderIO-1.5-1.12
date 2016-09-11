package crazypants.enderio.material;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class ItemMachinePart extends Item {

  private final IIcon[] icons;

  public static ItemMachinePart create() {
    ItemMachinePart mp = new ItemMachinePart();
    mp.init();
    return mp;
  }

  private ItemMachinePart() {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemMachinePart.unlocalisedName);

    icons = new IIcon[MachinePart.values().length];
  }

  private void init() {
    GameRegistry.registerItem(this, ModObject.itemMachinePart.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIconFromDamage(int damage) {
    damage = MathHelper.clamp_int(damage, 0, MachinePart.values().length - 1);
    return icons[damage];
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister IIconRegister) {
    int numParts = MachinePart.values().length;
    for (int i = 0; i < numParts; i++) {
      icons[i] = IIconRegister.registerIcon(MachinePart.values()[i].iconKey);
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, MachinePart.values().length - 1);
    return MachinePart.values()[i].unlocalisedName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < MachinePart.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

}
