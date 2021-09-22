# System Mobilnego Planowania Taktycznego

## Opis
Projekt aplikacji mobilnej do wspomagania planowania taktycznego z wykorzystaniem open street maps. Aplikacja potrzebuje 
wygenerowanego certyfikatu aby działać.

## Zasada działania
#### Autoryzacja
_Każdy użytkownik posiada własny,
ustalany na starcie aplikacji identyfikator._

![1.png](https://imgupload.pl/images/2021/09/22/Screenshot_1632310757.md.png)

#### Lokalizacja
_Dane na temat lokalizacji poszczególnych użytkowników oraz kształtów są przesyłane poprzez wydzielony serwer._

[![2.png](https://imgupload.pl/images/2021/09/22/Screenshot_1632310766.md.png)

#### Dodawanie znaków APP-6B
_Aplikacja posiada możliwość generowanie i dodawania znaków APP-6B, które zapisywane są w bazie danych i wyświetlane wszystkim użytkownikom._

[![3.png](https://imgupload.pl/images/2021/09/22/Screenshot_1632310772.md.png)

#### Zaznaczanie obszarów
_Aplikacja umożliwia poprzez kliknięcie przycisku w prawym górnym rogu zaznaczenie oraz dodanie obszaru._

[![4.png](https://imgupload.pl/images/2021/09/22/Screenshot_1632310790.md.png)

#### Ustawienia
_Aplikacja posiada specjalnie przygotowany panel ustawień, w którym zmienić można wielkość znaków oraz kolory użytkowników._

[![5.png](https://imgupload.pl/images/2021/09/22/Screenshot_1632310798.md.png)

#### Zastosowanie ustawień
_Widok aplikacji po dodaniu znaku, obszaru oraz zastosowaniu ustawień._

[![6.png](https://imgupload.pl/images/2021/09/22/Screenshot_1632310816.md.png)


## O projekcie

1. OSMdroid - Darmowa biblioteka wykorzystywanawe wszystkim co związane z mapami, tj. markerami, rysowaniem kształtów oraz wyświetlaniem samej mapy
    *  [OSMdroid](https://osmdroid.github.io/osmdroid/index.html).
2. Retrofit - Odpowiedzialny za komunikacje pomiędzy serwerem a aplikację
    *  [Retrofit](https://square.github.io/retrofit/).
3. Koin - inteligentna biblioteka do wstrzykiwania Kotlin, aby skupić się na aplikacji, a nie na narzędziach
    *  [Koin](https://insert-koin.io/).

## Instalacja

W celu zmian w projekcie należy pobrać projekt uruchomić go używając najlepiej Android Studio zbudować projekt oraz wprowadzić zmiany.
Drugim sposobem jest wgranie w telefon pliku .apk i zainstalowanie go na komputerze.

Dodatkowo, aby aplikacja działała należy uruchomić specjalny serwer który pobrać należy z drugiego repozytorium i uruchomić go.
Ponadto na telefonie należy zainstalować certyfikat SSL obsługiwany przez serwer, aby móc się zautoryzować w celu uzyskania 
informacji z serwera.

## Serwer
1. SMPT server



