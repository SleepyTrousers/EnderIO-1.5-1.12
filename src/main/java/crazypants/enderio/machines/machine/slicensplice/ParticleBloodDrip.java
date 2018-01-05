package crazypants.enderio.machines.machine.slicensplice;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ParticleBloodDrip extends Particle {

  private final EnumFacing facing;
  private final double offset;

  private int slowTime = 40;

  protected ParticleBloodDrip(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, EnumFacing facing) {
    super(worldIn, xCoordIn, yCoordIn, zCoordIn);
    this.facing = facing;
    this.offset = (rand.nextDouble() * 0.1f) + 1;
    this.motionX = 0.0D;
    this.motionY = 0.0D;
    this.motionZ = 0.0D;
    this.setParticleTextureIndex(113);
    this.setSize(0.01F, 0.01F);
    // MC-12269 - fix particle being offset to the NW
    this.setPosition(xCoordIn, yCoordIn, zCoordIn);
    this.particleGravity = 0.06F;
    this.particleMaxAge = (int) (64.0D / (Math.random() * 0.8D + 0.2D));
    this.particleRed = 0.3f;
    this.particleGreen = this.particleBlue = 0;
    setScale();
  }

  private void setScale() {
    // Divide by 50 (where the max is 40) so that it doesn't start infinitely small
    this.particleScale = (1 - ((float) slowTime / 50)) * (1.5f * (float) offset);
  }

  @Override
  public void onUpdate() {

    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;

    this.motionY -= this.particleGravity;

    if (this.slowTime-- > 0) {
      this.motionX *= 0.02D;
      this.motionY *= 0.02D;
      this.motionZ *= 0.02D;
      setScale();
    }

    this.move(this.motionX, this.motionY, this.motionZ);
    this.motionX *= 0.98;
    this.motionY *= 0.98;
    this.motionZ *= 0.98;

    if (this.particleMaxAge-- <= 0) {
      this.setExpired();
    }

    if (this.onGround) {
      setExpired();
      Particle fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(),
          posX + (0.1 * facing.getDirectionVec().getX()), posY, posZ + (0.1 * facing.getDirectionVec().getZ()), 0, 0, 0);

      if (fx != null) {
        fx.setRBGColorF(1, 0, 0);
      }
      this.motionX *= 0.7;
      this.motionZ *= 0.7;
    }
  }

  @Override
  public void renderParticle(@Nonnull VertexBuffer buffer, @Nonnull Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ,
      float rotationXY, float rotationXZ) {
    // Move the particle against the face of the block, and counteract the scaling so that it grows from the center
    buffer.setTranslation(-0.08 * facing.getDirectionVec().getX() * offset, -particleScale * height, -0.08 * facing.getDirectionVec().getZ() * offset);
    // Lie about rotation so that it always renders facing outwards from the machine
    super.renderParticle(buffer, entityIn, partialTicks, facing.getDirectionVec().getZ(), 1, facing.getDirectionVec().getX(), 0, 0);
    buffer.setTranslation(0, 0, 0);
  }
}
