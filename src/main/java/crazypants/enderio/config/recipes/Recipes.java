package crazypants.enderio.config.recipes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.enderio.config.recipes.xml.Alias;
import crazypants.enderio.config.recipes.xml.InvalidRecipeConfigException;
import crazypants.enderio.config.recipes.xml.Recipe;
import crazypants.enderio.config.recipes.xml.RecipeGameRecipe;
import net.minecraft.launchwrapper.Launch;

@XStreamAlias("recipes")
public class Recipes implements RecipeGameRecipe {

  @XStreamImplicit(itemFieldName = "alias")
  private List<Alias> aliases;

  @XStreamImplicit(itemFieldName = "recipe")
  private List<Recipe> recipes;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (recipes == null || recipes.isEmpty()) {
      throw new InvalidRecipeConfigException("No recipes");
    }
    return this;
  }

  @Override
  public boolean isValid() {
    return recipes != null;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  public void register() {
    Log.debug("Starting registering XML recipes");
    for (Recipe recipe : recipes) {
      recipe.register();
    }
    Log.debug("Done registering XML recipes");
  }

  public static Recipes fromFile() {
    XStream xstream = new XStream();
    if (Recipes.class.getClassLoader() != null) {
      xstream.setClassLoader(Recipes.class.getClassLoader());
    }
    xstream.processAnnotations(Recipes.class);

    try {
      return (Recipes) readConfig(xstream, "recipes.xml");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Object readConfig(XStream xstream, String fileName) throws IOException {
    File configFile = new File(Config.configDirectory, fileName);

    if (configFile.exists() && !((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"))) {
      return xstream.fromXML(configFile);
    }

    InputStream defaultFile = Recipes.class.getResourceAsStream("/assets/" + EnderIO.DOMAIN + "/config/" + fileName);
    if (defaultFile == null) {
      throw new IOException("Could not get resource /assets/" + EnderIO.DOMAIN + "/config/" + fileName + " from classpath. ");
    }

    Object myObject = xstream.fromXML(defaultFile);
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(configFile, false));
      xstream.toXML(myObject, writer);
    } finally {
      IOUtils.closeQuietly(writer);
    }
    return myObject;
  }

}
