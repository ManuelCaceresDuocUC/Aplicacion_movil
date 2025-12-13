# 📱 BarLácteo Móvil – Proyecto Android Full Stack

**Asignatura:** DSY1105 – Desarrollo de Aplicaciones Móviles  
**Estudiante:** Manuel Cáceres Marín  
**Institución:** Duoc UC – Sede Viña del Mar  
**Profesor:** MARIA IGNACIA COBO OLIVARES


---

Este proyecto representa la culminación del ramo, presentando una aplicación Android nativa desarrollada en **Kotlin con Jetpack Compose**. La solución ha evolucionado de una persistencia  local a una arquitectura robusta conectada a un **Backend Spring Boot hospedado en Railway**.

La App integra autenticación real, gestión de perfiles con imágenes en la nube, un carrito de compras dinámico y un **flujo de pago real integrado con Webpay**.
---

## 🎯 Objetivos y Alcance
- **Arquitectura Cliente-Servidor:** Conexión vía REST API con un backend Spring Boot.
- **Flujo de Pagos:** Integración de transacciones reales mediante Webpay (Transbank).
- **Gestión Avanzada de Imágenes:** Carga y subida de imágenes optimizadas (BLOB) desde/hacia base de datos remota.
- **UI Reactiva:** Uso de Jetpack Compose con actualización de estado en tiempo real (StateFlow).
- **Persistencia Híbrida:** DataStore para sesión local + MySQL (vía API) para datos transaccionales.

---

## ⚙️ Stack Tecnológico
### 📱 Android (Frontend)
- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Arquitectura:** MVVM (Model-View-ViewModel)
- **Networking:** Retrofit 2 + OkHttp + Gson
- **Imágenes:** Coil (Carga asíncrona y caché)
- **Inyección de Dependencias:** ViewModel Factory Pattern
- **Persistencia Local:** DataStore Preferences
- **Hardware:** Acceso a Cámara y Galería (`ActivityResultContracts`)

### ☁️ Backend & Servicios (Integrados)
- **Servidor:** Spring Boot (Java)
- **Base de Datos:** MySQL
- **Hosting:** Railway (Despliegue continuo)
- **Pasarela de Pago:** Webpay Plus (Integración REST)

---

## 🧠 Arquitectura del Proyecto
La estructura se ha refactorizado para soportar la comunicación remota:

```text
com.example.barlacteo_manuel_caceres  
├── data  
│   ├── remote  
│   │   ├── UsuariosApiService.kt  (Endpoints: Login, Registro, Pagos, Fotos)
│   │   └── NetworkModule.kt       (Configuración Retrofit Singletons)
│   ├── repository  
│   │   ├── ProfileRepository.kt   (Manejo de Multipart y BLOBs)
│   │   └── AuthRepository.kt      
│   └── local (DataStore para sesión)  
│  
├── domain  
│   └── model (Data Classes: Producto, Account, PedidoUsuario)  
│  
├── ui  
│   ├── viewmodel  
│   │   ├── CheckoutViewModel.kt   (Lógica de pagos y carrito)
│   │   ├── ProfileViewModel.kt    (Gestión de foto remota)
│   │   └── ...  
│   ├── profile (ProfileScreen, Lógica de cámara/galería)  
│   ├── principal (Catálogo y Carrito)  
│   └── utils (ImageUtils para compresión, formateadores de moneda)
