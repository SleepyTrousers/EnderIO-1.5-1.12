package crazypants.enderio.conduit.item;

import java.util.List;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import crazypants.util.Prep;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.itemFunctionUpgrade;

public class ItemFunctionUpgrade extends Item implements IResourceTooltipProvider, IHaveRenderers {

  private static final FunctionUpgrade UPGRADES[] = FunctionUpgrade.values();

  public static ItemFunctionUpgrade create() {
    ItemFunctionUpgrade result = new ItemFunctionUpgrade();
    result.init();
    return result;
  }

  protected ItemFunctionUpgrade() {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setUnlocalizedName(ModObject.itemFunctionUpgrade.getUnlocalisedName());
    setRegistryName(ModObject.itemFunctionUpgrade.getUnlocalisedName());
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);

  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {      
    for (FunctionUpgrade c : FunctionUpgrade.values()) {
      ClientUtil.regRenderer(this, c.ordinal(), c.baseName);
    }     
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    return getFunctionUpgrade(par1ItemStack).unlocName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < UPGRADES.length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  public static FunctionUpgrade getFunctionUpgrade(ItemStack par1ItemStack) {
    if (Prep.isValid(par1ItemStack) && par1ItemStack.getItem() == itemFunctionUpgrade.getItem()) {
      int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, UPGRADES.length - 1);
      return UPGRADES[i];
    } else {
      return null;
    }
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }

}
