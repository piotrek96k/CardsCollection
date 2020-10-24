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
-zbudować projekt, w folderze /PokemonCards wywołać polecenie mvn package -> przejść do /PokemonCards/target -> uruchomić aplikację poleceniem java -jar PokemonCards-0.0.1-SNAPSHOT.war  

Po uruchomieniu:  
W przeglądarce przejść do: https://localhost  
Dodać wyjątek bezpieczeństwa  
Zarejestrować użytkownika lub zalogować się za pomocą konta administratora  
nazwa użytkownika: admin  
hasło: admin  

Jeżeli próba wysłania maila nie będzie działać:  
  
Kontem wykorzystanym do wysyłania maili z aplikacji jest Google, niestety czasem Google sam wyłącza dostęp
do konta aplikacjom zewnętrznym, jeżeli tak się wydarzy należy:  
  
Zalogować się na konto Google:  
nazwa użytkownika: PokemonCardsAdm@gmail.com  
hasło: PokemonCards  
Otworzyć:  
https://myaccount.google.com/lesssecureapps?pli=1&rapt=AEjHL4PAtCBs733DVyoVX_mkj5LekVaryHIfCtqlMIEw3WDZ0n7WFXUkKkm61S62qlIJrdLu2-Kc-FjX74dM8u6y7MJZKVZpnA  

Włączyć opcję: Zezwalaj na mniej bezpieczne aplikacje  