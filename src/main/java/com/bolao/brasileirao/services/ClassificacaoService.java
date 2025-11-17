package com.bolao.brasileirao.services;

import com.bolao.brasileirao.entity.Palpite;
import com.bolao.brasileirao.entity.Usuario;
import com.bolao.brasileirao.repository.PalpiteRepository;
import com.bolao.brasileirao.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClassificacaoService {

    private final PalpiteRepository palpiteRepository;
    private final UsuarioRepository usuarioRepository;

    public ClassificacaoService(PalpiteRepository palpiteRepository,
                                UsuarioRepository usuarioRepository) {
        this.palpiteRepository = palpiteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<ClassificacaoDto> rankingGeral() {
        List<Palpite> todosPalpites = palpiteRepository.findAll();

        Map<Long, Integer> pontosPorUsuario = new HashMap<>();

        for (Palpite p : todosPalpites) {
            if (p.getPontos() == null) continue;
            pontosPorUsuario.merge(p.getUsuario().getId(), p.getPontos(), Integer::sum);
        }

        return pontosPorUsuario.entrySet().stream()
                .map(entry -> {
                    Usuario u = usuarioRepository.findById(entry.getKey()).orElseThrow();
                    return new ClassificacaoDto(u, entry.getValue());
                })
                .sorted(Comparator.comparingInt(ClassificacaoDto::pontos).reversed())
                .collect(Collectors.toList());
    }

    public record ClassificacaoDto(Usuario usuario, Integer pontos) { }
}
