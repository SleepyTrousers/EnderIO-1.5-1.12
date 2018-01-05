package crazypants.enderio.machines.machine.generator.zombie;

import net.minecraft.client.particle.ParticleBubble;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BubbleFX extends ParticleBubble {

  private final double yLimit;

  public BubbleFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
    super(world, x, y, z, motionX, motionY, motionZ);
    setRBGColorF(0.6f, 0.6f, 0.5f);
    yLimit = Math.floor(y) + 0.9;
  }

  @Override
  public void onUpdate() {
    prevPosX = posX;
    prevPosY = posY;
    prevPosZ = posZ;
    motionY += 0.002D;
    move(motionX, motionY, motionZ);
    motionX *= 0.8500000238418579D;
    motionY *= 0.8500000238418579D;
    motionZ *= 0.8500000238418579D;

    if(particleMaxAge-- <= 0 || posY > yLimit) {
      setExpired();
    }
  }

}
