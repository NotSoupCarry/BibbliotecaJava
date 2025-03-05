import java.util.Date;
import java.util.Scanner;
import java.sql.*;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Biblioteca biblioteca = new Biblioteca();

        Menu.menuPrincipale(scanner, biblioteca);

        scanner.close();
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
    private Connection conn;

    public Biblioteca() {
        this.conn = DBContext.connessioneDatabase();
    }

    // Metodo per stampare tutti i libri
    public void stampaTuttiLibri() {
        if (conn != null) {
            String query = "SELECT * FROM libri";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                System.out.println("\n === Elenco di tutti i libri: ===\n");
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    int quantitaTotale = rs.getInt("quantita_totale");
                    int quantitaPrestati = rs.getInt("quantita_prestati");
                    Date dataPubblicazione = rs.getDate("data_pubblicazione");

                    int disponibili = quantitaTotale - quantitaPrestati;
                    // Stampa i dettagli del libro
                    System.out.println(nome + " | " + dataPubblicazione + ", Copie Totali: " + quantitaTotale +
                            ", Prestati: " + quantitaPrestati + ", Disponibili: " + disponibili);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per prestare un libro
    public void prestaLibro(String nomeLibro) {
        if (conn != null) {
            // Ricerca il libro per nome
            String query = "SELECT ID, quantita_totale, quantita_prestati FROM libri WHERE nome = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomeLibro);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int libroId = rs.getInt("ID");
                    int quantitaTotale = rs.getInt("quantita_totale");
                    int quantitaPrestati = rs.getInt("quantita_prestati");

                    // Verifica se ci sono libri disponibili per il prestito
                    if (quantitaTotale > quantitaPrestati) {
                        // Se sì, aumenta i libri prestati
                        String updateQuery = "UPDATE libri SET quantita_prestati = quantita_prestati + 1 WHERE ID = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, libroId);
                            updateStmt.executeUpdate();
                            System.out.println("Libro prestato con successo!");
                        }
                    } else {
                        System.out.println("Non ci sono copie disponibili da prestare per il libro selezionato");
                    }
                } else {
                    System.out.println("Libro non trovato.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per restituire un libro
    public void restituisciLibro(String nomeLibro) {
        if (conn != null) {
            // Ricerca il libro per nome
            String query = "SELECT ID, quantita_totale, quantita_prestati FROM libri WHERE nome = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomeLibro);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int libroId = rs.getInt("ID");
                    int quantitaPrestati = rs.getInt("quantita_prestati");

                    // Verifica se ci sono libri prestati da restituire
                    if (quantitaPrestati > 0) {
                        // Se sì, diminuisce il numero di libri prestati
                        String updateQuery = "UPDATE libri SET quantita_prestati = quantita_prestati - 1 WHERE ID = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, libroId);
                            updateStmt.executeUpdate();
                            System.out.println("Libro restituito con successo!");
                        }
                    } else {
                        System.out.println("Non ci sono copie disponibili da restituire per il libro selezionato");
                    }
                } else {
                    System.out.println("Libro non trovato.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

// classe dei menu
class Menu {
    // menu principale
    public static void menuPrincipale(Scanner scanner, Biblioteca biblioteca) {
        int scelta;
        boolean exitMainMenu = false;
        System.out.println("\n==== Benvenuto in BIBBLIOTECA INSANA ====");

        while (!exitMainMenu) {
            System.out.println("\n==== Menu ====");
            System.out.println("1. Mostra tutti i Libri");
            System.out.println("2. Presta Libro");
            System.out.println("3. Restituisci Libro");
            System.out.println("4. Aggiungi Libro");
            System.out.println("5. Elimina Libro");
            System.out.println("6. Esci");

            System.out.print("Scegli un'opzione (1-4): ");
            scelta = Controlli.controlloInputInteri(scanner);
            scanner.nextLine();

            switch (scelta) {
                case 1:
                    biblioteca.stampaTuttiLibri();
                    break;
                case 2:
                    System.out.print("Inserisci il nome del libro da prestare: ");
                    String nomeLibroDaPrestare = Controlli.controlloInputStringhe(scanner);
                    biblioteca.prestaLibro(nomeLibroDaPrestare);
                    break;
                case 3:
                    System.out.print("Inserisci il nome del libro da restituire: ");
                    String nomeLibroDaRestituire = Controlli.controlloInputStringhe(scanner);
                    biblioteca.restituisciLibro(nomeLibroDaRestituire);
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    System.out.println("Uscita dal programma.");
                    exitMainMenu = true;
                    break;
                default:
                    System.out.println("Opzione non valida! Riprova.");
            }
        }
    }
}

// classe dei controlli
class Controlli {
    // Metodo per controllare che l'input stringa non sia vuoto
    public static String controlloInputStringhe(Scanner scanner) {
        String valore;
        do {
            valore = scanner.nextLine().trim();
            if (valore.isEmpty()) {
                System.out.print("Input non valido. Inserisci un testo: ");
            }
        } while (valore.isEmpty());
        return valore;
    }

    // Metodo per controllare l'input intero
    public static Integer controlloInputInteri(Scanner scanner) {
        Integer valore;
        do {
            while (!scanner.hasNextInt()) {
                System.out.print("Devi inserire un numero intero. Riprova ");
                scanner.next();
            }
            valore = scanner.nextInt();
            if (valore < 0) {
                System.out.print("Il numero non può essere negativo. Riprova: ");
            }
        } while (valore < 0);
        return valore;
    }
}
