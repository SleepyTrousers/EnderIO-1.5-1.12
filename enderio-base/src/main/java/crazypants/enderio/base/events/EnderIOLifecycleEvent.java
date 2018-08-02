package crazypants.enderio.base.events;

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

  public abstract static class PostInit extends EnderIOLifecycleEvent {

    public static class Pre extends PostInit {

    }

    public static class Post extends PostInit {

    }

  }

}
