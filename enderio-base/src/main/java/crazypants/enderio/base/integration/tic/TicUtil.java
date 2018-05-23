package crazypants.enderio.base.integration.tic;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.farming.FarmersRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class TicUtil {

  private TicUtil() {
  }

  @SubscribeEvent
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    FarmersRegistry.registerLogs("blockSlimeCongealed"); // oreDict
  }

  private static final @Nonnull String BASE_DATA = "TinkerData";
  private static final @Nonnull String BASE_MODIFIERS = "Modifiers";
  private static int TAG_TYPE_STRING = (new NBTTagString()).getId();

  public static final @Nonnull String BEHEADING = "beheading";
  public static final @Nonnull String CLEAVER = "beheading_cleaver";

  public static int getModifier(NBTTagCompound root, String identifier) {
    NBTTagList tagList = getBaseTag(root).getTagList(BASE_MODIFIERS, TAG_TYPE_STRING);

    for (int i = 0; i < tagList.tagCount(); i++) {
      if (identifier.equals(tagList.getStringTagAt(i))) {
        return tagList.getIntAt(i);
      }
    }

    return 0;
  }

  public static @Nonnull NBTTagCompound getBaseTag(NBTTagCompound root) {
    if (root == null || !root.hasKey(BASE_DATA)) {
      return new NBTTagCompound();
    }

    return root.getCompoundTag(BASE_DATA);
  }

  public static boolean isTinkerTool(NBTTagCompound root) {
    return root != null && root.hasKey(BASE_DATA);
  }

}
