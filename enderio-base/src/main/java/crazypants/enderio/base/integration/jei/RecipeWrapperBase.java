package crazypants.enderio.base.integration.jei;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.vecmath.Vector2i;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.recipe.RecipeLevel;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public abstract class RecipeWrapperBase implements IRecipeWrapper {

  private static final Map<Class<? extends RecipeWrapperBase>, Triple<IDrawable, IDrawable, Vector2i>> LEVEL = new IdentityHashMap<>();

  protected abstract RecipeLevel getRecipeLevel();

  public static void setLevelData(@Nonnull Class<? extends RecipeWrapperBase> clazz, @Nonnull IGuiHelper guiHelper, int x, int y, String textureSimple,
      String textureNormal) {
    IDrawable simpleFront = textureSimple != null ? guiHelper.createDrawable(new ResourceLocation(EnderIO.DOMAIN, textureSimple), 0, 0, 16, 16, 16, 16) : null;
    IDrawable normalFront = textureNormal != null ? guiHelper.createDrawable(new ResourceLocation(EnderIO.DOMAIN, textureNormal), 0, 0, 16, 16, 16, 16) : null;
    LEVEL.put(clazz, Triple.of(simpleFront, normalFront, new Vector2i(x, y)));
  }

  public RecipeWrapperBase() {
    super();
  }

  @Override
  public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
    Triple<IDrawable, IDrawable, Vector2i> level = LEVEL.get(this.getClass());
    if (level != null) {
      switch (getRecipeLevel()) {
      case ADVANCED:
        if (level.getMiddle() != null) {
          level.getMiddle().draw(minecraft, level.getRight().x, level.getRight().y);
        }
        break;
      case NORMAL:
        if (level.getLeft() != null) {
          level.getLeft().draw(minecraft, level.getRight().x, level.getRight().y);
        }
        break;
      case SIMPLE:
      case IGNORE:
      default:
        return;
      }
      IconEIO.map.render(IconEIO.GENERIC_VERBOTEN, level.getRight().x + 6, level.getRight().y - 6, true);
    }
  }

  @Override
  @Nonnull
  public List<String> getTooltipStrings(int mouseX, int mouseY) {
    Triple<IDrawable, IDrawable, Vector2i> level = LEVEL.get(this.getClass());
    if (level != null && mouseX >= level.getRight().x && mouseX <= level.getRight().x + 6 + 16 && mouseY >= level.getRight().y - 6
        && mouseY <= level.getRight().y + 16) {
      switch (getRecipeLevel()) {
      case ADVANCED:
        return Lang.JEI_NOTNORMAL.getLines();
      case NORMAL:
        return Lang.JEI_NOTSIMPLE.getLines();
      case SIMPLE:
      case IGNORE:
      default:
        break;
      }
    }
    return IRecipeWrapper.super.getTooltipStrings(mouseX, mouseY);
  }

}