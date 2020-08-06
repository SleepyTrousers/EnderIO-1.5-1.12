package crazypants.enderio.machines.machine.obelisk.attractor.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class SlimeAttractTask extends AttractTask {

  private final Method setDirection;

  public SlimeAttractTask(@Nonnull EntitySlime slime, @Nonnull FakePlayer target, @Nonnull BlockPos coord) {
    super(slime, target, coord);
    setDirection = ObfuscationReflectionHelper.findMethod(slime.getMoveHelper().getClass(), "func_179920_a", void.class, float.class, boolean.class);
  }

  @Override
  public void doUpdateTask() {
    float old = mob.rotationYaw;
    mob.faceEntity(target, 10.0F, 20.0F);
    try {
      setDirection.invoke(mob.getMoveHelper(), mob.rotationYaw, false);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      // e.printStackTrace();
    }
    mob.rotationYaw = old;
  }

}
