## Undercover - joc de societate pe Android

### Descrierea jocului
Aplicația Undercover este o adaptare digitală a jocului de societate cu același nume, unde jucatorii primesc roluri si cuvinte apropiate (castron vs farfurie) si scopul fiind acela de a identifica rolul oponenților fără a dezvălui identitatea proprie.


Jocul se desfășoară în runde, iar fiecare jucător primește un cuvânt secret. Jucătorii trebuie să comunice între ei folosind indicii subtile pentru a-și descoperi identitatea și a evita să fie descoperiți de ceilalți. La finalul fiecărei runde, jucătorii votează pentru a elimina un jucător suspectat de a fi undercover.

### Scenariu joc
1. Jucători undercover (au cuvantul "farfurie") : Stefan, Diana
2. Jucători civili (au cuvantul "castron") : Lorena, Tavi
3. Mr. White (nu are cuvant): Nicoleta

![alt text](image.png)

În prima rundă, Stefan zice "plat" iar Diana zice "supa". Nicoleta zice "perisoare. Ea este cea mai suspicioasă si jucatorii o elimină. 
Jocul continuă până când toți undercover sunt eliminați sau până când undercover reușește să elimine toți jucătorii normali.

![alt text](image-1.png)



### Despre aplicație - analiză State-of-the-Art
Am ales **Jetpack Compose** pentru a dezvolta aplicația, deoarece este un framework modern și declarativ pentru construirea interfețelor de utilizator pe Android. Acesta permite crearea de UI-uri reactive și ușor de întreținut, ceea ce este esențial pentru o aplicație de jocuri. De asemenea, Jetpack Compose oferă integrare ușoară cu alte biblioteci Android, cum ar fi ViewModel și LiveData, facilitând gestionarea stării aplicației.

Testarea se poate face folosind **Compose Test** sau **JUnit Test**, în funcție de nevoile specifice ale aplicației. Compose Test este ideal pentru **testarea interfeței** de utilizator, în timp ce JUnit Test este mai potrivit pentru testarea logicii aplicației și a funcționalităților **backend**. 
Ambele tipuri de teste sunt esențiale pentru asigurarea calității aplicației și pentru a ne asigura că aceasta funcționează corect pe diferite dispozitive și versiuni Android.

### Avantaje ale aplicației noastre
Față de [aplicația originală](https://apps.apple.com/bn/app/undercover-word-party-game/id946882449?uo=2), avem: 
- Generare de cuvinte din fișier CSV (ușor de extins)
- Opțiunea de a elimina/adăuga cuvinte marcate ca vulgare(18+)


### Resurse disponibile
- [Documentația oficială Jetpack Compose](https://developer.android.com/jetpack/compose/documentation)
- [Tutoriale Jetpack Compose](https://developer.android.com/jetpack/compose/tutorial)
- [Tutorial Compose Test](https://developer.android.com/jetpack/compose/testing)

## Scenarii de testare
### Testare UI - Compose Test Framework
- **WHEN** utilizatorul introduce jucătorii, **THEN** aplicația ar trebui să afișeze toți jucătorii în lista de jucători.
- **WHEN** râmân doi jucători, **THEN** aplicația ar trebui să afișeze mesajul "Jocul s-a terminat".

### Exemplu de test  - Compose Test Framework
```kotlin

class GameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gameScreen_displaysCorrectInitialState() {
        val players = listOf(
            Player("Stefan", "Civil", "apple"),
            Player("Diana", "Civil", "apple"),
            Player("Tavi", "Undercover", "banana"),
            Player("Nicoleta", "Mr. White", ""),
            Player("Lorena", "Civil", "apple"),
        )

        composeTestRule.setContent {
            GameScreen(
                players = players,
                onGameEnd = {},
                onResetWords = {},
                onNavigateToPlayers = {}
            )
        }


        composeTestRule.onAllNodesWithText("Stefan").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Diana").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Tavi").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Nicoleta").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Lorena").onFirst().assertIsDisplayed()
    }

    @Test
    fun gameScreen_buttonStates_areCorrect() {
        val endGamePlayers = listOf(
            Player("Stefan", "Civil", "apple"),
            Player("Nicoleta", "Mr.White", "")
        )

        composeTestRule.setContent {
            GameScreen(
                players = endGamePlayers,
                onGameEnd = {},
                onResetWords = {},
                onNavigateToPlayers = {}
            )
        }

        composeTestRule.onNodeWithText("Jocul s-a terminat").assertIsDisplayed()
    }
}
```

### Setup-ul mediului de dezvoltare
- **Instalarea Android Studio**
- **Instalarea SDK-ului Android**
- **Instalarea Jetpack Compose**
- **Dependințe necesare**:
``` 
dependencies {
    implementation "androidx.compose.ui:ui:1.0.0" - # pentru UI
    implementation "androidx.compose.ui:ui-tooling-preview:1.0.0" - # pentru previzualizare
    implementation "androidx.activity:activity-compose:1.3.1" - # pentru activități
    testImplementation "junit:junit:4.13.2" - # pentru testare backend
    androidTestImplementation "androidx.compose.ui:ui-test-junit4:1.0.0" - # pentru testare UI
}

```
Structura proiectului:
```
Undercover_Android_Game/
├── ui/
│   ├── MainScreen.kt          # Configurare joc
│   ├── GameScreen.kt          # Logica eliminării jucătorilor
├── data/
│   ├── Player.kt              # Model de date pentru jucător
│   ├── WordGenerator.kt       # Generare cuvinte din CSV
├── navigation/
│   ├── NavGraph.kt            # Gestionarea rutelor
├── test/
│   ├── androidTest/
│   │   ├── ui/
│   │   │   ├── MainScreenTest.kt  # Teste UI pentru MainScreen
│   │   │   ├── GameScreenTest.kt  # Teste UI pentru GameScreen
│   ├── unitTest/
│   │   ├── data/
│   │   │   ├── PlayerTest.kt      # Teste unitare pentru Player
│   │   │   ├── WordGeneratorTest.kt # Teste unitare pentru WordGenerator
│   │   ├── navigation/
│   │   │   ├── NavGraphTest.kt     # Teste unitare pentru NavGraph
└── ...

```

### Decizii de design
- **Arhitectura aplicației**: Am ales să folosim arhitectura MVC (Model-View-Controller) pentru a separa business logic de interfața utilizatorului. Aceasta ne permite să gestionăm mai ușor starea aplicației și să facem modificări fără a afecta alte părți ale codului.
- **Adăugare de cuvinte în CSV**: Am implementat o funcționalitate care permite utilizatorilor să adauge cuvinte noi în fișierul CSV, astfel încât să putem extinde rapid lista de cuvinte disponibile pentru joc.

### Alte opțiuni de testare 
- **Mockito**: pentru simularea dependențelor și testare unitară
- **Firebase Test Lab**: pentru testarea aplicației pe diferite dispozitive și versiuni Android
