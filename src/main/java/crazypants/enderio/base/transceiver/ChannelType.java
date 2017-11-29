package crazypants.enderio.base.transceiver;

import com.enderio.core.common.util.NNList;

public enum ChannelType {

  POWER,
  ITEM,
  FLUID;

  public static final NNList<ChannelType> VALUES = NNList.of(ChannelType.class);

}
