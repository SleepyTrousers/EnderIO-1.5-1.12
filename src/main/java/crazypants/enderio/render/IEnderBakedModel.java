package crazypants.enderio.render;

import javax.vecmath.Matrix4f;

import net.minecraftforge.client.model.IPerspectiveAwareModel;

public interface IEnderBakedModel extends IPerspectiveAwareModel {

  Matrix4f[] getTransformTypes();

}