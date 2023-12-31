package net.mclegacy.lp.auth;

public interface AuthPluginHandler
{
    void authenticate(String username, String ip) throws AuthHandlerException;
    void deleteAccount(String username) throws AuthHandlerException;
    boolean isInstalled();
}
