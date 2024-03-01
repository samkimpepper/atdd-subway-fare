package nextstep.auth.principal;

import lombok.extern.slf4j.Slf4j;
import nextstep.auth.AuthenticationException;
import nextstep.auth.token.JwtTokenProvider;
import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.alg.util.Pair;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

@Slf4j
public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {
    private JwtTokenProvider jwtTokenProvider;

    public AuthenticationPrincipalArgumentResolver(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String authorization = webRequest.getHeader("Authorization");
        boolean required = Objects.requireNonNull(parameter.getParameterAnnotation(AuthenticationPrincipal.class)).required();
        if (!required && authorization == null) {
            return new AnonymousMember();
        }

        if (!"bearer".equalsIgnoreCase(authorization.split(" ")[0]) || authorization.split(" ").length <= 1) {
            throw new AuthenticationException();
        }
        String token = authorization.split(" ")[1];

        Triple<Long, String, Integer> tokenBody = jwtTokenProvider.getPrincipal(token);
        Long id = tokenBody.getLeft();
        String email = tokenBody.getMiddle();
        int age = tokenBody.getRight();

        return new LoginMember(id, email, age);
    }
}
