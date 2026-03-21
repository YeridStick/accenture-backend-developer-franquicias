package co.franquicias.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sucursales")
public class SucursalEntity implements Persistable<String> {
    @Id
    private String id;
    private String franquiciaId;
    private String nombre;
    private Instant createdAt;
    private Instant updatedAt;

    @org.springframework.data.annotation.Transient
    @Builder.Default
    private boolean isNewRecord = false;

    @Override
    public boolean isNew() {
        return isNewRecord;
    }
}
