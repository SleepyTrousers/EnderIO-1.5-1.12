package crazypants.enderio.material;

import java.util.List;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.capacitor.DefaultCapacitorData;
import crazypants.enderio.capacitor.ICapacitorData;
import crazypants.enderio.capacitor.ICapacitorDataItem;
import crazypants.util.ClientUtil;

public class ItemCapacitor extends Item implements ICapacitorDataItem {

  public static ItemCapacitor create() {
    ItemCapacitor result = new ItemCapacitor();
    result.init();
    return result;
  }

  protected ItemCapacitor() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemBasicCapacitor.getUnlocalisedName());
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemBasicCapacitor.getUnlocalisedName());
  }

  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    final ResourceLocation[] resourceLocations = DefaultCapacitorData.getResourceLocations();
    ModelBakery.registerItemVariants(this, resourceLocations);
    for (int i = 0; i < resourceLocations.length; i++) {
      ClientUtil.regRenderer(this, i, resourceLocations[i]);
    }
  }
  
  @Override
  public String getUnlocalizedName(ItemStack stack) {
    return getCapacitorData(stack).getUnlocalizedName();
  }

  @Override  
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (int j = 0; j < DefaultCapacitorData.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  public int getMetadata(ItemStack stack) {
    return MathHelper.clamp_int(stack != null ? stack.getItemDamage() : 0, 0, DefaultCapacitorData.values().length - 1);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
    if (getMetadata(stack) > 0) {
      par3List.add(EnderIO.lang.localize("machine.tooltip.upgrade"));
      if(SpecialTooltipHandler.showAdvancedTooltips()) {
        SpecialTooltipHandler.addDetailedTooltipFromResources(par3List, "enderio.machine.tooltip.upgrade");
      } else {
        SpecialTooltipHandler.addShowDetailsTooltip(par3List);
      }
    }

  }

  @Override
  public ICapacitorData getCapacitorData(ItemStack stack) {
    return DefaultCapacitorData.values()[getMetadata(stack)];
  }

}
