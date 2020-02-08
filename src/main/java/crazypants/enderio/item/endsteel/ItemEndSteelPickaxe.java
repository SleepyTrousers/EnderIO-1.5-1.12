package crazypants.enderio.item.endsteel;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.item.darksteel.ItemDarkSteelPickaxe;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.TravelUpgrade;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ItemEndSteelPickaxe extends ItemDarkSteelPickaxe implements IEndSteelItem {

	  public static boolean isEquipped(EntityPlayer player) {
		    if(player == null) {
		      return false;
		    }
		    ItemStack equipped = player.getCurrentEquippedItem();
		    if(equipped == null) {
		      return false;
		    }
		    return equipped.getItem() == EndSteelItems.itemEndSteelPickaxe;
	  }

	  public static ItemEndSteelPickaxe create() {
		  ItemEndSteelPickaxe res = new ItemEndSteelPickaxe();
		    res.init();
		    MinecraftForge.EVENT_BUS.register(res);
		    return res;
	  }

	  public ItemEndSteelPickaxe() {
		  super("endSteel", ItemEndSteelSword.MATERIAL);
	  }

	  @Override
	  @SideOnly(Side.CLIENT)
	  public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
	    ItemStack is = new ItemStack(this);
	    par3List.add(is);

	    is = new ItemStack(this);
	    EnergyUpgrade.EMPOWERED_FIVE.writeToItem(is);
	    EnergyUpgrade.setPowerFull(is);
	    TravelUpgrade.INSTANCE.writeToItem(is);
	    par3List.add(is);
	  }

}
