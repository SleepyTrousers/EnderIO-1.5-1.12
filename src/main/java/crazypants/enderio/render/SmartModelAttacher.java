package crazypants.enderio.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistrySimple;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SmartModelAttacher {

  private static final List<Block> blocks = new ArrayList<Block>();

  public static void register(Block block) {
    blocks.add(block);
  }

  public static void create() {
    MinecraftForge.EVENT_BUS.register(new SmartModelAttacher());
  }

  /**
   * Registers the default ModelResourceLocation for the items of all blocks that have registered for MachineSmartModel-based rendering.
   * <p>
   * For items that have subtypes, all subtypes that are exposed to the creative inventory are registered. All subtypes are registered to the same model, as the
   * smart model can be damage-aware.
   */
  @SideOnly(Side.CLIENT)
  public static void registerBlockItemModels() {
    for (Block block : blocks) {
      Item item = Item.getItemFromBlock(block);
      ModelResourceLocation location = new ModelResourceLocation(item.getRegistryName(), "inventory");
      if (item.getHasSubtypes()) {
        List<ItemStack> list = new ArrayList<ItemStack>();
        block.getSubBlocks(item, EnderIOTab.tabEnderIO, list);
        for (ItemStack itemStack : list) {
          // This works but I should not use it:
          Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, itemStack.getItemDamage(), location);
          // This doesn't work:
          ModelLoader.setCustomModelResourceLocation(item, itemStack.getItemDamage(), location);
          // TODO: Fix it if you want.
        }
      } else {
        // This works but I should not use it:
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, location);
        // This doesn't work:
        ModelLoader.setCustomModelResourceLocation(item, 0, location);
        // TODO: Fix it if you want.
      }
    }
  }

  @SubscribeEvent()
  @SideOnly(Side.CLIENT)
  public void bakeModels(ModelBakeEvent event) {
    for (Block block : blocks) {
      Map<IBlockState, ModelResourceLocation> locations = new DefaultStateMapper().putStateModelLocations(block);

      if (block.getDefaultState().getPropertyNames().contains(EnumRenderMode.RENDER)) {
        IBlockState defaultState = block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.DEFAULTS);
        ModelResourceLocation defaultMrl = locations.get(defaultState);
        IBakedModel defaultBakedModel = event.modelRegistry.getObject(defaultMrl);
        MachineSmartModel model = new MachineSmartModel(defaultBakedModel);

        ModelResourceLocation itemMrl = new ModelResourceLocation(defaultMrl.getResourceDomain() + ":" + defaultMrl.getResourcePath() + "#inventory");
        event.modelRegistry.putObject(itemMrl, model);

        for (Entry<IBlockState, ModelResourceLocation> entry : locations.entrySet()) {
          if (entry.getKey().getValue(EnumRenderMode.RENDER) == EnumRenderMode.AUTO) {
            event.modelRegistry.putObject(entry.getValue(), model);
          }
        }
      } else {
        IBlockState defaultState = block.getDefaultState();
        ModelResourceLocation defaultMrl = locations.get(defaultState);
        IBakedModel defaultBakedModel = event.modelRegistry.getObject(defaultMrl);

        for (ModelResourceLocation mrl : locations.values()) {
          event.modelRegistry.putObject(mrl, new MachineSmartModel(event.modelRegistry.getObject(mrl)));
        }

        ModelResourceLocation itemMrl = new ModelResourceLocation(defaultMrl.getResourceDomain() + ":" + defaultMrl.getResourcePath() + "#inventory");
        if (event.modelRegistry.getObject(itemMrl) == null) {
          event.modelRegistry.putObject(itemMrl, new MachineSmartModel(defaultBakedModel));
        }
      }
    }
  }

}
