package com.memopet.memopet.global.filter;

import com.memopet.memopet.domain.member.entity.RefreshToken;
import com.memopet.memopet.domain.member.repository.RefreshTokenRepository;
import com.memopet.memopet.domain.member.service.AuthService;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.config.Code;
import com.memopet.memopet.global.config.RSAKeyRecord;
import com.memopet.memopet.global.token.JwtTokenGenerator;
import com.memopet.memopet.global.token.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAccessTokenFilter extends OncePerRequestFilter {

    private final RSAKeyRecord rsaKeyRecord;
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final AuthService authService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try{
            log.info("[JwtAccessTokenFilter:doFilterInternal] :: Started ");

            log.info("[JwtAccessTokenFilter:doFilterInternal]Filtering the Http Request:{}",request.getRequestURI());

            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            JwtDecoder jwtDecoder =  NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
            if(!authHeader.startsWith("Bearer")){
                filterChain.doFilter(request,response);
                return;
            }
            final String token = authHeader.substring(7);

            String checkedToken = checkIfAccessTokenIsValid(token);
            log.info("step1");
            final Jwt jwtToken = jwtDecoder.decode(checkedToken);
            response.setHeader("Authorization", "Bearer " + checkedToken);
            log.info("prev : " + token);
            log.info("new : " + checkedToken);
            log.info("step2");
            final String userName = jwtTokenUtils.getUserName(jwtToken);
            if(!userName.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = jwtTokenUtils.userDetails(userName);
                if(jwtTokenUtils.isTokenValid(jwtToken,userDetails)){
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken createdToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    createdToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(createdToken);
                    SecurityContextHolder.setContext(securityContext);
                }
            }
            log.info("[JwtAccessTokenFilter:doFilterInternal] Completed");

            filterChain.doFilter(request,response);
        }catch (Exception e){
            log.error("[JwtAccessTokenFilter:doFilterInternal] Exception due to :{}",e.getMessage());
            setResponse(response, e.getMessage());
            //throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, jwtValidationException.getMessage());
        }
    }

    @Transactional(readOnly = false)
    private String checkIfAccessTokenIsValid(String token) {
        // refreshToken check
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByAccessToken(token);
        String newAccessToken = token;
        if(optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();

            if(LocalDateTime.now().isAfter(refreshToken.getExpiredAt())) {
                refreshToken.setRevoked(true);
                throw new BadRequestRuntimeException("JWT token is expired");
            }

            if(!refreshToken.isRevoked()) {
                // created a new Authentication and access token
                Authentication authentication = authService.createAuthenticationObject(refreshToken.getMember());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Create new accessToken ****");
                newAccessToken = jwtTokenGenerator.generateAccessToken(authentication);
                refreshToken.setAccessToken(newAccessToken);
            }
        }
        return newAccessToken;
    }

    private void setResponse(HttpServletResponse response, String msg) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);

        JSONObject responseJson = new JSONObject();
        responseJson.put("message", msg);
        responseJson.put("code", HttpServletResponse.SC_NOT_ACCEPTABLE);

        response.getWriter().print(responseJson);
    }
}
