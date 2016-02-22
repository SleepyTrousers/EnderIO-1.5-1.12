package crazypants.enderio.render;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.EnderIO;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.RegistrySimple;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * TODO: Needs some love, but for now it's working.
 *
 */
public class SmartModelAttacher {

  private static final List<String> blocks = new ArrayList<String>();

  public static void register(String blockname) {
    blocks.add(blockname);
  }

  public static void create() {
    MinecraftForge.EVENT_BUS.register(new SmartModelAttacher());
  }

  @SubscribeEvent()
  public void bakeModels(ModelBakeEvent event) {
    for (String blockname : blocks) {
      ModelResourceLocation defaultModelLocation = new ModelResourceLocation(EnderIO.DOMAIN + ":" + blockname + "#render=defaults");
      ModelResourceLocation autoModelLocation = new ModelResourceLocation(EnderIO.DOMAIN + ":" + blockname + "#render=auto");
      ModelResourceLocation itemModelLocation = new ModelResourceLocation(EnderIO.DOMAIN + ":" + blockname + "#inventory");
      IBakedModel bakedModel = event.modelRegistry.getObject(defaultModelLocation);
      MachineSmartModel model = new MachineSmartModel(bakedModel);
      event.modelRegistry.putObject(autoModelLocation, model);
      event.modelRegistry.putObject(itemModelLocation, model);
    }
  }
}
