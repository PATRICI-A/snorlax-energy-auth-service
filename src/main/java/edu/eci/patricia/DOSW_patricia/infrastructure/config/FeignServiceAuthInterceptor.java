package edu.eci.patricia.DOSW_patricia.infrastructure.config;

import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Feign interceptor that adds a service-to-service JWT to all outbound calls to the profile service.
 * The KongAuthFilter in the profile service only decodes the base64 payload — it does not verify
 * the signature — so any JWT issued by this service's JwtService is accepted.
 */
@Component
@RequiredArgsConstructor
public class FeignServiceAuthInterceptor implements RequestInterceptor {

    private final JwtService jwtService;

    @Override
    public void apply(RequestTemplate template) {
        String token = jwtService.generateToken("internal-auth-service", "internal@auth.service");
        template.header("Authorization", "Bearer " + token);
    }
}
