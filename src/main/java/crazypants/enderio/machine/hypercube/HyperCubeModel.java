package crazypants.enderio.machine.hypercube;

import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

public class HyperCubeModel extends ModelBase implements IModel {

    public static final float SCALE = 1 / 16f;

    private static final String TEXTURE = "enderio:models/hyperCube.png";
    private static final String MODEL = "enderio:models/hyperCube.obj";

    private IModelCustom model;

    public HyperCubeModel() {
        model = AdvancedModelLoader.loadModel(new ResourceLocation(MODEL));
    }

    @Override
    public void render() {
        GL11.glPushMatrix();
        GL11.glScalef(SCALE, SCALE, SCALE);
        RenderUtil.bindTexture(TEXTURE);
        model.renderAll();
        GL11.glPopMatrix();
    }

    @Override
    public void render(TileHyperCube cube, double x, double y, double z) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
        render();
        GL11.glPopMatrix();
    }
}
