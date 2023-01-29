package crazypants.enderio.tool;

import java.lang.reflect.Method;

import crazypants.enderio.api.tool.ITool;

public interface IToolImpl {

    Class<?> getInterface();

    Object handleMethod(ITool yetaWrench, Method method, Object[] args);
}
