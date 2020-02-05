package crazypants.enderio.item.endsteel;

import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.item.darksteel.ItemDarkSteelArmor;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.IDarkSteelUpgrade;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class ItemEndSteelArmor extends ItemDarkSteelArmor implements IEndSteelItem {

	  public static final ArmorMaterial MATERIAL = EnumHelper.addArmorMaterial("endSteel", 50, new int[] { 4, 7, 10, 5 }, 25);

	  public ItemEndSteelArmor(int armorType) {
		super(MATERIAL, "endSteel", armorType);

	  }

	  public static ItemEndSteelArmor create(int armorType) {
		  ItemEndSteelArmor res = new ItemEndSteelArmor(armorType);
		  res.init();
		  return res;
	  }

	  public static ItemEndSteelArmor forArmorType(int armorType) {
		  switch (armorType) {
		    case 0: return EndSteelItems.itemEndSteelHelmet;
		    case 1: return EndSteelItems.itemEndSteelChestplate;
		    case 2: return EndSteelItems.itemEndSteelLeggings;
		    case 3: return EndSteelItems.itemEndSteelBoots;
		    }
		    return null;
	}

	public static int getPoweredProtectionIncrease(int armorType) {
		switch (armorType) {
		 case 0: return 1;
		 case 1: return 2;
		 case 2:
		 case 3: return 1;
		}
		return 0;
	}

	  @SuppressWarnings({ "unchecked", "rawtypes" })
	  @Override
	  @SideOnly(Side.CLIENT)
	  public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
	    ItemStack is = new ItemStack(this);
	    par3List.add(is);

	    is = new ItemStack(this);
	    EnergyUpgrade.EMPOWERED_FIVE.writeToItem(is);
	    EnergyUpgrade.setPowerFull(is);

	    Iterator<IDarkSteelUpgrade> iter = DarkSteelRecipeManager.instance.recipeIterator();
	    while (iter.hasNext()) {
	      IDarkSteelUpgrade upgrade = iter.next();
	      if (!(upgrade instanceof EnergyUpgrade) && upgrade.canAddToItem(is)) {
	        upgrade.writeToItem(is);
	      }
	    }

	    par3List.add(is);
	  }

}
