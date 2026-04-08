package sc2002.turnbased.engine;

import sc2002.turnbased.domain.EnemyCombatant;

public interface EnemyFactory {
    String getDisplayName();

    String getPluralDisplayName();

    int getMaxPerWave();

    EnemyCombatant create(String name);

    default String formatCount(int count) {
        return count + " " + (count == 1 ? getDisplayName() : getPluralDisplayName());
    }
}
