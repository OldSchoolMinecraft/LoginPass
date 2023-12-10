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
        generateConfigOption("holderUUID", "This is your servers unique ID on our backend servers. This will be provided after you complete the form.");
        generateConfigOption("c2key", "Visit the MCLegacy server registration form to obtain a C2 key @ mclegacy.net/c2");
        generateConfigOption("c2host", "0.0.0.0");
        generateConfigOption("c2port", 12992);
        generateConfigOption("debugMode", false);

        generateConfigOption("messages.authenticated", "&aYou have been authorized via &bMCLegacy!");
        generateConfigOption("messages.linkSuccess", "&aYour account has been linked successfully!");
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
