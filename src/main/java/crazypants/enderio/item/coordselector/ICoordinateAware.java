package crazypants.enderio.item.coordselector;

import javax.annotation.Nonnull;

import crazypants.enderio.api.teleport.ITravelAccessable;

public interface ICoordinateAware {

  public interface SingleTarget extends ICoordinateAware, ITravelAccessable {

    void setTarget(@Nonnull TelepadTarget target);

  }

  public interface MultipleTargets extends ICoordinateAware {

    void addTarget(@Nonnull TelepadTarget target);

  }

}
