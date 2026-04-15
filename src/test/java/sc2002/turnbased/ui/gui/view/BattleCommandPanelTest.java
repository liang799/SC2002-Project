package sc2002.turnbased.ui.gui.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

class BattleCommandPanelTest {
    @Test
    void escapeHtmlEscapesQuotesAndConvertsNewlines() throws Exception {
        assertEquals(
            "Use &quot;Item&quot;<br/>&lt;Potion&gt; &amp; wait",
            escapeHtml("Use \"Item\"\n<Potion> & wait")
        );
    }

    private static String escapeHtml(String text)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = BattleCommandPanel.class.getDeclaredMethod("escapeHtml", String.class);
        method.setAccessible(true);
        return (String) method.invoke(null, text);
    }
}
