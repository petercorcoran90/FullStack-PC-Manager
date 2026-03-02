package com.tus.pcmanager.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pc_builds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PcBuild {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String buildName;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private AppUser user;

	@ManyToMany
	@JoinTable(name = "build_part_join", joinColumns = @JoinColumn(name = "build_id"), inverseJoinColumns = @JoinColumn(name = "part_id"))
	private List<HardwarePart> parts = new ArrayList<>();

	public BigDecimal getTotalPrice() {
		BigDecimal total = BigDecimal.ZERO;

		if (parts != null) {
			for (HardwarePart part : parts) {
				if (part.getPrice() != null) {
					total = total.add(part.getPrice());
				}
			}
		}
		return total;
	}
}