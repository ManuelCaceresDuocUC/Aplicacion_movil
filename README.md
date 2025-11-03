📱 Proyecto Android Studio – Evaluación Parcial 2  
Asignatura: DSY1105 – Desarrollo de Aplicaciones Móviles  
Estudiante: Manuel Cáceres Marín  
Institución: Duoc UC – Sede Viña del Mar  
Profesor: [Nombre del docente]

---

Este proyecto fue desarrollado como parte de la Evaluación Parcial 2 del módulo Desarrollo de Aplicaciones Móviles (DSY1105). Consiste en una aplicación Android nativa desarrollada en Kotlin con Jetpack Compose, que implementa un flujo de autenticación (login y registro), gestión de perfil con foto tomada desde la cámara o galería, y navegación modular utilizando arquitectura MVVM. El objetivo fue demostrar dominio en interfaz visual, validaciones desacopladas, animaciones funcionales, uso de recursos nativos y persistencia local de datos.

---

## 🎯 Objetivos principales
- Implementar una interfaz coherente y funcional, respetando jerarquía visual y principios de usabilidad.
- Incorporar formularios validados con retroalimentación visual y mensajes de error.
- Aplicar validación centralizada y desacoplada desde la capa de ViewModel.
- Integrar animaciones funcionales con AnimatedVisibility y Crossfade.
- Usar recursos nativos del dispositivo (cámara y galería).
- Mantener una arquitectura limpia (MVVM) con persistencia local mediante DataStore.
- Demostrar trabajo colaborativo y control de versiones en GitHub.

---

## ⚙️ Tecnologías utilizadas
- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Arquitectura:** MVVM
- **Persistencia local:** DataStore
- **Navegación:** NavHost, NavController
- **Animaciones:** AnimatedVisibility, Crossfade
- **Recursos nativos:**
    - Cámara (ActivityResultContracts.TakePicture)
    - Galería (ActivityResultContracts.PickVisualMedia)
- **Inyección de dependencias:** ViewModel con Factory personalizada
- **Control de versiones:** Git y GitHub

---

## 🧠 Estructura general del proyecto
com.example.barlacteo_manuel_caceres  
├── data  
│   ├── local  
│   │   ├── AccountStore.kt  
│   │   ├── ProfileStore.kt  
│   │   └── DAOs / DataStore  
│   ├── repository  
│   │   ├── AuthRepository.kt  
│   │   ├── ProfileRepository.kt  
│   │   └── CatalogRepository.kt  
│   └── remote (simulado para esta evaluación)  
│  
├── ui  
│   ├── auth (Login, Register, ViewModel, Factory)  
│   ├── profile (ProfileScreen, cámara y galería)  
│   ├── principal (pantalla inicial)  
│   ├── nav (AppNav, Routes)  
│   ├── components (composables reutilizables)  
│   └── theme (colores, tipografía)  
│  
├── di (módulos y dependencias)  
└── utils (helpers generales)

---

## 💡 Funcionalidades implementadas
- Inicio de sesión y registro con validación de nombre y teléfono (+569########) y manejo de estado desde AuthViewModel.
- Animaciones con AnimatedVisibility para mostrar errores y Crossfade para transiciones visuales.
- Gestión de perfil que permite tomar una fotografía con la cámara o seleccionar una imagen desde la galería y guardarla localmente.
- Persistencia local con DataStore para conservar sesión y preferencias.
- Navegación modular implementada con NavHost y rutas definidas en Routes.kt.

---

## 📷 Recursos nativos utilizados
| Recurso | Descripción | Archivo |
|----------|--------------|---------|
| Cámara | Permite tomar una foto de perfil y guardarla localmente. | ProfileScreen.kt |
| Galería | Permite elegir una imagen desde el almacenamiento del dispositivo. | ProfileScreen.kt |

---

## 🧩 Validaciones y animaciones
Las validaciones se implementaron en AuthViewModel mediante la clase AuthValidator.kt, donde la UI refleja el estado (isFonoValid, isNombreValid, errorFono, errorNombre). Las animaciones se aplican en los formularios de Login y Registro para mejorar la fluidez visual y la experiencia del usuario.

---

## 💾 Persistencia local
Se utiliza DataStore Preferences para almacenar:
- Estado de sesión (usuario autenticado).
- Datos del perfil (nombre, teléfono, URI de imagen seleccionada).  
  Esto permite mantener la sesión activa entre ejecuciones.

---

## 🤝 Trabajo colaborativo
El proyecto se gestionó mediante GitHub con commits descriptivos y un tablero Trello para planificación y seguimiento de tareas.  
Las pruebas se realizaron directamente en Android Studio.  
Commit principal de entrega:  
`refactor(auth): centralizar validación en ViewModel y agregar animaciones funcionales`

---

## ▶️ Instrucciones para ejecución
1. Clonar el repositorio:  
   `git clone https://github.com/ManuelCaceresDuocUC/Barlacteo_Movil.git`
2. Abrir el proyecto en Android Studio (Giraffe o superior).
3. Compilar y ejecutar en un emulador o dispositivo físico.
4. Probar el flujo:
    - Crear cuenta con nombre y teléfono.
    - Tomar o elegir foto en perfil.
    - Cerrar sesión y volver a iniciar.

---

## 🧾 Conclusión
El desarrollo de esta aplicación permitió poner en práctica los contenidos del módulo, integrando buenas prácticas de diseño y navegación con Compose, validaciones desacopladas, arquitectura MVVM, recursos nativos (cámara y galería) y persistencia local con DataStore.  
La app es funcional, modular y cumple con todos los criterios exigidos en la Evaluación Parcial 2 de DSY1105.

---

## 📎 Enlaces
- **Repositorio GitHub:** [https://github.com/ManuelCaceresDuocUC/Barlacteo_Movil](https://github.com/ManuelCaceresDuocUC/Barlacteo_Movil)
- **Tablero Trello:** [https://trello.com/b/...](https://trello.com/b/...)
- **Profesor guía:** [Nombre del docente]

---

_Desarrollado por **Manuel Cáceres Marín** – 2025_
