package com.enderio.base.common.item;

import com.enderio.base.EnderIO;
import com.enderio.base.common.item.food.EnderiosItem;
import com.enderio.base.common.item.spawner.BrokenSpawnerItem;
import com.enderio.base.common.util.ItemModelUtils;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.NonNullLazyValue;
import com.tterrag.registrate.util.entry.ItemEntry;

import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

@SuppressWarnings("unused")
public class EIOItems {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    // region Materials

    public static final ItemEntry<MaterialItem> ELECTRICAL_STEEL_INGOT = materialItem("electrical_steel_ingot").register();
    public static final ItemEntry<MaterialItem> ENERGETIC_ALLOY_INGOT = materialItem("energetic_alloy_ingot").register();
    public static final ItemEntry<MaterialItem> VIBRANT_ALLOY_INGOT = materialItem("vibrant_alloy_ingot").register();
    public static final ItemEntry<MaterialItem> REDSTONE_ALLOY_INGOT = materialItem("redstone_alloy_ingot").register();
    public static final ItemEntry<MaterialItem> CONDUCTIVE_IRON_INGOT = materialItem("conductive_iron_ingot").register();
    public static final ItemEntry<MaterialItem> PULSATING_IRON_INGOT = materialItem("pulsating_iron_ingot").register();
    public static final ItemEntry<MaterialItem> DARK_STEEL_INGOT = materialItem("dark_steel_ingot").register();
    public static final ItemEntry<MaterialItem> SOULARIUM_INGOT = materialItem("soularium_ingot").register();
    public static final ItemEntry<MaterialItem> END_STEEL_INGOT = materialItem("end_steel_ingot").register();
    public static final ItemEntry<MaterialItem> CONSTRUCTION_ALLOY_INGOT = materialItem("construction_alloy_ingot").register();

    public static final ItemEntry<MaterialItem> ELECTRICAL_STEEL_NUGGET = materialItem("electrical_steel_nugget").register();
    public static final ItemEntry<MaterialItem> ENERGETIC_ALLOY_NUGGET = materialItem("energetic_alloy_nugget").register();
    public static final ItemEntry<MaterialItem> VIBRANT_ALLOY_NUGGET = materialItem("vibrant_alloy_nugget").register();
    public static final ItemEntry<MaterialItem> REDSTONE_ALLOY_NUGGET = materialItem("redstone_alloy_nugget").register();
    public static final ItemEntry<MaterialItem> CONDUCTIVE_IRON_NUGGET = materialItem("conductive_iron_nugget").register();
    public static final ItemEntry<MaterialItem> PULSATING_IRON_NUGGET = materialItem("pulsating_iron_nugget").register();
    public static final ItemEntry<MaterialItem> DARK_STEEL_NUGGET = materialItem("dark_steel_nugget").register();
    public static final ItemEntry<MaterialItem> SOULARIUM_NUGGET = materialItem("soularium_nugget").register();
    public static final ItemEntry<MaterialItem> END_STEEL_NUGGET = materialItem("end_steel_nugget").register();
    public static final ItemEntry<MaterialItem> CONSTRUCTION_ALLOY_NUGGET = materialItem("construction_alloy_nugget").register();

    // region Basic Materials

    public static final ItemEntry<MaterialItem> CONDUIT_BINDER = materialItem("conduit_binder").register();
    public static final ItemEntry<MaterialItem> SILICON = materialItem("silicon").register();

    // endregion

    // region Components

    // region Machine Parts

    public static final ItemEntry<MaterialItem> SIMPLE_MACHINE_PARTS = materialItem("simple_machine_parts").register();
    public static final ItemEntry<MaterialItem> INDUSTRIAL_MACHINE_PARTS = materialItem("industrial_machine_parts").register();
    public static final ItemEntry<MaterialItem> ENHANCED_MACHINE_PARTS = materialItem("enhanced_machine_parts").register();

    // endregion

    // region Circuits todo: better name

    public static final ItemEntry<MaterialItem> ZOMBIE_ELECTRODE = materialItem("zombie_electrode").register();

    public static final ItemEntry<MaterialItem> ZOMBIE_CONTROLLER = materialItem("zombie_controller").lang("Z-Logic Controller").register();

    public static final ItemEntry<MaterialItem> FRANKEN_ZOMBIE = materialItemGlinted("franken_zombie")
        .lang("Frank'N'Zombie")
        .model((ctx, prov) -> ItemModelUtils.mimicItem(ctx, EIOItems.ZOMBIE_CONTROLLER, prov))
        .register();

    public static final ItemEntry<MaterialItem> ENDER_RESONATOR = materialItem("ender_resonator").register();

    public static final ItemEntry<MaterialItem> SENTIENT_ENDER = materialItemGlinted("sentient_ender")
        .model((ctx, prov) -> ItemModelUtils.mimicItem(ctx, EIOItems.ENDER_RESONATOR, prov))
        .register();

    public static final ItemEntry<MaterialItem> SKELETAL_CONTRACTOR = materialItem("skeletal_contractor").register();
    public static final ItemEntry<MaterialItem> GUARDIAN_DIODE = materialItem("guardian_diode").register();

    // endregion

    // endregion

    // region Crystals

    public static final ItemEntry<MaterialItem> PULSATING_CRYSTAL = materialItemGlinted("pulsating_crystal").register();
    public static final ItemEntry<MaterialItem> VIBRANT_CRYSTAL = materialItemGlinted("vibrant_crystal").register();
    public static final ItemEntry<MaterialItem> ENDER_CRYSTAL = materialItemGlinted("ender_crystal").register();
    public static final ItemEntry<MaterialItem> ENTICING_CRYSTAL = materialItemGlinted("enticing_crystal").register();
    public static final ItemEntry<MaterialItem> WEATHER_CRYSTAL = materialItemGlinted("weather_crystal").register();
    public static final ItemEntry<MaterialItem> PRECIENT_CRYSTAL = materialItemGlinted("precient_crystal").register();

    // endregion

    // region Infinity

    public static ItemEntry<MaterialItem> INFINITY_CRYSTAL = materialItem("infinity_crystal").register();

    public static final ItemEntry<MaterialItem> GRAINS_OF_INFINITY = materialItem("grains_of_infinity").lang("Grains of Infinity").register();

    public static ItemEntry<MaterialItem> INFINITY_ROD = materialItem("infinity_rod").register();

    // endregion

    // region Powders and Fragments

    public static final ItemEntry<MaterialItem> FLOUR = materialItem("flour").register();
    public static final ItemEntry<MaterialItem> CONDUIT_BINDER_COMPOSITE = materialItem("conduit_binder_composite").register();
    public static final ItemEntry<MaterialItem> COAL_POWDER = materialItem("coal_powder").register();
    public static final ItemEntry<MaterialItem> IRON_POWDER = materialItem("iron_powder").register();
    public static final ItemEntry<MaterialItem> GOLD_POWDER = materialItem("gold_powder").register();
    public static final ItemEntry<MaterialItem> COPPER_POWDER = materialItem("copper_powder").register();
    public static final ItemEntry<MaterialItem> TIN_POWDER = materialItem("tin_powder").register(); // TODO: tin ingot tag depend
    public static final ItemEntry<MaterialItem> ENDER_PEARL_POWDER = materialItem("ender_pearl_powder").register();
    public static final ItemEntry<MaterialItem> OBSIDIAN_POWDER = materialItem("obsidian_powder").register();
    public static final ItemEntry<MaterialItem> ARDITE_POWDER = materialItem("ardite_powder").register(); // TODO: ardite ore tag depend
    public static final ItemEntry<MaterialItem> COBALT_POWDER = materialItem("cobalt_powder").register(); // TODO: cobalt ore tag depend
    public static final ItemEntry<MaterialItem> LAPIS_LAZULI_POWDER = materialItem("lapis_lazuli_powder").register();
    public static final ItemEntry<MaterialItem> QUARTZ_POWDER = materialItem("quartz_powder").register();

    public static final ItemEntry<MaterialItem> PRECIENT_POWDER = materialItemGlinted("precient_powder").lang("Grains of Prescience").register();

    public static final ItemEntry<MaterialItem> VIBRANT_POWDER = materialItemGlinted("vibrant_powder").lang("Grains of Vibrancy").register();

    public static final ItemEntry<MaterialItem> PULSATING_POWDER = materialItemGlinted("pulsating_powder").lang("Grains of Piezallity").register();

    public static final ItemEntry<MaterialItem> ENDER_CRYSTAL_POWDER = materialItemGlinted("ender_crystal_powder").lang("Grains of the End").register();

    public static final ItemEntry<MaterialItem> PHOTOVOLTAIC_COMPOSITE = materialItem("photovoltaic_composite").register();
    public static final ItemEntry<MaterialItem> SOUL_POWDER = materialItem("soul_powder").register();
    public static final ItemEntry<MaterialItem> CONFUSION_POWDER = materialItem("confusing_powder").register();
    public static final ItemEntry<MaterialItem> WITHERING_POWDER = materialItem("withering_powder").register();
    public static final ItemEntry<MaterialItem> ENDER_FRAGMENT = materialItem("ender_fragment").register();

    // endregion

    // skipped a few

    // region Gears

    public static final ItemEntry<GearItem> GEAR_WOOD = gearItem("wood_gear").lang("Wooden Gear").register();

    public static final ItemEntry<GearItem> GEAR_STONE = gearItem("stone_gear").lang("Stone Compound Gear").register();

    public static final ItemEntry<GearItem> GEAR_IRON = gearItem("iron_gear").lang("Infinity Bimetal Gear").register();

    public static final ItemEntry<GearItem> GEAR_ENERGIZED = gearItem("energized_gear").lang("Energized Bimetal Gear").register();

    public static final ItemEntry<GearItem> GEAR_VIBRANT = gearItem("vibrant_gear").lang("Vibrant Bimetal Gear").register();

    public static final ItemEntry<GearItem> GEAR_DARK_STEEL = gearItem("dark_bimetal_gear").lang("Dark Bimetal Gear").register();

    // endregion

    // region Dyes

    public static final ItemEntry<MaterialItem> DYE_GREEN = materialItem("organic_green_dye").tag(Tags.Items.DYES_GREEN, Tags.Items.DYES).register();

    public static final ItemEntry<MaterialItem> DYE_BROWN = materialItem("organic_brown_dye").tag(Tags.Items.DYES_BROWN, Tags.Items.DYES).register();

    public static final ItemEntry<MaterialItem> DYE_BLACK = materialItem("organic_black_dye").tag(Tags.Items.DYES_BLACK, Tags.Items.DYES).register();

    public static final ItemEntry<MaterialItem> DYE_INDUSTRIAL_BLEND = materialItem("industrial_dye_blend").tag(Tags.Items.DYES).register();

    public static final ItemEntry<MaterialItem> DYE_SOUL_ATTUNED_BLEND = materialItem("soul_attuned_dye_blend").tag(Tags.Items.DYES).register();

    public static final ItemEntry<MaterialItem> DYE_ENHANCED_BLEND = materialItem("enhanced_dye_blend").tag(Tags.Items.DYES).register();

    // endregion

    // region Misc Materials

    public static ItemEntry<MaterialItem> PHOTOVOLTAIC_PLATE = materialItem("photovoltaic_plate")
        .model((ctx, prov) -> prov.withExistingParent(prov.name(ctx), prov.mcLoc("block/pressure_plate_up")).texture("texture", prov.itemTexture(ctx)))
        .register();

    public static ItemEntry<MaterialItem> NUTRITIOUS_STICK = materialItem("nutritious_stick").register();

    public static ItemEntry<MaterialItem> PLANT_MATTER_GREEN = materialItem("plant_matter_green").lang("Clippings and Trimmings").register();

    public static ItemEntry<MaterialItem> PLANT_MATTER_BROWN = materialItem("plant_matter_brown").lang("Twigs and Prunings").register();

    public static ItemEntry<MaterialItem> GLIDER_WING = materialItem("glider_wing").register();
    public static ItemEntry<MaterialItem> GLIDER_WINGS = materialItem("glider_wings").register();

    public static ItemEntry<MaterialItem> ANIMAL_TOKEN = materialItemGlinted("animal_token").register();
    public static ItemEntry<MaterialItem> MONSTER_TOKEN = materialItemGlinted("monster_token").register();
    public static ItemEntry<MaterialItem> PLAYER_TOKEN = materialItemGlinted("player_token").register();

    public static ItemEntry<MaterialItem> UNFIRED_DEATH_URN = materialItem("unfired_death_urn").register();
    public static ItemEntry<MaterialItem> CAKE_BASE = materialItem("cake_base").register();
    public static ItemEntry<MaterialItem> BLACK_PAPER = materialItem("black_paper").register();
    public static ItemEntry<MaterialItem> CLAYED_GLOWSTONE = materialItem("clayed_glowstone").register();
    public static ItemEntry<MaterialItem> NETHERCOTTA = materialItem("nethercotta").register();
    public static ItemEntry<MaterialItem> REDSTONE_FILTER_BASE = materialItem("redstone_filter_base").register();
    public static ItemEntry<MaterialItem> REMOTE_AWARENESS_UPGRADE = materialItem("remote_awareness_upgrade").register();
    public static ItemEntry<MaterialItem> INGOT_ENDERIUM_BASE = materialItem("ingot_enderium_base").register(); // TODO: Depend on enderium ingot tag

    public static final ItemEntry<BrokenSpawnerItem> BROKEN_SPAWNER = REGISTRATE
        .item("broken_spawner", BrokenSpawnerItem::new)
        .model(ItemModelUtils::fakeBlockModel)
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.MAIN))
        .register();

    // endregion

    // region Builders

    private static ItemBuilder<MaterialItem, Registrate> materialItem(String name) {
        return REGISTRATE.item(name, props -> new MaterialItem(props, false)).group(new NonNullLazyValue<>(() -> EIOCreativeTabs.MAIN));
    }

    private static ItemBuilder<GearItem, Registrate> gearItem(String name) {
        return REGISTRATE
            .item(name, props -> new GearItem(props, false))
            .model((c, p) -> ItemModelUtils.gearItem(c, p))
            .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.MAIN));
    }

    //  private static ItemBuilder<MaterialItem, Registrate> dependMaterialItem(String name, Tag<Item> dependency) {
    //    return REGISTRATE.item(name, props -> new MaterialItem(props, false, dependency))
    //        .group(new NonNullLazyValue<>(() -> EnderIO.TAB_MAIN));
    //  }

    private static ItemBuilder<MaterialItem, Registrate> materialItemGlinted(String name) {
        return REGISTRATE.item(name, props -> new MaterialItem(props, true)).group(new NonNullLazyValue<>(() -> EIOCreativeTabs.MAIN));
    }

    // endregion

    // region Items

    // TODO: Will need sorted once we have added more.

    public static ItemEntry<SoulVialItem> EMPTY_SOUL_VIAL = groupedItem("empty_soul_vial", SoulVialItem::new, () -> EIOCreativeTabs.GEAR);

    public static ItemEntry<SoulVialItem> FILLED_SOUL_VIAL = REGISTRATE
        .item("filled_soul_vial", SoulVialItem::new)
        .properties(props -> props.stacksTo(1))
        .register();

    public static ItemEntry<EnderiosItem> ENDERIOS = REGISTRATE
        .item("enderios", EnderiosItem::new)
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.MAIN))
        .lang("\"Enderios\"")
        .properties(props -> props.stacksTo(1))
        .register();

    // endregion

    // region Creative Tab Icons

    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_NONE = dumbItem("enderface_none", EnderfaceItem::new);
    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_ITEMS = dumbItem("enderface_items", EnderfaceItem::new);
    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_MATERIALS = dumbItem("enderface_materials", EnderfaceItem::new);
    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_MACHINES = dumbItem("enderface_machines", EnderfaceItem::new);
    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_CONDUITS = dumbItem("enderface_conduits", EnderfaceItem::new);
    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_MOBS = dumbItem("enderface_mobs", EnderfaceItem::new);
    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_INVPANEL = dumbItem("enderface_invpanel", EnderfaceItem::new);

    // endregion

    // region Helpers

    public static <T extends Item> ItemEntry<T> dumbItem(String name, NonNullFunction<Item.Properties, T> factory) {
        return REGISTRATE.item(name, factory).register();
    }

    public static <T extends Item> ItemEntry<T> groupedItem(String name, NonNullFunction<Item.Properties, T> factory, NonNullSupplier<CreativeModeTab> tab) {
        return REGISTRATE.item(name, factory).group(tab).register();
    }

    // endregion

    public static void register() {}
}
