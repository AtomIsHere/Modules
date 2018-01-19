package com.github.atomishere.modules;

import com.github.atomishere.modules.api.Module;
import com.github.atomishere.modules.api.ModuleData;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Code borrowed from PluginClassLoader
 *
 * @see org.bukkit.plugin.java.PluginClassLoader
 */
final class ModuleClassLoader extends URLClassLoader {
    private final Module module;

    ModuleClassLoader(ClassLoader parent, File file, ModuleData data) throws ModuleException, MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName("modules.ModuleClass", true, this);
            } catch(ClassNotFoundException ex) {
                throw new ModuleException("Cannot find Module class", ex);
            }

            Class<? extends Module> moduleClass;
            try {
                moduleClass = jarClass.asSubclass(Module.class);
            } catch(ClassCastException ex) {
                throw new ModuleException("Module class does not implement Module", ex);
            }

            Constructor<? extends Module> con = moduleClass.getConstructor(ModuleData.class);
            module = con.newInstance(data);
        } catch(IllegalAccessException ex) {
            throw new ModuleException("No public constructor", ex);
        } catch(InstantiationException ex) {
            throw new ModuleException("Abnormal plugin type", ex);
        } catch(NoSuchMethodException ex) {
            throw new ModuleException("Malformed constructor", ex);
        } catch(InvocationTargetException ex) {
            throw new ModuleException("Unhandled Exception", ex);
        }
    }

    public Module getModule() {
        return module;
    }
}
