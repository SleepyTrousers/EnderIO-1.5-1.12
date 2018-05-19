package crazypants.enderio.zoo.entity.render;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.zoo.entity.EntityDireWolf;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderDirewolf extends RenderLiving<EntityDireWolf> {

  public static final Factory FACTORY = new Factory();
  
  private ResourceLocation wolfTextures = new ResourceLocation("enderzoo:entity/dire_wolf.png");

  private int debugCounter = 0;

  public RenderDirewolf(RenderManager rm) {
    super(rm, new ModelDireWolf(), 0.5f);
  }
    
  protected float handleRotationFloat(EntityDireWolf wolf, float p_77044_2_) {
    return wolf.getTailRotation();
  }

  @Override
  protected void preRenderCallback(EntityDireWolf entity, float partialTick) {

    if (debugCounter == 4) {
      System.out.println("RenderDirewolf.preRenderCallback: ");
      mainModel = new ModelDireWolf();
      debugCounter++;
    }

    float scale = 1.25f;
    GL11.glPushMatrix();
    GL11.glTranslatef(0.1f, 0, 0);
    GL11.glScalef(scale - 0.1f, scale, scale);
  }

  //  protected int shouldRenderPass(EntityDirewolf wolf, int pass, float p_77032_3_) {
  //    if(pass == 0 && wolf.getWolfShaking()) {
  //      float f1 = wolf.getBrightness(p_77032_3_) * wolf.getShadingWhileShaking(p_77032_3_);
  //      bindTexture(wolfTextures);
  //      GL11.glColor3f(f1, f1, f1);
  //      return 1;
  //    } else if(pass == 1 && wolf.isTamed()) {
  //      bindTexture(wolfCollarTextures);
  //      int j = wolf.getCollarColor();
  //      GL11.glColor3f(EntitySheep.fleeceColorTable[j][0], EntitySheep.fleeceColorTable[j][1], EntitySheep.fleeceColorTable[j][2]);
  //      return 1;
  //      return -1;
  //    } else {
  //      return -1;
  //    }
  //  }


  @Override
  public void doRender(EntityDireWolf entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
    super.doRender(entity, x, y, z, p_76986_8_, p_76986_9_);
    GL11.glPopMatrix();
    //RenderUtil.renderEntityBoundingBox((EntityLiving) entity, x, y, z);

  }

  @Override
  protected ResourceLocation getEntityTexture(EntityDireWolf p_110775_1_) {
    return wolfTextures;
  }
  
  public static class Factory implements IRenderFactory<EntityDireWolf> {

    @Override
    public Render<? super EntityDireWolf> createRenderFor(RenderManager manager) {
      return new RenderDirewolf(manager);
    }
  }

}
