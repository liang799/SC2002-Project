package sc2002.turnbased.domain;

import java.util.EnumMap;

public class CombatStatModifierRegistry {
    private final EnumMap<StatType, Integer> modifiers = new EnumMap<>(StatType.class);

    public int modifierFor(StatType statType) {
        return modifiers.getOrDefault(statType, 0);
    }

    public void adjust(StatType statType, int amount) {
        modifiers.put(statType, Math.addExact(modifierFor(statType), amount));
    }
}
