package crazypants.enderio.item.darksteel;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.TravelUpgrade;
import crazypants.enderio.item.darksteel.IDarkSteelItem.IEndSteelItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;

public class ItemEndSteelSword extends ItemDarkSteelSword implements IEndSteelItem {

	  static final ToolMaterial MATERIAL = EnumHelper.addToolMaterial("endSteel", Config.darkSteelPickMinesTiCArdite ? 5 : 3, 2000, 12, 5, 30);

	  public static boolean isEquipped(EntityPlayer player) {
		    if(player == null) {
		      return false;
		    }
		    ItemStack equipped = player.getCurrentEquippedItem();
		    if(equipped == null) {
		      return false;
		    }
		    return equipped.getItem() == DarkSteelItems.itemEndSteelSword;
		  }


	  public static ItemEndSteelSword create() {
		  ItemEndSteelSword res = new ItemEndSteelSword();
		    res.init();
		    MinecraftForge.EVENT_BUS.register(res);
		    return res;
	  }

	  public ItemEndSteelSword() {
		  super("endSteel",MATERIAL);
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
