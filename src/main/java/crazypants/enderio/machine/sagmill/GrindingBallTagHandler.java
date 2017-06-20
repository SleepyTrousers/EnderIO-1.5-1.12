package crazypants.enderio.machine.sagmill;

import crazypants.enderio.Log;
import crazypants.enderio.machine.recipe.CustomTagHandler;
import crazypants.enderio.machine.recipe.RecipeConfigParser;
import crazypants.enderio.machine.recipe.RecipeInput;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrindingBallTagHandler implements CustomTagHandler {

  private static final String ELEMENT_ROOT = "grindingBalls";

  private static final String BALL_ROOT = "grindingBall";

  private static final String EXCLUDES_ROOT = "excludes";

  private static final String AT_ID = "id";
  private static final String AT_REMOVE = "remove";
  private static final String AT_GM = "grindingMultiplier";
  private static final String AT_PM = "powerMultiplier";
  private static final String AT_CM = "chanceMultiplier";
  private static final String AT_DMJ = "durationRF";

  Map<String, GrindingBall> balls = new HashMap<String, GrindingBall>();
  List<RecipeInput> excludes = new ArrayList<RecipeInput>();

  boolean processStack = false;
  boolean processExclude = false;

  private String id;
  private float gm;
  private float pm;
  private float cm;
  private int drf;

  @Override
  public boolean startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (ELEMENT_ROOT.equals(localName)) {
      return true;
    }
    if (BALL_ROOT.equals(localName)) {
      id = RecipeConfigParser.getStringValue(AT_ID, attributes, null);
      if (id == null) {
        Log.warn("GrindingBallTagHandler: grinding ball specified without the '" + AT_ID + "' attribute. It will be ignored.");
        processStack = false;
        return true;
      }
      boolean remove = RecipeConfigParser.getBooleanValue(AT_REMOVE, attributes, false);
      if (remove) {
        GrindingBall res = balls.remove(id);
        if (res == null) {
          Log.warn("User config requested removal of grinding ball with id: " + id + " but it was not found.");
        } else {
          Log.info("Removed grinding ball with id=" + id + " due to user config.");
        }
        processStack = false;
        return true;
      }
      gm = RecipeConfigParser.getFloatValue(AT_GM, attributes, 1);
      cm = RecipeConfigParser.getFloatValue(AT_CM, attributes, 1);
      pm = RecipeConfigParser.getFloatValue(AT_PM, attributes, 1);
      drf = RecipeConfigParser.getIntValue(AT_DMJ, attributes, 24000);
      processStack = true;
      return true;
    }
    if (EXCLUDES_ROOT.equals(localName)) {
      processExclude = true;
      return true;
    }
    if (processStack && RecipeConfigParser.ELEMENT_ITEM_STACK.equals(localName)) {
      RecipeInput ri = RecipeConfigParser.getItemStack(attributes);
      if (ri != null) {
        GrindingBall gb = new GrindingBall(ri, gm, cm, pm, drf);
        balls.put(id, gb);
      }
    }
    if (processExclude && RecipeConfigParser.ELEMENT_ITEM_STACK.equals(localName)) {
      RecipeInput ri = RecipeConfigParser.getItemStack(attributes);
      if (ri != null) {
        boolean remove = RecipeConfigParser.getBooleanValue(AT_REMOVE, attributes, false);
        if (remove) {
          excludes.remove(ri);
          Log.info("Removed grinding ball exclude for " + ri.getInput().getDisplayName() + " due to user config.");
        } else {
          excludes.add(ri);
        }
      }
    }

    return false;
  }

  @Override
  public boolean endElement(String uri, String localName, String qName) throws SAXException {
    if (ELEMENT_ROOT.equals(localName) || BALL_ROOT.equals(localName) || EXCLUDES_ROOT.equals(localName)) {
      processStack = false;
      processExclude = false;
      return true;
    }
    return false;
  }

  @Override
  public void configProcessed() {
  }

}
