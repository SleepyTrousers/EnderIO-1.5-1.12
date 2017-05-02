package crazypants.enderio.material;

import java.util.List;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMachinePart extends Item implements IHaveRenderers  {

    public static ItemMachinePart create() {
    ItemMachinePart mp = new ItemMachinePart();
    mp.init();
    return mp;
  }

  private ItemMachinePart() {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    setUnlocalizedName(ModObject.itemMachinePart.getUnlocalisedName());
    setRegistryName(ModObject.itemMachinePart.getUnlocalisedName());
  }

  private void init() {
    GameRegistry.register(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {       
    for (MachinePart c : MachinePart.values()) {
      ClientUtil.regRenderer(this, c.ordinal(), c.baseName);
    }     
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp(par1ItemStack.getItemDamage(), 0, MachinePart.values().length - 1);
    return MachinePart.values()[i].unlocalisedName;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (int j = 0; j < MachinePart.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

}
