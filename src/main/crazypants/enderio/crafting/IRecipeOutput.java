package crazypants.enderio.crafting;

public interface IRecipeOutput extends IRecipeComponent {

  /**
   * The '% chance' this output is generated. This value is in the range 0-1.
   * 
   * @return
   */
  float getChance();

}
