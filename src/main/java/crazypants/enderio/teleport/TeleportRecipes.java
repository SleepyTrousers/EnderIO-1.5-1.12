package crazypants.enderio.teleport;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.config.Config;
import crazypants.enderio.init.EIOBlocks;
import crazypants.enderio.init.EIOItems;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.Material;
import crazypants.enderio.power.Capacitors;

public class TeleportRecipes {

  public static void addRecipes() {

    ItemStack conduitBinder = new ItemStack(EIOItems.itemMaterial, 4, Material.CONDUIT_BINDER.ordinal());
    ItemStack enderCapacitor = new ItemStack(EIOItems.itemBasicCapacitor, 1, Capacitors.ENDER_CAPACITOR.ordinal());

    //travel blocks
    if(Config.travelAnchorEnabled) {
      ItemStack travelBlock = new ItemStack(EIOBlocks.blockTravelPlatform);
      ItemStack pulsCry = new ItemStack(EIOItems.itemMaterial, 1, Material.PULSATING_CYSTAL.ordinal());
      GameRegistry.addShapedRecipe(travelBlock, "ibi", "bcb", "ibi", 'i', Items.iron_ingot, 'b', conduitBinder, 'c', pulsCry);
    }

    if(Config.travelStaffEnabled) {
      //travel staff
      ItemStack travelStaff = new ItemStack(EIOItems.itemTravelStaff);
      EIOItems.itemTravelStaff.setEnergy(travelStaff, 0);
      ItemStack endCry = new ItemStack(EIOItems.itemMaterial, 1, Material.ENDER_CRYSTAL.ordinal());
      ItemStack darkSteel = new ItemStack(EIOItems.itemAlloy, 1, Alloy.DARK_STEEL.ordinal());
      GameRegistry.addShapedRecipe(travelStaff, "  e", " s ", "s  ", 's', darkSteel, 'c', enderCapacitor, 'e', endCry);
    }

    if(Config.travelAnchorEnabled && Config.travelStaffEnabled) {
      ItemStack travelBlock = new ItemStack(EIOBlocks.blockTravelPlatform);
      ItemStack telepad = new ItemStack(EIOBlocks.blockTelePad);
      ItemStack octadic = new ItemStack(EIOItems.itemBasicCapacitor, 1, Capacitors.ENDER_CAPACITOR.ordinal());
      ItemStack staff = new ItemStack(EIOItems.itemTravelStaff, 1, OreDictionary.WILDCARD_VALUE);
      ItemStack fq = new ItemStack(EIOBlocks.blockFusedQuartz, 1, BlockFusedQuartz.Type.FUSED_QUARTZ.ordinal());
      GameRegistry.addRecipe(new ShapedOreRecipe(telepad, "gSg", "dAd", "dod", 'g', fq, 'S', staff, 'd', "ingotDarkSteel", 'A', travelBlock, 'o', octadic));
    }
  }
}
