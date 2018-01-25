# SIOPE Downloader
Questa libreria consente il download e il parsing dei dati presenti nel sito [siope.it](https://www.siope.it).

## Nozioni generali
Nel sito sono presenti entrate e uscite relative ad un certo ente in un certo mese/anno. Per poter parsare questi dati sono però necessari dei dati "anagrafici", che contengono alcune informazioni base (comuni, province, categorie, ecc.).

## Installazione
È possibile utilizzare questa libreria tramite maven:
```xml
<dependency>
  <groupId>com.github.mmauro94</groupId>
  <artifactId>siopeDownloader</artifactId>
  <version>1.1.1</version>
</dependency>
```

## Utilizzo
Per l'utilizzo della libreria in modo corretto è importante conoscere alcune classi.
### Classi interessanti
#### `AutoMap<T>`
Questa classe è una `Collection<T>` che racchiude una `Map<K, T>`. La chiave della mappa viene automaticamente gestita dalle varie implementazioni. Si possono quindi inserire valori senza specificare la chiave, rimanendo possibile ottenere un valore a partire dalla sua chiave. 
#### `AbstractDownloader`
Classe che viene implementata dalle classi che permettono di scaricare dati. Ha tre metodi principali di nostro interesse:
* `setOnProgressListener(OnProgressListener)`: permette di associare un listener di progresso
* `download()`: scarica e parsa i dati sul thread corrente
* `download(File)`: apre un nuovo thread in cui viene scaricato il contenuto su un file temporaneo specificato, mentre nel thread corrente il file viene letto e parsato parallelamente.
### Download anagrafiche
Come già detto, prima di scaricare i dati veri e propri, dobbiamo scaricare i dati anagrafici. Tutti questi dati sono raccolti nella classe `Anagrafiche`, e per scaricarli è sufficente scrivere:
```java
Anagrafiche a = new Anagrafiche.Downloader().download();
```
Siccome i dati non sono molti, non è necessario (anche se possibile) settare un listener di progresso o di parallelizare download e parsing.
In questa classe si trovano tutte le informazioni anagrafiche presenti su siope.it.
Ogni anagrafica contenuta è una `AutoMap`, che ha come chiave il suo codice, e come valore l'istanza della classe contenente le informazioni.

### Download dati entrate/uscite
Dal momento che i dati di entrate/uscite sono molti (nell'ordine dei milioni), si è reso necessario, soprattuto in dispositivi limitati come device Android, parallelizzare il download e il parsing/elaborazione dei dati.
Di seguito uno snippet per scaricare le entrate: 
```java
new Entrata.Downloader(anno, anagrafiche)
    .setOnProgressListener(System.out::println) //Progresso viene stampato sullo standard output
    .download(new File("temp.zip")); //File temporaneo
```
* `anno` è un valore intero che rappresenta l'anno del quale vogliamo effettuare il download.
* `anagrafiche` rappresenta un'istanza della classe `Anagrafiche`, spiegata in precedenza.

Viene restituito un oggetto di tipo `ClosableIterator` che itera attraverso le operazioni parsate. Bisogna inoltre ricordarsi di chiudere l'iteratore con il metodo `close()` quando si è finito di utilizzarlo per chiudere ed eliminare il file temporaneo.

### Esempio di utilizzo
Un esempio completo di utilizzo della libreria in cui si ipotizza di voler ottenere la somma delle spese di tutti gli enti, suddivise per provincia, nell'anno 2017, si può trovare [qui](https://github.com/MMauro94/siopeDownloader/tree/master/src/main/test/Example.java). 