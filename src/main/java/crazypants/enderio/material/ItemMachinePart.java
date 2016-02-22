package crazypants.enderio.material;

import java.util.List;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.util.ClientUtil;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMachinePart extends Item {

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
  }

  private void init() {
    GameRegistry.registerItem(this, ModObject.itemMachinePart.unlocalisedName);
  }

  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    List<ResourceLocation> names = MachinePart.resources();    
    ModelBakery.registerItemVariants(this, names.toArray(new ResourceLocation[names.size()]));    
    for (MachinePart c : MachinePart.values()) {
      ClientUtil.regRenderer(this, c.ordinal(), c.baseName);
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
