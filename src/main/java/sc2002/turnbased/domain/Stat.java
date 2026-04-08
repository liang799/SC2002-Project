package sc2002.turnbased.domain;

import java.util.Objects;
import java.util.function.IntUnaryOperator;

public record Stat(int value) {
    public Stat {
        if (value < 0) {
            throw new IllegalArgumentException("value cannot be negative");
        }
    }

    public Stat addFlat(int amount) {
        return modify(currentValue -> Math.addExact(currentValue, amount));
    }

    public Stat multiplyBy(double factor) {
        if (!Double.isFinite(factor) || factor < 0) {
            throw new IllegalArgumentException("factor cannot be negative");
        }
        return modify(currentValue -> Math.toIntExact(Math.round(currentValue * factor)));
    }

    public Stat clampMinimum(int minimum) {
        if (minimum < 0) {
            throw new IllegalArgumentException("minimum cannot be negative");
        }
        return modify(currentValue -> Math.max(currentValue, minimum));
    }

    public Stat modify(IntUnaryOperator modifier) {
        Objects.requireNonNull(modifier, "modifier");
        return new Stat(modifier.applyAsInt(value));
    }

    public Stat adjustBy(int amount) {
        return addFlat(amount);
    }
}
