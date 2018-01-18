package com.github.atomishere.modules;

import com.github.atomishere.modules.api.Module;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Code borrowed from PluginClassLoader
 *
 * @see org.bukkit.plugin.java.PluginClassLoader
 */
final class ModuleClassLoader extends URLClassLoader {
    private final Module module;

    ModuleClassLoader(ClassLoader parent, File file) throws ModuleException, MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);

        try {
            Class jarClass;
            try {
                jarClass = Class.forName("modules.ModuleClass", true, this);
            } catch (ClassNotFoundException ex) {
                throw new ModuleException("Cannot find Module class", ex);
            }

            Class moduleClass;
            try {
                moduleClass = jarClass.asSubclass(Module.class);
            } catch (ClassCastException ex) {
                throw new ModuleException("Module class does not implement Module", ex);
            }

            this.module = (Module) moduleClass.newInstance();
        } catch (IllegalAccessException ex) {
            throw new ModuleException("No public constructor", ex);
        } catch (InstantiationException ex) {
            throw new ModuleException("Abnormal plugin type", ex);
        }
    }

    public Module getModule() {
        return module;
    }
}
