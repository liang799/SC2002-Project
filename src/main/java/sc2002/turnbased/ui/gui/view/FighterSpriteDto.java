package sc2002.turnbased.ui.gui.view;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Objects;

import sc2002.turnbased.domain.Combatant;
import sc2002.turnbased.domain.CombatantId;
import sc2002.turnbased.report.CombatantSummary;

/**
 * Mutable DTO for arena rendering state. Battle rules stay in the engine/domain layer.
 */
public final class FighterSpriteDto {
    public final CombatantId id;
    public boolean player;
    public FighterType type;
    public String name;
    public int hp;
    public int maxHp;
    public int attack;
    public int baseAttack;
    public boolean alive;
    public List<String> statuses;
    public double x;
    public double y;
    public double offsetX;
    public double offsetY;
    public double walkPhase;
    public long hurtUntil;
    public long pulseUntil;

    private FighterSpriteDto(
        CombatantId id,
        String name,
        boolean player,
        int hp,
        int maxHp,
        int attack,
        int baseAttack,
        boolean alive,
        List<String> statuses
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.player = player;
        this.type = FighterType.fromName(name, player);
        this.hp = hp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.baseAttack = baseAttack;
        this.alive = alive;
        this.statuses = List.copyOf(statuses);
    }

    public static FighterSpriteDto fromCombatant(Combatant combatant, boolean player) {
        Objects.requireNonNull(combatant, "combatant");
        return new FighterSpriteDto(
            combatant.combatantId(),
            combatant.getName(),
            player,
            combatant.getCurrentHp(),
            combatant.getMaxHp(),
            combatant.getAttack(),
            combatant.getBaseAttack(),
            combatant.isAlive(),
            combatant.getActiveStatuses()
        );
    }

    public static FighterSpriteDto fromSummary(CombatantSummary summary, boolean player) {
        Objects.requireNonNull(summary, "summary");
        return new FighterSpriteDto(
            summary.getCombatantId(),
            summary.getName(),
            player,
            summary.getCurrentHp(),
            summary.getMaxHp(),
            summary.getCurrentAttack(),
            summary.getBaseAttack(),
            summary.isAlive(),
            summary.getActiveStatuses()
        );
    }

    public void updateFrom(Combatant combatant) {
        Objects.requireNonNull(combatant, "combatant");
        ensureSameId(combatant.combatantId());
        name = combatant.getName();
        hp = combatant.getCurrentHp();
        maxHp = combatant.getMaxHp();
        attack = combatant.getAttack();
        baseAttack = combatant.getBaseAttack();
        alive = combatant.isAlive();
        statuses = List.copyOf(combatant.getActiveStatuses());
        type = FighterType.fromName(name, player);
    }

    public void updateFrom(CombatantSummary summary) {
        Objects.requireNonNull(summary, "summary");
        ensureSameId(summary.getCombatantId());
        name = summary.getName();
        hp = summary.getCurrentHp();
        maxHp = summary.getMaxHp();
        attack = summary.getCurrentAttack();
        baseAttack = summary.getBaseAttack();
        alive = summary.isAlive();
        statuses = List.copyOf(summary.getActiveStatuses());
        type = FighterType.fromName(name, player);
    }

    public void setPlayer(boolean player) {
        this.player = player;
        type = FighterType.fromName(name, player);
    }

    public double drawX() {
        return x + offsetX;
    }

    public double drawY() {
        return y + offsetY;
    }

    public Shape bounds() {
        return new Rectangle2D.Double(drawX() - 54, drawY() - 116, 108, 124);
    }

    private void ensureSameId(CombatantId sourceId) {
        if (!id.equals(sourceId)) {
            throw new IllegalArgumentException("Cannot update sprite " + id + " from combatant " + sourceId);
        }
    }
}
