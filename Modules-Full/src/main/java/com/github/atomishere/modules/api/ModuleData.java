package com.github.atomishere.modules.api;

import com.github.atomishere.modules.ModulesPlugin;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ModuleData extends ModulesPlugin {
    private final Properties data;

    @Getter
    private final String mainClass;
    @Getter
    private final String name;
    @Getter
    private final String author;
    @Getter
    private final String version;

    public ModuleData(InputStream stream) throws IOException {
        this.data = new Properties();
        this.data.load(stream);
        this.mainClass = this.data.getProperty("mainClass", "modules.ModuleClass");
        this.name = this.data.getProperty("name");
        this.author = this.data.getProperty("author");
        this.version = this.data.getProperty("version");
        if(this.name == null ||
                this.author == null ||
                this.version == null) {
            throw new InvalidModuleException();
        }
    }
}
