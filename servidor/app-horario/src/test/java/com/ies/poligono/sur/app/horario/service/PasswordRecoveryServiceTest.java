package com.ies.poligono.sur.app.horario.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.dto.PasswordRecoveryResult;
import com.ies.poligono.sur.app.horario.model.Usuario;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceTest {

    private static final String TEST_EMAIL = "carlos.robles.dominguez.al@iespoligonosur.org";

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Carlos Robles");
        usuario.setEmail(TEST_EMAIL);
        usuario.setPassword("oldPassword");
        usuario.setRol("profesor");
    }

    @Test
    void recoverPasswordInProdSavesEncodedPasswordAndSendsEmail() {
        PasswordRecoveryService service = new PasswordRecoveryService(
                usuarioRepository,
                passwordEncoder,
                mailSender,
                "production",
                "no-reply@iespoligonosur.org",
                "smtp.iespoligonosur.org",
                "no-reply@iespoligonosur.org",
                "secret");

        when(usuarioRepository.findByEmail(TEST_EMAIL)).thenReturn(usuario);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded-temp-password");

        PasswordRecoveryResult result = service.recoverPassword(TEST_EMAIL);

        assertEquals("Si el correo existe, se ha enviado una contrasena temporal al correo indicado.", result.getMensaje());
        assertEquals("produccion", result.getAmbiente());
        assertNull(result.getContrasenaTemporal());
        verify(usuarioRepository, times(1)).save(usuario);
        assertEquals("encoded-temp-password", usuario.getPassword());

        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordEncoder, times(1)).encode(passwordCaptor.capture());
        String generatedPassword = passwordCaptor.getValue();
        assertNotNull(generatedPassword);
        assertEquals(8, generatedPassword.length());

        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(mailCaptor.capture());

        SimpleMailMessage sentMessage = mailCaptor.getValue();
        assertNotNull(sentMessage.getTo());
        assertEquals(TEST_EMAIL, sentMessage.getTo()[0]);
        assertEquals("no-reply@iespoligonosur.org", sentMessage.getFrom());
        assertEquals("Recuperacion de contrasena temporal", sentMessage.getSubject());
        assertNotNull(sentMessage.getText());
        assertTrue(sentMessage.getText().contains("Tu contrasena temporal es:"));
        assertTrue(sentMessage.getText().contains("Usa esta contrasena para iniciar sesion"));
        assertTrue(sentMessage.getText().contains(generatedPassword));
    }

    @Test
    void recoverPasswordOutsideProdReturnsTemporaryPasswordWithoutSendingEmail() {
        PasswordRecoveryService service = new PasswordRecoveryService(
                usuarioRepository,
                passwordEncoder,
                mailSender,
                "testing",
                "no-reply@iespoligonosur.org",
                "",
                "",
                "");

        when(usuarioRepository.findByEmail(TEST_EMAIL)).thenReturn(usuario);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded-temp-password");

        PasswordRecoveryResult result = service.recoverPassword(TEST_EMAIL);

        assertEquals("Si el correo existe, se ha generado una contrasena temporal para pruebas.",
                result.getMensaje());
        assertEquals("testing", result.getAmbiente());
        assertNotNull(result.getContrasenaTemporal());
        assertEquals(8, result.getContrasenaTemporal().length());
        assertEquals("encoded-temp-password", usuario.getPassword());
        verify(usuarioRepository, times(1)).save(usuario);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void recoverPasswordWithUnknownEmailReturnsNeutralResponse() {
        PasswordRecoveryService service = new PasswordRecoveryService(
                usuarioRepository,
                passwordEncoder,
                mailSender,
                "production",
                "no-reply@iespoligonosur.org",
                "",
                "",
                "");

        when(usuarioRepository.findByEmail(TEST_EMAIL)).thenReturn(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.recoverPassword(TEST_EMAIL));

        assertEquals("No se pudo enviar el correo de recuperacion. Revisa la configuracion SMTP del entorno.",
                exception.getMessage());
        verify(usuarioRepository, never()).findByEmail(TEST_EMAIL);
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(any(String.class));
    }

    @Test
    void recoverPasswordInProductionThrowsWhenEmailSendingFails() {
        PasswordRecoveryService service = new PasswordRecoveryService(
                usuarioRepository,
                passwordEncoder,
                mailSender,
                "production",
                "no-reply@iespoligonosur.org",
                "smtp.iespoligonosur.org",
                "no-reply@iespoligonosur.org",
                "secret");

        when(usuarioRepository.findByEmail(TEST_EMAIL)).thenReturn(usuario);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded-temp-password");
        org.mockito.Mockito.doThrow(new MailSendException("SMTP down"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.recoverPassword(TEST_EMAIL));

        assertEquals("No se pudo enviar el correo de recuperacion. Revisa la configuracion SMTP del entorno.",
                exception.getMessage());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void recoverPasswordThrowsWhenMailIsNotConfiguredAndTemporaryPasswordIsHidden() {
        PasswordRecoveryService service = new PasswordRecoveryService(
                usuarioRepository,
                passwordEncoder,
                mailSender,
                "production",
                "no-reply@iespoligonosur.org",
                "",
                "",
                "");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.recoverPassword(TEST_EMAIL));

        assertEquals("No se pudo enviar el correo de recuperacion. Revisa la configuracion SMTP del entorno.",
                exception.getMessage());
        verify(usuarioRepository, never()).findByEmail(TEST_EMAIL);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void recoverPasswordWithUnknownEmailInTestingReturnsNeutralResponse() {
        PasswordRecoveryService service = new PasswordRecoveryService(
                usuarioRepository,
                passwordEncoder,
                mailSender,
                "testing",
                "no-reply@iespoligonosur.org",
                "",
                "",
                "");

        when(usuarioRepository.findByEmail(TEST_EMAIL)).thenReturn(null);

        PasswordRecoveryResult result = service.recoverPassword(TEST_EMAIL);

        assertEquals("Si el correo existe, se ha generado una contrasena temporal para pruebas.",
                result.getMensaje());
        assertEquals("testing", result.getAmbiente());
        assertFalse(result.hasTemporaryPassword());
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(any(String.class));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}
