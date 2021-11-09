package com.enderio.machines.common.blockentity.data;

import java.util.function.UnaryOperator;

public enum RedstoneControl {
    ALWAYS_ACTIVE(bool -> true),
    ACTIVE_WITH_SIGNAL(bool -> bool),
    ACTIVE_WITHOUT_SIGNAL(bool -> !bool),
    NEVER_ACTIVE(bool -> false);
    UnaryOperator<Boolean> isActive;

    RedstoneControl(UnaryOperator<Boolean> isActive) {
        this.isActive = isActive;
    }

    public boolean isActive(boolean hasRedstone) {
        return isActive.apply(hasRedstone);
    }
}
