package crazypants.enderio.conduit;

import static crazypants.enderio.ModObject.blockPainter;
import static crazypants.enderio.material.Alloy.*;
import static crazypants.enderio.material.Material.*;
import static crazypants.enderio.material.endergy.AlloyEndergy.*;
import static crazypants.util.RecipeUtil.addShaped;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import appeng.api.AEApi;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.facade.ItemConduitFacade.FacadeType;
import crazypants.enderio.conduit.gas.GasUtil;
import crazypants.enderio.conduit.item.filter.ClearFilterRecipe;
import crazypants.enderio.conduit.item.filter.CopyFilterRecipe;
import crazypants.enderio.conduit.liquid.CrystallineEnderLiquidConduit;
import crazypants.enderio.conduit.liquid.CrystallinePinkSlimeEnderLiquidConduit;
import crazypants.enderio.conduit.liquid.EnderLiquidConduit;
import crazypants.enderio.conduit.liquid.MelodicEnderLiquidConduit;
import crazypants.enderio.conduit.liquid.StellarEnderLiquidConduit;
import crazypants.enderio.conduit.me.MEUtil;
import crazypants.enderio.conduit.oc.OCUtil;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.FrankenSkull;
import crazypants.enderio.material.Material;

public class ConduitRecipes {

    public static void addRecipes() {

        // Crafting Components
        ItemStack redstoneConduit = new ItemStack(EnderIO.itemRedstoneConduit, 1, 0);

        ItemStack fusedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 0);
        ItemStack fusedGlass = new ItemStack(EnderIO.blockFusedQuartz, 1, BlockFusedQuartz.Type.GLASS.ordinal());

        String crudeSteel = CRUDE_STEEL.getOreIngot();
        String energeticSilver = ENERGETIC_SILVER.getOreIngot();
        String crystallineAlloy = CRYSTALLINE_ALLOY.getOreIngot();
        String crystallinePinkSlime = CRYSTALLINE_PINK_SLIME.getOreIngot();
        String melodicAlloy = MELODIC_ALLOY.getOreIngot();
        String stellarAlloy = STELLAR_ALLOY.getOreIngot();

        String electricalSteel = ELECTRICAL_STEEL.getOreIngot();
        String phasedGold = PHASED_GOLD.getOreIngot();
        String conductiveIron = CONDUCTIVE_IRON.getOreIngot();
        String energeticGold = ENERGETIC_ALLOY.getOreIngot();
        String phasedIronNugget = PHASED_IRON_NUGGET.oreDict;
        String redstoneAlloy = REDSTONE_ALLOY.getOreIngot();

        String binder = CONDUIT_BINDER.oreDict;

        ItemStack zombieController = new ItemStack(
                EnderIO.itemFrankenSkull,
                1,
                FrankenSkull.ZOMBIE_CONTROLLER.ordinal());
        ItemStack skeletalContractor = new ItemStack(
                EnderIO.itemFrankenSkull,
                1,
                FrankenSkull.SKELETAL_CONTRACTOR.ordinal());

        // Recipes
        addShaped(
                new ItemStack(EnderIO.itemConduitFacade, 1, FacadeType.BASIC.ordinal()),
                "bbb",
                "b b",
                "bbb",
                'b',
                binder);
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(EnderIO.itemConduitFacade, 1, FacadeType.HARDENED.ordinal()),
                        " o ",
                        "oFo",
                        " o ",
                        'F',
                        EnderIO.itemConduitFacade,
                        'o',
                        "dustObsidian"));

        int numConduits = Config.numConduitsPerRecipe;
        addShaped(
                new ItemStack(EnderIO.itemLiquidConduit, numConduits, 0),
                "bbb",
                "###",
                "bbb",
                'b',
                binder,
                '#',
                fusedGlass);
        addShaped(
                new ItemStack(EnderIO.itemLiquidConduit, numConduits, 1),
                "bbb",
                "###",
                "bbb",
                'b',
                binder,
                '#',
                fusedQuartz);
        addShaped(
                new ItemStack(EnderIO.itemLiquidConduit, numConduits, EnderLiquidConduit.METADATA),
                "bbb",
                "#p#",
                "bbb",
                'b',
                binder,
                '#',
                fusedQuartz,
                'p',
                phasedGold);
        addShaped(
                new ItemStack(EnderIO.itemLiquidConduit, numConduits, CrystallineEnderLiquidConduit.METADATA),
                "bbb",
                "#p#",
                "bbb",
                'b',
                binder,
                '#',
                fusedQuartz,
                'p',
                crystallineAlloy);
        addShaped(
                new ItemStack(EnderIO.itemLiquidConduit, numConduits, CrystallinePinkSlimeEnderLiquidConduit.METADATA),
                "bbb",
                "#p#",
                "bbb",
                'b',
                binder,
                '#',
                fusedQuartz,
                'p',
                crystallinePinkSlime);
        addShaped(
                new ItemStack(EnderIO.itemLiquidConduit, numConduits, MelodicEnderLiquidConduit.METADATA),
                "bbb",
                "#p#",
                "bbb",
                'b',
                binder,
                '#',
                fusedQuartz,
                'p',
                melodicAlloy);
        addShaped(
                new ItemStack(EnderIO.itemLiquidConduit, numConduits, StellarEnderLiquidConduit.METADATA),
                "bbb",
                "#p#",
                "bbb",
                'b',
                binder,
                '#',
                fusedQuartz,
                'p',
                stellarAlloy);
        addShaped(new ItemStack(EnderIO.itemRedstoneConduit, numConduits, 0), "###", '#', redstoneAlloy);
        addShaped(
                new ItemStack(EnderIO.itemRedstoneConduit, 1, 1),
                "lbl",
                "bcb",
                "lbl",
                'b',
                binder,
                'c',
                redstoneConduit,
                'l',
                Blocks.lever);
        addShaped(
                new ItemStack(EnderIO.itemRedstoneConduit, numConduits, 2),
                "bbb",
                "###",
                "bbb",
                'b',
                binder,
                '#',
                redstoneAlloy);

        if (Config.useHardRecipes) {

            ItemStack lastTier;

            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 0),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    crudeSteel);
            lastTier = new ItemStack(EnderIO.itemPowerConduitEndergy, 1, 0);

            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 1),
                    "b#b",
                    "ccc",
                    "b#b",
                    'c',
                    lastTier,
                    'b',
                    binder,
                    '#',
                    "ingotIron");
            lastTier = new ItemStack(EnderIO.itemPowerConduitEndergy, 1, 1);

            if (OreDictionary.doesOreNameExist("ingotAluminum") || OreDictionary.doesOreNameExist("ingotAluminium")) {
                addShaped(
                        new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 2),
                        "b#b",
                        "ccc",
                        "b#b",
                        'c',
                        lastTier,
                        'b',
                        binder,
                        '#',
                        "ingotAluminum");
                addShaped(
                        new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 2),
                        "b#b",
                        "ccc",
                        "b#b",
                        'c',
                        lastTier,
                        'b',
                        binder,
                        '#',
                        "ingotAluminium");
                lastTier = new ItemStack(EnderIO.itemPowerConduitEndergy, 1, 2);
            }

            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 3),
                    "b#b",
                    "ccc",
                    "b#b",
                    'c',
                    lastTier,
                    'b',
                    binder,
                    '#',
                    "ingotGold");
            lastTier = new ItemStack(EnderIO.itemPowerConduitEndergy, 1, 3);

            if (OreDictionary.doesOreNameExist("ingotCopper") && OreDictionary.getOres("ingotCopper").size() > 0) {
                addShaped(
                        new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 4),
                        "b#b",
                        "ccc",
                        "b#b",
                        'c',
                        lastTier,
                        'b',
                        binder,
                        '#',
                        "ingotCopper");
                lastTier = new ItemStack(EnderIO.itemPowerConduitEndergy, 1, 4);
            }

            addShaped(
                    new ItemStack(EnderIO.itemPowerConduit, numConduits, 0),
                    "b#b",
                    "ccc",
                    "b#b",
                    'c',
                    lastTier,
                    'b',
                    binder,
                    '#',
                    conductiveIron);
            lastTier = new ItemStack(EnderIO.itemPowerConduit, 1, 0);

            if (OreDictionary.doesOreNameExist("ingotSilver")) {
                addShaped(
                        new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 5),
                        "b#b",
                        "ccc",
                        "b#b",
                        'c',
                        lastTier,
                        'b',
                        binder,
                        '#',
                        "ingotSilver");
                lastTier = new ItemStack(EnderIO.itemPowerConduitEndergy, 1, 5);
            }

            if (OreDictionary.doesOreNameExist("ingotElectrum")) {
                addShaped(
                        new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 6),
                        "b#b",
                        "ccc",
                        "b#b",
                        'c',
                        lastTier,
                        'b',
                        binder,
                        '#',
                        "ingotElectrum");
                lastTier = new ItemStack(EnderIO.itemPowerConduitEndergy, 1, 6);
            }

            addShaped(
                    new ItemStack(EnderIO.itemPowerConduit, numConduits, 1),
                    "b#b",
                    "ccc",
                    "b#b",
                    'c',
                    lastTier,
                    'b',
                    binder,
                    '#',
                    energeticGold);
            lastTier = new ItemStack(EnderIO.itemPowerConduit, 1, 1);

            if (OreDictionary.doesOreNameExist("ingotSilver")) {
                addShaped(
                        new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 7),
                        "b#b",
                        "ccc",
                        "b#b",
                        'c',
                        lastTier,
                        'b',
                        binder,
                        '#',
                        energeticSilver);
                lastTier = new ItemStack(EnderIO.itemPowerConduitEndergy, 1, 7);
            }

            addShaped(
                    new ItemStack(EnderIO.itemPowerConduit, numConduits, 2),
                    "b#b",
                    "ccc",
                    "b#b",
                    'c',
                    lastTier,
                    'b',
                    binder,
                    '#',
                    phasedGold);
            lastTier = new ItemStack(EnderIO.itemPowerConduit, 1, 2);

            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 8),
                    "b#b",
                    "ccc",
                    "b#b",
                    'c',
                    lastTier,
                    'b',
                    binder,
                    '#',
                    crystallineAlloy);
            lastTier = new ItemStack(EnderIO.itemPowerConduitEndergy, 1, 8);

            if (OreDictionary.doesOreNameExist("slimeballPink")) {
                addShaped(
                        new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 9),
                        "b#b",
                        "ccc",
                        "b#b",
                        'c',
                        lastTier,
                        'b',
                        binder,
                        '#',
                        crystallinePinkSlime);
                lastTier = new ItemStack(EnderIO.itemPowerConduitEndergy, 1, 9);
            }

            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 10),
                    "b#b",
                    "ccc",
                    "b#b",
                    'c',
                    lastTier,
                    'b',
                    binder,
                    '#',
                    melodicAlloy);
            lastTier = new ItemStack(EnderIO.itemPowerConduitEndergy, 1, 10);

            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 11),
                    "b#b",
                    "ccc",
                    "b#b",
                    'c',
                    lastTier,
                    'b',
                    binder,
                    '#',
                    stellarAlloy);

        } else {
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduit, numConduits, 0),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    conductiveIron);
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduit, numConduits, 1),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    energeticGold);
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduit, numConduits, 2),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    phasedGold);

            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 0),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    crudeSteel);
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 1),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    "ingotIron");
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 2),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    "ingotAluminum");
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 3),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    "ingotGold");
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 4),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    "ingotCopper");
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 5),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    "ingotSilver");
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 6),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    "ingotElectrum");
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 7),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    energeticSilver);
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 8),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    crystallineAlloy);
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 9),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    crystallinePinkSlime);
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 10),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    melodicAlloy);
            addShaped(
                    new ItemStack(EnderIO.itemPowerConduitEndergy, numConduits, 11),
                    "bbb",
                    "###",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    stellarAlloy);
        }
        if (GasUtil.isGasConduitEnabled()) {
            addShaped(
                    new ItemStack(EnderIO.itemGasConduit, numConduits, 0),
                    "bbb",
                    "#g#",
                    "bbb",
                    'b',
                    binder,
                    '#',
                    electricalSteel,
                    'g',
                    fusedGlass);
        }

        ItemStack itemConduit = new ItemStack(EnderIO.itemItemConduit, numConduits, 0);
        addShaped(itemConduit, "bbb", "###", "bbb", 'b', binder, '#', phasedIronNugget);

        MachineRecipeRegistry.instance
                .registerRecipe(blockPainter.unlocalisedName, EnderIO.itemConduitFacade.new FacadePainterRecipe());

        // Filter Recipes
        ItemStack basicFilter = new ItemStack(EnderIO.itemBasicFilterUpgrade, 1, 0);
        addShaped(basicFilter, " p ", "php", " p ", 'p', Items.paper, 'h', Blocks.hopper);

        ItemStack advFilter = new ItemStack(EnderIO.itemBasicFilterUpgrade, 1, 1);
        addShaped(advFilter, "rpr", "pzp", "rpr", 'p', Items.paper, 'z', zombieController, 'r', "dustRedstone");

        ItemStack bigFilter = new ItemStack(EnderIO.itemBigFilterUpgrade, 1, 0);
        addShaped(bigFilter, "opo", "psp", "opo", 'p', Items.paper, 's', skeletalContractor, 'o', "dustObsidian");

        ItemStack bigAdvFilter = new ItemStack(EnderIO.itemBigFilterUpgrade, 1, 1);
        addShaped(bigAdvFilter, "eme", "mfm", "eme", 'm', melodicAlloy, 'f', bigFilter, 'e', END_STEEL.getOreIngot());

        ItemStack modFilter = new ItemStack(EnderIO.itemModItemFilter, 1, 0);
        addShaped(modFilter, " p ", "pwp", " p ", 'p', Items.paper, 'w', EnderIO.itemYetaWench);

        ItemStack exFilt = new ItemStack(EnderIO.itemExistingItemFilter);
        addShaped(
                exFilt,
                " r ",
                "rfr",
                " c ",
                'c',
                new ItemStack(Items.comparator, 1, 0),
                'r',
                "dustRedstone",
                'f',
                advFilter);

        ItemStack powerFilt = new ItemStack(EnderIO.itemPowerItemFilter);
        addShaped(powerFilt, " p ", "pcp", " p ", 'p', Items.paper, 'c', EnderIO.itemConduitProbe);

        ClearFilterRecipe clearRec = new ClearFilterRecipe();
        MinecraftForge.EVENT_BUS.register(clearRec);
        GameRegistry.addRecipe(clearRec);

        CopyFilterRecipe copyRec = new CopyFilterRecipe();
        GameRegistry.addRecipe(copyRec);

        ItemStack speedUpgrade = new ItemStack(EnderIO.itemExtractSpeedUpgrade, 1, 0);
        addShaped(
                speedUpgrade,
                "iii",
                "epe",
                "ere",
                'p',
                Blocks.piston,
                'e',
                electricalSteel,
                'r',
                Blocks.redstone_torch,
                'i',
                "ingotIron");

        ItemStack speedDowngrade = new ItemStack(EnderIO.itemExtractSpeedUpgrade, 1, 1);
        addShaped(
                speedDowngrade,
                "iii",
                "ese",
                "ete",
                's',
                "slimeball",
                'e',
                electricalSteel,
                't',
                "stickWood",
                'i',
                "ingotIron");
        addShaped(
                speedDowngrade,
                "iii",
                "ese",
                "ete",
                's',
                "slimeball",
                'e',
                electricalSteel,
                't',
                "woodStick",
                'i',
                "ingotIron");

        if (MEUtil.isMEEnabled()) {
            addAeRecipes();
        }
        if (OCUtil.isOCEnabled()) {
            addOCRecipes();
        }
    }

    private static void addOCRecipes() {
        int numConduits = Config.numConduitsPerRecipe;
        String redstoneAlloy = REDSTONE_ALLOY.getOreIngot();
        String binder = CONDUIT_BINDER.oreDict;

        addShaped(
                new ItemStack(EnderIO.itemOCConduit, numConduits, 0),
                "bbb",
                "rir",
                "bbb",
                'b',
                binder,
                'r',
                redstoneAlloy,
                'i',
                "ingotIron");
    }

    @Method(modid = "appliedenergistics2")
    private static void addAeRecipes() {
        String fluix = "crystalFluix";
        String pureFluix = "crystalPureFluix";

        ItemStack quartzFiber = AEApi.instance().parts().partQuartzFiber.stack(1).copy();
        ItemStack conduitBinder = new ItemStack(EnderIO.itemMaterial, 1, Material.CONDUIT_BINDER.ordinal());
        ItemStack res = new ItemStack(EnderIO.itemMEConduit, Config.numConduitsPerRecipe / 2);

        addShaped(res.copy(), "bbb", "fqf", "bbb", 'b', conduitBinder, 'f', fluix, 'q', quartzFiber);
        addShaped(res.copy(), "bbb", "fqf", "bbb", 'b', conduitBinder, 'f', pureFluix, 'q', quartzFiber);

        res.stackSize = 1;
        ItemStack resDense = new ItemStack(EnderIO.itemMEConduit, 1, 1);
        ItemStack resUltra = new ItemStack(EnderIO.itemMEConduit, 1, 2);
        addShaped(resDense, "bCb", "CbC", "bCb", 'b', conduitBinder, 'C', res);
        if (Config.enableMEUltraDenseConduits) {
            addShaped(resUltra, "bCb", "CbC", "bCb", 'b', conduitBinder, 'C', resDense);
        }
    }
}
