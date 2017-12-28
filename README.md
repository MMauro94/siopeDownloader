# SIOPE Downloader
Questa libreria consente il download e il parsing dei dati presenti nel sito https://www.siope.it

## Nozioni generali
Nel sito sono presenti entrate e uscite relative ad un certo ente in un certo mese/anno. Per poter parsare questi dati sono però necessari dei dati "anagrafici", che contengono alcune informazioni base (comuni, province, categorie, ecc.).

## Utilizzo

### Download anagrafiche
Come già detto, prima di scaricare i dati veri e propri, dobbiamo scaricare i dati anagrafici. Tutti questi dati sono raccolti nella classe `Anagrafiche`, e per scaricarli è sufficente scrivere:
```java
Anagrafiche a = Anagrafiche.downloadAnagrafiche();
```
In questa classe si trovano tutte le informazioni anagrafiche presenti su siope.it.
Ogni anagrafica ritorna una mappa, che ha come chiave il suo codice, e come valore l'istanza della classe contenente le informazioni.

### Download dati entrate/uscite
Per scaricare i dati delle entrate/uscite è sufficiente utilizzare i seguenti metodi: 
```java
Entrata entrate = Entrata.downlaodEntrate(anno, anagrafiche);
Uscita uscite = Uscita.downloadUscite(anno, anagrafiche);
```
* `anno` è un valore intero che rappresenta l'anno del quale vogliamo effettuare il download.
* `anagrafiche` rappresenta un'istanza della classe `Anagrafiche`, spiegata in precedenza.

Entrambi i metodi restituiscono un `Iterable`, anzichè una struttura dati definita, come una lista. 
Questa scelta è stata fatta in quanto i dati pesano parecchio (nell'ordine delle centinaia di MiB), quindi è più pratico lasciare all'utente finale della libreria il compito di processare i dati man mano che vengono parsati, per evitare di occupare troppo spazio in RAM.

### Esempio di utilizzo
Per questo esempio, ipotizziamo di voler ottenere la somma delle spese di tutti gli enti, suddivise per provincia, nell'anno 2017.

```java
class Example {
    public static void main(String[] args) throws IOException {
        //Download anagrafiche
        final Anagrafiche a = Anagrafiche.downloadAnagrafiche();

        //Download uscite e raggruppamento per provincia
        final Map<Provincia, BigDecimal> map = new HashMap<>();
        for (Uscita u : Uscita.downloadUscite(2017, a)) {
            final Provincia p = u.getEnte().getComune().getProvincia();
            map.put(p, map.getOrDefault(p, BigDecimal.ZERO).add(u.getAmount()));
        }

        //Ordinamento risultati
        final ArrayList<Map.Entry<Provincia, BigDecimal>> entries = new ArrayList<>(map.entrySet());
        Comparator<Map.Entry<Provincia, BigDecimal>> comparing = Comparator.comparing(Map.Entry::getValue);
        entries.sort(comparing.reversed());

        //Print a schermo risultati
        for (Map.Entry<Provincia, BigDecimal> entry : entries) {
            System.out.println(entry.getKey().getNome() + ": " + entry.getValue().toPlainString() + "€");
        }
    }
}
```
