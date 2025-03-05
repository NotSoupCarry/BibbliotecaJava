import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
    }
}

// Classe per la connessione al database
class DBContext {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bibbliotecadb";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public static Connection connessioneDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

// classe dei libri
class Libro {
    private int id;
    private String nome;
    private int quantitaTotale;
    private int quantitaPrestati;
    private Date dataPubblicazione;

    // Costruttore
    public Libro(int id, String nome, int quantitaTotale, int quantitaPrestati, Date dataPubblicazione) {
        this.id = id;
        this.nome = nome;
        this.quantitaTotale = quantitaTotale;
        this.quantitaPrestati = quantitaPrestati;
        this.dataPubblicazione = dataPubblicazione;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuantitaTotale() {
        return quantitaTotale;
    }

    public void setQuantitaTotale(int quantitaTotale) {
        this.quantitaTotale = quantitaTotale;
    }

    public int getQuantitaPrestati() {
        return quantitaPrestati;
    }

    public void setQuantitaPrestati(int quantitaPrestati) {
        this.quantitaPrestati = quantitaPrestati;
    }

    public Date getDataPubblicazione() {
        return dataPubblicazione;
    }

    public void setDataPubblicazione(Date dataPubblicazione) {
        this.dataPubblicazione = dataPubblicazione;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", quantitaTotale=" + quantitaTotale +
                ", quantitaPrestati=" + quantitaPrestati +
                ", dataPubblicazione=" + dataPubblicazione +
                '}';
    }
}

// classe con metodi gestione db
class Biblioteca {
}

// classe dei menu
class Menu{

}

// classe dei controlli
class controlli{

}