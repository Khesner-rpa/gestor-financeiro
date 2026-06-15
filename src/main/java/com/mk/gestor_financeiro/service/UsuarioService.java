<<<<<<< HEAD
package com.mk.gestor_financeiro.service;

import com.mk.gestor_financeiro.dto.CadastroForm;
import com.mk.gestor_financeiro.model.Usuario;
import com.mk.gestor_financeiro.repository.UsuarioRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario cadastrar(CadastroForm form) {
        String email = normalizarEmail(form.getEmail());

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Ja existe uma conta com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(form.getNome().trim());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(form.getSenha()));

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado."));
    }

    @Transactional
    public void atualizarNome(String email, String nome) {
        Usuario usuario = buscarPorEmail(email);
        usuario.setNome(nome.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = buscarPorEmail(email);

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles("USER")
                .build();
    }

    private String normalizarEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return "";
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
=======
package com.mk.gestor_financeiro.service;

import com.mk.gestor_financeiro.dto.CadastroForm;
import com.mk.gestor_financeiro.model.Usuario;
import com.mk.gestor_financeiro.repository.UsuarioRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario cadastrar(CadastroForm form) {
        String email = normalizarEmail(form.getEmail());

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Ja existe uma conta com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(form.getNome().trim());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(form.getSenha()));

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado."));
    }

    @Transactional
    public void atualizarNome(String email, String nome) {
        Usuario usuario = buscarPorEmail(email);
        usuario.setNome(nome.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = buscarPorEmail(email);

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles("USER")
                .build();
    }

    private String normalizarEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return "";
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
>>>>>>> 6be877a264aef21474bf1ccbbf9c660e2ac5dcce
