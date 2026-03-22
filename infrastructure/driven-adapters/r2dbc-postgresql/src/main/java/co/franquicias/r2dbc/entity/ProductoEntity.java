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
@Table("productos")
public class ProductoEntity {
    @Id
    private String id;
    private String sucursalId;
    private String nombre;
    private Long precio;
    private Integer stock;
    private Instant createdAt;
    private Instant updatedAt;
}
