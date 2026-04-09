package sc2002.turnbased.domain.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class DefaultStatusEffectRegistryFactoryTest {
    @Test
    void create_WhenSupplierReturnsNull_FailsFastWithClearMessage() {
        DefaultStatusEffectRegistryFactory factory = new DefaultStatusEffectRegistryFactory(() -> null);

        NullPointerException exception = assertThrows(NullPointerException.class, factory::create);

        assertEquals(
            "statusEffectRegistrySupplier returned null StatusEffectRegistry",
            exception.getMessage()
        );
    }
}
