package com.project.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.project.domain.*;

public class Manager {
    private static SessionFactory factory;

    /**
     * Crea la SessionFactory per defecte
     */
    public static void createSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            
            // Registrem totes les classes que tenen anotacions JPA
            configuration.addAnnotatedClass(Biblioteca.class);
            configuration.addAnnotatedClass(Llibre.class);
            configuration.addAnnotatedClass(Exemplar.class);
            configuration.addAnnotatedClass(Prestec.class);
            configuration.addAnnotatedClass(Persona.class);
            configuration.addAnnotatedClass(Autor.class);

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
                
            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("No s'ha pogut crear la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Crea la SessionFactory amb un fitxer de propietats específic
     */
    public static void createSessionFactory(String propertiesFileName) {
        try {
            Configuration configuration = new Configuration();
            
            configuration.addAnnotatedClass(Biblioteca.class);
            configuration.addAnnotatedClass(Llibre.class);
            configuration.addAnnotatedClass(Exemplar.class);
            configuration.addAnnotatedClass(Prestec.class);
            configuration.addAnnotatedClass(Persona.class);
            configuration.addAnnotatedClass(Autor.class);

            Properties properties = new Properties();
            try (InputStream input = Manager.class.getClassLoader().getResourceAsStream(propertiesFileName)) {
                if (input == null) {
                    throw new IOException("No s'ha trobat " + propertiesFileName);
                }
                properties.load(input);
            }

            configuration.addProperties(properties);

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
                
            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Error creant la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Tanca la SessionFactory
     */
    public static void close() {
        if (factory != null) {
            factory.close();
        }
    }


    public static Autor addAutor(String nomAutor) {
        Transaction tx = null;
        Autor autor = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            // Crear y guardar el autor en la base de datos
            autor = new Autor(nomAutor);
            session.persist(autor);

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }

        return autor;
    }


    public static <T> Collection<T> listCollection(Class<T> clazz) {
        List<T> resultList = new ArrayList<>();

        try (Session session = factory.openSession()) {
            String hql = "FROM " + clazz.getSimpleName();  
            Query<T> query = session.createQuery(hql, clazz);
            resultList = query.list();
        } catch (HibernateException e) {
            e.printStackTrace();
        }

        return resultList;
    }


    public static <T> String collectionToString(Class<T> clazz, Collection<T> collection) {
        if (collection == null || collection.isEmpty()){
            return "No s'han trobat elements a la llista de "+ clazz.getSimpleName();
        }else {
            StringBuilder sb = new StringBuilder();
            sb.append("Taula ").append(clazz.getSimpleName()).append("\n");
            for (T item : collection){
                sb.append(item.toString()).append("\n");
            }
            return sb.toString();
        }

    }

    public static Llibre addLlibre(String isbn, String titol, String editorial, int anyPublicacio) {
        Transaction tx = null;
        Llibre llibre = null;
        try(Session session = factory.openSession()){
            tx = session.beginTransaction();
            llibre = new Llibre(isbn, titol, editorial,anyPublicacio);
            session.persist(llibre);
            tx.commit();

            System.out.println("llibre afegit");

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return llibre;
    }

    public static void updateAutor(int autorId, String nom, Set<Llibre> llibres) {
        Transaction tx = null;

        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            Autor autor = session.get(Autor.class, autorId);
            if (autor != null && autor.getNom().equals(nom) && !llibres.isEmpty()) {

                List<Llibre> llibresActuals = autor.getLlibres();
                if (!llibresActuals.isEmpty()){
                    //li treiem l'autor al llibre que no esta en els nous llibres
                    for (Llibre llibre: llibresActuals){
                        if (!llibres.contains(llibre)){
                            llibre.getAutors().remove(autor);
                            session.merge(llibre);
                        }
                    }
                }
                //afigim l'autor als nous llibre
                for (Llibre llibre: llibres){
                    if (!llibresActuals.contains(llibre)) {
                        llibre.getAutors().add(autor);
                        session.merge(llibre);
                    }
                }
                //actualitzem els llibres de l'autor
                autor.getLlibres().clear();
                autor.setLlibres(new ArrayList<>(llibres));
                session.merge(autor);
                tx.commit();
                System.out.println("Autor actualitzat correctament");
            } else {
                System.out.println("Autor amb ID " + autorId + " no trobat");
            }
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    public static Biblioteca addBiblioteca(String nom, String ciutat, String adreca, String tlf, String mail) {
        Transaction tx = null;
        Biblioteca biblioteca = null;
        try(Session session = factory.openSession()){
            tx = session.beginTransaction();

            biblioteca = new Biblioteca(nom,ciutat,adreca,tlf,mail);
            session.persist(biblioteca);
            tx.commit();

        }catch (HibernateException e){
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return biblioteca;
    }

    public static Exemplar addExemplar(String codi, Llibre llibre, Biblioteca biblioteca) {
        Transaction tx = null;
        Exemplar exemplar = null;

        try(Session session = factory.openSession()){
            tx = session.beginTransaction();
            if (llibre != null && biblioteca!=null){
                exemplar = new Exemplar(codi,llibre,biblioteca);

                llibre.getExemplars().add(exemplar);
                biblioteca.getExemplars().add(exemplar);

                session.persist(exemplar);

                session.merge(llibre);
                session.merge(biblioteca);
                tx.commit();
                System.out.println("Exemplar afegit correctament");
            }else {
                System.out.println("La biblioteca o el llibre son nulls");
            }
        }catch (HibernateException e){
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return exemplar;

    }

    public static Persona addPersona(String dni, String nom, String tlf, String mail) {
        Transaction tx = null;
        Persona persona = null;
        try (Session session = factory.openSession()){
            tx = session.beginTransaction();
            persona = new Persona(dni,nom,tlf,mail);

            session.persist(persona);
            tx.commit();

        }catch (HibernateException e){
            if (tx != null) tx.rollback();
            e.printStackTrace();
    }
        return persona;
    }

    public static Prestec addPrestec(Exemplar exemplar, Persona persona, LocalDate dataIniciPrestec, LocalDate dataRetornPrevista) {

        Transaction tx = null;
        Prestec prestec = null;
        try (Session session = factory.openSession()){
            tx = session.beginTransaction();


            if (exemplar.isDisponible()){
                prestec = new Prestec(exemplar,persona,dataIniciPrestec,dataRetornPrevista);
                exemplar.setDisponible(false);
                exemplar.getHistorialPrestecs().add(prestec);
                persona.getPrestecs().add(prestec);

                session.persist(prestec);

                session.merge(exemplar);
                session.merge(persona);

                tx.commit();
            }

        }catch (HibernateException e){
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return prestec;
    }

    public static void registrarRetornPrestec(int prestecId, LocalDate dataFinalPrestecReal) {

        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            Prestec prestec = session.get(Prestec.class, prestecId);
            if (prestec != null && prestec.isActiu()) {
                prestec.setDataRetornReal(dataFinalPrestecReal);
                prestec.setActiu(false);
                Exemplar exemplar = prestec.getExemplar();
                Persona persona = prestec.getPersona();
                if (exemplar != null) {
                    exemplar.setDisponible(true);
                    session.merge(exemplar);
                }
                session.merge(prestec);
                if (persona != null) {
                    List<Prestec> prestecsPersona = persona.getPrestecs();
                    if (prestecsPersona!= null && prestecsPersona.contains(prestec)){
                        int index = prestecsPersona.indexOf(prestec);
                        prestecsPersona.remove(index);
                        persona.setPrestecs(prestecsPersona);
                        session.merge(persona);
                    }

                }
                tx.commit();
                System.out.println("Perstec retornat correctament");
            } else {
                System.out.println("El prestec no existeix o ja s'ha fet el retorn");
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }


    }

    public static List<Llibre> findLlibresAmbAutors() {
        List<Llibre> lLibres = null;
        try (Session session = factory.openSession()){
            String hql = "From " + Llibre.class.getSimpleName();
            Query<Llibre> query = session.createQuery(hql,Llibre.class);
            lLibres = query.list();

        }catch (HibernateException e){
            e.printStackTrace();
        }
        return lLibres;
    }

    public static List<Object[]> findLlibresEnPrestec() {

        List<Object[]> llibresPrestats = new ArrayList<>();
        try (Session session = factory.openSession()){
            String hql = " From Prestec p where p.actiu=true";
            llibresPrestats = session.createQuery(hql,Object[].class).getResultList();


        }catch (HibernateException e){
            e.printStackTrace();
        }
        return llibresPrestats;

    }

    public static StringBuilder formatMultipleResult(List<Object[]> dades) {

        StringBuilder resultat = new StringBuilder();
        for (int i =0;i<dades.size();i++){
            for (int j=0;j<dades.get(i).length;j++) {
                resultat.append(dades.get(i)[j]).append(" ");
            }
            resultat.append("\n");
        }
        return resultat;
    }

    public static List<Object[]> findLlibresAmbBiblioteques() {
        List<Object[]> llibresBiblioteques = new ArrayList<>();
        try (Session session = factory.openSession()){
            String hql = "Select  l.titol, b.nom from "+ Biblioteca.class.getSimpleName()+" b JOIN b.exemplars e JOIN e.llibre l";
            llibresBiblioteques = session.createQuery(hql,Object[].class).getResultList();


        }catch (HibernateException e){
            e.printStackTrace();
        }
        return llibresBiblioteques;

    }
}
