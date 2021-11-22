package com.enderio.core.common.util;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
public final class Vector2i {
    private final int x;
    private final int y;

    public static final Vector2i MIN = new Vector2i(Integer.MIN_VALUE, Integer.MIN_VALUE);
    public static final Vector2i MAX = new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2i add(Vector2i other) {
        return add(other.x, other.y);
    }

    public Vector2i add(int x, int y) {
        return new Vector2i(this.x + x, this.y +y);
    }

    public Vector2i expand(int amount) {
        return add(amount, amount);
    }

    public Vector2i withX(int x) {
        return new Vector2i(x, this.y);
    }

    public Vector2i withY(int y) {
        return new Vector2i(this.x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Vector2i{" + "x=" + x + ", y=" + y + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof Vector2i other) {
            return x == other.x && y == other.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
