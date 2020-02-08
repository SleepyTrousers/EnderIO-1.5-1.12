package crazypants.enderio.item.endsteel;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ChestGenHooks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.ItemMagnet;
import crazypants.enderio.item.darksteel.ItemDarkSteelSword;
import crazypants.enderio.item.darksteel.PoweredItemRenderer;

public class EndSteelItems {

  public static ItemEndSteelArmor itemEndSteelHelmet;
  public static ItemEndSteelArmor itemEndSteelChestplate;
  public static ItemEndSteelArmor itemEndSteelLeggings;
  public static ItemEndSteelArmor itemEndSteelBoots;
  public static ItemEndSteelSword itemEndSteelSword;
  public static ItemEndSteelPickaxe itemEndSteelPickaxe;
  public static ItemEndSteelAxe itemEndSteelAxe;

  public static void createEndSteelArmorItems() {

	  itemEndSteelHelmet = ItemEndSteelArmor.create(0);
	  itemEndSteelChestplate = ItemEndSteelArmor.create(1);
	  itemEndSteelLeggings = ItemEndSteelArmor.create(2);
	  itemEndSteelBoots = ItemEndSteelArmor.create(3);

	  itemEndSteelSword = ItemEndSteelSword.create();
	  itemEndSteelPickaxe = ItemEndSteelPickaxe.create();
	  itemEndSteelAxe = ItemEndSteelAxe.create();

  }

  @SideOnly(Side.CLIENT)
  public static void registerItemRenderer() {
    PoweredItemRenderer dsr = new PoweredItemRenderer();
    MinecraftForgeClient.registerItemRenderer(itemEndSteelBoots, dsr);
    MinecraftForgeClient.registerItemRenderer(itemEndSteelLeggings, dsr);
    MinecraftForgeClient.registerItemRenderer(itemEndSteelChestplate, dsr);
    MinecraftForgeClient.registerItemRenderer(itemEndSteelHelmet, dsr);
    MinecraftForgeClient.registerItemRenderer(itemEndSteelSword, dsr);
    MinecraftForgeClient.registerItemRenderer(itemEndSteelPickaxe, dsr);
    MinecraftForgeClient.registerItemRenderer(itemEndSteelAxe, dsr);
  }
}
