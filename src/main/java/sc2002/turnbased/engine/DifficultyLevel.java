package sc2002.turnbased.engine;

public enum DifficultyLevel {
    EASY("Easy", "3 Goblins", "None", 3, 0),
    MEDIUM("Medium", "1 Goblin + 1 Wolf", "2 Wolves", 2, 2),
    HARD("Hard", "2 Goblins", "1 Goblin + 2 Wolves", 2, 3);

    private final String displayName;
    private final String initialSpawnDescription;
    private final String backupSpawnDescription;
    private final int initialEnemyCount;
    private final int backupEnemyCount;

    DifficultyLevel(
        String displayName,
        String initialSpawnDescription,
        String backupSpawnDescription,
        int initialEnemyCount,
        int backupEnemyCount
    ) {
        this.displayName = displayName;
        this.initialSpawnDescription = initialSpawnDescription;
        this.backupSpawnDescription = backupSpawnDescription;
        this.initialEnemyCount = initialEnemyCount;
        this.backupEnemyCount = backupEnemyCount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getInitialSpawnDescription() {
        return initialSpawnDescription;
    }

    public String getBackupSpawnDescription() {
        return backupSpawnDescription;
    }

    public int getInitialEnemyCount() {
        return initialEnemyCount;
    }

    public int getBackupEnemyCount() {
        return backupEnemyCount;
    }

    public int getTotalEnemyCount() {
        return initialEnemyCount + backupEnemyCount;
    }
}
