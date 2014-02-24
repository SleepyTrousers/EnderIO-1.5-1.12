package crazypants.enderio.teleport;

import static crazypants.enderio.ModObject.itemBasicCapacitor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.Material;
import crazypants.enderio.power.Capacitors;

public class TeleportRecipes {

  public static void addRecipes() {

    ItemStack conduitBinder = new ItemStack(ModObject.itemMaterial.actualId, 4, Material.CONDUIT_BINDER.ordinal());
    ItemStack enderCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, Capacitors.ENDER_CAPACITOR.ordinal());

    //travel blocks
    if(Config.travelAnchorEnabled) {
      ItemStack travelBlock = new ItemStack(EnderIO.blockTravelPlatform);
      ItemStack pulsCry = new ItemStack(ModObject.itemMaterial.actualId, 1, Material.PULSATING_CYSTAL.ordinal());
      GameRegistry
          .addShapedRecipe(travelBlock, "ibi", "bcb", "ibi", 'i', Item.ingotIron, 'b', conduitBinder, 'c', pulsCry);
    }

    if(Config.travelStaffEnabled) {
      //travel staff
      ItemStack travelStaff = new ItemStack(EnderIO.itemTravelStaff);
      ItemStack vibCry = new ItemStack(ModObject.itemMaterial.actualId, 1, Material.VIBRANT_CYSTAL.ordinal());
      ItemStack electricalSteel = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ELECTRICAL_STEEL.ordinal());
      GameRegistry
          .addShapedRecipe(travelStaff, "  g", " s ", "c  ", 's', electricalSteel, 'c', enderCapacitor, 'g', vibCry);
      GameRegistry
          .addShapedRecipe(travelStaff, "g  ", " s ", "  c", 's', electricalSteel, 'c', enderCapacitor, 'g', vibCry);
    }
  }

}
