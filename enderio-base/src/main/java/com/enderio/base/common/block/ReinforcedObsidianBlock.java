package com.enderio.base.common.block;

import com.enderio.base.config.base.BaseConfig;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class ReinforcedObsidianBlock extends Block {

    private static final int[] COLS = { 0x3c3056, 0x241e31, 0x1e182b, 0x0e0e15, 0x07070b };

    private static final Random RAND = new Random();

    public ReinforcedObsidianBlock(Properties props) {
        super(props);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRandom) {
        if (BaseConfig.CLIENT.MACHINE_PARTICLES.get() && pRandom.nextFloat() < .25f) {
            Direction face = Direction.getRandom(RAND);
            BlockPos spawnPos = pPos.relative(face, 1);
            if (!pLevel.getBlockState(spawnPos).isSolidRender(pLevel, spawnPos)) {

                double xd = face.getStepX() == 0 ? RAND.nextDouble() : face.getStepX() < 0 ? -0.05 : 1.05;
                double yd = face.getStepY() == 0 ? RAND.nextDouble() : face.getStepY() < 0 ? -0.05 : 1.05;
                double zd = face.getStepZ() == 0 ? RAND.nextDouble() : face.getStepZ() < 0 ? -0.05 : 1.05;

                double x = pPos.getX() + xd;
                double y = pPos.getY() + yd;
                double z = pPos.getZ() + zd;

                int col = COLS[RAND.nextInt(COLS.length)];
                pLevel.addParticle(new DustParticleOptions(new Vector3f(Vec3.fromRGB24(col)), 1.0F), x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean canDropFromExplosion(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
        return false;
    }

}
