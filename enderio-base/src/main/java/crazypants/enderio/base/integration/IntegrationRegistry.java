package crazypants.enderio.base.integration;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.ShortCallback;
import com.enderio.core.common.util.UserIdent;

import crazypants.enderio.base.EnderIO;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.RegistryBuilder;

@EventBusSubscriber(modid = EnderIO.MODID)
public class IntegrationRegistry {

  private static IForgeRegistry<IIntegration> REGISTRY = null;

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerRegistry(@Nonnull RegistryEvent.NewRegistry event) {
    REGISTRY = new RegistryBuilder<IIntegration>().setName(new ResourceLocation(EnderIO.DOMAIN, "integration")).setType(IIntegration.class)
        .setIDRange(0, Integer.MAX_VALUE - 1).create();
  }

  public static boolean isInSameTeam(@Nonnull UserIdent identA, @Nonnull UserIdent identB) {
    return NNList.wrap(IntegrationRegistry.REGISTRY.getValues()).apply(new ShortCallback<IIntegration>() {
      @Override
      public boolean apply(@Nonnull IIntegration e) {
        return e.isInSameTeam(identA, identB);
      }
    });
  }

}
