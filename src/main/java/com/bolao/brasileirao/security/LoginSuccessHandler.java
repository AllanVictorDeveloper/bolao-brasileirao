package com.bolao.brasileirao.security;

import com.bolao.brasileirao.entity.Usuario;
import com.bolao.brasileirao.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Object principal = authentication.getPrincipal();
        if (principal instanceof Usuario usuario) {
            usuarioRepository.findById(usuario.getId()).ifPresent(u -> {
                u.setUltimoLogin(LocalDateTime.now());
                usuarioRepository.save(u);
            });
        }

        response.sendRedirect(request.getContextPath() + "/home");
    }
}
