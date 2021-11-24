package com.enderio.core.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public class TooltipUtil {
    public static Component style(MutableComponent component) {
        return component.withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
    }

    public static void showShiftHint(List<Component> pTooltipComponents) {
        if (!showExtended()) {
            pTooltipComponents.add(new TranslatableComponent("tooltip.show_extended"));
        }
    }

    public static boolean showExtended() {
        return Screen.hasShiftDown();
    }
}
