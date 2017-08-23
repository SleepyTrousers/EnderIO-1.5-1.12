package com.jaquadro.minecraft.storagedrawers.api.pack;

public enum BlockConfiguration
{
    BasicFull1(BlockType.Drawers, 1, false),
    BasicFull2(BlockType.Drawers, 2, false),
    BasicFull4(BlockType.Drawers, 4, false),
    BasicHalf2(BlockType.Drawers, 2, true),
    BasicHalf4(BlockType.Drawers, 4, true),

    SortingFull1(BlockType.DrawersSorting, 1, false),
    SortingFull2(BlockType.DrawersSorting, 2, false),
    SortingFull4(BlockType.DrawersSorting, 4, false),
    SortingHalf2(BlockType.DrawersSorting, 2, true),
    SortingHalf4(BlockType.DrawersSorting, 4, true),

    Trim(BlockType.Trim, 0, false);

    private final BlockType type;
    private final int drawerCount;
    private final boolean halfDepth;

    BlockConfiguration (BlockType type, int count, boolean half) {
        this.type = type;
        this.drawerCount = count;
            this.halfDepth = half;
    }

    public BlockType getBlockType () {
        return type;
    }

    public int getDrawerCount () {
        return drawerCount;
    }

    public boolean isHalfDepth () {
        return halfDepth;
    }

    public static BlockConfiguration by (BlockType type, int drawerCount, boolean halfDepth) {
        for (BlockConfiguration config : values()) {
            if (config.type == type && config.drawerCount == drawerCount && config.halfDepth == halfDepth)
                return config;
        }

        return null;
    }
}
