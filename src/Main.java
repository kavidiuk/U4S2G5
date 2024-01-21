import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

enum Periodicita {
    SETTIMANALE, MENSILE, SEMESTRALE
}

class CatalogoElemento implements Serializable {
    private static final long serialVersionUID = 1L;
    private String isbn;
    private String titolo;
    private int annoPubblicazione;
    private int numeroPagine;

    public CatalogoElemento(String isbn, String titolo, int annoPubblicazione, int numeroPagine) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.annoPubblicazione = annoPubblicazione;
        this.numeroPagine = numeroPagine;
    }


    public String getIsbn() {
        return isbn;
    }

    public String getTitolo() {
        return titolo;
    }

    public int getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public int getNumeroPagine() {
        return numeroPagine;
    }

    @Override
    public String toString() {
        return "CatalogoElemento{" +
                "isbn='" + isbn + '\'' +
                ", titolo='" + titolo + '\'' +
                ", annoPubblicazione=" + annoPubblicazione +
                ", numeroPagine=" + numeroPagine +
                '}';
    }
}

class Libro extends CatalogoElemento implements Serializable {
    private static final long serialVersionUID = 1L;
    private String autore;
    private String genere;

    public Libro(String isbn, String titolo, int annoPubblicazione, int numeroPagine, String autore, String genere) {
        super(isbn, titolo, annoPubblicazione, numeroPagine);
        this.autore = autore;
        this.genere = genere;
    }

    public String getAutore() {
        return autore;
    }

    public String getGenere() {
        return genere;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "autore='" + autore + '\'' +
                ", genere='" + genere + '\'' +
                "} " + super.toString();
    }
}

class Rivista extends CatalogoElemento implements Serializable {
    private static final long serialVersionUID = 1L;
    private Periodicita periodicita;

    public Rivista(String isbn, String titolo, int annoPubblicazione, int numeroPagine, Periodicita periodicita) {
        super(isbn, titolo, annoPubblicazione, numeroPagine);
        this.periodicita = periodicita;
    }

    // Getter for periodicita

    public Periodicita getPeriodicita() {
        return periodicita;
    }

    @Override
    public String toString() {
        return "Rivista{" +
                "periodicita=" + periodicita +
                "} " + super.toString();
    }
}

class ArchivioBibliotecario {
    private List<CatalogoElemento> catalogo = new ArrayList<>();

    public void aggiungiElemento(CatalogoElemento elemento) {
        catalogo.add(elemento);
    }

    public void rimuoviElemento(String isbn) {
        catalogo.removeIf(e -> e.getIsbn().equals(isbn));
    }

    public Optional<CatalogoElemento> ricercaPerISBN(String isbn) {
        return catalogo.stream()
                .filter(e -> e.getIsbn().equals(isbn))
                .findFirst();
    }

    public List<CatalogoElemento> ricercaPerAnnoPubblicazione(int anno) {
        return catalogo.stream()
                .filter(e -> e.getAnnoPubblicazione() == anno)
                .collect(Collectors.toList());
    }

    public List<CatalogoElemento> ricercaPerAutore(String autore) {
        return catalogo.stream()
                .filter(e -> e instanceof Libro && ((Libro) e).getAutore().equals(autore))
                .collect(Collectors.toList());
    }

    public void salvataggioSuDisco(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(catalogo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void caricamentoDaDisco(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            catalogo = (List<CatalogoElemento>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void stampaCatalogo() {
        catalogo.forEach(System.out::println);
    }
}

public class Main {
    public static void main(String[] args) {
        ArchivioBibliotecario archivio = new ArchivioBibliotecario();

        archivio.aggiungiElemento(new Libro("123456", "Titolo Libro", 2022, 200, "Autore Libro", "Genere Libro"));

        Optional<CatalogoElemento> elemento = archivio.ricercaPerISBN("123456");
        elemento.ifPresent(System.out::println);

        List<CatalogoElemento> elementiAnno = archivio.ricercaPerAnnoPubblicazione(2022);
        elementiAnno.forEach(System.out::println);

        List<CatalogoElemento> elementiAutore = archivio.ricercaPerAutore("Autore Libro");
        elementiAutore.forEach(System.out::println);

        archivio.salvataggioSuDisco("archivio.dat");

        ArchivioBibliotecario archivioCaricato = new ArchivioBibliotecario();
        archivioCaricato.caricamentoDaDisco("archivio.dat");

        System.out.println("Catalogo Caricato:");
        archivioCaricato.stampaCatalogo();
    }
}
