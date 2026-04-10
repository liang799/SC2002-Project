package sc2002.turnbased.engine;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.support.TestCombatantBuilder;

@Tag("unit")
class TargetReferenceTest {
    @Test
    void resolveFrom_WhenCombatantsShareTheSameName_UsesCombatantIdInsteadOfName() {
        Combatant firstGoblin = TestCombatantBuilder.aCombatant()
            .named("Goblin")
            .build();
        Combatant secondGoblin = TestCombatantBuilder.aCombatant()
            .named("Goblin")
            .build();

        Combatant resolvedTarget = TargetReference.enemy(secondGoblin).resolveFrom(List.of(firstGoblin, secondGoblin));

        assertSame(secondGoblin, resolvedTarget);
    }
}
