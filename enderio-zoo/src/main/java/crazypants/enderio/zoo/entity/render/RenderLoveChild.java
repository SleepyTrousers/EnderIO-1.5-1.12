package crazypants.enderio.zoo.entity.render;

import javax.annotation.Nonnull;

import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.entity.EntityLoveChild;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLoveChild extends RenderBiped<EntityLoveChild> {

  public static final Factory FACTORY = new Factory();

  private static final @Nonnull ResourceLocation LOVELY_TEXTURES = new ResourceLocation(EnderIOZoo.DOMAIN, "entity/lovechild.png");

  public RenderLoveChild(RenderManager renderManagerIn) {
    super(renderManagerIn, new ModelZombie(), 0.5F);
    LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
      @Override
      protected void initArmor() {
        this.modelLeggings = new ModelZombie(0.5F, true);
        this.modelArmor = new ModelZombie(1.0F, true);
      }
    };
    this.addLayer(layerbipedarmor);
  }

  @Override
  protected @Nonnull ResourceLocation getEntityTexture(@Nonnull EntityLoveChild entity) {
    return LOVELY_TEXTURES;
  }

  public static class Factory implements IRenderFactory<EntityLoveChild> {

    @Override
    public Render<? super EntityLoveChild> createRenderFor(RenderManager manager) {
      return new RenderLoveChild(manager);
    }
  }

}
