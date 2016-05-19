package crazypants.enderio.item.darksteel;

import java.util.List;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.util.ClientUtil;

public class ItemGliderWing extends Item implements IResourceTooltipProvider {

  public static ItemGliderWing create() {
    ItemGliderWing result = new ItemGliderWing();
    result.init();
    return result;
  }

  protected ItemGliderWing() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemGliderWing.getUnlocalisedName());
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemGliderWing.getUnlocalisedName());
  }

  @SideOnly(Side.CLIENT)
  public void registerRenderers() {       
    ModelBakery.registerItemVariants(this, new ResourceLocation("enderio:itemGliderWing"),new ResourceLocation("enderio:itemGliderWings"));        
    ClientUtil.regRenderer(this, 0,"itemGliderWing");
    ClientUtil.regRenderer(this, 1 ,"itemGliderWings");    
  }  

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, 1);
    if(i == 0) {
      return super.getUnlocalizedName();
    }
    return super.getUnlocalizedName() + "s";
  }

  @Override  
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (int j = 0; j < 2; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }


//  @Override
//  @SideOnly(Side.CLIENT)
//  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
//    if(par1ItemStack != null && par1ItemStack.getItemDamage() > 0) {
//      par3List.add(EnderIO.lang.localize("machine.tooltip.upgrade"));
//    }
//
//  }

}
