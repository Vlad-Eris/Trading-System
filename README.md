Acest proiect simulează un sistem de tranzacționare pentru stocks și crypto, permițând utilizatorilor să cumpere și să vândă active, să aplice diferite strategii de tranzacționare,să vizualizeze istoricul
tranzacțiilor și să primească notificări pe baza prețurilor din watchlist.

Cerințe Implementate: 

Gestionarea activelor: Sistemul gestionează o listă de stocks și crypto disponibile, cu simboluri, prețuri curente și cantități. 
Gestionarea conturilor utilizatorilor: Utilizatorii se pot înregistra, pot vizualiza portofoliul și își pot urmări tranzacțiile. 
Tranzacționarea activelor: Utilizatorii pot căuta activele disponibile, pot cumpăra/vinde și pot aplica diferite strategii de tranzacționare.
Strategii de tranzacționare: Sistemul include strategii pentru day trading, investiții pe termen lung și auto-buy/sell pe praguri de preț. 
Istoricul tranzacțiilor: Toate tranzacțiile sunt păstrate într-un istoric vizibil pentru utilizator.
Notificări: Utilizatorii primesc notificări când activele din watchlist își schimbă prețul.

Design Patterns Folosite

Factory Method
Motivație: Oferă o metodă centralizată pentru crearea obiectelor de tipul Asset (Stock sau Crypto), fără a încărca clasele principale cu detalii de instanțiere.
Beneficii: Permite adăugarea de noi tipuri de active fără a modifica clasele existente și asigură o gestionare ușoară a tipurilor de active.
Implementare: AssetFactory.createAsset() creează un Stock sau un Crypto pe baza tipului specificat ("stock", "crypto").

Strategy 
Motivație: Permite utilizatorilor să aleagă din mai multe strategii de tranzacționare (ex: Day Trading, Long-Term Investing), fără a modifica codul principal. Aceasta face sistemul flexibil și ușor de extins.
Beneficii: Permite schimbarea dinamică a strategiei de tranzacționare, fără a afecta restul aplicației.
Implementare: Interfața TradingStrategy definește metoda execute() și este implementată de clasele DayTradingStrategy, LongTermStrategy și ThresholdStrategy.

Command
Motivație: Permite encapsularea comenzilor de tranzacționare (BUY/SELL) și le face mai ușor de gestionat. De asemenea, permite implementarea funcționalității de UNDO/REDO.
Beneficii: Permite un control mai mare asupra comenzilor, inclusiv manipularea acestora prin undo/redo și execuția comandă cu detalii clare.
Implementare: Interfața Order definește metodele execute(), undo(), și description(). Comenzile BuyOrder și SellOrder implementează această interfață.

Observer
Motivație: Permite actualizarea în timp real a prețurilor și notificarea utilizatorilor atunci când activele din watchlist se modifică.
Beneficii: Permite adăugarea și eliminarea observatorilor (utilizatori) și notificarea acestora atunci când prețul unui activ se schimbă.
Implementare: PriceTicker implementează interfața Subject, iar UserNotifier implementează Observer.

Singleton
Motivație: Asigură că MarketService este instanțiat o singură dată, permițând accesul global la instanța acestuia.
Beneficii: Previne instanțierea mai multor obiecte de același tip, oferind un punct unic de acces și asigurând consistența datelor.
Implementare: Clasa MarketService implementează pattern-ul Singleton.

Structura Fișierelor: 
command/: conține clasele pentru gestionarea comenzilor de tranzacționare (BuyOrder, SellOrder, TradeExecutor). model/: conține entitățile de bază (Asset, Stock, Crypto, Transaction, User). 
service/: conține serviciile care manipulează logica de tranzacționare și portofoliu (MarketService, PortfolioService).
observer/: conține implementările pentru pattern-ul Observer pentru a notifica utilizatorii (PriceTicker, UserNotifier). 
strategy/: conține strategii de tranzacționare (TradingStrategy, DayTradingStrategy, LongTermStrategy, ThresholdStrategy, TradeFeeDecorator). 
factory/: conține clasa AssetFactory pentru crearea activelor.
