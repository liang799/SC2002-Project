package sc2002.turnbased.domain.status;

import sc2002.turnbased.domain.CombatStats;

public interface StatModifierEffect {
    CombatStats modifyStats(CombatStats stats);
}
