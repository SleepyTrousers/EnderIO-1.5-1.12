package crazypants.enderio.conduit;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import appeng.api.AEApi;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.conduit.facade.ItemConduitFacade.FacadeType;
import crazypants.enderio.conduit.item.filter.ClearFilterRecipe;
import crazypants.enderio.conduit.item.filter.CopyFilterRecipe;
import crazypants.enderio.conduit.me.MEUtil;
import crazypants.enderio.config.Config;
import crazypants.enderio.init.EIOBlocks;
import crazypants.enderio.init.EIOItems;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.FrankenSkull;
import crazypants.enderio.material.Material;
import static crazypants.enderio.ModObject.*;

public class ConduitRecipes {

  public static void addRecipes() {

    //Crafting Components
    ItemStack redstoneConduit = new ItemStack(EIOItems.itemRedstoneConduit, 1, 0);
    ItemStack conduitBinder = new ItemStack(EIOItems.itemMaterial, 1, Material.CONDUIT_BINDER.ordinal());

    ItemStack fusedQuartz = new ItemStack(EIOBlocks.blockFusedQuartz, 1, 0);
    ItemStack fusedGlass = new ItemStack(EIOBlocks.blockFusedQuartz, 1, BlockFusedQuartz.Type.GLASS.ordinal());

    ItemStack conductiveIron = new ItemStack(EIOItems.itemAlloy, 1, Alloy.CONDUCTIVE_IRON.ordinal());
    ItemStack energeticGold = new ItemStack(EIOItems.itemAlloy, 1, Alloy.ENERGETIC_ALLOY.ordinal());
    ItemStack phasedGold = new ItemStack(EIOItems.itemAlloy, 1, Alloy.PHASED_GOLD.ordinal());
    ItemStack phasedIron = new ItemStack(EIOItems.itemAlloy, 1, Alloy.PHASED_IRON.ordinal());
    ItemStack phasedIronNugget = new ItemStack(EIOItems.itemMaterial, 1, Material.PHASED_IRON_NUGGET.ordinal());
    ItemStack redstoneAlloy = new ItemStack(EIOItems.itemAlloy, 1, Alloy.REDSTONE_ALLOY.ordinal());
    ItemStack electricalSteel = new ItemStack(EIOItems.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    ItemStack zombieController = new ItemStack(EIOItems.itemFrankenSkull, 1, FrankenSkull.ZOMBIE_CONTROLLER.ordinal());

    //Recipes
    GameRegistry.addShapedRecipe(new ItemStack(EIOItems.itemConduitFacade, 1, FacadeType.BASIC.ordinal()), "bbb", "b b", "bbb",
        'b', conduitBinder);
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EIOItems.itemConduitFacade, 1, FacadeType.HARDENED.ordinal()), " o ",
        "oFo", " o ", 'F', EIOItems.itemConduitFacade, 'o', "dustObsidian"));

    int numConduits = Config.numConduitsPerRecipe;
    GameRegistry.addShapedRecipe(new ItemStack(EIOItems.itemLiquidConduit, numConduits, 0), "bbb", "###", "bbb", 'b',
        conduitBinder, '#', fusedGlass);
    GameRegistry.addShapedRecipe(new ItemStack(EIOItems.itemLiquidConduit, numConduits, 1), "bbb", "###", "bbb", 'b',
        conduitBinder, '#', fusedQuartz);
    GameRegistry.addShapedRecipe(new ItemStack(EIOItems.itemLiquidConduit, numConduits, 2), "bbb", "#p#", "bbb", 'b',
        conduitBinder, '#', fusedQuartz, 'p', phasedGold);
    GameRegistry.addShapedRecipe(new ItemStack(EIOItems.itemPowerConduit, numConduits, 0), "bbb", "###", "bbb", 'b', conduitBinder,
        '#', conductiveIron);
    GameRegistry.addShapedRecipe(new ItemStack(EIOItems.itemPowerConduit, numConduits, 1), "bbb", "###", "bbb", 'b', conduitBinder,
        '#', energeticGold);
    GameRegistry.addShapedRecipe(new ItemStack(EIOItems.itemPowerConduit, numConduits, 2), "bbb", "###", "bbb", 'b', conduitBinder,
        '#', phasedGold);
    GameRegistry.addShapedRecipe(new ItemStack(EIOItems.itemRedstoneConduit, numConduits, 0), "###", '#', redstoneAlloy);
    GameRegistry.addShapedRecipe(new ItemStack(EIOItems.itemRedstoneConduit, 1, 1), "lbl", "bcb", "lbl", 'b', conduitBinder, 'c',
        redstoneConduit, 'l',
        Blocks.lever);
    GameRegistry.addShapedRecipe(new ItemStack(EIOItems.itemRedstoneConduit, numConduits, 2), "bbb", "###", "bbb", 'b',
        conduitBinder, '#',
        redstoneAlloy);
    GameRegistry.addShapedRecipe(new ItemStack(EIOItems.itemGasConduit, numConduits, 0), "bbb", "#g#", "bbb", 'b', conduitBinder,
        '#',
        electricalSteel, 'g', fusedGlass);

    ItemStack itemConduit = new ItemStack(EIOItems.itemItemConduit, numConduits, 0);
    GameRegistry.addShapedRecipe(itemConduit, "bbb", "###", "bbb", 'b', conduitBinder, '#', phasedIronNugget);

    MachineRecipeRegistry.instance.registerRecipe(blockPainter.unlocalisedName,
        EIOItems.itemConduitFacade.new FacadePainterRecipe());

    //Filter Recipes
    ItemStack basicFilter = new ItemStack(EIOItems.itemBasicFilterUpgrade, 1, 0);
    GameRegistry.addShapedRecipe(basicFilter, " p ", "php", " p ", 'p', Items.paper, 'h', Blocks.hopper);

    ItemStack advFilter = new ItemStack(EIOItems.itemBasicFilterUpgrade, 1, 1);
    GameRegistry.addRecipe(new ShapedOreRecipe(advFilter, "rpr", "pzp", "rpr", 'p', Items.paper, 'z', zombieController, 'r', Items.redstone));

    ItemStack modFilter = new ItemStack(EIOItems.itemModItemFilter, 1, 0);
    GameRegistry.addShapedRecipe(modFilter, " p ", "pwp", " p ", 'p', Items.paper, 'w', EIOItems.itemYetaWench);

    ItemStack exFilt = new ItemStack(EIOItems.itemExistingItemFilter);
    GameRegistry.addShapedRecipe(exFilt, " r ", "rfr", " c ", 'c', new ItemStack(Items.comparator, 1, 0), 'r', Items.redstone, 'f', advFilter);

    ItemStack powerFilt = new ItemStack(EIOItems.itemPowerItemFilter);
    GameRegistry.addShapedRecipe(powerFilt, " p ", "pcp", " p ", 'p', Items.paper, 'c', EIOItems.itemConduitProbe);

    ClearFilterRecipe clearRec = new ClearFilterRecipe();
    MinecraftForge.EVENT_BUS.register(clearRec);
    GameRegistry.addRecipe(clearRec);

    CopyFilterRecipe copyRec = new CopyFilterRecipe();
    GameRegistry.addRecipe(copyRec);

    ItemStack speedUpgrade = new ItemStack(EIOItems.itemExtractSpeedUpgrade, 1, 0);
    GameRegistry.addShapedRecipe(speedUpgrade, "iii","epe","ere", 'p', Blocks.piston, 'e', electricalSteel, 'r', Blocks.redstone_torch, 'i', Items.iron_ingot);

    ItemStack speedDowngrade = new ItemStack(EIOItems.itemExtractSpeedUpgrade, 1, 1);
    GameRegistry.addRecipe(new ShapedOreRecipe(speedDowngrade, "iii","ese","ete", 's', "slimeball", 'e', electricalSteel, 't', "stickWood", 'i', Items.iron_ingot));
    GameRegistry.addRecipe(new ShapedOreRecipe(speedDowngrade, "iii","ese","ete", 's', "slimeball", 'e', electricalSteel, 't', "woodStick", 'i', Items.iron_ingot));

    if (MEUtil.isMEEnabled()) {
      addAeRecipes();
    }
  }

  @Method(modid = "appliedenergistics2")
  private static void addAeRecipes() {
    String fluix = "crystalFluix";
    String pureFluix = "crystalPureFluix";
    
    ItemStack quartzFiber = AEApi.instance().parts().partQuartzFiber.stack(1).copy();
    ItemStack conduitBinder = new ItemStack(EIOItems.itemMaterial, 1, Material.CONDUIT_BINDER.ordinal());
    ItemStack res = new ItemStack(EIOItems.itemMEConduit, Config.numConduitsPerRecipe / 2);

    GameRegistry.addRecipe(new ShapedOreRecipe(res.copy(), "bbb", "fqf", "bbb", 'b', conduitBinder, 'f', fluix, 'q', quartzFiber));
    GameRegistry.addRecipe(new ShapedOreRecipe(res.copy(), "bbb", "fqf", "bbb", 'b', conduitBinder, 'f', pureFluix, 'q', quartzFiber));

    res.stackSize = 1;
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EIOItems.itemMEConduit, 1, 1), "bCb", "CbC", "bCb", 'b',
        conduitBinder, 'C', res));
  }
}
