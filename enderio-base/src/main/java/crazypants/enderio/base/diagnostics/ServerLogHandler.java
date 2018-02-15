package crazypants.enderio.base.diagnostics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.DefaultErrorHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ServerLogHandler implements Appender {

  private static ServerLogHandler instance;

  private boolean started;
  private ErrorHandler handler = new DefaultErrorHandler(this);

  public static void init() {
    instance = new ServerLogHandler();
    ((Logger) ObfuscationReflectionHelper.getPrivateValue(MinecraftServer.class, FMLCommonHandler.instance().getMinecraftServerInstance(), "LOGGER",
        "field_147145_h")).addAppender(instance);
    startAppender();
  }

  public static ServerLogHandler instance() {
    return instance;
  }

  public static void startAppender() {
    instance.start();
  }

  @Override
  public void append(LogEvent event) {
    if (event.getMessage().getFormattedMessage().startsWith("This crash report has been saved to: ")) {

      String report;
      try {
        report = readFileToString(new File(event.getMessage().getFormattedMessage().substring(37)));
      } catch (Throwable e) {
        StringWriter writer = new StringWriter();
        writer.write("Crash report could not be read!\r\n\r\n");
        e.printStackTrace(new PrintWriter(writer));
        report = writer.toString();
      }

      paste(report);
    }
  }

  public static String readFileToString(File f) throws FileNotFoundException, IOException {
    BufferedReader reader = new BufferedReader(new FileReader(f));
    String ret = "";

    char[] buffer = new char[1024];
    int read = 0;
    while ((read = reader.read(buffer)) != -1) {
      ret += String.valueOf(buffer, 0, read);
    }

    reader.close();
    return ret;
  }

  @Override
  public String getName() {
    return "CrashReporterAppender";
  }

  @Override
  public Layout<? extends Serializable> getLayout() {
    return null;
  }

  @Override
  public boolean ignoreExceptions() {
    return false;
  }

  @Override
  public ErrorHandler getHandler() {
    return handler;
  }

  @Override
  public void setHandler(ErrorHandler handler) {

  }

  @Override
  public void start() {
    this.started = true;
  }

  @Override
  public void stop() {
    this.started = false;
  }

  @Override
  public boolean isStarted() {
    return started;
  }

  @Override
  public State getState() {
    return started ? State.STARTED : State.STOPPED;
  }

  @Override
  public void initialize() {

  }

  @Override
  public boolean isStopped() {
    return !started;
  }

  public void paste(String text) {
    try {
      Http.post(new URL("http://eve.j-e-b.net/crash"), text);
    } catch (Throwable e) {

    }
  }
}