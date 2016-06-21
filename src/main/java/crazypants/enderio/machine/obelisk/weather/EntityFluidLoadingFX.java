package crazypants.enderio.machine.obelisk.weather;

import java.awt.Color;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

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
    
    if (particleAge++ >= MAX_LIFE) {
      setDead();
    }
  }
  
  @Override
  public void renderParticle(Tessellator p_70539_1_, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
    if (particleAge >= 2) {
      super.renderParticle(p_70539_1_, p_70539_2_, p_70539_3_, p_70539_4_, p_70539_5_, p_70539_6_, p_70539_7_);
    }
  }

}
