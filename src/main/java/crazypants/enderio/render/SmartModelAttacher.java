package crazypants.enderio.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import crazypants.enderio.EnderIOTab;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SmartModelAttacher {

  private static class RegistrationHolder<T extends Comparable<T>, V extends T> {
    Block block;
    IProperty<T> property;
    V defaultsValue;
    V autoValue;

    protected RegistrationHolder(Block block, IProperty<T> property, V defaultsValue, V autoValue) {
      this.block = block;
      this.property = property;
      this.defaultsValue = defaultsValue;
      this.autoValue = autoValue;
    }
  }

  private static final List<RegistrationHolder> blocks = new ArrayList<RegistrationHolder>();

  public static void register(Block block) {
    register(block, EnumRenderMode.RENDER, EnumRenderMode.DEFAULTS, EnumRenderMode.AUTO);
  }

  public static <T extends Comparable<T>, V extends T> void register(Block block, IProperty<T> property, V defaultsValue, V autoValue) {
    blocks.add(new RegistrationHolder(block, property, defaultsValue, autoValue));
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
    for (RegistrationHolder holder : blocks) {
      Block block = holder.block;
      Item item = Item.getItemFromBlock(block);
      ModelResourceLocation location = new ModelResourceLocation(item.getRegistryName(), "inventory");
      if (item.getHasSubtypes()) {
        List<ItemStack> list = new ArrayList<ItemStack>();
        block.getSubBlocks(item, EnderIOTab.tabEnderIO, list);
        for (ItemStack itemStack : list) {
          ModelLoader.setCustomModelResourceLocation(item, itemStack.getItemDamage(), location);
        }
      } else {        
        ModelLoader.setCustomModelResourceLocation(item, 0, location);        
      }
    }
  }

  @SubscribeEvent()
  @SideOnly(Side.CLIENT)
  public void bakeModels(ModelBakeEvent event) {
    for (RegistrationHolder holder : blocks) {
      Block block = holder.block;
      Map<IBlockState, ModelResourceLocation> locations = new DefaultStateMapper().putStateModelLocations(block);

      if (holder.property != null && block.getDefaultState().getPropertyNames().contains(holder.property)) {
        IBlockState defaultState = block.getDefaultState().withProperty(holder.property, holder.defaultsValue);
        ModelResourceLocation defaultMrl = locations.get(defaultState);
        IBakedModel defaultBakedModel = event.modelRegistry.getObject(defaultMrl);
        MachineSmartModel model = new MachineSmartModel(defaultBakedModel);

        ModelResourceLocation itemMrl = new ModelResourceLocation(defaultMrl.getResourceDomain() + ":" + defaultMrl.getResourcePath() + "#inventory");
        event.modelRegistry.putObject(itemMrl, model);

        for (Entry<IBlockState, ModelResourceLocation> entry : locations.entrySet()) {
          if (entry.getKey().getValue(holder.property) == holder.autoValue) {
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
