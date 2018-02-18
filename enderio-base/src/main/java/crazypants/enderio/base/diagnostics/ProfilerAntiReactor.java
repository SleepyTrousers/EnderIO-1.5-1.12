package crazypants.enderio.base.diagnostics;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ProfilerAntiReactor extends Profiler {

  public ProfilerAntiReactor() {
  }

  @Override
  public void endSection() {
    if (this.profilingEnabled && !isBlacklisted(new RuntimeException())) {
      super.endSection();
    }
  }

  private boolean isBlacklisted(RuntimeException ex) {
    StackTraceElement[] stackTrace = ex.getStackTrace();
    for (int i = 0; i < stackTrace.length; i++) {
      if (stackTrace[i].getClassName().equals("erogenousbeef.bigreactors.common.multiblock.MultiblockReactor.updateServer")
          && stackTrace[i].getLineNumber() == 556) {
        return true;
      }
    }
    return false;
  }

  public static void init(FMLServerAboutToStartEvent event) {
    ReflectionHelper.setPrivateValue(MinecraftServer.class, event.getServer(), new ProfilerAntiReactor(), "profiler", "field_71304_b");
  }

}
