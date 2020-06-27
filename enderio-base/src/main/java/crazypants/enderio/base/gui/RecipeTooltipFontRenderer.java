package crazypants.enderio.base.gui;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Special font renderer that can render a recipe (crafting 3x3) to be used for tooltips.
 * <p>
 * To use this in a GUI:
 * <ul>
 * <li>The GUI must be a subclass if {@link GuiContainerBaseEIO}.
 * <li>Add a field of the type RecipeTooltipFontRenderer to your class. <br>
 * <code>final @Nonnull RecipeTooltipFontRenderer rtfr;</code>
 * <li>Initialize that field in your constructor. <br>
 * <code>rtfr = new RecipeTooltipFontRenderer(this);</code>
 * <li>Override getFontRenderer() to return it. <br>
 * <code>@Override<br>public @Nonnull FontRenderer getFontRenderer() {<br>return rtfr;<br>}</code>
 * <li>Register your recipe(s) with the font renderer. <br>
 * <code>rtfr.registerRecipe("abc123", new NNList<>(...));</code><br>
 * The ID can be any string, it is fine to replace already registered recipes. The NNList should have up to 9 ItemStacks for the 9 slots of the crafting grid
 * (in reading order).
 * <li>Add 6(!) lines to any tooltip that is part of your GUI. The first one has {@link #RECIPE_ID} followed directly by the ID you used to register the recipe
 * to be rendered followed by {@link #RECIPE_END}. The other 5 have {@link #RECIPE}, those only provide enough space for the recipe to be rendered. Both types
 * of lines can have extra text which is rendered to the right of the recipe grid (even if it was in front of the marker).
 * </ul>
 */
public class RecipeTooltipFontRenderer extends FontRenderer {

<<<<<<< HEAD
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

    protected final @Nonnull GuiContainerBaseEIO<?> gui;
    protected final @Nonnull Map<String, NNList<ItemStack>> recipes = new HashMap<>();

    @SuppressWarnings("null")
    public RecipeTooltipFontRenderer(@Nonnull GuiContainerBaseEIO<?> gui) {
        super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
        if (Minecraft.getMinecraft().gameSettings.language != null) {
            setUnicodeFlag(Minecraft.getMinecraft().isUnicode());
            setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
        }
        onResourceManagerReload(null);
        // This renderer should not be kept around for longer than a GUI is open, so we don't need to register it for
        // resource manager reloads.
        this.gui = gui;
    }

    public void registerRecipe(String id, @Nonnull NNList<ItemStack> recipe) {
        recipes.put(id, recipe);
    }

    @Override
    public int getStringWidth(@Nonnull String text) {
        final Matcher match = PATTERN.matcher(text);
        if (match.find()) {
            final int width = super.getStringWidth(match.replaceFirst(""));
            return FULL_WIDTH + (width > 0 ? width + 2 : 0);
        }
        return super.getStringWidth(text);
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
            return super.drawStringWithShadow(match.replaceFirst(""), x + FULL_WIDTH + 2, y, color);
        } else {
            return super.drawStringWithShadow(text, x, y, color);
        }
    }

    @SuppressWarnings("null")
    protected void drawRecipeAt(int x, int y, @Nonnull String id, int color) {
        float old = gui.setZLevel(300.0F);
        gui.drawGradientRect(x + 0 * WIDTH, y + 0 * WIDTH, x + 3 * WIDTH + BORDER, y + 3 * WIDTH + BORDER, color & 0xFF7F7F7F, color & 0xFF7F7F7F);

        for (int r = 0; r < 4; r++) {
            gui.drawGradientRect(x + 0 * WIDTH, y + r * WIDTH, x + 3 * WIDTH + BORDER, y + r * WIDTH + BORDER, color, color);
            gui.drawGradientRect(x + r * WIDTH, y + 0 * WIDTH, x + r * WIDTH + BORDER, y + 3 * WIDTH + BORDER, color, color);
        }
        gui.setZLevel(old);

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();

        final RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        old = renderItem.zLevel;
        renderItem.zLevel = 400;
        int pos = 0;
        for (ItemStack stack : NullHelper.first(recipes.get(id), NNList.<ItemStack> emptyList())) {
            Point point = LOCS.get(pos++);
            gui.drawFakeItemStack(x + point.x, y + point.y, stack);
        }
        renderItem.zLevel = old;

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
    }
=======
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

  protected final @Nonnull GuiContainerBaseEIO<?> gui;
  protected final @Nonnull Map<String, NNList<ItemStack>> recipes = new HashMap<>();

  @SuppressWarnings("null")
  public RecipeTooltipFontRenderer(@Nonnull GuiContainerBaseEIO<?> gui) {
    super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
    if (Minecraft.getMinecraft().gameSettings.language != null) {
      setUnicodeFlag(Minecraft.getMinecraft().isUnicode());
      setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
    }
    onResourceManagerReload(null);
    // This renderer should not be kept around for longer than a GUI is open, so we don't need to register it for
    // resource manager reloads.
    this.gui = gui;
  }

  public void registerRecipe(String id, @Nonnull NNList<ItemStack> recipe) {
    recipes.put(id, recipe);
  }

  @Override
  public int getStringWidth(@Nonnull String text) {
    final Matcher match = PATTERN.matcher(text);
    if (match.find()) {
      final int width = super.getStringWidth(match.replaceFirst(""));
      return FULL_WIDTH + (width > 0 ? width + 2 : 0);
    }
    return super.getStringWidth(text);
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
      return super.drawStringWithShadow(match.replaceFirst(""), x + FULL_WIDTH + 2, y, color);
    } else {
      return super.drawStringWithShadow(text, x, y, color);
    }
  }

  @SuppressWarnings("null")
  protected void drawRecipeAt(int x, int y, @Nonnull String id, int color) {
    float old = gui.setZLevel(300.0F);
    gui.drawGradientRect(x + 0 * WIDTH, y + 0 * WIDTH, x + 3 * WIDTH + BORDER, y + 3 * WIDTH + BORDER, color & 0xFF7F7F7F, color & 0xFF7F7F7F);

    for (int r = 0; r < 4; r++) {
      gui.drawGradientRect(x + 0 * WIDTH, y + r * WIDTH, x + 3 * WIDTH + BORDER, y + r * WIDTH + BORDER, color, color);
      gui.drawGradientRect(x + r * WIDTH, y + 0 * WIDTH, x + r * WIDTH + BORDER, y + 3 * WIDTH + BORDER, color, color);
    }
    gui.setZLevel(old);

    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
    RenderHelper.enableGUIStandardItemLighting();
    GlStateManager.enableRescaleNormal();

    final RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
    old = renderItem.zLevel;
    renderItem.zLevel = 400;
    int pos = 0;
    for (ItemStack stack : NullHelper.first(recipes.get(id), NNList.<ItemStack> emptyList())) {
      Point point = LOCS.get(pos++);
      gui.drawFakeItemStack(x + point.x, y + point.y, stack);
    }
    renderItem.zLevel = old;
    
    GlStateManager.disableRescaleNormal();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableLighting();
    GlStateManager.disableDepth();
  }
>>>>>>> pr/1

}
