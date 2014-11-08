package crazypants.enderio.tool;

import java.lang.reflect.Method;

public interface IToolImpl {

  Class<?> getInterface();

  Object handleMethod(ITool yetaWrench, Method method, Object[] args);

}
