package crazypants.enderio.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class BaseRecipeHandler<T extends IRecipeWrapper> implements IRecipeHandler<T> {

  private final Class<T> clazz;
  private final String uid;
  
  public BaseRecipeHandler(Class<T> clazz, String uid) {  
    this.clazz = clazz;
    this.uid = uid;
  }

  @Override
  public Class<T> getRecipeClass() {
    return clazz;
  }

  @Override
  public String getRecipeCategoryUid() {
    return uid;
  }

  @Override
  public IRecipeWrapper getRecipeWrapper(T recipe) {
    return recipe;
  }
  
  @Override
  public boolean isRecipeValid(T recipe) {
    return true;
  }

}
