package crazypants.enderio.conduit;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.facade.EnumFacadeType;
import crazypants.enderio.conduit.item.filter.ClearFilterRecipe;
import crazypants.enderio.conduit.item.filter.CopyFilterRecipe;
import crazypants.enderio.config.Config;
import crazypants.enderio.material.FrankenSkull;
import crazypants.enderio.material.fusedQuartz.FusedQuartzType;

import static crazypants.enderio.material.Alloy.CONDUCTIVE_IRON;
import static crazypants.enderio.material.Alloy.ELECTRICAL_STEEL;
import static crazypants.enderio.material.Alloy.ENERGETIC_ALLOY;
import static crazypants.enderio.material.Alloy.REDSTONE_ALLOY;
import static crazypants.enderio.material.Alloy.VIBRANT_ALLOY;
import static crazypants.enderio.material.Material.CONDUIT_BINDER;
import static crazypants.enderio.material.Material.PULSATING_IRON_NUGGET;
import static crazypants.util.RecipeUtil.addShaped;

public class ConduitRecipes {

  public static void addRecipes() {

    //Crafting Components
    ItemStack redstoneConduit = new ItemStack(EnderIO.itemRedstoneConduit, 1, 0);

    ItemStack fusedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 0);
    ItemStack fusedGlass = new ItemStack(EnderIO.blockFusedQuartz, 1, FusedQuartzType.FUSED_GLASS.ordinal());

    String electricalSteel = ELECTRICAL_STEEL.getOreIngot();
    String phasedGold = VIBRANT_ALLOY.getOreIngot();
    String conductiveIron = CONDUCTIVE_IRON.getOreIngot();
    String energeticGold = ENERGETIC_ALLOY.getOreIngot();
    String phasedIronNugget = PULSATING_IRON_NUGGET.oreDict;
    String redstoneAlloy = REDSTONE_ALLOY.getOreIngot();

    String binder = CONDUIT_BINDER.oreDict;

    ItemStack zombieController = new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ZOMBIE_CONTROLLER.ordinal());

    //Recipes
    addShaped(new ItemStack(EnderIO.blockConduitFacade, 1, EnumFacadeType.BASIC.ordinal()), "bbb", "b b", "bbb", 'b', binder);
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EnderIO.blockConduitFacade, 1, EnumFacadeType.HARDENED.ordinal()), " o ", "oFo", " o ", 'F',
        new ItemStack(EnderIO.blockConduitFacade, 1, EnumFacadeType.BASIC.ordinal()), 'o', "dustObsidian"));

    int numConduits = Config.numConduitsPerRecipe;
    if (numConduits <= 0 || numConduits > 64) {
      numConduits = Config.recipeLevel > 2 ? 2 : Config.recipeLevel > 1 ? 8 : 12;
    }
    addShaped(new ItemStack(EnderIO.itemLiquidConduit, numConduits, 0), "bbb", "###", "bbb", 'b', binder, '#', fusedGlass);
    addShaped(new ItemStack(EnderIO.itemLiquidConduit, numConduits, 1), "bbb", "###", "bbb", 'b', binder, '#', fusedQuartz);
    addShaped(new ItemStack(EnderIO.itemLiquidConduit, numConduits, 2), "bbb", "#p#", "bbb", 'b', binder, '#', fusedQuartz, 'p', phasedGold);
    addShaped(new ItemStack(EnderIO.itemPowerConduit, numConduits, 0), "bbb", "###", "bbb", 'b', binder, '#', conductiveIron);
    addShaped(new ItemStack(EnderIO.itemPowerConduit, numConduits, 1), "bbb", "###", "bbb", 'b', binder, '#', energeticGold);
    addShaped(new ItemStack(EnderIO.itemPowerConduit, numConduits, 2), "bbb", "###", "bbb", 'b', binder, '#', phasedGold);
    addShaped(new ItemStack(EnderIO.itemRedstoneConduit, numConduits, 0), "###", '#', redstoneAlloy);
    addShaped(new ItemStack(EnderIO.itemRedstoneConduit, 1, 1), "lbl", "bcb", "lbl", 'b', binder, 'c', redstoneConduit, 'l', Blocks.LEVER);
    addShaped(new ItemStack(EnderIO.itemRedstoneConduit, numConduits, 2), "bbb", "###", "bbb", 'b', binder, '#', redstoneAlloy);

//    if (GasUtil.isGasConduitEnabled()) {
//      addShaped(new ItemStack(EnderIO.itemGasConduit, numConduits, 0), "bbb", "#g#", "bbb", 'b', binder, '#', electricalSteel, 'g', fusedGlass);
//    }

    ItemStack itemConduit = new ItemStack(EnderIO.itemItemConduit, numConduits, 0);
    addShaped(itemConduit, "bbb", "###", "bbb", 'b', binder, '#', phasedIronNugget);

    //Filter Recipes
    ItemStack basicFilter = new ItemStack(EnderIO.itemBasicFilterUpgrade, 1, 0);
    addShaped(basicFilter, " p ", "php", " p ", 'p', Items.PAPER, 'h', Blocks.HOPPER);

    ItemStack advFilter = new ItemStack(EnderIO.itemBasicFilterUpgrade, 1, 1);
    addShaped(advFilter, "rpr", "pzp", "rpr", 'p', Items.PAPER, 'z', zombieController, 'r', "dustRedstone");

    ItemStack modFilter = new ItemStack(EnderIO.itemModItemFilter, 1, 0);
    addShaped(modFilter, " p ", "pwp", " p ", 'p', Items.PAPER, 'w', EnderIO.itemYetaWench);

    ItemStack exFilt = new ItemStack(EnderIO.itemExistingItemFilter);
    addShaped(exFilt, " r ", "rfr", " c ", 'c', new ItemStack(Items.COMPARATOR, 1, 0), 'r', "dustRedstone", 'f', advFilter);

    ItemStack powerFilt = new ItemStack(EnderIO.itemPowerItemFilter);
    addShaped(powerFilt, " p ", "pcp", " p ", 'p', Items.PAPER, 'c', EnderIO.itemConduitProbe);

    ClearFilterRecipe clearRec = new ClearFilterRecipe();
    MinecraftForge.EVENT_BUS.register(clearRec);
    GameRegistry.addRecipe(clearRec);

    CopyFilterRecipe copyRec = new CopyFilterRecipe();
    GameRegistry.addRecipe(copyRec);

    ItemStack speedUpgrade = new ItemStack(EnderIO.itemExtractSpeedUpgrade, 1, 0);
    addShaped(speedUpgrade, "iii", "epe", "ere", 'p', Blocks.PISTON, 'e', electricalSteel, 'r', Blocks.REDSTONE_TORCH, 'i', "ingotIron");

    ItemStack speedDowngrade = new ItemStack(EnderIO.itemExtractSpeedUpgrade, 1, 1);
    addShaped(speedDowngrade, "iii", "ese", "ete", 's', "slimeball", 'e', electricalSteel, 't', "stickWood", 'i', "ingotIron");
    addShaped(speedDowngrade, "iii", "ese", "ete", 's', "slimeball", 'e', electricalSteel, 't', "woodStick", 'i', "ingotIron");

//    if (MEUtil.isMEEnabled()) {
//      addAeRecipes();
//    }
//    if (OCUtil.isOCEnabled()) {
//      addOCRecipes();
//    }
  }

//  private static void addOCRecipes() {
//    int numConduits = Config.numConduitsPerRecipe;
//    String redstoneAlloy = REDSTONE_ALLOY.getOreIngot();
//    String binder = CONDUIT_BINDER.oreDict;
//
//    addShaped(new ItemStack(EnderIO.itemOCConduit, numConduits, 0), "bbb", "rir", "bbb", 'b', binder, 'r', redstoneAlloy, 'i',
//        "ingotIron");
//  }

//  @Method(modid = "appliedenergistics2")
//  private static void addAeRecipes() {
//    String fluix = "crystalFluix";
//    String pureFluix = "crystalPureFluix";
//
//    ItemStack quartzFiber = AEApi.instance().parts().partQuartzFiber.stack(1).copy();
//    ItemStack conduitBinder = new ItemStack(EnderIO.itemMaterial, 1, Material.CONDUIT_BINDER.ordinal());
//    ItemStack res = new ItemStack(EnderIO.itemMEConduit, Config.numConduitsPerRecipe / 2);
//
//    addShaped(res.copy(), "bbb", "fqf", "bbb", 'b', conduitBinder, 'f', fluix, 'q', quartzFiber);
//    addShaped(res.copy(), "bbb", "fqf", "bbb", 'b', conduitBinder, 'f', pureFluix, 'q', quartzFiber);
//
//    res.stackSize = 1;
//    addShaped(new ItemStack(EnderIO.itemMEConduit, 1, 1), "bCb", "CbC", "bCb", 'b', conduitBinder, 'C', res);
//  }
}
