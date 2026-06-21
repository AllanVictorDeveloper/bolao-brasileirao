package com.bolao.brasileirao.services;

import com.bolao.brasileirao.entity.Palpite;
import com.bolao.brasileirao.entity.Usuario;
import com.bolao.brasileirao.repository.PalpiteRepository;
import com.bolao.brasileirao.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.*;
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

        Map<Long, Integer> pontosPorUsuario   = new HashMap<>();
        Map<Long, Integer> palpitesPorUsuario = new HashMap<>();
        Map<Long, Integer> placaresExatos     = new HashMap<>();

        for (Palpite p : todosPalpites) {
            Long uid = p.getUsuario().getId();
            palpitesPorUsuario.merge(uid, 1, Integer::sum);

            if (p.getPontos() != null) {
                pontosPorUsuario.merge(uid, p.getPontos(), Integer::sum);
            }

            if (p.cravouPlacar()) placaresExatos.merge(uid, 1, Integer::sum);
        }

        return usuarioRepository.findAllByOrderByNomeAsc().stream()
                .map(u -> new ClassificacaoDto(
                        u,
                        pontosPorUsuario.getOrDefault(u.getId(), 0),
                        palpitesPorUsuario.getOrDefault(u.getId(), 0),
                        placaresExatos.getOrDefault(u.getId(), 0)
                ))
                .sorted(Comparator.comparingInt(ClassificacaoDto::pontos).reversed()
                        .thenComparing(d -> d.usuario().getNome()))
                .collect(Collectors.toList());
    }

    public record ClassificacaoDto(
            Usuario usuario,
            Integer pontos,
            int palpitesFeitos,
            int placaresExatos
    ) {}
}
