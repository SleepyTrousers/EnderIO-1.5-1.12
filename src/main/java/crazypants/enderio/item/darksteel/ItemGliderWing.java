package crazypants.enderio.item.darksteel;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.material.ItemCapacitor;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.util.Lang;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public class ItemGliderWing extends Item {

  private static final BasicCapacitor CAP = new BasicCapacitor();

  public static ItemGliderWing create() {
    ItemGliderWing result = new ItemGliderWing();
    result.init();
    return result;
  }

  private IIcon wingsIcon;

  protected ItemGliderWing() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemGliderWing.unlocalisedName);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemGliderWing.unlocalisedName);
  }

  @Override
  public IIcon getIconFromDamage(int damage) {
    damage = MathHelper.clamp_int(damage, 0, 1);
    if(damage == 0) {
      return itemIcon;
    }
    return wingsIcon;
  }

  @Override
  public void registerIcons(IIconRegister register) {
    itemIcon = register.registerIcon("enderio:itemGliderWing");
    wingsIcon = register.registerIcon("enderio:itemGliderWings");
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, Capacitors.values().length - 1);
    if(i == 0) {
      return super.getUnlocalizedName();
    }
    return super.getUnlocalizedName() + "s";
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < 2; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

 
//  @Override
//  @SideOnly(Side.CLIENT)
//  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
//    if(par1ItemStack != null && par1ItemStack.getItemDamage() > 0) {
//      par3List.add(Lang.localize("machine.tooltip.upgrade"));
//    }
//
//  }
  
}
