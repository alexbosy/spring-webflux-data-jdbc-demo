package io.cryptorush.userservice.rest.util;

import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;

@Component
public class SpringWebFluxIpResolver implements IpResolver {

    public static final String UNKNOWN_IP = "unknown";

    @Override
    public String resolveIpAddress(InetSocketAddress remoteAddress) {
        if (remoteAddress != null) {
            String hostName = remoteAddress.getHostName();
            InetAddress address = remoteAddress.getAddress();
            if (address != null) {
                return address.getHostAddress();
            } else if (hostName != null) {
                //The real IP from X-Forwarded-For header. It will be populated by ForwardedHeaderTransformer bean.
                return hostName;
            }
        }
        return UNKNOWN_IP;
    }
}
