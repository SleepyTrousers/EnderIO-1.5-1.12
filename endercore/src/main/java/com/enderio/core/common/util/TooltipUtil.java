package com.enderio.core.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

public class TooltipUtil {
    public static Component style(MutableComponent component) {
        return component.withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
    }

    public static void showShiftHint(List<Component> pTooltipComponents) {
        if (!Minecraft.getInstance().options.keyShift.isDown()) {
            pTooltipComponents.add(new TextComponent("<Hold Shift>"));
        }
    }

    public static boolean showExtended() {
        // TODO: This isn't working as expected...
        return Minecraft.getInstance().options.keyShift.isDown();
    }
}
