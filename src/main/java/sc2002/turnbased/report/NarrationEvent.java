package sc2002.turnbased.report;

import java.util.Objects;

public record NarrationEvent(String text) implements BattleEvent {
    public NarrationEvent {
        text = Objects.requireNonNull(text, "text");
    }

    @Override
    public <T> T visit(Visitor<T> visitor) {
        return visitor.onNarration(this);
    }

    public String getText() {
        return text;
    }
}
