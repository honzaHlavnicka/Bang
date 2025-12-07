# Jak si vyrobit vlastní hru?
Engine je připravený tak, že by měl podporovat velké množství her. Pokud si do něj chceš nějakou naprogramovat, tak použij přesně tento návod. Pokud by něco nefungovalo, nebo by něco engine nepodporoval, tak přidej issue na [github](https://github.com/honzaHlavnicka/Bang/issues). Pokud chceš svojí hru umístit na veřejný server, tak vytvoř pull request. Očekává se alespoň nějaká znalost jazyka Java.

1) **Vytvoř si maven projekt**, na to budeš potřebovat nějaké IDE, třeba Netbeans.
2) **Přidej `plugin-sdk`** jako závislost.
   plugin-sdk si stáhni buďto celé z tohoto repozitáře, a nebo jednodušeji pouze už připravený .jar z repozitáře (bude možné později)
   poté sdk nahraj jako knihovnu, nebo na něj odkaž v pom.xml
3) **Implementuj rozhraní `HerniPlugin`**, je jedno jak si tvojí třídu pojmenuješ, server si jí později najde sám. 
   tento soubor by neměl dělat nic převratného, stačí aby vracel jméno tvojí hry, její popis a uměl vytvářet
   nevytvářej třídě nějaký speciální konstruktor.
4) **Do `HerniPlugin` přidej `HerniPravidla`**, to je soubor, ve kterém si můžeš upravit logiku hry, omezit co hráči mohou dělat, co se jim zobrazuje a podobně.
5) **Vytvoř si karty.** Udělej si kolik tříd chceš, které budou dědit z `Karta`. Karta může být `HratelnaKarta`, `VylozitelnaKarta` a `SpalitelnaKarta`. Nezapomeň, že pokud nebudeš tyto rozhraní implementovat, tak karta nebude umět to co má. (a nestačí pouze implementovat, karta musí vždy i dědit)
   karet můžeš mít kolik potřebuješ, jedna třída může reprezentovat i více karet, ale pokud mají kartu odlišnou logiku, tak se hodí je oddělit do více tříd.
7) **Vytvoř postavy a role** (to zatím nejde, takže to neřeš)
8) **Dodělej pravidla hry**, nezapomeň, aby do balíčku vkláídali karty a buildni projekt. Výsledný .jar soubor můžeš nahrát do složky pluginy, která se nachází tam, kde spouštíš server.
   Pokud server ještě nemáš, tak si ho stáhni a spusť. Potom můžeš otevřít honza.svs.gyarab.cz?adress=ws://locahost:port, kde port je používaný port (server ti ho vypíše do konzole).
   Tady se podívej, jestli tvoje hra jde vytvořit. Otestuj ji a oprav co nefunguje.
9) **Máš hotovo!** Teď už ji můžeš hrát buď na lokální síti (ostatní se připojí na honza.svs.gyarab.cz?adress=ws://tvoje lokální IP:port), nebo přidej pull request na GitHub a já ji přidám do centrálního serveru, aby ji mohl hrát kdokoliv.
    Při přidávání pull requestu nahraj celý kód, ne jenom .jar soubor, abych mohl ověřit, zda neobsahuje škodlivý obsah.
    Pokud nechceš hru zveřejňovat, ale chceš hrát po globální síti, tak si nastuduj tunelování.
