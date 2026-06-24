# 🌤️ WeatherApp

Una aplicación Android nativa en Kotlin que muestra el clima actual de cualquier ciudad, permite buscar nuevas ubicaciones y guardar favoritos en la nube.

Desarrollada como parte del challenge técnico de **AranguriApps**.

---

## 📱 Funcionalidades

- Clima actual con temperatura, descripción, humedad, viento y sensación térmica
- Búsqueda de ciudades por nombre
- Guardado de ciudades favoritas sincronizado con Supabase
- Navegación fluida entre pantallas con Jetpack Navigation

---

## 🏗️ Arquitectura

Se eligió **MVVM + Clean Architecture** por las siguientes razones:

- **Separación de responsabilidades clara**: cada capa tiene una responsabilidad definida y no conoce los detalles de implementación de las otras.
- **Testabilidad**: los Use Cases y ViewModels son fáciles de testear de forma aislada.
- **Escalabilidad**: agregar nuevas features (ej. pronóstico extendido) no requiere tocar capas existentes.
- **Recomendación oficial de Google** para aplicaciones Android modernas.

### Capas

```
presentation/   → Compose UI + ViewModels (estado de UI)
domain/         → Use Cases + Interfaces de repositorios + Modelos de negocio
data/           → Implementaciones (Retrofit, Supabase, mappers)
di/             → Módulos de Hilt (inyección de dependencias)
```

### Flujo de datos

```
UI (Compose) → ViewModel → UseCase → Repository Interface
                                            ↓
                                   RepositoryImpl → API / Supabase
```

---

## 🛠️ Stack tecnológico

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje principal |
| Jetpack Compose | UI declarativa |
| MVVM + Clean Architecture | Arquitectura |
| Hilt | Inyección de dependencias |
| Retrofit + Gson | Llamadas HTTP y parsing |
| OkHttp Logging Interceptor | Debug de red |
| Coil | Carga de imágenes (íconos del clima) |
| Kotlin Coroutines + Flow | Programación asíncrona y estado reactivo |
| Navigation Compose | Navegación entre pantallas |
| OpenWeatherMap API | Datos del clima |
| Supabase | Backend para favoritos |

---

## 🤖 Uso de herramientas de IA

Durante el desarrollo se utilizó **Claude (Anthropic)** como copiloto principal:

- **Scaffolding inicial**: generación de la estructura de carpetas y clases base (DTOs, repositorios, módulos de Hilt).
- **Boilerplate de Compose**: pantallas iniciales con estado de loading/error/success.
- **Mappers**: transformación de DTOs de la API a modelos de dominio.
- **Debugging**: identificación de errores de compilación y problemas de configuración de Gradle.
- **README**: estructura y redacción de la documentación.

El criterio técnico aplicado sobre la salida de la IA incluyó: revisión de cada clase generada, ajuste de nombres y paquetes al proyecto real, corrección de imports, y auditoría de que la arquitectura respete las capas definidas.

---

## 🔑 Configuración de API Keys

1. Obtené tu API key gratuita en [openweathermap.org](https://openweathermap.org/api)
2. Creá un proyecto en [supabase.com](https://supabase.com) y copiá la URL y la anon key
3. En `local.properties` (no commitear):

```
WEATHER_API_KEY=tu_api_key_aqui
SUPABASE_URL=https://xxxx.supabase.co
SUPABASE_ANON_KEY=tu_anon_key_aqui
```

---

## 🚀 Cómo compilar y correr el proyecto

### Requisitos previos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- Android SDK API 26+
- Conexión a internet (para la API y Supabase)

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/tuusuario/weather-app.git
cd weather-app

# 2. Agregar las API keys en local.properties (ver sección anterior)

# 3. Sincronizar Gradle
# Abrir en Android Studio → File → Sync Project with Gradle Files

# 4. Correr en emulador o dispositivo físico
# Run → Run 'app'  (o Shift+F10)
```

También podés instalar directamente el APK adjunto sin necesidad de compilar.

---

## 📁 Estructura del proyecto

```
app/src/main/java/com/tuapp/weather/
├── data/
│   ├── remote/
│   │   ├── api/          # Interfaces de Retrofit
│   │   └── dto/          # Data classes de la API
│   ├── repository/       # Implementaciones de repositorios + mappers
│   └── local/            # Cliente Supabase y operaciones de favoritos
├── domain/
│   ├── model/            # Modelos de negocio
│   ├── repository/       # Interfaces (contratos)
│   └── usecase/          # Casos de uso
├── presentation/
│   ├── home/             # Pantalla principal + ViewModel
│   ├── search/           # Búsqueda de ciudades + ViewModel
│   └── favorites/        # Favoritos + ViewModel
├── di/                   # Módulos de Hilt
└── ui/theme/             # Tema, colores, tipografía
```

---

## 🗃️ Base de datos (Supabase)

Tabla `favorite_cities`:

```sql
create table favorite_cities (
  id uuid default gen_random_uuid() primary key,
  city_name text not null,
  country text not null,
  created_at timestamp with time zone default now()
);
```

---

## 🧪 Tests

<!-- Completar cuando se agreguen tests -->

- [ ] Unit tests para Use Cases
- [ ] Unit tests para ViewModels
- [ ] Tests de integración del repositorio

---

## 📸 Capturas de pantalla

<!-- Agregar capturas cuando la app esté terminada -->

| Home | Búsqueda | Favoritos |
|------|----------|-----------|
| _pronto_ | _pronto_ | _pronto_ |

---

## 📝 Decisiones técnicas

- **`Result<T>`** nativo de Kotlin para manejo de errores, evitando dependencias adicionales como Arrow.
- **`StateFlow`** sobre `LiveData` por ser más idiomático con Coroutines y Compose.
- **Hilt** sobre Koin por ser la solución recomendada por Google y con mejor soporte en el ecosistema Jetpack.
- **Coil** sobre Glide/Picasso por su soporte nativo para Compose.

---