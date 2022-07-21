package crazypants.enderio.teleport;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.Material;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.teleport.telepad.ItemCoordSelector;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class TeleportRecipes {

    public static void addRecipes() {

        ItemStack conduitBinder = new ItemStack(EnderIO.itemMaterial, 4, Material.CONDUIT_BINDER.ordinal());
        ItemStack enderCapacitor = new ItemStack(EnderIO.itemBasicCapacitor, 1, Capacitors.ENDER_CAPACITOR.ordinal());

        // travel blocks
        if (Config.travelAnchorEnabled) {
            ItemStack travelBlock = new ItemStack(EnderIO.blockTravelPlatform);
            ItemStack pulsCry = new ItemStack(EnderIO.itemMaterial, 1, Material.PULSATING_CYSTAL.ordinal());
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    travelBlock, "ibi", "bcb", "ibi", 'i', "ingotIron", 'b', conduitBinder, 'c', pulsCry));
        }

        if (Config.travelStaffEnabled) {
            // travel staff
            ItemStack travelStaff = new ItemStack(EnderIO.itemTravelStaff);
            EnderIO.itemTravelStaff.setEnergy(travelStaff, 0);
            ItemStack endCry = new ItemStack(EnderIO.itemMaterial, 1, Material.ENDER_CRYSTAL.ordinal());
            ItemStack darkSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.DARK_STEEL.ordinal());
            GameRegistry.addShapedRecipe(
                    travelStaff, "  e", " s ", "s  ", 's', darkSteel, 'c', enderCapacitor, 'e', endCry);
        }

        if (Config.travelAnchorEnabled && Config.travelStaffEnabled) {
            ItemStack travelBlock = new ItemStack(EnderIO.blockTravelPlatform);
            ItemStack telepad = new ItemStack(EnderIO.blockTelePad);
            ItemStack octadic = new ItemStack(EnderIO.itemBasicCapacitor, 1, Capacitors.ENDER_CAPACITOR.ordinal());
            ItemStack staff = new ItemStack(EnderIO.itemTravelStaff, 1, OreDictionary.WILDCARD_VALUE);
            ItemStack fq = new ItemStack(EnderIO.blockFusedQuartz, 1, BlockFusedQuartz.Type.FUSED_QUARTZ.ordinal());
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    telepad,
                    "gSg",
                    "dAd",
                    "dod",
                    'g',
                    fq,
                    'S',
                    staff,
                    'd',
                    "ingotDarkSteel",
                    'A',
                    travelBlock,
                    'o',
                    octadic));
        }

        ItemStack coordSelector = new ItemStack(EnderIO.itemCoordSelector);
        ItemCoordSelector.init(coordSelector);
        GameRegistry.addRecipe(new ShapedOreRecipe(
                coordSelector,
                "sps",
                " cs",
                "  s",
                's',
                "ingotElectricalSteel",
                'p',
                Items.ender_pearl,
                'c',
                Items.compass));
    }
}
