package com.enderio.base;

import com.enderio.base.common.item.EnderfaceItem;
import com.enderio.base.common.item.MaterialItem;
import com.enderio.base.common.util.ItemModelUtils;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.NonNullLazyValue;
import com.tterrag.registrate.util.entry.ItemEntry;

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

    // region Machine Chassis and Parts

    public static final ItemEntry<MaterialItem> SIMPLE_MACHINE_CHASSIS = materialItem("simple_machine_chassis")
        .model(ItemModelUtils::fakeBlockModel)
        .register();

    public static final ItemEntry<MaterialItem> SIMPLE_MACHINE_PARTS = materialItem("simple_machine_parts").register();

    public static final ItemEntry<MaterialItem> INDUSTRIAL_MACHINE_CHASSIS = materialItem("industrial_machine_chassis")
        .model(ItemModelUtils::fakeBlockModel)
        .register();

    public static final ItemEntry<MaterialItem> INDUSTRIAL_MACHINE_PARTS = materialItem("industrial_machine_parts").register();

    public static final ItemEntry<MaterialItem> END_STEEL_MACHINE_CHASSIS = materialItem("end_steel_machine_chassis")
        .lang("End Steel Chassis")
        .model(ItemModelUtils::fakeBlockModel)
        .register();

    public static final ItemEntry<MaterialItem> SOUL_MACHINE_CHASSIS = materialItem("soul_machine_chassis")
        .model(ItemModelUtils::fakeBlockModel)
        .register();

    public static final ItemEntry<MaterialItem> ENHANCED_MACHINE_CHASSIS = materialItem("enhanced_machine_chassis")
        .model(ItemModelUtils::fakeBlockModel)
        .register();

    public static final ItemEntry<MaterialItem> ENHANCED_MACHINE_PARTS = materialItem("enhanced_machine_parts").register();

    public static final ItemEntry<MaterialItem> SOULLESS_MACHINE_CHASSIS = materialItem("soulless_machine_chassis")
        .model(ItemModelUtils::fakeBlockModel)
        .register();

    // endregion

    // region Circuits todo: better name

    public static final ItemEntry<MaterialItem> ZOMBIE_ELECTRODE = materialItem("zombie_electrode").register();

    public static final ItemEntry<MaterialItem> ZOMBIE_CONTROLLER = materialItem("zombie_controller")
        .lang("Z-Logic Controller")
        .register();

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

    // region Powders and Fragments

    public static final ItemEntry<MaterialItem> GRAINS_OF_INFINITY = materialItem("grains_of_infinity")
        .lang("Grains of Infinity")
        .register();

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

    public static final ItemEntry<MaterialItem> PRECIENT_POWDER = materialItemGlinted("precient_powder")
        .lang("Grains of Prescience")
        .register();

    public static final ItemEntry<MaterialItem> VIBRANT_POWDER = materialItemGlinted("vibrant_powder")
        .lang("Grains of Vibrancy")
        .register();

    public static final ItemEntry<MaterialItem> PULSATING_POWDER = materialItemGlinted("pulsating_powder")
        .lang("Grains of Piezallity")
        .register();

    public static final ItemEntry<MaterialItem> ENDER_CRYSTAL_POWDER = materialItemGlinted("ender_crystal_powder")
        .lang("Grains of the End")
        .register();

    public static final ItemEntry<MaterialItem> PHOTOVOLTAIC_COMPOSITE = materialItem("photovoltaic_composite").register();

    public static final ItemEntry<MaterialItem> SOUL_POWDER = materialItem("soul_powder").register();

    public static final ItemEntry<MaterialItem> CONFUSION_POWDER = materialItem("confusing_powder").register();

    public static final ItemEntry<MaterialItem> WITHERING_POWDER = materialItem("withering_powder").register();

    public static final ItemEntry<MaterialItem> ENDER_FRAGMENT = materialItem("ender_fragment").register();

    // endregion

    // skipped a few

    // region Gears

    public static final ItemEntry<MaterialItem> GEAR_WOOD = materialItem("wood_gear")
        .lang("Wooden Gear")
        .register();

    public static final ItemEntry<MaterialItem> GEAR_STONE = materialItem("stone_gear")
        .lang("Stone Compound Gear")
        .register();

    public static final ItemEntry<MaterialItem> GEAR_IRON = materialItem("iron_gear")
        .lang("Infinity Bimetal Gear")
        .register();

    public static final ItemEntry<MaterialItem> GEAR_ENERGIZED = materialItem("energized_gear")
        .lang("Energized Bimetal Gear")
        .register();

    public static final ItemEntry<MaterialItem> GEAR_VIBRANT = materialItem("vibrant_gear")
        .lang("Vibrant Bimetal Gear")
        .register();

    public static final ItemEntry<MaterialItem> GEAR_DARK_STEEL = materialItem("dark_bimetal_gear")
        .lang("Dark Bimetal Gear")
        .register();

    // endregion

    // region Dyes

    public static final ItemEntry<MaterialItem> DYE_GREEN = materialItem("organic_green_dye")
        .tag(Tags.Items.DYES_GREEN, Tags.Items.DYES)
        .register();

    public static final ItemEntry<MaterialItem> DYE_BROWN = materialItem("organic_brown_dye")
        .tag(Tags.Items.DYES_BROWN, Tags.Items.DYES)
        .register();

    public static final ItemEntry<MaterialItem> DYE_BLACK = materialItem("organic_black_dye")
        .tag(Tags.Items.DYES_BLACK, Tags.Items.DYES)
        .register();

    public static final ItemEntry<MaterialItem> DYE_INDUSTRIAL_BLEND = materialItem("industrial_dye_blend")
        .tag(Tags.Items.DYES)
        .register();

    public static final ItemEntry<MaterialItem> DYE_SOUL_ATTUNED_BLEND = materialItem("soul_attuned_dye_blend")
        .tag(Tags.Items.DYES)
        .register();

    public static final ItemEntry<MaterialItem> DYE_ENHANCED_BLEND = materialItem("enhanced_dye_blend")
        .tag(Tags.Items.DYES)
        .register();

    // endregion

    // region Misc Materials

    public static ItemEntry<MaterialItem> INFINITY_ROD = materialItem("infinity_rod").register();

    public static ItemEntry<MaterialItem> PHOTOVOLTAIC_PLATE = materialItem("photovoltaic_plate")
        .model((ctx, prov) -> prov
            .withExistingParent(prov.name(ctx), prov.mcLoc("block/pressure_plate_up"))
            .texture("texture", prov.itemTexture(ctx)))
        .register();

    public static ItemEntry<MaterialItem> NUTRITIOUS_STICK = materialItem("nutritious_stick").register();

    public static ItemEntry<MaterialItem> PLANT_MATTER_GREEN = materialItem("plant_matter_green")
        .lang("Clippings and Trimmings")
        .register();

    public static ItemEntry<MaterialItem> PLANT_MATTER_BROWN = materialItem("plant_matter_brown")
        .lang("Twigs and Prunings")
        .register();

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

    //  public static final ItemEntry<ItemBrokenSpawner> BROKEN_SPAWNER = REGISTRATE.item("broken_spawner", ItemBrokenSpawner::new)
    //      .model(ItemModelUtils::fakeBlockModel)
    //      .group(new NonNullLazyValue<>(() -> EnderIOGroups.Materials)).register();

    // endregion

    // region Builders

    private static ItemBuilder<MaterialItem, Registrate> materialItem(String name) {
        return REGISTRATE
            .item(name, props -> new MaterialItem(props, false))
            .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.MATERIALS));
    }

    //  private static ItemBuilder<MaterialItem, Registrate> dependMaterialItem(String name, Tag<Item> dependency) {
    //    return REGISTRATE.item(name, props -> new MaterialItem(props, false, dependency))
    //        .group(new NonNullLazyValue<>(() -> EIOCreativeTab.MATERIALS));
    //  }

    private static ItemBuilder<MaterialItem, Registrate> materialItemGlinted(String name) {
        return REGISTRATE
            .item(name, props -> new MaterialItem(props, true))
            .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.MATERIALS));
    }

    // endregion

    // region Creative Tab Icons

    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_NONE = REGISTRATE
        .item("enderface_none", EnderfaceItem::new)
        .register();

    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_ITEMS = REGISTRATE
        .item("enderface_items", EnderfaceItem::new)
        .register();

    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_MATERIALS = REGISTRATE
        .item("enderface_materials", EnderfaceItem::new)
        .register();

    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_MACHINES = REGISTRATE
        .item("enderface_machines", EnderfaceItem::new)
        .register();

    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_CONDUITS = REGISTRATE
        .item("enderface_conduits", EnderfaceItem::new)
        .register();

    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_MOBS = REGISTRATE
        .item("enderface_mobs", EnderfaceItem::new)
        .register();

    public static ItemEntry<EnderfaceItem> CREATIVE_ICON_INVPANEL = REGISTRATE
        .item("enderface_invpanel", EnderfaceItem::new)
        .register();

    // endregion

    public static void register() {}
}
