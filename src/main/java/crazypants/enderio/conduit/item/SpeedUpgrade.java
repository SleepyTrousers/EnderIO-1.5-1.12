package crazypants.enderio.conduit.item;

public enum SpeedUpgrade {
    UPGRADE("enderio:extractSpeedUpgrade", "item.itemExtractSpeedUpgrade", 15) {
        @Override
        public int getMaximumExtracted(int stackSize) {
            return BASE_MAX_EXTRACTED + Math.min(stackSize, maxStackSize) * 4;
        }
    },
    DOWNGRADE("enderio:extractSpeedDowngrade", "item.itemExtractSpeedDowngrade", 1) {
        @Override
        public int getMaximumExtracted(int stackSize) {
            return 1;
        }
    };

    public static final int BASE_MAX_EXTRACTED = 4;

    public final String iconName;
    public final String unlocName;
    public final int maxStackSize;

    private SpeedUpgrade(String iconName, String unlocName, int maxStackSize) {
        this.iconName = iconName;
        this.unlocName = unlocName;
        this.maxStackSize = maxStackSize;
    }

    public abstract int getMaximumExtracted(int stackSize);
}
