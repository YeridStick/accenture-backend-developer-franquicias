package co.franquicias.api.dto.producto;

import java.util.Map;

public record ProductoViewDTO(
        String productoId,
        String productoNombre,
        int stock,
        long precio,
        String franquiciaId,
        String franquiciaNombre,
        String sucursalId,
        String sucursalNombre
) {
    public static ProductoViewDTO fromMap(Map<String, Object> m) {
        return new ProductoViewDTO(
                (String) m.get("productoId"),
                (String) m.get("productoNombre"),
                toInt(m.get("stock"), 0),
                toLong(m.get("precio"), 0L),
                (String) m.get("franquiciaId"),
                (String) m.get("franquiciaNombre"),
                (String) m.get("sucursalId"),
                (String) m.get("sucursalNombre")
        );
    }

    private static int toInt(Object val, int def) {
        if (val == null) return def;
        if (val instanceof Number n) return n.intValue();
        try { return Integer.parseInt(val.toString()); }
        catch (Exception e) { return def; }
    }

    private static long toLong(Object val, long def) {
        if (val == null) return def;
        if (val instanceof Number n) return n.longValue();
        try { return Long.parseLong(val.toString()); }
        catch (Exception e) { return def; }
    }
}
