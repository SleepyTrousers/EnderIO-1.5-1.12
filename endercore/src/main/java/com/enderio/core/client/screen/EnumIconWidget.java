package com.enderio.core.client.screen;

import com.enderio.core.common.util.Vector2i;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumIconWidget<T extends Enum<T> & IIcon> extends AbstractWidget implements IFullScreenListener, IMultiWidget {

    private Supplier<T> getter;
    private Consumer<T> setter;

    private Map<T, SelectionWidget> icons = new HashMap<>();

    private static final int ELEMENTS_IN_ROW = 5;
    private static final int SPACE_BETWEEN_ELEMENTS = 3;

    private boolean isExpanded = false;

    private int mouseButton = 0;

    private final EIOScreen addedOn;

    public EnumIconWidget(EIOScreen addedOn, int pX, int pY, Supplier<T> getter, Consumer<T> setter) {
        super(pX, pY, getter.get().getIconSize().getX(), getter.get().getIconSize().getY(), TextComponent.EMPTY);
        this.getter = getter;
        this.setter = setter;
        T[] values = (T[])(getter.get().getClass().getEnumConstants());
        for (int i = 0; i < values.length; i++) {
            T value = values[i];
            Vector2i pos = calculatePosition(value, values.length, i);
            SelectionWidget widget = new SelectionWidget(pos.getX() + pX + value.getIconSize().getX() / 2 + 5, pos.getY() + pY, value);
            widget.visible = false;
            icons.put(value, widget);
        }
        this.addedOn = addedOn;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        mouseButton = pButton;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected boolean isValidClickButton(int pButton) {
        return pButton == InputConstants.MOUSE_BUTTON_LEFT || pButton == InputConstants.MOUSE_BUTTON_RIGHT;
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        if (isExpanded) {
            selectNext(mouseButton != InputConstants.MOUSE_BUTTON_RIGHT);
        } else {
            isExpanded = true;
            icons.values().forEach(icon -> icon.visible = true);
        }
    }

    private void selectNext(boolean isForward) {
        T[] values = (T[])(getter.get().getClass().getEnumConstants());
        int index = getter.get().ordinal() + (isForward ? 1 : -1) + values.length;
        setter.accept(values[index%values.length]);
    }

    private Vector2i calculatePosition(T icon, int amount, int index) {
        int maxColumns = Math.min(amount, ELEMENTS_IN_ROW);
        int column = getColumn(index);
        int row = getRow(index);
        int maxWidth = maxColumns * (icon.getIconSize().getX() + 2) + (maxColumns - 1) * SPACE_BETWEEN_ELEMENTS;
        int x = -maxWidth / 2 + column * (icon.getIconSize().getX() + 2) + (column - 1) * SPACE_BETWEEN_ELEMENTS;
        int y = icon.getIconSize().getY() + 5 + SPACE_BETWEEN_ELEMENTS + row * (icon.getIconSize().getY() + 2) + (row - 1) * SPACE_BETWEEN_ELEMENTS;
        return new Vector2i(x, y);
    }

    private static int getColumn(int index) {
        return index%ELEMENTS_IN_ROW;
    }

    private static int getRow(int index) {
        return index / ELEMENTS_IN_ROW;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks) {
        T icon = getter.get();

        if (isExpanded) {
            int minLeft = Integer.MAX_VALUE;
            int minTop = Integer.MAX_VALUE;
            int maxLeft = Integer.MIN_VALUE;
            int maxTop = Integer.MIN_VALUE;
            for (SelectionWidget value : icons.values()) {
                minLeft = Math.min(minLeft, value.x);
                minTop = Math.min(minTop, value.y);
                maxLeft = Math.max(maxLeft, value.x + value.getWidth());
                maxTop = Math.max(maxTop, value.y + value.getHeight());
            }
            addedOn.renderSimpleArea(pPoseStack, minLeft - 3, minTop - 3, maxLeft + 3, maxTop + 3);
        }
        addedOn.renderIconBackground(pPoseStack, x, y, icon);
        addedOn.renderIcon(pPoseStack, x + 1, y + 1, icon);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

    @Override
    public void onGlobalClick(double mouseX, double mouseY) {
        //icons.values().forEach(icon -> icon.visible = false);
    }

    @Override
    public Collection<? extends AbstractWidget> getOtherWidgets() {
        return icons.values();
    }

    private class SelectionWidget extends AbstractWidget {

        private final T value;

        public SelectionWidget(int pX, int pY, T value) {
            super(pX, pY, value.getIconSize().getX() + 2, value.getIconSize().getY() + 2, value.getTooltip());
            this.value = value;
        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            super.onClick(pMouseX, pMouseY);
            setter.accept(value);
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks) {
            if (getter.get() != value) {
                addedOn.renderIconBackground(pPoseStack, x, y, value);
            } else {
                addedOn.fill(pPoseStack, x, y, x + width, y + height, 0xFF0020FF); //TODO: Client Config
                addedOn.fill(pPoseStack, x + 1, y + 1, x + width - 1, y + height - 1, 0xFF8B8B8B);
            }
            if (isMouseOver(pMouseX, pMouseY)) {
                Component tooltip = value.getTooltip();
                if (tooltip != TextComponent.EMPTY) {
                    addedOn.renderTooltip(pPoseStack, tooltip, pMouseX, pMouseY);
                }
            }
            addedOn.renderIcon(pPoseStack, x + 1, y + 1, value);
        }
    }
}
