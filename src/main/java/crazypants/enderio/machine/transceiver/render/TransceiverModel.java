package crazypants.enderio.machine.transceiver.render;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.machine.transceiver.TileTransceiver;
import net.minecraft.client.model.ModelBase;

public class TransceiverModel extends ModelBase implements IModelTrans {
  
  public static final float SCALE = 1 / 16f;

  private static final String TEXTURE = "enderio:models/transceiver.png";
//  private static final String MODEL = "enderio:models/transceiver.obj";
//
//  private IModel model;

  public TransceiverModel() {
//    model = AdvancedModelLoader.loadModel(new ResourceLocation(MODEL));
    
//    model = ModelLoaderRegistry.getMissingModel();
//    try
//    {
//        model = ModelLoaderRegistry.getModel(new ResourceLocation(MODEL));
//    }
//    catch (IOException e)
//    {
//        model = ModelLoaderRegistry.getMissingModel();
//    }
  }

  @Override
  public void render() {
    GL11.glPushMatrix();
    GL11.glScalef(SCALE, SCALE, SCALE);
    RenderUtil.bindTexture(TEXTURE);
//    model.renderAll();
    GL11.glPopMatrix();
  }

  @Override
  public void render(TileTransceiver cube, double x, double y, double z) {
    GL11.glPushMatrix();
    GL11.glTranslatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
    render();
    GL11.glPopMatrix();
  }
}
