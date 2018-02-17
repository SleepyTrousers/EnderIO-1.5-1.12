package crazypants.enderio.base.diagnostics;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ProfilerEIO extends Profiler {

  private static class Element {
    String name;
    RuntimeException starter, ender;

    Element(String name, RuntimeException starter) {
      this.name = name;
      this.starter = starter;
    }
  }

  private List<Element> stack = new LinkedList<>();
  private List<Element> discarded = new LinkedList<>();

  public ProfilerEIO() {
  }

  @Override
  public void clearProfiling() {
    stack.clear();
    discarded.clear();
    super.clearProfiling();
  }

  @Override
  public void startSection(@Nonnull String name) {
    if (this.profilingEnabled) {
      super.startSection(name);
      if (stack.isEmpty() && name.equals("root")) {
        discarded.clear();
      }
      stack.add(0, new Element(name, new RuntimeException()));
    }
  }

  @Override
  public void endSection() {
    if (this.profilingEnabled) {
      if (stack.isEmpty()) {
        StringBuilder b = new StringBuilder();
        b.append("Profiler Underrun!\n");
        b.append("Last 20 endSection()s:\n");
        for (Element element : discarded) {
          b.append("Section: " + element.name + "\n");
          b.append("Starter: " + e2s(element.starter) + "\n");
          b.append("Ender: " + e2s(element.ender) + "\n\n");
        }
        Log.error(b.toString());
        throw new RuntimeException(b.toString());
      }
      Element element = stack.remove(0);
      element.ender = new RuntimeException();
      discarded.add(element);
      while (discarded.size() > 1000) {
        discarded.remove(0);
      }
      super.endSection();
    }
  }

  private String e2s(RuntimeException ex) {
    StringWriter errors = new StringWriter();
    ex.printStackTrace(new PrintWriter(errors));
    return errors.toString();
  }

  public static void init(FMLServerAboutToStartEvent event) {
    ReflectionHelper.setPrivateValue(MinecraftServer.class, event.getServer(), new ProfilerEIO(), "profiler", "field_71304_b");
  }

}
