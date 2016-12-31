package crazypants.enderio.enderface;

import java.util.List;

import crazypants.enderio.ModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEnderface extends Item implements IHaveRenderers {

  public static ItemEnderface create() {
    ItemEnderface result = new ItemEnderface();
    result.init();
    return result;
  }

  protected ItemEnderface() {
    setCreativeTab(null);
    setUnlocalizedName("enderio." + ModObject.itemEnderface.name());
    setMaxStackSize(1);
    setRegistryName(ModObject.itemEnderface.getUnlocalisedName());
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    if (tab != null) {
      for (int i = 0; i < 4; i++) {
        subItems.add(new ItemStack(itemIn, 1, i));
      }
    }
  }

  @Override
  public void registerRenderers() {
    ClientUtil.regRenderer(this, 0, ModObject.itemEnderface.getUnlocalisedName());
    ClientUtil.regRenderer(this, 1, ModObject.itemEnderface.getUnlocalisedName() + "_items");
    ClientUtil.regRenderer(this, 2, ModObject.itemEnderface.getUnlocalisedName() + "_materials");
    ClientUtil.regRenderer(this, 3, ModObject.itemEnderface.getUnlocalisedName() + "_machines");
  }

}
