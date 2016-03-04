package crazypants.enderio.render;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * A class to manage the GlStateManager's state.
 * <p>
 * It allows you to:
 * <ul>
 * <li>Record the GlStateManager's state
 * <li>Re-apply a recorded state
 * <li>convert the state into valid java code for inclusion into source code
 * <li>Recreate a state object from that code.
 * </ul>
 * It also has two ways of dealing with GlStateManager's inability to keep in sync with the OpenGl state it should manage. It can either:
 * <ul>
 * <li>Apply the state in a way that forced the state to be different from its state. This means that every change is first executed with a value that differs
 * from the intended value, then executed with the intended value. ("useDoubleAction")
 * <li>Apply the change to both GlStateManager and OpenGl. This means that every change is first sent to GlStateManager and then also executed with GL11 calls.
 * ("forceAction")
 * </ul>
 * The second option creates less additional Java and OpenGl calls, but relies on the GlStateManager directly executing GL11 calls (which is the current
 * implementation). The first option has more overhead, but will work no matter what the GlStateManager does with the data.
 *
 */
public class GlState {

  public static boolean useDoubleAction = false;
  public static boolean forceAction = true;

  private interface State {
    String getName();

    State create(Iterator<Object> data);

    void store(List<Object> data);

    State create();

    void apply();
  }

  private static final Map<String, State> allStates = new HashMap<String, State>();
  
  protected static void addState(State state) {
    allStates.put(state.getName(), state);
  }

  private final Map<String, State> states = new HashMap<String, State>();

  /**
   * Stores the state as a list of simple objects.
   * <p>
   * Not very useful on its own, see toString() instead.
   */
  public List<Object> store() {
    List<Object> result = new ArrayList<Object>();
    for (State state : states.values()) {
      result.add(state.getName());
      state.store(result);
    }
    return result;
  }

  /**
   * Creates Java code to recreate the state
   */
  @Override
  public String toString() {
    List<Object> data = store();
    StringBuilder sb = new StringBuilder();
    sb.append("GlState state = GlState.create(");
    for (Object o : data) {
      if (o instanceof String) {
        sb.append("\"");
        sb.append(o);
        sb.append("\", ");
      } else if (o instanceof Float) {
        String s = String.format((Locale) null, "%.12f", o).replaceFirst("(\\.\\d+?)0+$", "$1");
        sb.append(s);
        sb.append("f, ");
      } else {
        sb.append(o);
        sb.append(", ");
      }
    }
    sb.setLength(sb.length() - 2);
    sb.append(");");
    return sb.toString();
  }

  /**
   * Creates a state from the given list of objects.
   * <p>
   * This could either be the list as returned from store() or as created by toString().
   */
  public static GlState create(Object... data) {
    GlState glstate = new GlState();

    Iterator iterator = Arrays.asList(data).iterator();
    while (iterator.hasNext()) {
      Object key = iterator.next();
      State state = allStates.get(key);
      if (state == null) {
        throw new RuntimeException("Invalid State data, not a key: " + state);
      }
      try {
        State newState = state.create(iterator);
        glstate.states.put(newState.getName(), newState);
      } catch (NoSuchElementException e) {
        throw new RuntimeException("Invalid State data, not enough data for: " + state);
      } catch (ClassCastException e1) {
        throw new RuntimeException("Invalid State data, bad data for: " + state + ", got " + e1);
      }
    }

    return glstate;
  }
  
  /**
   * Create a state object that reflects the current state of the GlStateMananger
   */
  public static GlState create() {
    GlState glstate = new GlState();
    for (State state : allStates.values()) {
      State newState = state.create();
      if (newState != null) {
        glstate.states.put(newState.getName(), newState);
      }
    }
    return glstate;
  }
  
  /**
   * Applies the state to GlStateMananger
   */
  public void apply() {
    for (State state : states.values()) {
      state.apply();
    }
  }
  
  /**
   * Applies the state to GlStateMananger, but only those sub-states that are also included in the given filter.
   */
  public void apply_filtered(GlState filter) {
    for (String stateName : filter.states.keySet()) {
      if (states.containsKey(stateName)) {
        states.get(stateName).apply();
      }
    }
  }

  private static class GlConstant {
    private final int constant;

    private GlConstant(int constant) {
      this.constant = constant;
    }

    @Override
    public String toString() {
      for (Field field : GL11.class.getDeclaredFields()) {
        if (field.getType() == int.class) {
          if (!field.isAccessible()) {
            field.setAccessible(true);
          }
          int val;
          try {
            val = field.getInt(null);
            if (val == constant) {
              return "GL11." + field.getName();
            }
          } catch (IllegalArgumentException e) {
          } catch (IllegalAccessException e) {
          }
        }
      }
      return "" + constant;
    }
  }

  static {
    addState(new AlphaState(false, 0, 0f));
    addState(new LightingState(false));
    addState(new LightState(new boolean[8]));
    addState(new ColorMaterialState(false, 0, 0));
    addState(new BlendState(false, 0, 0));
    addState(new DepthState(false, false, 0));
    addState(new FogState(false, 0, 0f, 0f, 0f));
    addState(new CullState(false, 0));
    addState(new PolygonOffsetState(false, 0f, 0f));
    addState(new ColorLogicState(false, 0));
    addState(new ClearState(0, 0, 0, 0, 0));
    addState(new NormalizeState(false));
    addState(new ShadeModelState(0));
    addState(new RescaleNormalState(false));
    addState(new ColorMask(false, false, false, false));
    addState(new ColorState(0, 0, 0, 0));
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class AlphaState implements State {
    private final boolean alphaTest;
    private final int func;
    private final float ref;

    @Override
    public String getName() {
      return "alpha";
    }

    private AlphaState(boolean alphaTest, int func, float ref) {
      this.alphaTest = alphaTest;
      this.func = func;
      this.ref = ref;
    }

    @Override
    public void store(List<Object> data) {
      data.add(alphaTest);
      if (alphaTest) {
        data.add(new GlConstant(func));
        data.add(ref);
      }
    }

    @Override
    public State create(Iterator<Object> data) {
      boolean next = (Boolean) data.next();
      if (next) {
        return new AlphaState(next, (Integer) data.next(), (Float) data.next());
      } else {
        return new AlphaState(next, 0, 0f);
      }
    }

    @Override
    public State create() {
      return new AlphaState(GlStateManager.alphaState.alphaTest.currentState, GlStateManager.alphaState.func, GlStateManager.alphaState.ref);
    }

    @Override
    public void apply() {
      if (alphaTest) {
        if (useDoubleAction) {
          GlStateManager.disableAlpha();
          GlStateManager.alphaFunc(GL11.GL_NEVER, 0f);
          GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0f);
        }
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(func, ref);
        if (forceAction) {
          GL11.glEnable(GL11.GL_ALPHA_TEST);
          GL11.glAlphaFunc(func, ref);
        }
      } else {
        if (useDoubleAction) {
          GlStateManager.enableAlpha();
        }
        GlStateManager.disableAlpha();
        if (forceAction) {
          GL11.glDisable(GL11.GL_ALPHA_TEST);
        }
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class LightingState implements State {
    private final boolean lighting;

    @Override
    public String getName() {
      return "lighting";
    }

    private LightingState(boolean lighting) {
      this.lighting = lighting;
    }

    @Override
    public void store(List<Object> data) {
      data.add(lighting);
    }

    @Override
    public State create(Iterator<Object> data) {
      return new LightingState((Boolean) data.next());
    }

    @Override
    public State create() {
      return new LightingState(GlStateManager.lightingState.currentState);
    }

    @Override
    public void apply() {
      if (lighting) {
        if (useDoubleAction) {
          GlStateManager.disableLighting();
        }
        GlStateManager.enableLighting();
        if (forceAction) {
          GL11.glEnable(GL11.GL_LIGHTING);
        }
      } else {
        if (useDoubleAction) {
          GlStateManager.enableLighting();
        }
        GlStateManager.disableLighting();
        if (forceAction) {
          GL11.glDisable(GL11.GL_LIGHTING);
        }
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class LightState implements State {
    private final boolean[] light;

    @Override
    public String getName() {
      return "light";
    }

    private LightState(boolean[] light) {
      this.light = light;
    }

    @Override
    public void store(List<Object> data) {
      for (int i = 0; i < light.length; i++) {
        data.add(light[i]);
      }
    }

    @Override
    public State create(Iterator<Object> data) {
      boolean[] b = new boolean[8];
      for (int i = 0; i < light.length; i++) {
        b[i] = (Boolean) data.next();
      }
      return new LightState(b);
    }

    @Override
    public State create() {
      boolean[] b = new boolean[8];
      for (int i = 0; i < light.length; i++) {
        b[i] = GlStateManager.lightState[i].currentState;
      }
      return new LightState(b);
    }

    @Override
    public void apply() {
      for (int i = 0; i < light.length; i++) {
        if (light[i]) {
          if (useDoubleAction) {
            GlStateManager.disableLight(i);
          }
          GlStateManager.enableLight(i);
          if (forceAction) {
            GL11.glEnable(GL11.GL_LIGHT0 + i);
          }
        } else {
          if (useDoubleAction) {
            GlStateManager.enableLight(i);
          }
          GlStateManager.disableLight(i);
          if (forceAction) {
            GL11.glDisable(GL11.GL_LIGHT0 + i);
          }
        }
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class ColorMaterialState implements State {
    private final boolean colorMaterial;
    private final int face;
    private final int mode;

    @Override
    public String getName() {
      return "alpha";
    }

    private ColorMaterialState(boolean colorMaterial, int face, int mode) {
      this.colorMaterial = colorMaterial;
      this.face = face;
      this.mode = mode;
    }

    @Override
    public void store(List<Object> data) {
      data.add(colorMaterial);
      if (colorMaterial) {
        data.add(new GlConstant(face));
        data.add(new GlConstant(mode));
      }
    }

    @Override
    public State create(Iterator<Object> data) {
      boolean next = (Boolean) data.next();
      if (next) {
        return new ColorMaterialState(next, (Integer) data.next(), (Integer) data.next());
      } else {
        return new ColorMaterialState(next, 0, 0);
      }
    }

    @Override
    public State create() {
      return new ColorMaterialState(GlStateManager.colorMaterialState.colorMaterial.currentState, GlStateManager.colorMaterialState.face,
          GlStateManager.colorMaterialState.mode);
    }

    @Override
    public void apply() {
      if (colorMaterial) {
        if (useDoubleAction) {
          GlStateManager.disableColorMaterial();
          GlStateManager.colorMaterial(GL11.GL_FRONT, GL11.GL_EMISSION);
          GlStateManager.colorMaterial(GL11.GL_BACK, GL11.GL_EMISSION);
        }
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(face, mode);
        if (forceAction) {
          GL11.glEnable(GL11.GL_COLOR_MATERIAL);
          GL11.glColorMaterial(face, mode);
        }
      } else {
        if (useDoubleAction) {
          GlStateManager.enableColorMaterial();
        }
        GlStateManager.disableColorMaterial();
        if (forceAction) {
          GL11.glDisable(GL11.GL_COLOR_MATERIAL);
        }
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class BlendState implements State {
    private final boolean blend;
    private final int srcFactor;
    private final int dstFactor;

    @Override
    public String getName() {
      return "blend";
    }

    private BlendState(boolean blend, int srcFactor, int dstFactor) {
      this.blend = blend;
      this.srcFactor = srcFactor;
      this.dstFactor = dstFactor;
    }

    @Override
    public void store(List<Object> data) {
      data.add(blend);
      if (blend) {
        data.add(new GlConstant(srcFactor));
        data.add(new GlConstant(dstFactor));
      }
    }

    @Override
    public State create(Iterator<Object> data) {
      boolean next = (Boolean) data.next();
      if (next) {
        return new BlendState(next, (Integer) data.next(), (Integer) data.next());
      } else {
        return new BlendState(next, 0, 0);
      }
    }

    @Override
    public State create() {
      return new BlendState(GlStateManager.blendState.blend.currentState, GlStateManager.blendState.srcFactor, GlStateManager.blendState.dstFactor);
    }

    @Override
    public void apply() {
      if (blend) {
        if (useDoubleAction) {
          GlStateManager.disableBlend();
          GlStateManager.blendFunc(GL11.GL_ZERO, GL11.GL_ZERO);
          GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(srcFactor, dstFactor);
        if (forceAction) {
          GL11.glEnable(GL11.GL_BLEND);
          GL11.glBlendFunc(srcFactor, dstFactor);
        }
      } else {
        if (useDoubleAction) {
          GlStateManager.enableBlend();
        }
        GlStateManager.disableBlend();
        if (forceAction) {
          GL11.glDisable(GL11.GL_BLEND);
        }
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class DepthState implements State {
    private final boolean depthTest;
    private final boolean maskEnabled;
    private final int depthFunc;

    @Override
    public String getName() {
      return "depth";
    }

    private DepthState(boolean depthTest, boolean maskEnabled, int depthFunc) {
      this.depthTest = depthTest;
      this.maskEnabled = maskEnabled;
      this.depthFunc = depthFunc;
    }

    @Override
    public void store(List<Object> data) {
      data.add(depthTest);
      if (depthTest) {
        data.add(maskEnabled);
        if (maskEnabled) {
          data.add(new GlConstant(depthFunc));
        }
      }
    }

    @Override
    public State create(Iterator<Object> data) {
      Boolean next0 = (Boolean) data.next();
      if (next0) {
        Boolean next1 = (Boolean) data.next();
        if (next1) {
          return new DepthState(next0, next1, (Integer) data.next());
        } else {
          return new DepthState(next0, next1, 0);
        }
      } else {
        return new DepthState(next0, false, 0);
      }
    }

    @Override
    public State create() {
      return new DepthState(GlStateManager.depthState.depthTest.currentState, GlStateManager.depthState.maskEnabled, GlStateManager.depthState.depthFunc);
    }

    @Override
    public void apply() {
      if (depthTest) {
        if (useDoubleAction) {
          GlStateManager.disableDepth();
          GlStateManager.depthMask(!maskEnabled);
        }
        GlStateManager.enableDepth();
        GlStateManager.depthMask(maskEnabled);
        if (maskEnabled) {
          if (useDoubleAction) {
            GlStateManager.depthFunc(GL11.GL_NEVER);
          }
          GlStateManager.depthFunc(depthFunc);
        }
        if (forceAction) {
          GL11.glEnable(GL11.GL_DEPTH_TEST);
          GL11.glDepthMask(maskEnabled);
          GL11.glDepthFunc(depthFunc);
        }
      } else {
        if (useDoubleAction) {
          GlStateManager.enableDepth();
        }
        GlStateManager.disableDepth();
        if (forceAction) {
          GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class FogState implements State {
    private final boolean fog;
    private final int mode;
    private final float density;
    private final float start;
    private final float end;

    @Override
    public String getName() {
      return "fog";
    }

    private FogState(boolean fog, int mode, float density, float start, float end) {
      this.fog = fog;
      this.mode = mode;
      this.density = density;
      this.start = start;
      this.end = end;
    }

    @Override
    public void store(List<Object> data) {
      data.add(fog);
      if (fog) {
        data.add(new GlConstant(mode));
        data.add(density);
        data.add(start);
        data.add(end);
      }
    }

    @Override
    public State create(Iterator<Object> data) {
      Boolean next = (Boolean) data.next();
      if (next) {
        return new FogState(next, (Integer) data.next(), (Float) data.next(), (Float) data.next(), (Float) data.next());
      } else {
        return new FogState(next, 0, 0, 0, 0);
      }
    }

    @Override
    public State create() {
      return new FogState(GlStateManager.fogState.fog.currentState, GlStateManager.fogState.mode, GlStateManager.fogState.density,
          GlStateManager.fogState.start, GlStateManager.fogState.end);
    }

    @Override
    public void apply() {
      if (fog) {
        if (useDoubleAction) {
          GlStateManager.disableFog();
          GlStateManager.setFog(GL11.GL_LINEAR);
          GlStateManager.setFogDensity(.5f);
          GlStateManager.setFogStart(.5f);
          GlStateManager.setFogEnd(.5f);
          GlStateManager.setFog(GL11.GL_EXP);
          GlStateManager.setFogDensity(.6f);
          GlStateManager.setFogStart(.6f);
          GlStateManager.setFogEnd(.6f);
        }
        GlStateManager.enableFog();
        GlStateManager.setFog(mode);
        GlStateManager.setFogDensity(density);
        GlStateManager.setFogStart(start);
        GlStateManager.setFogEnd(end);
        if (forceAction) {
          GL11.glEnable(GL11.GL_FOG);
          GL11.glFogi(GL11.GL_FOG_MODE, mode);
          GL11.glFogf(GL11.GL_FOG_DENSITY, density);
          GL11.glFogf(GL11.GL_FOG_START, start);
          GL11.glFogf(GL11.GL_FOG_END, end);
        }
      } else {
        if (useDoubleAction) {
          GlStateManager.enableFog();
        }
        GlStateManager.disableFog();
        if (forceAction) {
          GL11.glDisable(GL11.GL_FOG);
        }
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class CullState implements State {
    private final boolean cullFace;
    private final int mode;

    @Override
    public String getName() {
      return "cullface";
    }

    private CullState(boolean cullFace, int mode) {
      this.cullFace = cullFace;
      this.mode = mode;
    }

    @Override
    public void store(List<Object> data) {
      data.add(cullFace);
      if (cullFace) {
        data.add(new GlConstant(mode));
      }
    }

    @Override
    public State create(Iterator<Object> data) {
      Boolean next = (Boolean) data.next();
      if (next) {
        return new CullState(next, (Integer) data.next());
      } else {
        return new CullState(next, 0);
      }
    }

    @Override
    public State create() {
      return new CullState(GlStateManager.cullState.cullFace.currentState, GlStateManager.cullState.mode);
    }

    @Override
    public void apply() {
      if (cullFace) {
        if (useDoubleAction) {
          GlStateManager.disableCull();
          GlStateManager.cullFace(GL11.GL_FRONT);
          GlStateManager.cullFace(GL11.GL_BACK);
        }
        GlStateManager.enableCull();
        GlStateManager.cullFace(mode);
        if (forceAction) {
          GL11.glEnable(GL11.GL_CULL_FACE);
          GL11.glCullFace(mode);
        }
      } else {
        if (useDoubleAction) {
          GlStateManager.enableFog();
        }
        GlStateManager.disableFog();
        if (forceAction) {
          GL11.glDisable(GL11.GL_CULL_FACE);
        }
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class PolygonOffsetState implements State {
    private final boolean polygonOffsetFill;
    private final float factor;
    private final float units;

    @Override
    public String getName() {
      return "polygonoffset";
    }

    private PolygonOffsetState(boolean polygonOffsetFill, float factor, float units) {
      this.polygonOffsetFill = polygonOffsetFill;
      this.factor = factor;
      this.units = units;
    }

    @Override
    public void store(List<Object> data) {
      data.add(polygonOffsetFill);
      if (polygonOffsetFill) {
        data.add(factor);
        data.add(units);
      }
    }

    @Override
    public State create(Iterator<Object> data) {
      boolean next = (Boolean) data.next();
      if (next) {
        return new PolygonOffsetState(next, (Float) data.next(), (Float) data.next());
      } else {
        return new PolygonOffsetState(next, 0, 0);
      }
    }

    @Override
    public State create() {
      return new PolygonOffsetState(GlStateManager.polygonOffsetState.polygonOffsetFill.currentState, GlStateManager.polygonOffsetState.factor,
          GlStateManager.polygonOffsetState.units);
    }

    @Override
    public void apply() {
      if (polygonOffsetFill) {
        if (useDoubleAction) {
          GlStateManager.disablePolygonOffset();
          GlStateManager.doPolygonOffset(.5f, 1f);
          GlStateManager.doPolygonOffset(.6f, 1f);
        }
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(factor, units);
        if (forceAction) {
          GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
          GL11.glPolygonOffset(factor, units);
        }
      } else {
        if (useDoubleAction) {
          GlStateManager.enablePolygonOffset();
        }
        GlStateManager.disablePolygonOffset();
        if (forceAction) {
          GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        }
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class ColorLogicState implements State {
    private final boolean colorLogicOp;
    private final int opcode;

    @Override
    public String getName() {
      return "colorlogic";
    }

    private ColorLogicState(boolean colorLogicOp, int opcode) {
      this.colorLogicOp = colorLogicOp;
      this.opcode = opcode;
    }

    @Override
    public void store(List<Object> data) {
      data.add(colorLogicOp);
      if (colorLogicOp) {
        data.add(new GlConstant(opcode));
      }
    }

    @Override
    public State create(Iterator<Object> data) {
      boolean next = (Boolean) data.next();
      if (next) {
        return new ColorLogicState(next, (Integer) data.next());
      } else {
        return new ColorLogicState(next, 0);
      }
    }

    @Override
    public State create() {
      return new ColorLogicState(GlStateManager.colorLogicState.colorLogicOp.currentState, GlStateManager.colorLogicState.opcode);
    }

    @Override
    public void apply() {
      if (colorLogicOp) {
        if (useDoubleAction) {
          GlStateManager.disableColorLogic();
          GlStateManager.colorLogicOp(GL11.GL_CLEAR);
          GlStateManager.colorLogicOp(GL11.GL_SET);
        }
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(opcode);
        if (forceAction) {
          GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
          GL11.glLogicOp(opcode);
        }
      } else {
        if (useDoubleAction) {
          GlStateManager.enableColorLogic();
        }
        GlStateManager.disableColorLogic();
        if (forceAction) {
          GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        }
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class ClearState implements State {
    private final double depth;
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    @Override
    public String getName() {
      return "clear";
    }

    private ClearState(double depth, float red, float green, float blue, float alpha) {
      this.depth = depth;
      this.red = red;
      this.green = green;
      this.blue = blue;
      this.alpha = alpha;
    }

    @Override
    public void store(List<Object> data) {
      data.add(depth);
      data.add(red);
      data.add(green);
      data.add(blue);
      data.add(alpha);
    }

    @Override
    public State create(Iterator<Object> data) {
      return new ClearState((Double) data.next(), (Float) data.next(), (Float) data.next(), (Float) data.next(), (Float) data.next());
    }

    @Override
    public State create() {
      return new ClearState(GlStateManager.clearState.depth, GlStateManager.clearState.color.red, GlStateManager.clearState.color.green,
          GlStateManager.clearState.color.blue, GlStateManager.clearState.color.alpha);
    }

    @Override
    public void apply() {
      if (useDoubleAction) {
        GlStateManager.clearDepth(.5);
        GlStateManager.clearDepth(.6);
        GlStateManager.clearColor(.5f, .5f, .5f, .5f);
        GlStateManager.clearColor(.6f, .5f, .5f, .5f);
      }
      GlStateManager.clearDepth(depth);
      GlStateManager.clearColor(red, green, blue, alpha);
      if (forceAction) {
        GL11.glClearDepth(depth);
        GL11.glClearColor(red, green, blue, alpha);
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class NormalizeState implements State {
    private final boolean normalize;

    @Override
    public String getName() {
      return "normalize";
    }

    private NormalizeState(boolean normalize) {
      this.normalize = normalize;
    }

    @Override
    public void store(List<Object> data) {
      data.add(normalize);
    }

    @Override
    public State create(Iterator<Object> data) {
      return new NormalizeState((Boolean) data.next());
    }

    @Override
    public State create() {
      return new NormalizeState(GlStateManager.normalizeState.currentState);
    }

    @Override
    public void apply() {
      if (normalize) {
        if (useDoubleAction) {
          GlStateManager.disableNormalize();
        }
        GlStateManager.enableNormalize();
        if (forceAction) {
          GL11.glEnable(GL11.GL_NORMALIZE);
        }
      } else {
        if (useDoubleAction) {
          GlStateManager.enableNormalize();
        }
        GlStateManager.disableNormalize();
        if (forceAction) {
          GL11.glDisable(GL11.GL_NORMALIZE);
        }
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class ShadeModelState implements State {
    private final int activeShadeModel;

    @Override
    public String getName() {
      return "shademodel";
    }

    private ShadeModelState(int activeShadeModel) {
      this.activeShadeModel = activeShadeModel;
    }

    @Override
    public void store(List<Object> data) {
      data.add(new GlConstant(activeShadeModel));
    }

    @Override
    public State create(Iterator<Object> data) {
      return new ShadeModelState((Integer) data.next());
    }

    @Override
    public State create() {
      return new ShadeModelState(GlStateManager.activeShadeModel);
    }

    @Override
    public void apply() {
      if (useDoubleAction) {
        GlStateManager.shadeModel(activeShadeModel == GL11.GL_FLAT ? GL11.GL_SMOOTH : GL11.GL_FLAT);
      }
      GlStateManager.shadeModel(activeShadeModel);
      if (forceAction) {
        GL11.glShadeModel(activeShadeModel);
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class RescaleNormalState implements State {
    private final boolean rescalenormal;

    @Override
    public String getName() {
      return "rescalenormal";
    }

    private RescaleNormalState(boolean rescalenormal) {
      this.rescalenormal = rescalenormal;
    }

    @Override
    public void store(List<Object> data) {
      data.add(rescalenormal);
    }

    @Override
    public State create(Iterator<Object> data) {
      return new RescaleNormalState((Boolean) data.next());
    }

    @Override
    public State create() {
      return new RescaleNormalState(GlStateManager.rescaleNormalState.currentState);
    }

    @Override
    public void apply() {
      if (rescalenormal) {
        if (useDoubleAction) {
          GlStateManager.disableRescaleNormal();
        }
        GlStateManager.enableRescaleNormal();
        if (forceAction) {
          GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
      } else {
        if (useDoubleAction) {
          GlStateManager.enableRescaleNormal();
        }
        GlStateManager.disableRescaleNormal();
        if (forceAction) {
          GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class ColorMask implements State {
    private final boolean red;
    private final boolean green;
    private final boolean blue;
    private final boolean alpha;

    @Override
    public String getName() {
      return "colormask";
    }

    private ColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
      this.red = red;
      this.green = green;
      this.blue = blue;
      this.alpha = alpha;
    }

    @Override
    public void store(List<Object> data) {
      data.add(red);
      data.add(green);
      data.add(blue);
      data.add(alpha);
    }

    @Override
    public State create(Iterator<Object> data) {
      return new ColorMask((Boolean) data.next(), (Boolean) data.next(), (Boolean) data.next(), (Boolean) data.next());
    }

    @Override
    public State create() {
      return new ColorMask(GlStateManager.colorMaskState.red, GlStateManager.colorMaskState.green, GlStateManager.colorMaskState.blue,
          GlStateManager.colorMaskState.alpha);
    }

    @Override
    public void apply() {
      if (useDoubleAction) {
        GlStateManager.colorMask(!red, green, blue, alpha);
      }
      GlStateManager.colorMask(red, green, blue, alpha);
      if (forceAction) {
        GL11.glColorMask(red, green, blue, alpha);
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////

  private static class ColorState implements State {
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    @Override
    public String getName() {
      return "color";
    }

    private ColorState(float red, float green, float blue, float alpha) {
      this.red = red;
      this.green = green;
      this.blue = blue;
      this.alpha = alpha;
    }

    @Override
    public void store(List<Object> data) {
      data.add(red);
      data.add(green);
      data.add(blue);
      data.add(alpha);
    }

    @Override
    public State create(Iterator<Object> data) {
      return new ColorState((Float) data.next(), (Float) data.next(), (Float) data.next(), (Float) data.next());
    }

    @Override
    public State create() {
      return new ColorState(GlStateManager.colorState.red, GlStateManager.colorState.green, GlStateManager.colorState.blue, GlStateManager.colorState.alpha);
    }

    @Override
    public void apply() {
      if (useDoubleAction) {
        GlStateManager.color(1f - red, green, blue, alpha);
      }
      GlStateManager.color(red, green, blue, alpha);
      if (forceAction) {
        GL11.glColor4f(red, green, blue, alpha);
      }
    }
  }

}
