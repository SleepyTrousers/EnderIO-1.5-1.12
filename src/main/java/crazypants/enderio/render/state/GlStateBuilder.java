package crazypants.enderio.render.state;


/**
 * Create a GlState object that reflects the current state of the GlStateMananger.
 * <p>
 * Uncomment and enable the AT in build.gradle to use.
 */
public class GlStateBuilder {

  /* @formatter:off

  public static GlState create() {
    GlState glstate = new GlState();
    glstate.addState(new GlState.AlphaState(GlStateManager.alphaState.alphaTest.currentState, GlStateManager.alphaState.func, GlStateManager.alphaState.ref));
    glstate.addState(new GlState.LightingState(GlStateManager.lightingState.currentState));
    glstate.addState(createLightState());
    glstate.addState(new GlState.ColorMaterialState(GlStateManager.colorMaterialState.colorMaterial.currentState, GlStateManager.colorMaterialState.face,
        GlStateManager.colorMaterialState.mode));
    glstate.addState(new GlState.BlendState(GlStateManager.blendState.blend.currentState, GlStateManager.blendState.srcFactor,
        GlStateManager.blendState.dstFactor));
    glstate.addState(new GlState.DepthState(GlStateManager.depthState.depthTest.currentState, GlStateManager.depthState.maskEnabled,
        GlStateManager.depthState.depthFunc));
    glstate.addState(new GlState.FogState(GlStateManager.fogState.fog.currentState, GlStateManager.fogState.mode, GlStateManager.fogState.density,
        GlStateManager.fogState.start, GlStateManager.fogState.end));
    glstate.addState(new GlState.CullState(GlStateManager.cullState.cullFace.currentState, GlStateManager.cullState.mode));
    glstate.addState(new GlState.PolygonOffsetState(GlStateManager.polygonOffsetState.polygonOffsetFill.currentState, GlStateManager.polygonOffsetState.factor,
        GlStateManager.polygonOffsetState.units));
    glstate.addState(new GlState.ColorLogicState(GlStateManager.colorLogicState.colorLogicOp.currentState, GlStateManager.colorLogicState.opcode));
    glstate.addState(new GlState.ClearState(GlStateManager.clearState.depth, GlStateManager.clearState.color.red, GlStateManager.clearState.color.green,
        GlStateManager.clearState.color.blue, GlStateManager.clearState.color.alpha));
    glstate.addState(new GlState.NormalizeState(GlStateManager.normalizeState.currentState));
    glstate.addState(new GlState.ShadeModelState(GlStateManager.activeShadeModel));
    glstate.addState(new GlState.RescaleNormalState(GlStateManager.rescaleNormalState.currentState));
    glstate.addState(new GlState.ColorMask(GlStateManager.colorMaskState.red, GlStateManager.colorMaskState.green, GlStateManager.colorMaskState.blue,
        GlStateManager.colorMaskState.alpha));
    glstate.addState(new GlState.ColorState(GlStateManager.colorState.red, GlStateManager.colorState.green, GlStateManager.colorState.blue,
        GlStateManager.colorState.alpha));
    return glstate;
  }

  public static GlState.LightState createLightState() {
    boolean[] b = new boolean[8];
    for (int i = 0; i < b.length; i++) {
      b[i] = GlStateManager.lightState[i].currentState;
    }
    return new GlState.LightState(b);
  }

  */

}
