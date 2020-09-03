# Pokemon Cards
Serwis do kolekcjonowania kart pokemon z wykorzystaniem https://pokemontcg.io/

Minimalna wersja javy: 12
Baza danych: PostgreSQL

Przed uruchomieniem:
należy utworzyć bazę danych o nazwie Pokemon_Cards
w pliku /src/main/resources/application.properties ustawić
spring.datasource.username=twoja nazwa_użytkownika (w projekcie postgres)
spring.datasource.password=twoje hasło (w projekcie postgres)
ewentualnie można zmienić nazwę bazy danych wówczas w
spring.datasource.url=jdbc:postgresql://localhost:5432/Pokemon_Cards
należy zamienić Pokemon_Cards na Twoja_Nazwa

Uruchomienie:
Przy wykorzystaniu IDE
-Eclipse: w ProjectExplorer wybrać PokemonCards/src/main/java/com/pokemoncards/PokemonCardsApplication.java -> Run As Java Application
-IntelliJ: w explorzerze wybrać PokemonCards/src/main/java/com/pokemoncards/PokemonCardsApplication.java -> run -> PokemonCardsApplication
Za pomocą konsoli (wymagana instalacja mavena)
-przejść do folderu /PokemonCards -> uruchomić aplikację poleceniem mvn spring-boot:run

Po uruchomieniu:
W przeglądarce przejść do: localhost:8080