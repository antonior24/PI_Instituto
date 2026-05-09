package com.ies.poligono.sur.app.horario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario_imagen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioImagen {

	@Id
	@Column(name = "id_usuario")
	private Long idUsuario;

	@Column(name = "mime_type", nullable = false)
	private String mimeType;

	@Lob
	@Column(name = "datos", nullable = false, columnDefinition = "LONGBLOB")
	private byte[] datos;
}

