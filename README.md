# Pokemon Cards
Serwis do kolekcjonowania kart pokemon z wykorzystaniem https://pokemontcg.io/

Minimalna wersja javy: 12
Baza danych: PostgreSQL

Przed uruchomieniem:
należy utworzyć bazę danych o nazwie Pokemon_Cards
w klasie com.pokemoncards.config.DataSourceConfig ustawić
USERNAME = "twoja nazwa użytkownika";
PASSWORD = "twoje hasło";
ewentualnie można zmienić nazwę bazy danych wówczas w zmiennej
URL="jdbc:postgresql://localhost:5432/Pokemon_Cards";
należy zamienić Pokemon_Cards na Twoja_Nazwa

Uruchomienie:
Przy wykorzystaniu IDE
-Eclipse: w ProjectExplorer wybrać PokemonCards/src/main/java/com/pokemoncards/PokemonCardsApplication.java -> Run As Java Application
-IntelliJ: w explorzerze wybrać PokemonCards/src/main/java/com/pokemoncards/PokemonCardsApplication.java -> run -> PokemonCardsApplication
Za pomocą konsoli (wymagana instalacja mavena)
-przejść do folderu /PokemonCards -> uruchomić aplikację poleceniem mvn spring-boot:run

Po uruchomieniu:
W przeglądarce przejść do: https://localhost
Dodać wyjątek bezpieczeństwa
Zarajestrować użytkownika lub zalogować się za pomocą konta administratora
nazwa użytkownika: admin
hasło: admin