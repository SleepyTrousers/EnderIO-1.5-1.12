package crazypants.enderio.base.events;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ModSoundRegisterEvent extends Event {

  @SubscribeEvent
  public static void registerSounds(@Nonnull RegistryEvent.Register<SoundEvent> event) {
    MinecraftForge.EVENT_BUS.post(new ModSoundRegisterEvent(event.getRegistry()));
  }

  private final IForgeRegistry<SoundEvent> registry;

  public ModSoundRegisterEvent(IForgeRegistry<SoundEvent> registry) {
    this.registry = registry;
  }

  public IForgeRegistry<SoundEvent> getRegistry() {
    return registry;
  }

}
