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

public class ItemMaterial extends Item {

  public static ItemMaterial create() {
    ItemMaterial mp = new ItemMaterial();
    mp.init();
    return mp;
  }

  private ItemMaterial() {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemMaterial.unlocalisedName);

  }

  private void init() {
    GameRegistry.registerItem(this, ModObject.itemMaterial.unlocalisedName);
  }

  @SideOnly(Side.CLIENT)
  public void addRenderers() {
    List<ResourceLocation> names = Material.resources();    
    ModelBakery.registerItemVariants(this, names.toArray(new ResourceLocation[names.size()]));    
    for (Material c : Material.values()) {
      ClientUtil.regRenderer(this, c.ordinal(), c.baseName);
    }     
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, Material.values().length - 1);
    return Material.values()[i].unlocalisedName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < Material.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean hasEffect(ItemStack par1ItemStack) {
    if(par1ItemStack == null) {
      return false;
    }
    int damage = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, Material.values().length - 1);
    return Material.values()[damage].hasEffect;
  }

}
