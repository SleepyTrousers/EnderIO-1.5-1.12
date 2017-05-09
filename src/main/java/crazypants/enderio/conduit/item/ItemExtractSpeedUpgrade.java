package crazypants.enderio.conduit.item;

import java.util.List;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

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

public class ItemExtractSpeedUpgrade extends Item implements IResourceTooltipProvider, IHaveRenderers  {

  private static final SpeedUpgrade UPGRADES[] = SpeedUpgrade.values();

  public static ItemExtractSpeedUpgrade create() {
    ItemExtractSpeedUpgrade result = new ItemExtractSpeedUpgrade();
    result.init();
    return result;
  }

  protected ItemExtractSpeedUpgrade() {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setUnlocalizedName(ModObject.itemExtractSpeedUpgrade.getUnlocalisedName());
    setRegistryName(ModObject.itemExtractSpeedUpgrade.getUnlocalisedName());
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
    for (SpeedUpgrade c : SpeedUpgrade.values()) {
      ClientUtil.regRenderer(this, c.ordinal(), c.baseName);
    }     
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    return getSpeedUpgrade(par1ItemStack).unlocName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < UPGRADES.length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  public static SpeedUpgrade getSpeedUpgrade(ItemStack par1ItemStack) {
    int i = MathHelper.clamp(par1ItemStack.getItemDamage(), 0, UPGRADES.length - 1);
    return UPGRADES[i];
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }

}
