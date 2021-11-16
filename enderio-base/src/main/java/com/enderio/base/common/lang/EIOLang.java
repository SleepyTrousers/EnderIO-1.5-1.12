package com.enderio.base.common.lang;

import com.enderio.base.EnderIO;
import com.tterrag.registrate.Registrate;
import net.minecraft.network.chat.Component;

public class EIOLang {
    private static Registrate REGISTRATE = EnderIO.registrate();

    public static final Component COORDINATE_SELECTOR_NO_PAPER = REGISTRATE.addLang("info", EnderIO.loc("coordinate_selector.no_paper"), "No Paper in Inventory");
    public static final Component COORDINATE_SELECTOR_NO_BLOCK = REGISTRATE.addLang("info", EnderIO.loc("coordinate_selector.no_block"), "No Block in Range");
    public static final Component REDSTONE_ALWAYS_ACTIVE = REGISTRATE.addLang("gui", EnderIO.loc("redstone.always_active"), "Always active");
    public static final Component REDSTONE_ACTIVE_WITH_SIGNAL = REGISTRATE.addLang("gui", EnderIO.loc("redstone.active_with_signal"), "Active with signal");
    public static final Component REDSTONE_ACTIVE_WITHOUT_SIGNAL = REGISTRATE.addLang("gui", EnderIO.loc("redstone.active_without_signal"), "Active without signal");
    public static final Component REDSTONE_NEVER_ACTIVE = REGISTRATE.addLang("gui", EnderIO.loc("redstone.never_active"), "Never active");

    public static void register() {}
}
