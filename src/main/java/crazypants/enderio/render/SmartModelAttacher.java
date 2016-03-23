package crazypants.enderio.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.render.pipeline.RelayingBakedModel;

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

  @SuppressWarnings("rawtypes")
  private static final List<RegistrationHolder> blocks = new ArrayList<RegistrationHolder>();

  public static void register(Block block) {
    register(block, EnumRenderMode.RENDER, EnumRenderMode.DEFAULTS, EnumRenderMode.AUTO);
  }

  /**
   * Register a block that does not have one of our special rendering properties. All its blockstates will be rendered by our smart model, to the render mapper
   * cannot reference them and must get its blockstates from elsewhere.
   */
  public static void registerNoProps(Block block) {
    register(block, null, null, null);
  }

  public static <T extends Comparable<T>, V extends T> void register(Block block, IProperty<T> property, V defaultsValue, V autoValue) {
    blocks.add(new RegistrationHolder<T, V>(block, property, defaultsValue, autoValue));
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
    for (RegistrationHolder<?, ?> holder : blocks) {
      Block block = holder.block;
      Item item = Item.getItemFromBlock(block);
      if (item != null) {
        ModelResourceLocation location = new ModelResourceLocation(item.getRegistryName(), "inventory");
        if (item.getHasSubtypes()) {
          List<ItemStack> list = new ArrayList<ItemStack>();
          item.getSubItems(item, EnderIOTab.tabNoTab, list);
          for (ItemStack itemStack : list) {
            ModelLoader.setCustomModelResourceLocation(item, itemStack.getItemDamage(), location);
          }
        } else {
          ModelLoader.setCustomModelResourceLocation(item, 0, location);
        }
      }
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
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
        if (defaultBakedModel == null) {
          throw new RuntimeException("Model for state " + defaultState + " failed to load from " + defaultMrl + ". "
              + debugOutput(event.modelRegistry, defaultMrl));
        }
        RelayingBakedModel model = new RelayingBakedModel(defaultBakedModel);

        ModelResourceLocation itemMrl = new ModelResourceLocation(defaultMrl.getResourceDomain() + ":" + defaultMrl.getResourcePath() + "#inventory");
        event.modelRegistry.putObject(itemMrl, model);

        for (Entry<IBlockState, ModelResourceLocation> entry : locations.entrySet()) {
          if (entry.getKey().getValue(holder.property) == holder.autoValue) {
            event.modelRegistry.putObject(entry.getValue(), model);
          } else if (event.modelRegistry.getObject(entry.getValue()) == null) {
            event.modelRegistry.putObject(entry.getValue(), defaultBakedModel);
          }
        }
      } else {
        IBlockState defaultState = block.getDefaultState();
        ModelResourceLocation defaultMrl = locations.get(defaultState);
        IBakedModel defaultBakedModel = event.modelRegistry.getObject(defaultMrl);

        for (ModelResourceLocation mrl : locations.values()) {
          IBakedModel model = event.modelRegistry.getObject(mrl);
          event.modelRegistry.putObject(mrl, new RelayingBakedModel(model != null ? model : defaultBakedModel));
        }

        ModelResourceLocation itemMrl = new ModelResourceLocation(defaultMrl.getResourceDomain() + ":" + defaultMrl.getResourcePath() + "#inventory");
        if (event.modelRegistry.getObject(itemMrl) == null) {
          event.modelRegistry.putObject(itemMrl, new RelayingBakedModel(defaultBakedModel));
        }
      }
    }
  }

  private static String debugOutput(IRegistry<ModelResourceLocation, IBakedModel> modelRegistry, ModelResourceLocation defaultMrl) {
    String prefix = defaultMrl.getResourceDomain()+ ":" + defaultMrl.getResourcePath();
    if (modelRegistry instanceof RegistrySimple) {
      RegistrySimple rg = (RegistrySimple) modelRegistry;
      StringBuilder sb = new StringBuilder();
      for (Object key : rg.getKeys()) {
        if (key.toString().startsWith(prefix)) {
          sb.append(key + "; ");
        }
      }
      if (sb.length() > 0) {
        sb.setLength(sb.length() - 2);
      } else {
        sb.append("(none)");
      }
      return "Loaded states for " + prefix + " are: " + sb.toString();
    } else {
      return "Loaded states could not be determined because modelRegistry is not a RegistrySimple.";
    }
  }
  
  
}
