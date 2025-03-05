import java.util.Date;
import java.util.Scanner;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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

// classe utenti
class Utente {
    private int id;
    private String username;
    private String ruolo;

    public Utente(int id, String username, String ruolo) {
        this.id = id;
        this.username = username;
        this.ruolo = ruolo;
    }

    public int getId() {
        return id;
    }

    public String getRuolo() {
        return ruolo;
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
    public void prestaLibro(String nomeLibro, int idUtente) {
        if (conn != null) {
            String query = "SELECT ID, quantita_totale, quantita_prestati FROM libri WHERE nome = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomeLibro);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int libroId = rs.getInt("ID");
                    int quantitaTotale = rs.getInt("quantita_totale");
                    int quantitaPrestati = rs.getInt("quantita_prestati");

                    if (quantitaTotale > quantitaPrestati) {
                        // Registra il prestito
                        String prestitoQuery = "INSERT INTO prestiti (id_utente, id_libro, restituito) VALUES (?, ?, false)";
                        try (PreparedStatement prestitoStmt = conn.prepareStatement(prestitoQuery)) {
                            prestitoStmt.setInt(1, idUtente);
                            prestitoStmt.setInt(2, libroId);
                            prestitoStmt.executeUpdate();
                        }

                        // Aggiorna i libri prestati
                        String updateQuery = "UPDATE libri SET quantita_prestati = quantita_prestati + 1 WHERE ID = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, libroId);
                            updateStmt.executeUpdate();
                            System.out.println("Libro prestato con successo!");
                        }
                    } else {
                        System.out.println("Non ci sono copie disponibili per il prestito.");
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
    public void restituisciLibro(String nomeLibro, int idUtente) {
        if (conn != null) {
            String query = "SELECT p.id, p.id_libro FROM prestiti p " +
                           "JOIN libri l ON p.id_libro = l.ID " +
                           "WHERE l.nome = ? AND p.id_utente = ? AND p.restituito = false";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomeLibro);
                stmt.setInt(2, idUtente);
                ResultSet rs = stmt.executeQuery();
    
                if (rs.next()) {
                    int prestitoId = rs.getInt("id");
                    int libroId = rs.getInt("id_libro");
    
                    // Segna il prestito come restituito
                    String updatePrestito = "UPDATE prestiti SET restituito = true WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updatePrestito)) {
                        updateStmt.setInt(1, prestitoId);
                        updateStmt.executeUpdate();
                    }
    
                    // Aggiorna il numero di libri prestati nella tabella libri
                    String updateLibro = "UPDATE libri SET quantita_prestati = quantita_prestati - 1 WHERE ID = ?";
                    try (PreparedStatement updateLibroStmt = conn.prepareStatement(updateLibro)) {
                        updateLibroStmt.setInt(1, libroId);
                        updateLibroStmt.executeUpdate();
                        System.out.println("Libro restituito con successo!");
                    }
                } else {
                    System.out.println("Non hai preso in prestito questo libro o lo hai già restituito.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    

    // Metodo per controllare se esiste il libro
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

    // Metodo per aggiungere un libro
    public void aggiungiLibro(Libro libro) {
        try {
            if (libroEsiste(libro.getNome())) {
                System.out.println("Il libro è già presente nella biblioteca.");
                return;
            }

            String query = "INSERT INTO libri (nome, quantita_totale, quantita_prestati, data_pubblicazione) VALUES (?, ?, 0, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, libro.getNome());
                pstmt.setInt(2, libro.getQuantitaTotale());
                pstmt.setDate(3, new java.sql.Date(libro.getDataPubblicazione().getTime())); // Corretto l'indice
                pstmt.executeUpdate();
                System.out.println("Libro aggiunto con successo.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per rimuovere un libro
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

    // Metodo per registrare un nuovo utente
    public void registrazioneUtente(Scanner scanner) {
        System.out.print("Inserisci un username: ");
        String username = Controlli.controlloInputStringhe(scanner);

        System.out.print("Inserisci una password: ");
        String password = Controlli.controlloInputStringhe(scanner);

        // Controllo se l'utente esiste già
        if (utenteEsiste(username)) {
            System.out.println("Questo username è già in uso. Scegli un altro.");
            return;
        }

        String query = "INSERT INTO utenti (username, password, ruolo) VALUES (?, ?, 'Utente')";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Idealmente, dovresti criptare la password
            pstmt.executeUpdate();
            System.out.println("Registrazione completata con successo! Ora puoi effettuare il login.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per verificare se un utente esiste già
    private boolean utenteEsiste(String username) {
        String query = "SELECT COUNT(*) FROM utenti WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Metodo per il login
    public Utente loginUtente(Scanner scanner) {
        System.out.print("Inserisci il tuo username: ");
        String username = Controlli.controlloInputStringhe(scanner);

        System.out.print("Inserisci la tua password: ");
        String password = Controlli.controlloInputStringhe(scanner);

        String query = "SELECT ID, ruolo FROM utenti WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("ID");
                String ruolo = rs.getString("ruolo");
                System.out.println("Login effettuato con successo! Ruolo: " + ruolo);
                return new Utente(id, username, ruolo);
            } else {
                System.out.println("Credenziali errate. Riprova.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

// classe dei menu
class Menu {
    // menu principale
    public static void menuPrincipale(Scanner scanner, Biblioteca biblioteca) {
        int scelta;
        boolean exitMainMenu = false;
        System.out.println("\n==== Benvenuto in BIBBLIOTECA INSANA ====\n");
        System.out.println("==== REGISTRATI PER PRENDERE IN PRESTITO I LIBRI ====\n");

        while (!exitMainMenu) {
            System.out.println("\n==== Menu Libraio====");
            System.out.println("1. Registrati");
            System.out.println("2. Login");
            System.out.println("3. Mostra tutti i libri");
            System.out.println("4. Esci");

            System.out.print("Scegli un'opzione (1-4): ");
            scelta = Controlli.controlloInputInteri(scanner);
            scanner.nextLine();

            switch (scelta) {
                case 1:
                    biblioteca.registrazioneUtente(scanner);
                    break;
                case 2:
                    Utente utenteLoggato = biblioteca.loginUtente(scanner);
                    if (utenteLoggato != null) {
                        if (utenteLoggato.getRuolo().equals("Libraio")) {
                            menuLibraio(scanner, biblioteca);
                        } else {
                            menuUtente(scanner, biblioteca, utenteLoggato);
                        }
                    }
                    break;
                case 3:
                    biblioteca.stampaTuttiLibri();
                    break;
                case 4:
                    System.out.println("Uscita dal programma.");
                    exitMainMenu = true;
                    break;
                default:
                    System.out.println("Opzione non valida! Riprova.");
            }
        }
    }

    // Menu per gli utenti normali
    public static void menuUtente(Scanner scanner, Biblioteca biblioteca, Utente utente) {
        boolean exitUserMenu = false;

        while (!exitUserMenu) {
            System.out.println("\n==== Menu Utente ====");
            System.out.println("1. Mostra tutti i libri");
            System.out.println("2. Prendere in prestito un libro");
            System.out.println("3. Restituire un libro");
            System.out.println("4. Logout");

            System.out.print("Scegli un'opzione (1-4): ");
            int scelta = Controlli.controlloInputInteri(scanner);
            scanner.nextLine();

            switch (scelta) {
                case 1:
                    biblioteca.stampaTuttiLibri();
                    break;
                case 2:
                    System.out.print("Inserisci il nome del libro da prendere in prestito: ");
                    String libroDaPrendere = scanner.nextLine();
                    biblioteca.prestaLibro(libroDaPrendere, utente.getId());
                    break;
                case 3:
                    System.out.print("Inserisci il nome del libro da restituire: ");
                    String libroDaRestituire = scanner.nextLine();
                    biblioteca.restituisciLibro(libroDaRestituire, utente.getId());
                    break;
                case 4:
                    System.out.println("Logout effettuato.");
                    exitUserMenu = true;
                    break;
                default:
                    System.out.println("Opzione non valida! Riprova.");
            }
        }
    }

    // menu libraio
    public static void menuLibraio(Scanner scanner, Biblioteca biblioteca) {
        int sceltaMenuLibraio;
        boolean exitMenuLibraio = false;

        while (!exitMenuLibraio) {
            System.out.println("\n==== Menu Libraio====");
            System.out.println("1. Mostra tutti i Libri");
            System.out.println("2. Aggiungi Libro");
            System.out.println("3. Elimina Libro");
            System.out.println("4. Esci");

            System.out.print("Scegli un'opzione (1-6): ");
            sceltaMenuLibraio = Controlli.controlloInputInteri(scanner);
            scanner.nextLine();

            switch (sceltaMenuLibraio) {
                case 1:
                    biblioteca.stampaTuttiLibri();
                    break;
                case 2:
                    System.out.print("Nome del libro: ");
                    String nome = scanner.nextLine();
                    System.out.print("Quantità totale: ");
                    int quantitaTotale = scanner.nextInt();

                    scanner.nextLine();

                    System.out.print("Data di pubblicazione (YYYY-MM-DD): ");
                    Date dataPubblicazione = Controlli.controlloInputData(scanner);

                    try {
                        biblioteca.aggiungiLibro(new Libro(0, nome, quantitaTotale, 0, dataPubblicazione));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Formato data non valido.");
                    }
                    break;
                case 3:
                    System.out.print("Nome del libro da rimuovere: ");
                    biblioteca.rimuoviLibro(scanner.nextLine());
                    break;
                case 4:
                    System.out.println("Uscita dal programma.");
                    exitMenuLibraio = true;
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

    // Metodo per controllare che la data sia nel formato corretto (YYYY-MM-DD)
    public static Date controlloInputData(Scanner scanner) {
        String dataStr;
        Date data = null;
        boolean dataValida = false;

        while (!dataValida) {
            dataStr = scanner.nextLine().trim();

            // Controllo se la data è nel formato corretto
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false); // Disabilita la modalità lenient per evitare date non valide (es. 2023-02-30)

            try {
                data = sdf.parse(dataStr);
                dataValida = true; // Se la data è valida, esci dal ciclo
            } catch (ParseException e) {
                System.out.println("Formato data non valido. Riprova.");
            }
        }

        return data;
    }
}
