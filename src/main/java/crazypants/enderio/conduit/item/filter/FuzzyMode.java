package crazypants.enderio.conduit.item.filter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CycleButton;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;

public enum FuzzyMode implements CycleButton.ICycleEnum {

    DISABLED(IconEIO.FILTER_FUZZY_DISABLED) {

        @Override
        boolean compare(ItemStack stack) {
            return false;
        }
    },
    FUZZY_25(IconEIO.FILTER_FUZZY_25) {

        @Override
        boolean compare(ItemStack stack) {
            return compareQuarter(stack, 1);
        }
    },
    FUZZY_50(IconEIO.FILTER_FUZZY_50) {

        @Override
        boolean compare(ItemStack stack) {
            return compareQuarter(stack, 2);
        }
    },
    FUZZY_75(IconEIO.FILTER_FUZZY_75) {

        @Override
        boolean compare(ItemStack stack) {
            return compareQuarter(stack, 3);
        }
    },
    FUZZY_99(IconEIO.FILTER_FUZZY_99) {

        @Override
        boolean compare(ItemStack stack) {
            return stack.getItemDamageForDisplay() >= 1;
        }
    };

    final IWidgetIcon icon;

    private FuzzyMode(IWidgetIcon icon) {
        this.icon = icon;
    }

    abstract boolean compare(ItemStack stack);

    private static boolean compareQuarter(ItemStack stack, int ratio) {
        return stack.getItemDamageForDisplay() * 4 >= stack.getMaxDamage() * ratio;
    }

    @Override
    public IWidgetIcon getIcon() {
        return icon;
    }

    @Override
    public List<String> getTooltipLines() {
        return Arrays.asList(
                EnderIO.lang.localize("gui.conduit.item.fuzzy.".concat(name().toLowerCase(Locale.US))),
                EnumChatFormatting.GRAY + EnderIO.lang.localize("gui.conduit.item.fuzzy.tooltip.0"),
                EnumChatFormatting.GRAY + EnderIO.lang.localize("gui.conduit.item.fuzzy.tooltip.1"),
                EnumChatFormatting.GRAY + EnderIO.lang.localize(
                        "gui.conduit.item.fuzzy.tooltip.2",
                        EnderIO.lang.localize("gui.conduit.item.whitelist")));
    }
}
