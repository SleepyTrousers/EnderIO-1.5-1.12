/*

Copyright © 2014 RainWarrior

Permission is granted to anyone to use this software for any purpose,
including commercial applications, and to alter it and redistribute it
freely, subject to the following restrictions:

   1. The origin of this software must not be misrepresented; you must not
   claim that you wrote the original software.

   2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

   3. Altered source versions must be plainly marked as such, and must not be
   misrepresented as being the original software.

   4. This notice may not be removed or altered from any source
   distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

package crazypants.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;
import net.minecraftforge.client.model.techne.TechneModel;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.vecmath.Vector3d;

/**
 * Slightly modified to fit the EnderIO source.
 * 
 * @author RainWarrior
 */
public class TechneUtil {

  private static class DefaultVertexTransform implements VertexTransform {

    private static DefaultVertexTransform INSTANCE = new DefaultVertexTransform();

    @Override
    public void apply(crazypants.vecmath.Vertex vertex) {
    }

    @Override
    public void apply(Vector3d vec) {
    }

    @Override
    public void applyToNormal(crazypants.vecmath.Vector3f vec) {
    }
  }

  public static VertexTransform vt = DefaultVertexTransform.INSTANCE;

  private static final Tessellator tes = Tessellator.instance;

  public static List<GroupObject> bakeModel(ModelRenderer model) {
    return bakeModel(model, 1);
  }

  public static List<GroupObject> bakeModel(ModelRenderer model, float scale) {
    return bakeModel(model, scale, new Matrix4f());
  }

  public static List<GroupObject> bakeModel(ModelRenderer model, float scale, Matrix4f matrix) {
    return bakeModel(model, scale, matrix, false);
  }

  /**
   * Convert ModelRenderer to a list of GroupObjects, for ease of use in ISBRH
   * and other static contexts.
   * 
   * @param scale
   *          the scale factor, usually last argument to rendering methods
   * @param matrix
   *          initial transformation matrix (replaces calling
   *          glTranslate/glRotate/e.t.c. before rendering)
   * @param rotateYFirst
   *          true, of the order of rotations be like in
   *          ModelRenderer.renderWithRotation, false if like in
   *          ModelRenderer.render
   */
  @SuppressWarnings("unchecked")
  public static List<GroupObject> bakeModel(ModelRenderer model, float scale, Matrix4f matrix, boolean rotateYFirst) {
    Matrix4f m = new Matrix4f(matrix);

    m.translate(new Vector3f(model.offsetX + model.rotationPointX * scale, model.offsetY + model.rotationPointY * scale, model.offsetZ + model.rotationPointZ
        * scale));

    if(!rotateYFirst) {
      m.rotate(model.rotateAngleZ, new Vector3f(0, 0, 1));
    }
    m.rotate(model.rotateAngleY, new Vector3f(0, 1, 0));
    m.rotate(model.rotateAngleX, new Vector3f(1, 0, 0));

    if(rotateYFirst) {
      m.rotate(model.rotateAngleZ, new Vector3f(0, 0, 1));
    }

    Vector4f vec = new Vector4f();
    List<GroupObject> res = new ArrayList<GroupObject>();
    for (ModelBox box : (List<ModelBox>) model.cubeList) {
      GroupObject obj = new GroupObject("", GL11.GL_QUADS);
      TexturedQuad[] quads = (TexturedQuad[]) ObfuscationReflectionHelper.getPrivateValue(ModelBox.class, box, "quadList", "field_78254_i");
      for (int i = 0; i < quads.length; i++) {
        Face face = new Face();
        face.vertices = new Vertex[4];
        face.textureCoordinates = new TextureCoordinate[4];
        for (int j = 0; j < 4; j++) {
          PositionTextureVertex pv = quads[i].vertexPositions[j];

          vec.x = (float) pv.vector3D.xCoord * scale;
          vec.y = (float) pv.vector3D.yCoord * scale;
          vec.z = (float) pv.vector3D.zCoord * scale;
          vec.w = 1;

          Matrix4f.transform(m, vec, vec);

          face.vertices[j] = new Vertex(vec.x / vec.w, vec.y / vec.w, vec.z / vec.w);

          face.textureCoordinates[j] = new TextureCoordinate(pv.texturePositionX, pv.texturePositionY);
        }
        face.faceNormal = face.calculateFaceNormal();
        obj.faces.add(face);
      }
      res.add(obj);
    }
    return res;
  }

  public static List<GroupObject> bakeModel(TechneModel model) {
    return bakeModel(model, 1);
  }

  public static List<GroupObject> bakeModel(TechneModel model, float scale) {
    return bakeModel(model, scale, new Matrix4f());
  }

  public static List<GroupObject> bakeModel(TechneModel model, float scale, Matrix4f m) {
    return bakeModel(model, scale, m, false);
  }

  /**
   * Use this to convert TechneModel to it's static representation
   */
  @SuppressWarnings("unchecked")
  public static List<GroupObject> bakeModel(TechneModel model, float scale, Matrix4f m, boolean rotateYFirst) {
    Map<String, ModelRenderer> parts = (Map<String, ModelRenderer>) ObfuscationReflectionHelper.getPrivateValue(TechneModel.class, model, "parts");
    List<GroupObject> res = Lists.newArrayList();
    for (Map.Entry<String, ModelRenderer> e : parts.entrySet()) {
      GroupObject obj = bakeModel(e.getValue(), scale, m, rotateYFirst).get(0);
      res.add(obj);
    }
    return res;
  }

  public static List<GroupObject> getModel(String modelPath) {
    TechneModel tm = (TechneModel) AdvancedModelLoader.loadModel(new ResourceLocation(EnderIO.MODID.toLowerCase(), modelPath + ".tcn"));
    return TechneUtil.bakeModel(tm, 1f / 16, new Matrix4f().scale(new Vector3f(-1, -1, 1)));
  }

  public static void renderWithIcon(List<GroupObject> model, IIcon icon, IIcon override, Tessellator tes) {
    renderWithIcon(model, icon, override, tes, null);
  }

  public static void renderWithIcon(List<GroupObject> model, IIcon icon, IIcon override, Tessellator tes, VertexTransform vt) {
    for (GroupObject go : model) {
      for (Face f : go.faces) {
        Vertex n = f.faceNormal;
        tes.setNormal(n.x, n.y, n.z);
        ForgeDirection normal = getNormalFor(n);
        ForgeDirection right = normal.getRotation(ForgeDirection.DOWN);
        if(normal == right) {
          right = ForgeDirection.EAST;
        }
        ForgeDirection down = normal.getRotation(right.getOpposite());

        for (int i = 0; i < f.vertices.length; i++) {
          Vertex vert = f.vertices[i];
          Vector3d v = new Vector3d(vert);
          if(vt != null) {
            vt.apply(v);
          }

          TextureCoordinate t = f.textureCoordinates[i];
          if(override != null) {

            Vector3d tv = new Vector3d(v);
            tv.add(0.5, 0, 0.5);

            double interpU = Math.abs(tv.x * right.offsetX + tv.y * right.offsetY + tv.z * right.offsetZ);
            double interpV = Math.abs(tv.x * down.offsetX + tv.y * down.offsetY + tv.z * down.offsetZ);

            if(normal == ForgeDirection.SOUTH || normal == ForgeDirection.WEST) {
              interpU = 1 - interpU;
            }
            if(normal != ForgeDirection.UP && normal != ForgeDirection.DOWN) {
              interpV = 1 - interpV;
            }

            tes.addVertexWithUV(v.x, v.y, v.z, override.getInterpolatedU(interpU * 16), override.getInterpolatedV(interpV * 16));
          } else {
            tes.addVertexWithUV(v.x, v.y, v.z, icon.getInterpolatedU(t.u * 16), icon.getInterpolatedV(t.v * 8));
          }
        }
      }
    }
  }

  private static ForgeDirection getNormalFor(Vertex n) {
    if(n.x != 0) {
      return n.x > 0 ? ForgeDirection.EAST : ForgeDirection.WEST;
    } else if(n.y != 0) {
      return n.y > 0 ? ForgeDirection.UP : ForgeDirection.DOWN;
    } else {
      return n.z > 0 ? ForgeDirection.SOUTH : ForgeDirection.NORTH;
    }
  }

  public static void renderInventoryBlock(List<GroupObject> model, Block block, int metadata, RenderBlocks rb) {
    renderInventoryBlock(model, getIconFor(block, metadata), block, metadata, rb);
  }

  public static void renderInventoryBlock(List<GroupObject> model, IIcon icon, Block block, int metadata, RenderBlocks rb) {
    tes.startDrawingQuads();
    tes.setColorOpaque_F(1, 1, 1);
    tes.addTranslation(0, -0.5f, 0);
    renderWithIcon(model, icon, rb.overrideBlockTexture, tes, vt);
    tes.addTranslation(0, 0.5f, 0);
    tes.draw();
    resetVT();
  }

  public static boolean renderWorldBlock(List<GroupObject> model, IBlockAccess world, int x, int y, int z, Block block, RenderBlocks rb) {
    IIcon icon = getIconFor(block, world, x, y, z);
    return renderWorldBlock(model, icon, world, x, y, z, block, rb);
  }

  public static boolean renderWorldBlock(List<GroupObject> model, IIcon icon, IBlockAccess world, int x, int y, int z, Block block, RenderBlocks rb) {
    tes.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
    tes.setColorOpaque_F(1, 1, 1);
    tes.addTranslation(x + .5F, y + 0.0375f, z + .5F);
    renderWithIcon(model, icon, rb.overrideBlockTexture, tes, vt);
    tes.addTranslation(-x - .5F, -y - 0.0375f, -z - .5F);
    resetVT();
    return true;
  }

  private static void resetVT() {
    vt = DefaultVertexTransform.INSTANCE;
  }

  private static IIcon getIconFor(Block block, IBlockAccess world, int x, int y, int z) {
    if(block instanceof AbstractMachineBlock<?>) {
      return ((AbstractMachineBlock<?>) block).getModelIcon(world, x, y, z);
    }
    return getIconFor(block, world.getBlockMetadata(x, y, z));
  }

  private static IIcon getIconFor(Block block, int metadata) {
    if(block instanceof AbstractMachineBlock<?>) {
      return ((AbstractMachineBlock<?>) block).getModelIcon();
    }
    return block.getIcon(0, metadata);
  }
}