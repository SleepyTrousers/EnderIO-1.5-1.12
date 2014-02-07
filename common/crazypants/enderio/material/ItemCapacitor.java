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
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.ICapacitorItem;

public class ItemCapacitor extends Item implements ICapacitorItem {

  private static final BasicCapacitor CAP = new BasicCapacitor();

  public static ItemCapacitor create() {
    ItemCapacitor result = new ItemCapacitor();
    result.init();
    return result;
  }

  private final Icon[] icons;

  protected ItemCapacitor() {
    super(ModObject.itemBasicCapacitor.id);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemBasicCapacitor.unlocalisedName);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);

    icons = new Icon[Capacitors.values().length];
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemBasicCapacitor.unlocalisedName);
  }

  @Override
  public Icon getIconFromDamage(int damage) {
    damage = MathHelper.clamp_int(damage, 0, Capacitors.values().length - 1);
    return icons[damage];
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    for (int i = 0; i < Capacitors.values().length; i++) {
      icons[i] = iconRegister.registerIcon(Capacitors.values()[i].iconKey);
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, Capacitors.values().length - 1);
    return Capacitors.values()[i].unlocalisedName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < Capacitors.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  public ICapacitor getCapacitor(ItemStack stack) {
    int damage = MathHelper.clamp_int(stack.getItemDamage(), 0, Capacitors.values().length - 1);
    return Capacitors.values()[damage].capacitor;
  }

}
