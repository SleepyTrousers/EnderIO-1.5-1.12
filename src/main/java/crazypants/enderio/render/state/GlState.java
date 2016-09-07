package crazypants.enderio.render.state;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.FogMode;

/**
 * A class to manage the GlStateManager's state.
 * <p>
 * It allows you to:
 * <ul>
 * <li>Record the GlStateManager's state (using GlStateBuilder)
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

    void apply();
  }

  private static final Map<String, State> allStates = new HashMap<String, State>();
  
  private static void registerState(State state) {
    allStates.put(state.getName(), state);
  }

  void addState(State state) {
    states.put(state.getName(), state);
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

    Iterator<Object> iterator = Arrays.asList(data).iterator();
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

  protected static class GlConstant {
    protected final int constant;

    protected GlConstant(int constant) {
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
    registerState(new AlphaState(false, 0, 0f));
    registerState(new LightingState(false));
    registerState(new LightState(new boolean[8]));
    registerState(new ColorMaterialState(false, 0, 0));
    registerState(new BlendState(false, 0, 0));
    registerState(new DepthState(false, false, 0));
    registerState(new FogState(false, 0, 0f, 0f, 0f));
    registerState(new CullState(false, 0));
    registerState(new PolygonOffsetState(false, 0f, 0f));
    registerState(new ColorLogicState(false, 0));
    registerState(new ClearState(0, 0, 0, 0, 0));
    registerState(new NormalizeState(false));
    registerState(new ShadeModelState(0));
    registerState(new RescaleNormalState(false));
    registerState(new ColorMask(false, false, false, false));
    registerState(new ColorState(0, 0, 0, 0));
  }

  /**
   * A complete state as it is set by Minecraft when calling a TESR for TE rendering
   */
  public static final GlState CLEAN_TESR_STATE_COMPLETE = GlState.create("color", 1.0f, 1.0f, 1.0f, 1.0f, "shademodel", GL11.GL_FLAT, "rescalenormal", false,
      "blend", true, GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, "colorlogic", false, "clear", 1.0, 0.704647362232f, 0.823216617107f, 0.998214125633f,
      0.0f, "lighting", false, "colormask", true, true, true, true, "depth", true, true, GL11.GL_LEQUAL, "light", true, true, false, false, false, false,
      false, false, "cullface", true, GL11.GL_BACK, "polygonoffset", false, "alpha", true, GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE, "normalize",
      false, "fog", true, GL11.GL_LINEAR, 1.0f, 144.0f, 192.0f);

  /**
   * A state as it is set by Minecraft when calling a TESR for TE rendering with only the states we expect to change
   */
  public static final GlState CLEAN_TESR_STATE = GlState.create("color", 1.0f, 1.0f, 1.0f, 1.0f, "shademodel", GL11.GL_FLAT, "blend", true, GL11.GL_SRC_ALPHA,
      GL11.GL_ONE_MINUS_SRC_ALPHA, "lighting", false, "depth", true, true, GL11.GL_LEQUAL, "cullface", true, GL11.GL_BACK, "alpha", true,
      GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);

  // ///////////////////////////////////////////////////////////////////////

  protected static class AlphaState implements State {
    protected final boolean alphaTest;
    protected final int func;
    protected final float ref;

    @Override
    public String getName() {
      return "alpha";
    }

    AlphaState(boolean alphaTest, int func, float ref) {
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

  protected static class LightingState implements State {
    protected final boolean lighting;

    @Override
    public String getName() {
      return "lighting";
    }

    protected LightingState(boolean lighting) {
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

  protected static class LightState implements State {
    protected final boolean[] light;

    @Override
    public String getName() {
      return "light";
    }

    protected LightState(boolean[] light) {
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

  protected static class ColorMaterialState implements State {
    protected final boolean colorMaterial;
    protected final int face;
    protected final int mode;

    @Override
    public String getName() {
      return "alpha";
    }

    protected ColorMaterialState(boolean colorMaterial, int face, int mode) {
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

  protected static class BlendState implements State {
    protected final boolean blend;
    protected final int srcFactor;
    protected final int dstFactor;

    @Override
    public String getName() {
      return "blend";
    }

    protected BlendState(boolean blend, int srcFactor, int dstFactor) {
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

  protected static class DepthState implements State {
    protected final boolean depthTest;
    protected final boolean maskEnabled;
    protected final int depthFunc;

    @Override
    public String getName() {
      return "depth";
    }

    protected DepthState(boolean depthTest, boolean maskEnabled, int depthFunc) {
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

  protected static class FogState implements State {
    protected final boolean fog;
    protected final int mode;
    protected final float density;
    protected final float start;
    protected final float end;

    @Override
    public String getName() {
      return "fog";
    }

    protected FogState(boolean fog, int mode, float density, float start, float end) {
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
    public void apply() {
      //TODO: 1.10 some of these things removed
      if (fog) {
        if (useDoubleAction) {
          GlStateManager.disableFog();
          GlStateManager.setFog(FogMode.LINEAR);
          GlStateManager.setFogDensity(.5f);
          GlStateManager.setFogStart(.5f);
          GlStateManager.setFogEnd(.5f);
          GlStateManager.setFog(FogMode.EXP);
          GlStateManager.setFogDensity(.6f);
          GlStateManager.setFogStart(.6f);
          GlStateManager.setFogEnd(.6f);
        }
        GlStateManager.enableFog();
//        GlStateManager.setFog(mode);
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

  protected static class CullState implements State {
    protected final boolean cullFace;
    protected final int mode;

    @Override
    public String getName() {
      return "cullface";
    }

    protected CullState(boolean cullFace, int mode) {
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
    public void apply() {
      if (cullFace) {
        if (useDoubleAction) {
          GlStateManager.disableCull();
          GlStateManager.cullFace(CullFace.FRONT);
          GlStateManager.cullFace(CullFace.BACK);
        }
        GlStateManager.enableCull();
        //TODO: 1.10
        //GlStateManager.cullFace(mode);
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

  protected static class PolygonOffsetState implements State {
    protected final boolean polygonOffsetFill;
    protected final float factor;
    protected final float units;

    @Override
    public String getName() {
      return "polygonoffset";
    }

    protected PolygonOffsetState(boolean polygonOffsetFill, float factor, float units) {
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

  protected static class ColorLogicState implements State {
    protected final boolean colorLogicOp;
    protected final int opcode;

    @Override
    public String getName() {
      return "colorlogic";
    }

    protected ColorLogicState(boolean colorLogicOp, int opcode) {
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

  protected static class ClearState implements State {
    protected final double depth;
    protected final float red;
    protected final float green;
    protected final float blue;
    protected final float alpha;

    @Override
    public String getName() {
      return "clear";
    }

    protected ClearState(double depth, float red, float green, float blue, float alpha) {
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

  protected static class NormalizeState implements State {
    protected final boolean normalize;

    @Override
    public String getName() {
      return "normalize";
    }

    protected NormalizeState(boolean normalize) {
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

  protected static class ShadeModelState implements State {
    protected final int activeShadeModel;

    @Override
    public String getName() {
      return "shademodel";
    }

    protected ShadeModelState(int activeShadeModel) {
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

  protected static class RescaleNormalState implements State {
    protected final boolean rescalenormal;

    @Override
    public String getName() {
      return "rescalenormal";
    }

    protected RescaleNormalState(boolean rescalenormal) {
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

  protected static class ColorMask implements State {
    protected final boolean red;
    protected final boolean green;
    protected final boolean blue;
    protected final boolean alpha;

    @Override
    public String getName() {
      return "colormask";
    }

    protected ColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
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

  protected static class ColorState implements State {
    protected final float red;
    protected final float green;
    protected final float blue;
    protected final float alpha;

    @Override
    public String getName() {
      return "color";
    }

    protected ColorState(float red, float green, float blue, float alpha) {
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
