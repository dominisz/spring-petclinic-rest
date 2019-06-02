# Refaktoryzacja Spring PetClinic w wersji REST

## Spring PetClinic

Spring PetClinic to przykładowa aplikacja webowa z dostępnym kodem źródłowym. Aplikacja może służyc do obsługi kliniki dla zwierząt. Umożliwia obsługę lekarzy, wizyt, zwierząt oraz ich właścicieli. Więcej informacji o aplikacji można znaleźć na stronie https://github.com/spring-projects/spring-petclinic. 

Aplikacja dostępna jest w kilku wersjach:
- oryginalna aplikacja wykorzystująca Spring MVC
- aplikacja tylko w wersji backendowej udostępniająca zasoby REST - https://github.com/spring-petclinic/spring-petclinic-rest
- aplikacja frontendowa wykorzystująca Angulara - https://github.com/spring-petclinic/spring-petclinic-angular
- aplikacja frontendowa wykorzystująca ReactJs - https://github.com/spring-petclinic/spring-petclinic-reactjs
- aplikacja z wykorzystaniem microserwisów - https://github.com/spring-petclinic/spring-petclinic-microservices

## Spring PetClinit REST

W zadaniu wykorzystamy wersję z REST API. Oryginalne repozytorium https://github.com/spring-petclinic/spring-petclinic-rest zostało sforkowane.

## Uruchomienie aplikacji

```
git clone https://github.com/dominisz/spring-petclinic-rest
cd spring-petclinic-rest
./mvnw spring-boot:run
```
Aplikacja dostępna jest pod adresem
```
http://localhost:9966/petclinic
```
Aplikacja wykorzystuje Swaggera do utworzenia dokumentacji dla dostępnych endpointów.

## Refaktoryzacja aplikacji

W aplikacji znajduje się szereg testów. Należy je regularnie uruchamiać w trakcie refaktoryzacji.

Należy poprawić (przynajmniej) następujące elementy aplikacji:
- formatowanie - w trakcie pracy wykorzystać google-java-format https://github.com/google/google-java-format
- wskazówki i podpowiedzi wykorzystywanego IDE
- nowe elementy Javy 8 - strumienie, wyrażenia lambda, klasa `Optional`
- zduplikowane fragmenty kodu
- zbędne fragmenty kodu
- nadmiarowy kod w klasach kontrolerów
- rozbudowane klasy serwisowe


