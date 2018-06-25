package crazypants.enderio.zoo.entity.render;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.entity.EntityDireSlime;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDireSlime extends RenderLiving<EntityDireSlime> {

  public static final Factory FACTORY = new Factory();

  private static final ResourceLocation magmaCubeTextures = new ResourceLocation(EnderIOZoo.DOMAIN, "entity/direslime.png");

  public RenderDireSlime(RenderManager p_i46186_1_) {
    super(p_i46186_1_, new ModelDireSlime(), 0.25F);
  }

  @Override
  protected void preRenderCallback(@Nonnull EntityDireSlime direSlime, float partialTick) {
    int i = direSlime.getSlimeSize();
    float f1 = (direSlime.prevSquishFactor + (direSlime.squishFactor - direSlime.prevSquishFactor) * partialTick) / (i * 0.5F + 1.0F);
    float f2 = 1.0F / (f1 + 1.0F);
    float f3 = i;
    GL11.glScalef(f2 * f3, 1.0F / f2 * f3, f2 * f3);
  }

  @Override
  protected ResourceLocation getEntityTexture(@Nonnull EntityDireSlime p_110775_1_) {
    return magmaCubeTextures;
  }

  @Override
  protected void applyRotations(@Nonnull EntityDireSlime p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
    // this.rotateCorpse(entityLiving, p_77043_2_, p_77043_3_, partialTicks);
    if (p_77043_1_.deathTime > 0) {
      float f3 = (p_77043_1_.deathTime + p_77043_4_ - 1.0F) / 20.0F * 1.6F;
      f3 = Math.max(MathHelper.sqrt(f3), 1.0F);
      GL11.glRotatef(f3 * this.getDeathMaxRotation(p_77043_1_), 0.0F, 0.0F, 1.0F);
    }
  }

  public static class Factory implements IRenderFactory<EntityDireSlime> {

    @Override
    public Render<? super EntityDireSlime> createRenderFor(RenderManager manager) {
      return new RenderDireSlime(manager);
    }
  }

}
