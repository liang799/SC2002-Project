package sc2002.turnbased.domain;

import java.util.Objects;
import java.util.function.UnaryOperator;

public record CombatStats(Stat attack, Stat defense, Stat speed) {
    public CombatStats {
        Objects.requireNonNull(attack, "attack");
        Objects.requireNonNull(defense, "defense");
        Objects.requireNonNull(speed, "speed");
    }

    public Stat stat(StatType statType) {
        return switch (Objects.requireNonNull(statType, "statType")) {
            case ATTACK -> attack;
            case DEFENSE -> defense;
            case SPEED -> speed;
        };
    }

    public int valueOf(StatType statType) {
        return stat(statType).value();
    }

    public static Builder builder() {
        return new Builder();
    }

    public CombatStats withStat(StatType type, Stat updatedStat) {
        Objects.requireNonNull(updatedStat, "updatedStat");
        return switch (Objects.requireNonNull(type, "type")) {
            case ATTACK -> new CombatStats(updatedStat, defense, speed);
            case DEFENSE -> new CombatStats(attack, updatedStat, speed);
            case SPEED -> new CombatStats(attack, defense, updatedStat);
        };
    }

    public CombatStats addFlat(StatType type, int amount) {
        return modify(type, stat -> stat.addFlat(amount));
    }

    public CombatStats multiplyBy(StatType type, double factor) {
        return modify(type, stat -> stat.multiplyBy(factor));
    }

    public CombatStats clampMinimum(StatType type, int minimum) {
        return modify(type, stat -> stat.clampMinimum(minimum));
    }

    public CombatStats modify(StatType type, UnaryOperator<Stat> modifier) {
        Objects.requireNonNull(modifier, "modifier");
        return withStat(type, modifier.apply(stat(type)));
    }

    public CombatStats apply(CombatStatsModifier modifier) {
        return Objects.requireNonNull(modifier, "modifier").applyTo(this);
    }

    public static final class Builder {
        private Stat attack;
        private Stat defense;
        private Stat speed;

        public Builder attack(int attack) {
            return attack(new Stat(attack));
        }

        public Builder attack(Stat attack) {
            this.attack = Objects.requireNonNull(attack, "attack");
            return this;
        }

        public Builder defense(int defense) {
            return defense(new Stat(defense));
        }

        public Builder defense(Stat defense) {
            this.defense = Objects.requireNonNull(defense, "defense");
            return this;
        }

        public Builder speed(int speed) {
            return speed(new Stat(speed));
        }

        public Builder speed(Stat speed) {
            this.speed = Objects.requireNonNull(speed, "speed");
            return this;
        }

        public CombatStats build() {
            return new CombatStats(attack, defense, speed);
        }
    }
}
