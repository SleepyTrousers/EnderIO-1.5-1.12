package com.enderio.core.common.util;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import javax.annotation.concurrent.Immutable;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Immutable
public class Vector2i {
    private final int x;
    private final int y;
}
