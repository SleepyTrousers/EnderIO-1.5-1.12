package crazypants.enderio.base.integration.jei;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.IProducer;

import crazypants.enderio.base.block.skull.SkullType;
import crazypants.enderio.base.config.config.InfinityConfig;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.material.material.Material;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

public enum ItemHidingHelper {
  CONDUIT(mod("enderioconduits", PersonalConfig.hideConduits), ModObject.itemConduitFacade, ModObject.itemFluidFilter, ModObject.itemRedstoneNotFilter,
      ModObject.itemRedstoneOrFilter, ModObject.itemRedstoneAndFilter, ModObject.itemRedstoneNorFilter, ModObject.itemRedstoneNandFilter,
      ModObject.itemRedstoneXorFilter, ModObject.itemRedstoneXnorFilter, ModObject.itemRedstoneToggleFilter, ModObject.itemRedstoneCountingFilter,
      ModObject.itemRedstoneSensorFilter, ModObject.itemRedstoneTimerFilter),
  DUAL_USE(mod("enderioconduits", PersonalConfig.hideConduits).and(mod("enderiomachines", PersonalConfig.hideMachineParts)), ModObject.itemYetaWrench,
      ModObject.itemConduitProbe, ModObject.itemBasicItemFilter, ModObject.itemAdvancedItemFilter, ModObject.itemLimitedItemFilter, ModObject.itemBigItemFilter,
      ModObject.itemBigAdvancedItemFilter, ModObject.itemExistingItemFilter, ModObject.itemModItemFilter, ModObject.itemPowerItemFilter),
  TRIPPLE_USE(mod("enderioconduits", PersonalConfig.hideConduits).and(mod("enderiomachines", PersonalConfig.hideMachineParts))
      .and(mod("enderiomachines", PersonalConfig.hideToolsAndArmor)), ModObject.itemBasicCapacitor),
  MATS(mod("enderiomachines", PersonalConfig.hideMaterials), ModObject.blockAlloy, ModObject.itemAlloyIngot, ModObject.itemAlloyNugget, ModObject.itemAlloyBall,
      ModObject.blockFusedQuartz, ModObject.blockFusedGlass, ModObject.blockEnlightenedFusedQuartz, ModObject.blockEnlightenedFusedGlass,
      ModObject.blockDarkFusedQuartz, ModObject.blockDarkFusedGlass, ModObject.blockPaintedFusedQuartz, ModObject.blockDecoration1, ModObject.blockDecoration2,
      ModObject.blockDecoration3, ModObject.blockIndustrialInsulation, ModObject.itemEnderFood),
  INFINITY(mod("enderiomachines", PersonalConfig.hideMaterials).or(x -> PersonalConfig.hideInfinity.get() && !InfinityConfig.inWorldCraftingEnabled.get()),
      ModObject.block_infinity_fog),
  BLOCKS(mod("enderiomachines", PersonalConfig.hideMaterialBlocks), ModObject.blockDarkSteelAnvil, ModObject.blockDarkSteelLadder, ModObject.blockDarkIronBars,
      ModObject.blockDarkSteelTrapdoor, ModObject.blockDarkSteelDoor, ModObject.blockReinforcedObsidian, ModObject.blockEndIronBars,
      ModObject.block_detector_block, ModObject.block_detector_block_silent),
  TOOLS(mod("enderiomachines", PersonalConfig.hideToolsAndArmor), ModObject.itemXpTransfer, ModObject.itemColdFireIgniter, ModObject.itemCoordSelector,
      ModObject.itemLocationPrintout, ModObject.itemTravelStaff, ModObject.itemRodOfReturn, ModObject.itemMagnet, ModObject.itemSoulVial,
      ModObject.itemDarkSteelHelmet, ModObject.itemDarkSteelChestplate, ModObject.itemDarkSteelLeggings, ModObject.itemDarkSteelBoots,
      ModObject.itemDarkSteelSword, ModObject.itemDarkSteelPickaxe, ModObject.itemDarkSteelAxe, ModObject.itemDarkSteelBow, ModObject.itemDarkSteelShears,
      ModObject.itemEndSteelSword, ModObject.itemEndSteelPickaxe, ModObject.itemEndSteelAxe, ModObject.itemEndSteelBow, ModObject.itemEndSteelHelmet,
      ModObject.itemEndSteelChestplate, ModObject.itemEndSteelLeggings, ModObject.itemEndSteelBoots, ModObject.itemStaffOfLevity),
  ZOO(mod("enderiozoo", PersonalConfig.hideMobDrops), ModObject.blockConfusionCharge, ModObject.blockConcussionCharge, ModObject.blockEnderCharge,
      ModObject.item_owl_egg),
  MATS2(mod("enderiomachines", PersonalConfig.hideMaterials), () -> new ItemStack(ModObject.blockEndermanSkull.getItemNN(), 1, SkullType.TORMENTED.ordinal())),
  INFINITY2(x -> PersonalConfig.hideInfinity.get() && !InfinityConfig.inWorldCraftingEnabled.get(), Material.POWDER_INFINITY),
  DUAL_USE2(mod("enderioconduits", PersonalConfig.hideConduits).and(mod("enderiomachines", PersonalConfig.hideMachineParts)), Material.CONDUIT_BINDER,
      Material.POWDER_BINDER_COMPOSITE),
  CONDUIT2(mod("enderioconduits", PersonalConfig.hideConduits), Material.REDSTONE_FILTER_BASE),
  CONDUIT3(mod("enderioinvpanel", PersonalConfig.hideConduits), Material.REMOTE_AWARENESS_UPGRADE),
  TOOLS2(mod("enderiomachines", PersonalConfig.hideToolsAndArmor), Material.GLIDER_WING, Material.GLIDER_WINGS, Material.NUTRITIOUS_STICK,
      Material.VIBRANT_CYSTAL, Material.SKELETAL_CONTRACTOR, Material.INFINITY_ROD),
  MATS_TE(mod("thermalfoundation", PersonalConfig.hideTEMaterials), Material.INGOT_ENDERIUM_BASE, Material.GRINDING_BALL_SIGNALUM,
      Material.GRINDING_BALL_ENDERIUM, Material.GRINDING_BALL_LUMIUM),
  MATS_TIC(mod("enderiointegrationtic", PersonalConfig.hideTiCMaterials).or(mod("tconstruct", PersonalConfig.hideTiCMaterials)), Material.POWDER_ARDITE,
      Material.POWDER_COBALT),
  MATS3(mod("enderiomachines", PersonalConfig.hideMaterials), Material.SIMPLE_MACHINE_CHASSI, Material.MACHINE_CHASSI, Material.CHASSIPARTS,
      Material.PLATE_PHOTOVOLTAIC, Material.SILICON, Material.GEAR_ENERGIZED, Material.GEAR_VIBRANT, Material.PULSATING_CYSTAL, Material.ENDER_CRYSTAL,
      Material.ATTRACTOR_CRYSTAL, Material.WEATHER_CRYSTAL, Material.PRECIENT_CRYSTAL, Material.POWDER_FLOUR, Material.POWDER_COAL, Material.POWDER_IRON,
      Material.POWDER_GOLD, Material.POWDER_COPPER, Material.POWDER_TIN, Material.POWDER_ENDER, Material.POWDER_OBSIDIAN, Material.POWDER_ARDITE,
      Material.POWDER_COBALT, Material.POWDER_LAPIS, Material.POWDER_QUARTZ, Material.POWDER_PRECIENT, Material.POWDER_VIBRANT, Material.POWDER_PULSATING,
      Material.POWDER_ENDER_CYSTAL, Material.POWDER_PHOTOVOLTAIC, Material.INGOT_ENDERIUM_BASE, Material.ZOMBIE_ELECTRODE, Material.ZOMBIE_CONTROLLER,
      Material.FRANKEN_ZOMBIE, Material.ENDER_RESONATOR, Material.SENTIENT_ENDER, Material.PLANT_GREEN, Material.PLANT_BROWN, Material.DYE_GREEN,
      Material.DYE_BROWN, Material.DYE_BLACK, Material.DYE_MACHINE, Material.DYE_SOUL_MACHINE, Material.SOUL_MACHINE_CHASSIS, Material.ENHANCED_MACHINE_CHASSIS,
      Material.UNSOULED_MACHINE_CHASSIS, Material.GUARDIAN_DIODE, Material.GRINDING_BALL_SIGNALUM, Material.GRINDING_BALL_ENDERIUM,
      Material.GRINDING_BALL_LUMIUM, Material.REDSTONE_FILTER_BASE, Material.DISH, Material.END_STEEL_MACHINE_CHASSIS, Material.DYE_ENHANCED_MACHINE,
      Material.ENHANCED_CHASSIPARTS, Material.SIMPLE_CHASSIPARTS, Material.CAKE_BASE, Material.BRICK_GLAZED_NETHER),
  ZOO2(mod("enderiozoo", PersonalConfig.hideMobDrops), Material.POWDER_CONFUSION, Material.SHARD_ENDER, Material.POWDER_WITHERING),
  TAP(mod("ic2", PersonalConfig.hideTreetap).and(mod("techreborn", PersonalConfig.hideTreetap)), ModObject.itemDarkSteelTreetap),

  ;

  private final @Nonnull NNList<Supplier<ItemStack>> suppliers = new NNList<>();
  private final @Nonnull Predicate<IModRegistry> predicate;

  @SafeVarargs
  private ItemHidingHelper(Predicate<IModRegistry> predicate, @Nonnull Supplier<ItemStack>... suppliers) {
    this.suppliers.addAll(suppliers);
    this.predicate = NullHelper.notnull(predicate, "predicate fail");
  }

  private ItemHidingHelper(Predicate<IModRegistry> predicate, final @Nonnull IProducer... producers) {
    for (IProducer producer : producers) {
      this.suppliers.add(() -> new ItemStack(producer.getItemNN(), 1, OreDictionary.WILDCARD_VALUE));
    }
    this.predicate = NullHelper.notnull(predicate, "predicate fail");
  }

  private ItemHidingHelper(Predicate<IModRegistry> predicate, final @Nonnull Material... materials) {
    for (Material material : materials) {
      this.suppliers.add(() -> material.getStack());
    }
    this.predicate = NullHelper.notnull(predicate, "predicate fail");
  }

  public static void hide(@Nonnull IModRegistry registry) {
    if (!PersonalConfig.disableHiding.get()) {
      IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
      for (ItemHidingHelper elem : values()) {
        if (elem.predicate.test(registry)) {
          for (Supplier<ItemStack> supplier : elem.suppliers) {
            blacklist.addIngredientToBlacklist(supplier.get());
          }
        }
      }
    }
  }

  private static @Nonnull Predicate<IModRegistry> mod(final @Nonnull String modid, final @Nonnull IValue<Boolean> condition) {
    return factory -> !Loader.isModLoaded(modid) && condition.get();
  }

  @FunctionalInterface
  public interface Supplier<T> extends java.util.function.Supplier<T> {
    @Override
    @Nonnull
    T get();
  }
}
