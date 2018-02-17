package crazypants.enderio.base.diagnostics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.profiler.Profiler;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Prof {

  public static void start(@Nonnull IBlockAccess world, @Nonnull String section) {
    start(getProfiler(world), section);
  }

  public static void start(@Nonnull IBlockAccess world, @Nonnull String section, @Nullable Object param) {
    start(getProfiler(world), section, param);
  }

  public static void next(@Nonnull IBlockAccess world, @Nonnull String section) {
    next(getProfiler(world), section);
  }

  public static void next(@Nonnull IBlockAccess world, @Nonnull String section, @Nullable Object param) {
    next(getProfiler(world), section, param);
  }

  public static void stop(@Nonnull IBlockAccess world) {
    stop(getProfiler(world));
  }

  public static void start(Profiler profiler, @Nonnull String section) {
    if (profiler != null) {
      profiler.startSection(section);
    }
  }

  public static void start(@Nullable Profiler profiler, @Nonnull String section, @Nullable Object param) {
    if (profiler != null) {
      start(profiler, makeSection(section, param));
    }
  }

  public static void next(@Nullable Profiler profiler, @Nonnull String section) {
    if (profiler != null) {
      profiler.endStartSection(section);
    }
  }

  public static void next(@Nullable Profiler profiler, @Nonnull String section, @Nullable Object param) {
    if (profiler != null) {
      profiler.endStartSection(makeSection(section, param));
    }
  }

  public static void stop(@Nullable Profiler profiler) {
    if (profiler != null) {
      profiler.endSection();
    }
  }

  public static void stop(@Nullable Profiler profiler, int count) {
    if (profiler != null) {
      for (int i = 0; i < count; i++) {
        profiler.endSection();
      }
    }
  }

  private static @Nonnull String makeSection(@Nonnull String section, @Nullable Object param) {
    if (param != null) {
      return section + param.getClass().getName().replace(".", "_").replaceAll("[^a-zA-Z_]", "").replace("crazypants_enderio_machines_machine_", "");
    } else {
      return section;
    }
  }

  private static Profiler getProfiler(@Nonnull IBlockAccess world) {
    if (world instanceof World) {
      Profiler profiler = ((World) world).profiler;
      return profiler.profilingEnabled ? profiler : null;
    } else {
      return null;
    }
  }

}
