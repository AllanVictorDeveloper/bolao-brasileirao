package com.bolao.brasileirao.configuration.initial;

import com.bolao.brasileirao.entity.Usuario;
import com.bolao.brasileirao.enums.Perfil;
import com.bolao.brasileirao.repository.UsuarioRepository;
import com.bolao.brasileirao.utils.PasswordDefaultUsuarios;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class InitialUserLoader implements ApplicationRunner {

    private final UsuarioRepository iUsuarioRepository;
    private final Validator validator; // Injetar o Validator

    public InitialUserLoader(UsuarioRepository iUsuarioRepository, Validator validator) {
        this.iUsuarioRepository = iUsuarioRepository;
        this.validator = validator;
    }


    @Override
    public void run(ApplicationArguments args) {
        String username = "allanvictor.developer@gmail.com";


        if (!this.iUsuarioRepository.existsUsuarioByEmail(username)) {
            Usuario usuario = new Usuario();
            usuario.setCriadoPor("sistema");
            usuario.setEmail("allanvictor.developer@gmail.com");
            usuario.setNome("Allan");
            usuario.setSobrenome("Victor");
            usuario.setPassword(new BCryptPasswordEncoder().encode(PasswordDefaultUsuarios.ADMIN_DEFAULT_PASSWORD));
            usuario.setPerfil(Perfil.ADMINISTRADOR);


            // Validar o usuário antes de salvar
            Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
            if (!violations.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (ConstraintViolation<Usuario> violation : violations) {
                    sb.append(violation.getMessage()).append("\n");
                }
                throw new IllegalArgumentException("Erro de validação: " + sb.toString());
            }

            this.iUsuarioRepository.save(usuario);
        }
    }
}
