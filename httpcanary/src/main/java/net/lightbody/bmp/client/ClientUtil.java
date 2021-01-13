package net.lightbody.bmp.client;

import net.lightbody.bmp.proxy.dns.AdvancedHostResolver;
import net.lightbody.bmp.proxy.dns.NativeCacheManipulatingResolver;
import net.lightbody.bmp.proxy.dns.NativeResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A utility class with convenience methods for clients using BrowserMob Proxy in embedded mode.
 */
public class ClientUtil {
    /**
     * Creates a {@link NativeCacheManipulatingResolver} instance that can be used when
     * calling {@link net.lightbody.bmp.BrowserMobProxy#setHostNameResolver(AdvancedHostResolver)}.
     *
     * @return a new NativeCacheManipulatingResolver
     */
    public static AdvancedHostResolver createNativeCacheManipulatingResolver() {
        return new NativeCacheManipulatingResolver();
    }

    /**
     * Creates a {@link NativeResolver} instance that <b>does not support cache manipulation</b> that can be used when
     * calling {@link net.lightbody.bmp.BrowserMobProxy#setHostNameResolver(AdvancedHostResolver)}.
     *
     * @return a new NativeResolver
     */
    public static AdvancedHostResolver createNativeResolver() {
        return new NativeResolver();
    }



    /**
     * Attempts to retrieve a "connectable" address for this device that other devices on the network can use to connect to a local proxy.
     * This is a "reasonable guess" that is suitable in many (but not all) common scenarios.
     * TODO: define the algorithm used to discover a "connectable" local host
     *
     * @return a "reasonable guess" at an address that can be used by other machines on the network to reach this host
     */
    public static InetAddress getConnectableAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Could not resolve localhost", e);
        }
    }
}
