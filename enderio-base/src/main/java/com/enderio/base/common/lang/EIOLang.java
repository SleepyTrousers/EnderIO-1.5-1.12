package com.enderio.base.common.lang;

import com.enderio.base.EnderIO;
import com.enderio.base.common.capability.capacitors.ICapacitorData;
import com.enderio.core.common.util.TooltipUtil;
import com.tterrag.registrate.Registrate;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EIOLang {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final Component BLOCK_BLAST_RESISTANT = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("block.blast_resistant"), "Blast resistant"));

    // region Fused Quartz

    public static final Component FUSED_QUARTZ_EMITS_LIGHT = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("fused_quartz.emits_light"), "Emits light"));
    public static final Component FUSED_QUARTZ_BLOCKS_LIGHT = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("fused_quartz.blocks_light"), "Blocks light"));

    public static final Component GLASS_COLLISION_PLAYERS_PASS = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("collision.players_pass"), "Not solid to players"));
    public static final Component GLASS_COLLISION_PLAYERS_BLOCK = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("collision.players_block"), "Only solid to players"));
    public static final Component GLASS_COLLISION_MOBS_PASS = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("collision.mobs_pass"), "Not solid to monsters"));
    public static final Component GLASS_COLLISION_MOBS_BLOCK = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("collision.mobs_block"), "Only solid to monsters"));
    public static final Component GLASS_COLLISION_ANIMALS_PASS = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("collision.animals_pass"), "Not solid to animals"));
    public static final Component GLASS_COLLISION_ANIMALS_BLOCK = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("collision.animals_block"), "Only solid to animals"));

    // endregion

    // region Items

    public static final Component DARK_STEEL_LADDER_FASTER = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("dark_steel_ladder.faster"), "Faster than regular ladders"));

    public static final Component SOUL_VIAL_ERROR_PLAYER = REGISTRATE.addLang("message", EnderIO.loc("soul_vial.error_player"), "You cannot put player in a bottle!");
    public static final Component SOUL_VIAL_ERROR_FAILED = REGISTRATE.addLang("message", EnderIO.loc("soul_vial.error_failed"), "This entity cannot be captured.");
    public static final Component SOUL_VIAL_ERROR_DEAD = REGISTRATE.addLang("message", EnderIO.loc("soul_vial.error_dead"), "Cannot capture a dead mob!");

    public static final Component COORDINATE_SELECTOR_NO_PAPER = REGISTRATE.addLang("info", EnderIO.loc("coordinate_selector.no_paper"), "No Paper in Inventory");
    public static final Component COORDINATE_SELECTOR_NO_BLOCK = REGISTRATE.addLang("info", EnderIO.loc("coordinate_selector.no_block"), "No Block in Range");
    public static final Component REDSTONE_ALWAYS_ACTIVE = REGISTRATE.addLang("gui", EnderIO.loc("redstone.always_active"), "Always active");
    public static final Component REDSTONE_ACTIVE_WITH_SIGNAL = REGISTRATE.addLang("gui", EnderIO.loc("redstone.active_with_signal"), "Active with signal");
    public static final Component REDSTONE_ACTIVE_WITHOUT_SIGNAL = REGISTRATE.addLang("gui", EnderIO.loc("redstone.active_without_signal"), "Active without signal");
    public static final Component REDSTONE_NEVER_ACTIVE = REGISTRATE.addLang("gui", EnderIO.loc("redstone.never_active"), "Never active");

    public static final TranslatableComponent ENERGY_AMOUNT = REGISTRATE.addLang("info", EnderIO.loc("energy.amount"), "%s \u00B5I");

    // endregion

    // region Upgrades

    public static final Component DS_UPGRADE_ITEM_NO_XP = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.no_xp"), "Not enough XP");
    public static final Component DS_UPGRADE_AVAILABLE = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.available"), "Available Upgrades");
    public static final TranslatableComponent DS_UPGRADE_XP_COST = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.cost"), "Costs %s Levels");
    public static final Component DS_UPGRADE_ACTIVATE = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.activate"), "Right Click to Activate");
    public static final Component DS_UPGRADE_AVAILABLE = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.available"), "Available Upgrades").withStyle(
        ChatFormatting.YELLOW);

    public static final Component DS_UPGRADE_EMPOWERED = upgradeNameBuilder("empowered", "Empowered");
    public static final Component DS_UPGRADE_SPOON = upgradeNameBuilder("spoon", "Spoon");
    public static final Component DS_UPGRADE_FORK = upgradeNameBuilder("fork", "Fork");
    public static final Component DS_UPGRADE_DIRECT = upgradeNameBuilder("direct", "Direct");

    private static Component upgradeNameBuilder(String upgrade, String name) {
        return REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade." + upgrade), name).withStyle(ChatFormatting.DARK_AQUA);
    }

    // endregion

    // region Capacitors

    public static final Component CAPACITOR_ALL_ENERGY_CONSUMPSTION = capacitorDescriptionBuilder("type", ICapacitorData.ALL_ENERGY_CONSUMPSTION, "Leaky");
    public static final Component CAPACITOR_ALL_PRODUCTION_SPEED = capacitorDescriptionBuilder("type", ICapacitorData.ALL_PRODUCTION_SPEED, "Fast");
    public static final Component CAPACITOR_ALLOY_ENERGY_CONSUMPSTION = capacitorDescriptionBuilder("type", ICapacitorData.ALLOY_ENERGY_CONSUMPSTION,
        "Melted");
    public static final Component CAPACITOR_ALLOY_PRODUCTION_SPEED = capacitorDescriptionBuilder("type", ICapacitorData.ALLOY_PRODUCTION_SPEED, "Smelting");

    public static final Component CAPACITOR_DUD = capacitorDescriptionBuilder("base", "0", "Capacitor Dud");
    public static final Component CAPACITOR_GOOD = capacitorDescriptionBuilder("base", "1", "Good Capacitor");
    public static final Component CAPACITOR_ENHANCED = capacitorDescriptionBuilder("base", "2", "Enhanced Capacitor");
    public static final Component CAPACITOR_WONDER = capacitorDescriptionBuilder("base", "3", "Wonder Capacitor");

    public static final Component CAPACITOR_FLAVOR0 = capacitorDescriptionBuilder("flavor", "0", "An attached note describes this as \"%1$s %2$s %3$s\"");
    public static final Component CAPACITOR_FLAVOR1 = capacitorDescriptionBuilder("flavor", "1",
        "You can decipher ancient runes that translate roughly as \"%1$s %2$s %3$s\". Odd...");

    public static final Component CAPACITOR_FAILED = capacitorDescriptionBuilder("grade", "0", "Failed");
    public static final Component CAPACITOR_INCREDIBLY = capacitorDescriptionBuilder("grade", "4", "Incredibly");
    public static final Component CAPACITOR_UNSTABLE = capacitorDescriptionBuilder("grade", "5", "Unstable");

    public static Component capacitorDescriptionBuilder(String type, String value, String description) {
        return REGISTRATE.addLang("description", new ResourceLocation(EnderIO.DOMAIN, "capacitor." + type + "." + value), description);
    }

    // endregion

    public static void register() {}
}
