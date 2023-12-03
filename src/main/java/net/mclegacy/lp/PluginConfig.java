package net.mclegacy.lp;

import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.UUID;

public class PluginConfig extends Configuration
{
    public PluginConfig(File file)
    {
        super(file);
    }

    public void reload()
    {
        load();
        write();
        save();
    }

    public void write()
    {
        generateConfigOption("holderName", "You will set your Holder Name when you fill out the registration form. Enter that value here, and be EXACT.");
        generateConfigOption("c2key", "Visit the MCLegacy server registration form to obtain a C2 key @ mclegacy.net/c2");

        //TODO: configurable error messages
    }

    private void generateConfigOption(String key, Object defaultValue)
    {
        if (this.getProperty(key) == null) this.setProperty(key, defaultValue);
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    public Object getConfigOption(String key)
    {
        return this.getProperty(key);
    }

    public Object getConfigOption(String key, Object defaultValue)
    {
        Object value = getConfigOption(key);
        if (value == null) value = defaultValue;
        return value;
    }
}
