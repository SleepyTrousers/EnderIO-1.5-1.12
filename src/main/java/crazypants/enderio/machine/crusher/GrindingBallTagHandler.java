package crazypants.enderio.machine.crusher;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import crazypants.enderio.machine.recipe.CustomTagHandler;
import crazypants.enderio.machine.recipe.RecipeConfigParser;
import crazypants.enderio.machine.recipe.RecipeInput;

public class GrindingBallTagHandler implements CustomTagHandler{

  private static final String ELEMENT_ROOT = "grindingBalls";

  private static final String BALL_ROOT = "grindingBall";

  private static final String AT_GM = "grindingMultiplier";
  private static final String AT_PM = "powerMultiplier";
  private static final String AT_CM = "chanceMultiplier";
  private static final String AT_DMJ = "durationMJ";

  List<GrindingBall> balls = new ArrayList<GrindingBall>();

  boolean processStack = false;

  private float gm;
  private float pm;
  private float cm;
  private int dmj;

  @Override
  public boolean startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if(ELEMENT_ROOT.equals(localName)) {
      return true;
    }
    if(BALL_ROOT.equals(localName)) {
      gm = RecipeConfigParser.getFloatValue(AT_GM, attributes, 1);
      cm = RecipeConfigParser.getFloatValue(AT_CM, attributes, 1);
      pm = RecipeConfigParser.getFloatValue(AT_PM, attributes, 1);
      dmj = RecipeConfigParser.getIntValue(AT_DMJ, attributes, 2400);
      processStack = true;
      return true;
    }
    if(processStack && RecipeConfigParser.ELEMENT_ITEM_STACK.equals(localName)) {
      RecipeInput ri = RecipeConfigParser.getItemStack(attributes);
      GrindingBall gb = new GrindingBall(ri,gm,cm,pm,dmj);
      balls.add(gb);
    }

    return false;
  }

  @Override
  public boolean endElement(String uri, String localName, String qName) throws SAXException {
    if(ELEMENT_ROOT.equals(localName) || BALL_ROOT.equals(localName)) {
      processStack = false;
      return true;
    }
    return false;
  }

}
