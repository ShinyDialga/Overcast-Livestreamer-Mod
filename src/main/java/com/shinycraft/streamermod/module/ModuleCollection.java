package com.shinycraft.streamermod.module;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShinyDialga45 on 10/17/2015.
 */
public class ModuleCollection {

    private static List<Module> modules = new ArrayList<Module>();

    public static List<Module> getModules() {
        return modules;
    }

    public static Module getModule(Class<? extends Module> clazz) {
        for (Module module : getModules()) {
            if (module.getClass().equals(clazz)) {
                return module;
            }
        }
        return null;
    }

}
