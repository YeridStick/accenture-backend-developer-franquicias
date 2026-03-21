package co.franquicias.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("franquicias")
public class FranquiciaEntity {
    @Id
    private String id;
    private String nombre;
    private Instant createdAt;
    private Instant updatedAt;
}
