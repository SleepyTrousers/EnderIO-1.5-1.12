package crazypants.enderio.tool;

import crazypants.enderio.api.tool.ITool;
import java.lang.reflect.Method;

public interface IToolImpl {

    Class<?> getInterface();

    Object handleMethod(ITool yetaWrench, Method method, Object[] args);
}
