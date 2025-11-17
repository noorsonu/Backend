package com.main.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@NotBlank(message = "Email must not be blank")
	@Email(message = "Email must be a valid email")
	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = true, unique = true)
	private String phoneNumber;

	@Column(nullable = false)
	private boolean isBlocked = false;

	@Column(nullable = false)
	private String userType;

	@NotBlank(message = "Password must not be blank")
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = true, updatable = false)
	private Instant createdAt;
	
	@Column(nullable = true)
	private Instant updatedAt;
	
	@PrePersist
	public void onCreate() {
		if (this.createdAt == null) {
			this.createdAt = Instant.now();
		}
		if (this.updatedAt == null) {
			this.updatedAt = Instant.now();
		}
	}
	
	@PreUpdate
	public void onUpdate() {
		this.updatedAt = Instant.now();
	}
}
