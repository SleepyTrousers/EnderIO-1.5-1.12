package crazypants.enderio.item.darksteel;

import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.item.darksteel.IDarkSteelItem.IEndSteelItem;
import crazypants.enderio.item.darksteel.IDarkSteelItem.IStellarItem;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.IDarkSteelUpgrade;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class ItemStellarArmor extends ItemDarkSteelArmor implements IStellarItem {

	  public static final ArmorMaterial MATERIAL = EnumHelper.addArmorMaterial("stellarAlloy", 75, new int[] {8, 14, 18, 7}, 25);

	  public ItemStellarArmor(int armorType) {
		super(MATERIAL, "stellar", armorType);

	  }

	  public static ItemStellarArmor create(int armorType) {
		  ItemStellarArmor res = new ItemStellarArmor(armorType);
		  res.init();
		  return res;
	  }

	  public static ItemStellarArmor forArmorType(int armorType) {
		  switch (armorType) {
		    case 0: return DarkSteelItems.itemStellarHelmet;
		    case 1: return DarkSteelItems.itemStellarChestplate;
		    case 2: return DarkSteelItems.itemStellarLeggings;
		    case 3: return DarkSteelItems.itemStellarBoots;
		    }
		    return null;
	}

	public static int getPoweredProtectionIncrease(int armorType) {
		switch (armorType) {
		 case 0: return 2;
		 case 1: return 1;
		 case 2: return 4;
		 case 3: return 3;
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
