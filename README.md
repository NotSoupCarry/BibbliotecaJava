
# 📚 **BIBBLIOTECA INSANA** 📚

Benvenuto in **BIBBLIOTECA INSANA**, un'applicazione gestionale per una biblioteca che ti permette di registrarti, accedere, prendere in prestito libri, restituirli e molto altro! Che tu sia un utente o un libraio, questa app ti aiuterà a gestire i libri e i prestiti in modo facile e intuitivo.

---

## 🚀 **Caratteristiche principali**

- **Registrazione e Login**: Crea un account o accedi per iniziare a usare i servizi della biblioteca.
- **Gestione Libri**: Visualizza i libri disponibili, aggiungi nuovi libri, rimuovi quelli obsoleti (funzionalità riservata ai librai).
- **Prestito e Restituzione**: Prendi in prestito i libri disponibili e restituiscili quando hai finito.
- **Storico Prestiti**: Controlla facilmente quali libri hai preso in prestito e quando devi restituirli.
- **Semplice e Intuitiva**: Un'interfaccia chiara e facile da navigare per gli utenti e i librai.

---

## 💻 **Tecnologie utilizzate**

- **Java**: Linguaggio principale per lo sviluppo dell'applicazione.
- **MySQL**: Database per la gestione di utenti, libri e prestiti.
- **JDBC**: Connessione tra Java e MySQL per operazioni CRUD.
- **SimpleDateFormat**: Gestione delle date per la pubblicazione dei libri e dei prestiti.

---

## 🛠 **Funzionalità per gli utenti**

1. **Registrazione**: Se non sei registrato, puoi facilmente creare un account con un nome utente e una password.
2. **Login**: Accedi con le tue credenziali per iniziare a prendere libri in prestito.
3. **Visualizzazione Libri**: Puoi visualizzare tutti i libri disponibili nella biblioteca, con informazioni su quantità totale, prestati e disponibili.
4. **Prestito di Libri**: Se un libro è disponibile, puoi prenderlo in prestito e tenere traccia della tua data di prestito.
5. **Restituzione di Libri**: Una volta finito di leggere un libro, restituiscilo e rendi disponibile una copia per altri utenti.

---

## 🧑‍💻 **Funzionalità per i librai**

1. **Gestione Libri**: I librai possono aggiungere nuovi libri alla biblioteca, rimuovere libri esistenti e aggiornare la quantità disponibile.
2. **Gestione Prestiti**: Possono visualizzare lo storico dei prestiti e gestire eventuali problemi relativi a libri restituiti o non restituiti.

---

## 📦 **Come eseguire l'applicazione**

### 1. **Clona il repository**

   Puoi clonare questo repository usando il comando:

   ```bash
   git clone https://github.com/NotSoupCarry/BibbliotecaJava
   ```

### 2. **Configura il database**

   - Assicurati di avere un server MySQL in esecuzione e crea un database chiamato `bibliotecadb`.
   - Crea le tabelle necessarie (utenti, libri, prestiti) nel database seguendo lo schema fornito nel codice o adattandolo alle tue esigenze.

### 3. **Configura le credenziali del database**

   Nella classe `DBContext`, aggiorna le seguenti variabili con le tue credenziali di MySQL:

   ```java
   private static final String DB_URL = "jdbc:mysql://localhost:3306/bibbliotecadb";
   private static final String DB_USERNAME = "root";
   private static final String DB_PASSWORD = "root";
   ```

### 4. **Compila ed esegui il progetto**

   Esegui il progetto in un IDE come IntelliJ IDEA, Eclipse o da terminale con il comando:

   ```bash
   javac App.java
   java App
   ```

---

## 📝 **Screenshot** (opzionale)

![Screenshot](https://via.placeholder.com/800x400.png?text=BIBBLIOTECA+INSANA+App)  
*Interfaccia utente dell'app in esecuzione.*

---

## 🔧 **Contributi**

Se vuoi contribuire al progetto, sentiti libero di forkare il repository, fare le tue modifiche e inviare una pull request. Ogni contributo è benvenuto!

---

## 📄 **Licenza**

Distribuito sotto la Licenza MIT. Vedi il file [LICENSE](LICENSE) per i dettagli.

---

## 💬 **Contatti**

- [GitHub](https://github.com/NotSoupCarry)
- [Email](mailto:conzagiuseppee@gmail.com)

---

Grazie per aver scelto **BIBBLIOTECA INSANA**! Buon divertimento nella lettura! 📚🎉
