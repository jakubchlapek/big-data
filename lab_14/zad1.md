# Technologie Big Data

## Zadanie 2: Delta Lake vs Apache Iceberg

### Delta Lake

- **Ograniczona kompatybilność z systemami** – format Delta nie jest wspierany przez wszystkie silniki bazodanowe  
- **Obsługa transakcji ACID** – zapewnia integralność operacji dzięki gwarancjom atomowości, spójności, izolacji i trwałości  
- **Możliwość wersjonowania danych** – pozwala na śledzenie zmian w zbiorach danych oraz powrót do wcześniejszych wersji  
- **Integracja z wieloma silnikami obliczeniowymi** – współpracuje z różnymi narzędziami przetwarzania danych  
- **Lepsza wydajność przy małych plikach** – zoptymalizowany do pracy z dużą liczbą niewielkich plików  

### Apache Iceberg

**Iceberg sprawdzi się lepiej w następujących sytuacjach:**

- **Potrzeba pracy z różnymi silnikami analitycznymi** – lepsza współpraca z narzędziami spoza ekosystemu Databricks  
- **Obsługa bardzo dużych wolumenów danych** – skuteczniejsze zarządzanie danymi w skali petabajtowej  
- **Brak zależności od Databricks** – idealne rozwiązanie dla środowisk niezwiązanych z tą platformą  

---

## Zadanie 3: Wady architektury Medallion (Bronze/Silver/Gold)

### Problemy operacyjne

- **Większa złożoność techniczna** – każda warstwa to dodatkowa logika, która wymaga implementacji i utrzymania  
- **Wyższe koszty obliczeniowe** – wielokrotne przetwarzanie danych oznacza większe zużycie zasobów  
- **Opóźniony dostęp do danych** – konieczność przechodzenia przez wszystkie poziomy transformacji wydłuża czas odpowiedzi  

### Trudności w zarządzaniu danymi

- **Powielanie danych** – różne wersje tych samych danych w wielu warstwach zwiększają zapotrzebowanie na przestrzeń  
- **Brak elastyczności** – sztywna struktura może nie być optymalna dla mniej złożonych przypadków użycia  
- **Problemy z analizą błędów** – wykrycie źródła problemu może być trudne ze względu na liczbę przetwarzań pośrednich  

### Wyzwania przy rozwoju

- **Wysoki próg wejścia** – wymagane głębokie zrozumienie zależności i procesów ETL  
- **Powielanie reguł biznesowych** – podobne transformacje mogą być wielokrotnie kodowane na różnych poziomach  
- **Złożone testowanie** – konieczność testów jednostkowych, integracyjnych i walidacyjnych dla każdej warstwy osobno  

### Ograniczenia architektoniczne

- **Przesadna komplikacja prostych projektów** – wdrażanie pełnej architektury Medallion tam, gdzie nie jest to potrzebne, prowadzi do niepotrzebnego przerostu formy nad treścią  
- **Nienaturalne podziały procesu** – część przekształceń mogłaby być realizowana bez dzielenia na warstwy  
- **Trudności z synchronizacją danych** – niespójności pomiędzy wersjami danych mogą prowadzić do błędnych analiz  

### Ryzyka i utrzymanie

- **Większe ryzyko błędów ludzkich** – rozbudowany kod zwiększa prawdopodobieństwo pomyłek  
- **Wolniejszy rozwój rozwiązań** – każdy poziom architektury wymaga osobnego przygotowania i wdrożenia  
- **Złożony audyt** – trudność w prześledzeniu, które dokładnie dane zostały użyte na poszczególnych etapach  

### Problemy z użytkowaniem

- **Ograniczony dostęp do danych surowych** – eksploracja ad hoc może być utrudniona przez konieczność przejścia przez pipeline  
- **Potencjalny bałagan przy słabej implementacji** – brak standardów może prowadzić do powstania nieczytelnej i niespójnej struktury danych  
- **Kłopoty z ewolucją schematu** – zmiany w strukturze danych mogą powodować błędy w kolejnych etapach przetwarzania  

### Trudności w automatyzacji

- **Złożoność monitoringu i retry’ów** – procesy pomiędzy warstwami wymagają zaawansowanego zarządzania  
- **Zależności między etapami** – rozbudowana sieć zależności może utrudniać zarządzanie i debugging  
