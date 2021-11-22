package com.enderio.core.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TooltipUtil {
    public static Component style(MutableComponent component) {
        return component.withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
    }
}
