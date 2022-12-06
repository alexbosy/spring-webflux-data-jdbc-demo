package io.cryptorush.userservice.rest.util;

import java.net.InetSocketAddress;

public interface IpResolver {
    String resolveIpAddress(InetSocketAddress inetSocketAddress);
}
