package crazypants.enderio.base.events;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class EnderIOLifecycleEvent extends Event {

  public abstract static class Config extends EnderIOLifecycleEvent {

    public static class Pre extends Config {

    }

    public static class Post extends Config {

    }

  }

  public static class PreInit extends EnderIOLifecycleEvent {

  }

  public abstract static class Init extends EnderIOLifecycleEvent {

    private final @Nonnull FMLInitializationEvent event;

    public @Nonnull FMLInitializationEvent getEvent() {
      return event;
    }

    public Init(@Nonnull FMLInitializationEvent event) {
      this.event = event;
    }

    public static class Pre extends Init {

      public Pre(@Nonnull FMLInitializationEvent event) {
        super(event);
      }

    }

    public static class Normal extends Init {

      public Normal(@Nonnull FMLInitializationEvent event) {
        super(event);
      }

    }

    public static class Post extends Init {

      public Post(@Nonnull FMLInitializationEvent event) {
        super(event);
      }

    }

  }

  public abstract static class PostInit extends EnderIOLifecycleEvent {

    public static class Pre extends PostInit {

    }

    public static class Post extends PostInit {

    }

  }

}
