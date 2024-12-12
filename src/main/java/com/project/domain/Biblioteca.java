package com.project.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "biblioteques")
public class Biblioteca implements Serializable {
    /*
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Biblioteca[id=%d, nom='%s', ciutat='%s'", 
            bibliotecaId, nom, ciutat));
        
        if (adreca != null) {
            sb.append(String.format(", adreca='%s'", adreca));
        }
        if (telefon != null) {
            sb.append(String.format(", tel='%s'", telefon));
        }
        if (email != null) {
            sb.append(String.format(", email='%s'", email));
        }
        
        if (!exemplars.isEmpty()) {
            sb.append(", exemplars={");
            boolean first = true;
            for (Exemplar e : exemplars) {
                if (!first) sb.append(", ");
                sb.append(e.getCodiBarres());
                first = false;
            }
            sb.append("}");
        }
        
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Biblioteca that = (Biblioteca) o;
        return bibliotecaId == that.bibliotecaId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(bibliotecaId);
    }
    */
}