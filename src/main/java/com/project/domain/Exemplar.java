package com.project.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exemplars")
public class Exemplar implements Serializable {
    /*
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Exemplar[id=%d, codi='%s', disponible=%s", 
            exemplarId, codiBarres, disponible));
        
        if (llibre != null) {
            sb.append(String.format(", llibre='%s'", llibre.getTitol()));
        }
        
        if (biblioteca != null) {
            sb.append(String.format(", biblioteca='%s'", biblioteca.getNom()));
        }
        
        if (!historialPrestecs.isEmpty()) {
            long prestectsActius = historialPrestecs.stream()
                .filter(p -> p.isActiu())
                .count();
            sb.append(String.format(", prestecs=%d (actius=%d)", 
                historialPrestecs.size(), prestectsActius));
        }
        
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exemplar exemplar = (Exemplar) o;
        return exemplarId == exemplar.exemplarId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(exemplarId);
    }
    */
}