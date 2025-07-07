package mg.dirk.classes;

import java.util.Date;

public class TestClass1 {
    private String nom;
    private int age;
    private Date entree;

    public int getAge() {
        return age;
    }

    public Date getEntree() {
        return entree;
    }

    public String getNom() {
        return nom;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setEntree(Date entree) {
        this.entree = entree;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public TestClass1() {
    }

    public TestClass1(String nom, int age, Date entree) {
        this.setAge(age);
        this.setNom(nom);
        this.setEntree(entree);
    }

}
