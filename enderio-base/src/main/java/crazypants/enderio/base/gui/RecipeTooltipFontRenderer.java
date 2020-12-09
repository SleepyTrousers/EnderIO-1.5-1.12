package crazypants.enderio.base.gui;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Special font renderer that can render a recipe (crafting 3x3) to be used for tooltips.
 * <p>
 * To use this in a GUI:
 * <ul>
 * <li>Add a field of the type RecipeTooltipFontRenderer to your class. <br>
 * <code>final @Nonnull RecipeTooltipFontRenderer rtfr;</code>
 * <li>Initialize that field in your constructor. <br>
 * <code>rtfr = new RecipeTooltipFontRenderer();</code>
 * <li>Override getFontRenderer() to return it. <br>
 * <code>@Override<br>public @Nonnull FontRenderer getFontRenderer() {<br>return rtfr;<br>}</code>
 * <li>Register your recipe(s) with the font renderer. <br>
 * <code>rtfr.registerRecipe("abc123", new NNList<>(...));</code><br>
 * The ID can be any string, it is fine to replace already registered recipes. The NNList should have up to 9 ItemStacks for the 9 slots of the crafting grid
 * (in reading order).
 * <li>Manual way: Add 6(!) lines to any tooltip that is part of your GUI. The first one has {@link #RECIPE_ID} followed directly by the ID you used to register
 * the recipe to be rendered followed by {@link #RECIPE_END}. The other 5 have {@link #RECIPE}, those only provide enough space for the recipe to be rendered.
 * Both types of lines can have extra text which is rendered to the right of the recipe grid (even if it was in front of the marker).
 * <li>Single-use way: Add the result of addRecipe() to your tooltip lines.
 * </ul>
 */
public class RecipeTooltipFontRenderer extends FontRenderer {

  private static final @Nonnull String RECIPE_BASE = "#RECIPE";
  public static final @Nonnull String RECIPE_END = "#";
  private static final @Nonnull String RECIPE_ID_MARKER = "=";

  public static final @Nonnull String RECIPE = RECIPE_BASE + RECIPE_END;
  public static final @Nonnull String RECIPE_ID = RECIPE_BASE + RECIPE_ID_MARKER;

  private static final @Nonnull String MATCHER = RECIPE_BASE + RECIPE_ID_MARKER + "?([^" + RECIPE_END + "]*)" + RECIPE_END;
  private static final Pattern PATTERN = Pattern.compile(MATCHER);

  private static final int BORDER = 1, MARGIN = 1, ITEM = 16;
  private static final int S1 = BORDER + MARGIN;
  private static final int S2 = S1 + ITEM + MARGIN + BORDER + MARGIN;
  private static final int S3 = S2 + ITEM + MARGIN + BORDER + MARGIN;
  private static final int WIDTH = BORDER + MARGIN + ITEM + MARGIN;
  private static final int FULL_WIDTH = 3 * WIDTH + BORDER;

  private static final @Nonnull NNList<Point> LOCS = new NNList<>( //
      new Point(S1, S1), new Point(S2, S1), new Point(S3, S1), //
      new Point(S1, S2), new Point(S2, S2), new Point(S3, S2), //
      new Point(S1, S3), new Point(S2, S3), new Point(S3, S3) //
  );

  protected final @Nonnull Map<String, NNList<ItemStack>> recipes = new HashMap<>();

  private static final @Nonnull RecipeTooltipFontRenderer instance = new RecipeTooltipFontRenderer();

  public static @Nonnull RecipeTooltipFontRenderer getInstance() {
    return instance;
  }

  protected RecipeTooltipFontRenderer() {
    super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
    if (NullHelper.untrust(Minecraft.getMinecraft().gameSettings.language) != null) {
      setUnicodeFlag(Minecraft.getMinecraft().isUnicode());
      setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
    }
    ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
    onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
  }

  public @Nonnull NNList<String> registerRecipe(@Nonnull String id, @Nonnull NNList<ItemStack> recipe) {
    recipes.put(id, recipe);
    return makeRecipeLines(id);
  }

  public @Nonnull NNList<String> makeRecipeLines(@Nonnull String id) {
    NNList<String> result = new NNList<>();
    result.add(RECIPE_ID + id + RECIPE_END);
    result.add(RECIPE);
    result.add(RECIPE);
    result.add(RECIPE);
    result.add(RECIPE);
    result.add(RECIPE);
    return result;
  }

  @Override
  public int getStringWidth(@Nonnull String text) {
    final Matcher match = PATTERN.matcher(text);
    if (match.find()) {
      final int width = super.getStringWidth(withoutMatch(match));
      return FULL_WIDTH + (width > 0 ? width + 2 : 0);
    }
    return super.getStringWidth(text);
  }

  private @Nonnull String withoutMatch(final Matcher match) {
    return NullHelper.first(match.replaceFirst(""), "");
  }

  @Override
  public @Nonnull List<String> listFormattedStringToWidth(@Nonnull String text, int wrapWidth) {
    final List<String> result = super.listFormattedStringToWidth(text, wrapWidth);
    if (result.size() > 1 && PATTERN.matcher(text).find()) {
      for (int i = 0; i < result.size(); i++) {
        if (!PATTERN.matcher(result.get(i)).find()) {
          result.set(i, RECIPE + result.get(i));
        }
      }
    }
    return result;
  }

  @Override
  public int drawStringWithShadow(@Nonnull String text, float x, float y, int color) {
    final Matcher match = PATTERN.matcher(text);
    if (match.find()) {
      String id = match.group(1);
      if (id != null && !id.isEmpty()) {
        drawRecipeAt((int) x, (int) y, id, color);
      }
      return super.drawStringWithShadow(withoutMatch(match), x + FULL_WIDTH + 2, y, color);
    } else {
      return super.drawStringWithShadow(text, x, y, color);
    }
  }

  @SuppressWarnings("null")
  protected void drawRecipeAt(int x, int y, @Nonnull String id, int color) {
    drawRectangle(300, x + 0 * WIDTH, y + 0 * WIDTH, x + 3 * WIDTH + BORDER, y + 3 * WIDTH + BORDER, color & 0xFF7F7F7F);

    for (int r = 0; r < 4; r++) {
      drawRectangle(300, x + 0 * WIDTH, y + r * WIDTH, x + 3 * WIDTH + BORDER, y + r * WIDTH + BORDER, color);
      drawRectangle(300, x + r * WIDTH, y + 0 * WIDTH, x + r * WIDTH + BORDER, y + 3 * WIDTH + BORDER, color);
    }

    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
    RenderHelper.enableGUIStandardItemLighting();
    GlStateManager.enableRescaleNormal();

    final RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
    float old = renderItem.zLevel;
    renderItem.zLevel = 400;
    int pos = 0;
    for (ItemStack stack : NullHelper.first(recipes.get(id), NNList.<ItemStack> emptyList())) {
      Point point = LOCS.get(pos++);
      renderItem.renderItemAndEffectIntoGUI(stack, x + point.x, y + point.y);
      GlStateManager.enableAlpha();
    }
    renderItem.zLevel = old;

    GlStateManager.disableRescaleNormal();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableLighting();
    GlStateManager.disableDepth();
  }

  protected void drawRectangle(double zLevel, int left, int top, int right, int bottom, int color) {
    float a = (color >> 24 & 255) / 255.0F;
    float r = (color >> 16 & 255) / 255.0F;
    float g = (color >> 8 & 255) / 255.0F;
    float b = (color & 255) / 255.0F;
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
        GlStateManager.DestFactor.ZERO);
    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferbuilder = tessellator.getBuffer();
    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
    bufferbuilder.pos(right, top, zLevel).color(r, g, b, a).endVertex();
    bufferbuilder.pos(left, top, zLevel).color(r, g, b, a).endVertex();
    bufferbuilder.pos(left, bottom, zLevel).color(r, g, b, a).endVertex();
    bufferbuilder.pos(right, bottom, zLevel).color(r, g, b, a).endVertex();
    tessellator.draw();
    GlStateManager.shadeModel(GL11.GL_FLAT);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
  }

}
