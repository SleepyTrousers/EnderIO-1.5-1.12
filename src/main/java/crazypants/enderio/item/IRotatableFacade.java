package crazypants.enderio.item;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IRotatableFacade {
    boolean tryRotateFacade(World world, int x, int y, int z, ForgeDirection axis);
}
