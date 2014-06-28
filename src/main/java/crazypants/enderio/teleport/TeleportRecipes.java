package crazypants.enderio.teleport;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.Material;
import crazypants.enderio.power.Capacitors;

public class TeleportRecipes {

  public static void addRecipes() {

    ItemStack conduitBinder = new ItemStack(EnderIO.itemMaterial, 4, Material.CONDUIT_BINDER.ordinal());
    ItemStack enderCapacitor = new ItemStack(EnderIO.itemBasicCapacitor, 1, Capacitors.ENDER_CAPACITOR.ordinal());

    //travel blocks
    if(Config.travelAnchorEnabled) {
      ItemStack travelBlock = new ItemStack(EnderIO.blockTravelPlatform);
      ItemStack pulsCry = new ItemStack(EnderIO.itemMaterial, 1, Material.PULSATING_CYSTAL.ordinal());
      GameRegistry
          .addShapedRecipe(travelBlock, "ibi", "bcb", "ibi", 'i', Items.iron_ingot, 'b', conduitBinder, 'c', pulsCry);
    }

    if(Config.travelStaffEnabled) {
      //travel staff
      ItemStack travelStaff = new ItemStack(EnderIO.itemTravelStaff);
      EnderIO.itemTravelStaff.setEnergy(travelStaff, 0);
      ItemStack vibCry = new ItemStack(EnderIO.itemMaterial, 1, Material.VIBRANT_CYSTAL.ordinal());
      ItemStack electricalSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal());
      IRecipe rec = GameRegistry
          .addShapedRecipe(travelStaff, "  g", " s ", "c  ", 's', electricalSteel, 'c', enderCapacitor, 'g', vibCry);
      rec = GameRegistry
          .addShapedRecipe(travelStaff, "g  ", " s ", "  c", 's', electricalSteel, 'c', enderCapacitor, 'g', vibCry);      
    }
  }

}
