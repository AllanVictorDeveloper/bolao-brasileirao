package com.bolao.brasileirao.controller;

import com.bolao.brasileirao.entity.Usuario;
import com.bolao.brasileirao.enums.Perfil;
import com.bolao.brasileirao.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
public class AdminUsuarioController {

    private final UsuarioService usuarioService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pagina", "admin-usuarios");
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin-usuarios";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("pagina", "admin-usuarios");
        model.addAttribute("perfis", Perfil.values());
        return "admin-usuario-form";
    }

    @PostMapping("/novo")
    public String salvar(@ModelAttribute Usuario usuario,
                         @RequestParam String senhaRaw,
                         @AuthenticationPrincipal Usuario admin,
                         RedirectAttributes redirect) {

        if (usuarioService.emailJaExiste(usuario.getEmail())) {
            redirect.addFlashAttribute("erro", "E-mail já cadastrado: " + usuario.getEmail());
            return "redirect:/admin/usuarios";
        }

        usuario.setPassword(passwordEncoder.encode(senhaRaw));
        usuario.setAtivo(true);
        usuario.setMudouSenha(false);
        usuario.setCriadoPor(admin.getNome() + " " + admin.getSobrenome());
        usuarioService.salvar(usuario);

        redirect.addFlashAttribute("sucesso", "Usuário " + usuario.getNome() + " cadastrado com sucesso.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/toggle-ativo")
    public String toggleAtivo(@PathVariable Long id, RedirectAttributes redirect) {
        usuarioService.toggleAtivo(id);
        return "redirect:/admin/usuarios";
    }
}
