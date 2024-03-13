package com.gentle.store.shopping.cart.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Component
@Slf4j
public class UriHelper {

    /**
     * Basis-URI ermitteln, d.h. ohne Query-Parameter.
     *
     * @param request Servlet-Request
     * @return Die Basis-URI als String
     */
    public URI getBaseUri(final HttpServletRequest request) {
        final var forwardedHost = request.getHeader(Constants.X_FORWARDED_HOST);
        if (forwardedHost != null) {
            // Forwarding durch Kubernetes Ingress Controller oder Spring Cloud Gateway
            return getBaseUriForwarded(request, forwardedHost);
        }

        // KEIN Forwarding von einem API-Gateway
        // URI aus Schema, Host, Port und Pfad
        final var uriComponents = ServletUriComponentsBuilder.fromRequestUri(request).build();
        final var baseUri =
            STR."\{uriComponents.getScheme()}://\{uriComponents.getHost()}:\{uriComponents.getPort()}\{Constants.SHOPPING_CART_PATH}";
        log.debug("getBaseUri (ohne Forwarding): baseUri={}", baseUri);
        return URI.create(baseUri);
    }

    private URI getBaseUriForwarded(final HttpServletRequest request, final String forwardedHost) {
        // x-forwarded-host = Hostname des API-Gateways

        // "https" oder "http"
        final var forwardedProto = request.getHeader(Constants.X_FORWARDED_PROTO);
        if (forwardedProto == null) {
            throw new IllegalStateException(STR."Kein \"\{Constants.X_FORWARDED_PROTO}\" im Header");
        }

        var forwardedPrefix = request.getHeader(Constants.X_FORWARDED_PREFIX);
        // x-forwarded-prefix: null bei Kubernetes Ingress Controller bzw. "/kunden" bei Spring Cloud Gateway
        if (forwardedPrefix == null) {
            log.trace("getBaseUriForwarded: Kein \"{}\" im Header", Constants.X_FORWARDED_PREFIX);
            forwardedPrefix = Constants.CUSTOMER_PREFIX;
        }
        final var baseUri = STR."\{forwardedProto}://\{forwardedHost}\{forwardedPrefix}\{Constants.SHOPPING_CART_PATH}";
        log.debug("getBaseUriForwarded: baseUri={}", baseUri);
        return URI.create(baseUri);
    }
}
