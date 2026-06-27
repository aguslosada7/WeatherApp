# 🌤️ WeatherApp

Una aplicación **Kotlin Multiplatform (KMP)** que muestra el clima actual basado en tu ubicación, permite buscar ciudades y guardar favoritas en la nube.

Desarrollada como parte del challenge técnico de **AranguriApps**.

> ⚠️ **Nota de plataforma**: El proyecto está configurado para Android e iOS con KMP. Por limitaciones de hardware (sin entorno macOS/Xcode), la compilación y verificación iOS no fue posible. Sin embargo, toda la lógica de negocio, repositorios y ViewModels viven en `shared/commonMain` y son 100% reutilizables por la app iOS.

---

## 📱 Funcionalidades

- Clima actual basado en la **ubicación GPS** del dispositivo
- Temperatura, descripción, humedad, viento y sensación térmica
- **Pronóstico de las próximas 24 horas** con íconos animados
- **Tema dinámico** que cambia los colores según la hora del día (amanecer, mañana, tarde, atardecer, noche)
- **Íconos con tinte** según condición climática (sol dorado, luna azul, lluvia, tormenta, nieve)
- **Búsqueda de ciudades** por nombre con resultado inmediato
- **Ciudades favoritas** sincronizadas con Supabase, con vista expandible animada
- Navegación fluida con transiciones entre pantallas
- Manejo de errores con opción de reintento

---

## 🏗️ Arquitectura

Se eligió **MVVM + Clean Architecture** con **Kotlin Multiplatform** por las siguientes razones:

- **Máxima reutilización de código**: toda la lógica (repositorios, use cases, ViewModels) vive en `shared/commonMain` y es compartida entre Android e iOS.
- **Separación de responsabilidades**: cada capa tiene una responsabilidad definida e independiente.
- **Testabilidad**: los Use Cases son testeables de forma aislada sin dependencias de plataforma.
- **Escalabilidad**: agregar features o una nueva plataforma tiene impacto mínimo en el código existente.

### Capas

```
shared/commonMain
├── domain/        → Modelos, interfaces de repositorios, Use Cases
├── data/          → Implementaciones (Ktor, Supabase, mappers, DTOs)
├── presentation/  → ViewModels compartidos
└── di/            → Módulos de Koin

composeApp (Android)
├── presentation/  → Pantallas Compose
├── navigation/    → AppNavigation con transiciones
└── ui/            → Tema dinámico, tintes de íconos

iosApp (iOS)
└── ContentView    → UI SwiftUI (consume ViewModels del shared vía framework)
```

### Flujo de datos

```
Compose UI → ViewModel (shared) → UseCase (shared) → Repository Interface (shared)
                                                              ↓
                                                   RepositoryImpl → Ktor / Supabase
```

---

## 🗺️ Diagrama de arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                    androidApp (Compose)                 │
│  ┌─────────────┐  ┌──────────────┐  ┌───────────────┐   │
│  │  HomeScreen │  │ SearchScreen │  │FavoritesScreen│   │
│  └──────┬──────┘  └──────┬───────┘  └───────┬───────┘   │
└─────────┼────────────────┼──────────────────┼───────────┘
          │                │                  │
┌─────────▼────────────────▼──────────────────▼────────────┐
│                  shared/commonMain                       │
│  ┌──────────────────────────────────────────────────┐    │
│  │                  presentation/                   │    │
│  │  HomeViewModel  SearchViewModel  FavoritesVM     │    │
│  └──────────────────────┬───────────────────────────┘    │
│                         │                                │
│  ┌──────────────────────▼───────────────────────────┐    │
│  │                    domain/                       │    │
│  │  GetWeatherByCity  GetForecast  Favorites UCs    │    │
│  │  WeatherRepository (interface)                   │    │
│  └──────────────────────┬───────────────────────────┘    │
│                         │                                │
│  ┌──────────────────────▼───────────────────────────┐    │
│  │                     data/                        │    │
│  │  WeatherRepositoryImpl  FavoritesRepositoryImpl  │    │
│  │  WeatherApi (Ktor)      SupabaseClient           │    │
│  └──────────────────────┬───────────────────────────┘    │
└─────────────────────────┼────────────────────────────────┘
                          │
          ┌───────────────┴───────────────┐
          ▼                               ▼
  OpenWeatherMap API               Supabase (PostgreSQL)
  (clima + forecast)               (ciudades favoritas)
```

---

## 🛠️ Stack tecnológico

| Tecnología | Uso |
|---|---|
| Kotlin Multiplatform | Lógica compartida Android + iOS |
| Jetpack Compose | UI declarativa Android |
| Ktor | Networking multiplataforma |
| Kotlinx Serialization | Parsing JSON |
| Koin | Inyección de dependencias KMP-compatible |
| Kotlinx Coroutines + Flow | Asincronismo y estado reactivo |
| Kotlinx DateTime | Manejo de timestamps del forecast |
| Coil 3 | Carga de imágenes con soporte KMP |
| Navigation Compose (JetBrains) | Navegación con transiciones animadas |
| OpenWeatherMap API | Clima actual y pronóstico horario |
| Supabase (Postgrest) | Backend para ciudades favoritas |
| Google Play Services Location | Geolocalización GPS |
| MockK + Coroutines Test | Testing de Use Cases |

---

## 🤖 Uso de herramientas de IA

Durante el desarrollo se utilizó **Claude (Anthropic)** como copiloto principal:

- **Scaffolding inicial**: estructura de módulos KMP, configuración de `build.gradle.kts` para targets Android e iOS.
- **Setup de Ktor y Supabase**: configuración del cliente HTTP, plugins de serialización y cliente Supabase KMP.
- **Módulos Koin**: definición del grafo de dependencias compartido entre plataformas.
- **DTOs y mappers**: data classes con `@Serializable` y transformación a modelos de dominio.
- **ViewModels compartidos**: manejo de `StateFlow`, `sealed class` para estados de UI y coroutines.
- **UI con Compose**: pantallas, animaciones, tema dinámico por hora y tintes de íconos.
- **Unit tests**: estructura de tests con MockK para Use Cases.
- **Debugging**: resolución de conflictos de versiones de Gradle, compatibilidad KMP y errores de compilación.
- **README y documentación**: estructura, redacción y diagrama de arquitectura.

El criterio técnico aplicado incluyó: revisión de compatibilidad KMP de cada dependencia, ajuste de versiones para evitar conflictos, corrección de imports entre `commonMain`/`androidMain`, auditoría de que la arquitectura respete las capas definidas, y validación de que el código generado compile y funcione correctamente.

---

## 🔑 Configuración

1. Obtené tu API key gratuita en [openweathermap.org](https://openweathermap.org/api)
2. Creá el archivo `shared/src/commonMain/kotlin/weatherapp/config/AppConfig.kt` con el siguiente contenido:

```kotlin
package weatherapp.config

object AppConfig {
    const val WEATHER_API_KEY = "tu_api_key_aqui"
    const val SUPABASE_URL = "https://zpdfgsbdqcmmitmcjpfd.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpwZGZnc2JkcWNtbWl0bWNqcGZkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODIzMzA3NDgsImV4cCI6MjA5NzkwNjc0OH0.dxAbGG_XktjLqWBQnoUXH-y5ZX0Q6-QRwO7ZdiU0sms"
}
```

> Este archivo está en `.gitignore` por seguridad y debe crearse manualmente al clonar el proyecto.
> La `SUPABASE_ANON_KEY` es una clave pública por diseño — lo que protege los datos son las Row Level Security policies de Supabase.

---

## 🚀 Cómo compilar y correr el proyecto

### Requisitos previos

- Android Studio Meerkat (2024.3) o superior
- JDK 17
- Android SDK API 26+
- Conexión a internet (para la API y Supabase)

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/aguslosada7/WeatherApp.git
cd weather-app

# 2. Crear AppConfig.kt con las keys (ver sección anterior)

# 3. Abrir en Android Studio
# File → Open → seleccionar la carpeta raíz del proyecto

# 4. Sincronizar Gradle
# File → Sync Project with Gradle Files

# 5. Correr en emulador o dispositivo físico
# Seleccionar androidApp → Run (Shift+F10)
```

---

## 🧪 Tests

Tests unitarios implementados en `shared/src/commonTest/`:

- `GetWeatherByCityUseCaseTest` — éxito, fallo y verificación de llamada al repositorio
- `GetWeatherByCoordsUseCaseTest` — éxito, fallo y verificación de coordenadas exactas
- `FavoritesUseCasesTest` — GetFavorites, AddFavorite, RemoveFavorite e IsFavorite

```bash
# Correr tests
./gradlew :shared:testDebugUnitTest
```

---

## 📝 Decisiones técnicas

- **Ktor sobre Retrofit**: Retrofit no es compatible con KMP; Ktor es la opción nativa para networking multiplataforma.
- **Koin sobre Hilt**: Hilt es exclusivo de Android; Koin tiene soporte completo para KMP y `commonMain`.
- **Kotlinx Serialization sobre Gson/Moshi**: solución oficial de JetBrains, compatible con KMP sin reflection.
- **`Result<T>`** nativo de Kotlin para manejo de errores, sin dependencias adicionales como Arrow.
- **`StateFlow`** sobre `LiveData` por ser multiplataforma y más idiomático con Coroutines y Compose.
- **Navigation Compose de JetBrains** sobre la de AndroidX para evitar conflictos con Compose Multiplatform.
- **`async`/`await` en paralelo** en HomeViewModel para cargar clima y forecast simultáneamente, reduciendo el tiempo de carga.
- **Tema dinámico por hora** en vez de seguir el tema del sistema, para una experiencia más inmersiva y contextual.

---
