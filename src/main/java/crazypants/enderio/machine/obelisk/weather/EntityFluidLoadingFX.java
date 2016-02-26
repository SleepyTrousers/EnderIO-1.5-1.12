package crazypants.enderio.machine.obelisk.weather;

import java.awt.Color;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityFluidLoadingFX extends EntityDropParticleFX {

  public static final int MAX_LIFE = 10;
  
  public EntityFluidLoadingFX(World world, double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
    super(world, x1, y1, z1, Material.water);
    this.motionX = (x2 - x1) / MAX_LIFE;
    this.motionY = (y2 - y1) / MAX_LIFE;
    this.motionZ = (z2 - z1) / MAX_LIFE;
    
    particleRed = color.getRed() / 256f;
    particleGreen = color.getGreen() / 256f;
    particleBlue = color.getBlue() / 256f;
    
    particleGravity = 0;

    noClip = true;
  }
  
  @Override
  public void onUpdate() {
    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;
    
    this.moveEntity(motionX, motionY, motionZ);
    
    if(particleAge++ >= MAX_LIFE) {
      setDead();
    }
  }

  @Override
  public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ,
      float rotationXY, float rotationXZ) {
    if(particleAge >= 2) {
      super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }
  }
}
