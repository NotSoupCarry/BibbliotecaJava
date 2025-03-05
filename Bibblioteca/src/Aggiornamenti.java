import java.sql.*;
import java.util.*;
import java.util.Date;

public class Aggiornamenti {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        biblioteca.menu();
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

class Libro {
    private int id;
    private String nome;
    private int quantitaTotale;
    private int quantitaPrestati;
    private Date dataPubblicazione;

    public Libro(int id, String nome, int quantitaTotale, int quantitaPrestati, Date dataPubblicazione) {
        this.id = id;
        this.nome = nome;
        this.quantitaTotale = quantitaTotale;
        this.quantitaPrestati = quantitaPrestati;
        this.dataPubblicazione = dataPubblicazione;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantitaTotale() {
        return quantitaTotale;
    }


    public Date getDataPubblicazione() {
        return dataPubblicazione;
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

class Biblioteca {
    private Connection conn;

    public Biblioteca() {
        conn = DBContext.connessioneDatabase();
        if (conn == null) {
            System.out.println("Errore di connessione al database.");
        }
    }

    public boolean libroEsiste(String nome) throws SQLException {
        String query = "SELECT COUNT(*) FROM libri WHERE nome = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, nome);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            return true;
        }
        return false;
    }

    public void aggiungiLibro(Libro libro) {
        try {
            if (libroEsiste(libro.getNome())) {
                System.out.println("Il libro è già presente nella biblioteca.");
                return;
            }

            String query = "INSERT INTO libri (nome, quantitaTotale, quantitaPrestati, dataPubblicazione) VALUES (?, ?, 0, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, libro.getNome());
            pstmt.setInt(2, libro.getQuantitaTotale());
            pstmt.setDate(4, new java.sql.Date(libro.getDataPubblicazione().getTime()));
            pstmt.executeUpdate();
            System.out.println("Libro aggiunto con successo.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rimuoviLibro(String nome) {
        try {
            String query = "DELETE FROM libri WHERE nome = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nome);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Libro rimosso con successo.");
            } else {
                System.out.println("Il libro non esiste nella biblioteca.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void menu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMenu Biblioteca:");
            System.out.println("1. Aggiungi libro");
            System.out.println("2. Rimuovi libro");
            System.out.println("3. Esci");
            System.out.print("Scelta: ");
            int scelta = scanner.nextInt();
            scanner.nextLine(); 

            switch (scelta) {
                case 1:
                    System.out.print("Nome del libro: ");
                    String nome = scanner.nextLine();
                    System.out.print("Quantità totale: ");
                    int quantitaTotale = scanner.nextInt();

                    

                    System.out.print("Data di pubblicazione (YYYY-MM-DD): ");
                    String dataStr = scanner.nextLine();
                    try {
                        java.sql.Date dataPubblicazione = java.sql.Date.valueOf(dataStr);
                        aggiungiLibro(new Libro(0, nome, quantitaTotale, 0, dataPubblicazione));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Formato data non valido.");
                    }
                    break;
                case 2:
                    System.out.print("Nome del libro da rimuovere: ");
                    rimuoviLibro(scanner.nextLine());
                    break;
                case 3:
                    System.out.println("Chiusura del programma.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }
}
