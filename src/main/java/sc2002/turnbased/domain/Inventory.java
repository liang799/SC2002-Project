package sc2002.turnbased.domain;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class Inventory {
    private final EnumMap<ItemType, Integer> itemCounts = new EnumMap<>(ItemType.class);

    public void add(ItemType itemType, int count) {
        itemCounts.merge(itemType, count, Integer::sum);
    }

    public int countOf(ItemType itemType) {
        return itemCounts.getOrDefault(itemType, 0);
    }

    public void use(ItemType itemType) {
        int currentCount = countOf(itemType);
        if (currentCount <= 0) {
            throw new IllegalStateException("No item remaining: " + itemType.getDisplayName());
        }
        itemCounts.put(itemType, currentCount - 1);
    }

    public Map<ItemType, Integer> snapshot() {
        return Collections.unmodifiableMap(new EnumMap<>(itemCounts));
    }
}
