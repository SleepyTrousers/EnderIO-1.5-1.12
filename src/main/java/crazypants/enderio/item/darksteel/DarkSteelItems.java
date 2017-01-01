package crazypants.enderio.item.darksteel;

import crazypants.enderio.ModObject;
import crazypants.enderio.item.ItemMagnet;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgradePowerAdapter;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.recipe.HelmetPainterTemplate;
import crazypants.util.ClientUtil;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DarkSteelItems {

  public static ItemGliderWing itemGliderWing;

  public static ItemDarkSteelArmor itemDarkSteelHelmet;
  public static ItemDarkSteelArmor itemDarkSteelChestplate;
  public static ItemDarkSteelArmor itemDarkSteelLeggings;
  public static ItemDarkSteelArmor itemDarkSteelBoots;
  public static ItemDarkSteelSword itemDarkSteelSword;
  public static ItemDarkSteelPickaxe itemDarkSteelPickaxe;
  public static ItemDarkSteelAxe itemDarkSteelAxe;
  public static ItemDarkSteelBow itemDarkSteelBow;
  public static ItemDarkSteelShears itemDarkSteelShears;

  public static ItemMagnet itemMagnet;

  private DarkSteelItems() {
  }

  public static void createDarkSteelArmorItems() {
    itemGliderWing = ItemGliderWing.create();

    itemDarkSteelHelmet = ItemDarkSteelArmor.create(EntityEquipmentSlot.HEAD);
    itemDarkSteelChestplate = ItemDarkSteelArmor.create(EntityEquipmentSlot.CHEST);
    itemDarkSteelLeggings = ItemDarkSteelArmor.create(EntityEquipmentSlot.LEGS);
    itemDarkSteelBoots = ItemDarkSteelArmor.create(EntityEquipmentSlot.FEET);

    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.getUnlocalisedName(), new HelmetPainterTemplate());

    itemDarkSteelSword = ItemDarkSteelSword.create();
    itemDarkSteelPickaxe = ItemDarkSteelPickaxe.create();
    itemDarkSteelAxe = ItemDarkSteelAxe.create();
    itemDarkSteelBow = ItemDarkSteelBow.create();
    itemDarkSteelShears = ItemDarkSteelShears.create();

    itemMagnet = ItemMagnet.create();
    
    MinecraftForge.EVENT_BUS.register(new EnergyUpgradePowerAdapter());
  }

  @SideOnly(Side.CLIENT)
  public static void onClientPreInit() {
    itemGliderWing.registerRenderers();
    
    ClientUtil.registerRenderer(DarkSteelItems.itemDarkSteelBoots, DarkSteelItems.itemDarkSteelBoots.getItemName());
    ClientUtil.registerRenderer(DarkSteelItems.itemDarkSteelLeggings, DarkSteelItems.itemDarkSteelLeggings.getItemName());
    ClientUtil.registerRenderer(DarkSteelItems.itemDarkSteelChestplate, DarkSteelItems.itemDarkSteelChestplate.getItemName());
    ClientUtil.registerRenderer(DarkSteelItems.itemDarkSteelHelmet, DarkSteelItems.itemDarkSteelHelmet.getItemName());
    
    ClientUtil.registerRenderer(DarkSteelItems.itemDarkSteelAxe, DarkSteelItems.itemDarkSteelAxe.getItemName());
    ClientUtil.registerRenderer(DarkSteelItems.itemDarkSteelSword, DarkSteelItems.itemDarkSteelSword.getItemName());
    ClientUtil.registerRenderer(DarkSteelItems.itemDarkSteelShears, DarkSteelItems.itemDarkSteelShears.getItemName());
    ClientUtil.registerRenderer(DarkSteelItems.itemDarkSteelPickaxe, DarkSteelItems.itemDarkSteelPickaxe.getItemName());
    ClientUtil.registerRenderer(DarkSteelItems.itemDarkSteelBow, ItemDarkSteelBow.NAME);

    ClientUtil.registerRenderer(itemMagnet, ModObject.itemMagnet.getUnlocalisedName());
  }

}
