package crazypants.enderio.machine.painter.paint;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPaintRegister extends INBTSerializable<NBTBase> {

  void setPaintSource(BlockPos pos, @Nullable IBlockState paintSource);

  IBlockState getPaintSource(BlockPos pos);

  void tick(World world);

  void resetClient();

}