# README del Proyecto

## Índice
1. [Tecnologías utilizadas](#-tecnologías-utilizadas)  
2. [Descripción del módulo](#-descripción-del-módulo)  
3. [Funcionamiento del módulo](#-funcionamiento-del-módulo)  
4. [Diagramas](#-diagramas)  
5. [Funcionalidades](#-funcionalidades)  
6. [Endpoints expuestos](#-endpoints-expuestos)  
7. [Manejo de errores](#-manejo-de-errores)  
8. [Mensajería (colas/tópicos)](#-mensajería-colastópicos)  
9. [Pruebas](#-pruebas)  
10. [Ejecución del proyecto](#-ejecución-del-proyecto)  
11. [Despliegue CI/CD](#-despliegue-cicd)  
12. [Estructura del código](#-estructura-del-código)  
13. [Documentación del código](#-documentación-del-código)  
14. [Conexiones con servicios externos](#-conexiones-con-servicios-externos)  
15. [Calidad de código](#-calidad-de-código)  
16. [Pipelines](#-pipelines)  

---

## Tecnologías utilizadas
Describe aquí las tecnologías usadas en el proyecto.  
Ejemplo:
- Lenguaje:  
- Framework:  
- Base de datos:  
- Herramientas adicionales:  

---

## Descripción del módulo
Explica qué hace el módulo, su propósito dentro del sistema y su responsabilidad principal.

---

## Funcionamiento del módulo
Describe:
- Cómo funciona internamente  
- Qué otros módulos lo consumen  
- Patrones utilizados (ej: MVC, Hexagonal, etc.)  
- Estilo de arquitectura  

---

## Diagramas
Incluye o referencia:
- Diagramas de datos  
- Diagramas de clases  
- Diagramas de componentes  



- Diagramas de Secuencia
Un diagrama de secuencia es un tipo de diagrama UML que muestra, en orden temporal, cómo interactúan los actores y los componentes del sistema mediante mensajes o llamadas.

Es importante porque permite entender el comportamiento dinámico de cada funcionalidad, validar los flujos antes de implementar, detectar errores de lógica y comunicar claramente cómo se procesa cada solicitud de extremo a extremo.


- `RegistroDeUsuario.png`

  Muestra el flujo de registro: envío de datos, validación de correo, creación del usuario y envío del OTP.

    ![RegistroDeUsuario](src/main/resources/RegistroDeUsuario.png)

- `ValidacionDeOTP.png`

  Describe la validación del código OTP, la activación de la cuenta y la generación de tokens.

    ![ValidacionDeOTP](src/main/resources/ValidacionDeOTP.png)

- `InicioDeSesion.png`

  Representa el inicio de sesión con validación de credenciales y emisión de access token y refresh token.

    ![InicioDeSesion](src/main/resources/InicioDeSesion.png)

- `CerrarSesion.png`

  Explica el cierre de sesión mediante la revocación del refresh token activo.

    ![CerrarSesion](src/main/resources/CerrarSesion.png)

- `RenovacionDeToken.png`

  Ilustra la renovación de sesión usando un refresh token válido para emitir nuevos tokens.

    ![RenovacionDeToken](src/main/resources/RenovacionDeToken.png)

- `ReenviarOTP.png`

  Muestra el proceso para generar y reenviar un nuevo OTP cuando el anterior no es usable.

    ![ReenviarOTP](src/main/resources/ReenviarOTP.png)

- `RecuperarContraseña.png`

  Describe la solicitud de recuperación de contraseña y el envío del código de recuperación al correo.

    ![RecuperarContraseña](src/main/resources/RecuperarContraseña.png)

- `ResetearContraseña.png`

  Representa la validación del código de recuperación y la actualización final de la contraseña.

    ![ResetearContraseña](src/main/resources/ResetearContraseña.png)


---

## Funcionalidades
Lista las funcionalidades principales del módulo y una breve descripción de cada una.

---

## Endpoints expuestos
Para cada endpoint especifica:

- Ruta (endpoint)  
- Método HTTP  
- Entrada  
- Salida  
- Happy Path (flujo exitoso)  

---

## Manejo de errores
Describe:
- Tipos de errores  
- Códigos HTTP  
- Respuestas en caso de fallo  

---

## Mensajería (colas/tópicos)
Si aplica, describe:
- Nombre de colas o tópicos  
- Información enviada  
- Respuestas posibles  
- Happy Path  
- Dead Letter (manejo de fallos)  

---

## Pruebas
Incluye:
- Evidencia de pruebas  
- Tipos de pruebas (unitarias, integración, etc.)  
- Cómo ejecutarlas  

---

## Ejecución del proyecto
Explica cómo correr el proyecto localmente.  
Ejemplo:
- Instalación de dependencias  
- Variables de entorno  
- Comando de ejecución  

---

## Despliegue CI/CD
Describe:
- Proceso de despliegue  
- Enlace en Azure  
- Swagger expuesto  

---

## Estructura del código
Explica cómo está organizado el proyecto en carpetas y archivos.

---

## Documentación del código
Indica:
- Convenciones usadas  
- Documentación por funciones, clases y propiedades  

---

## Conexiones con servicios externos
Lista los servicios externos que utiliza el sistema y cómo se conectan.

---

## Calidad de código
Incluye:
- Métricas de calidad  
- Herramientas utilizadas (Jacoco, Sonar, etc.)  

---

## Pipelines
Cada repositorio debe tener:
- Pipeline de desarrollo  
- Pipeline de producción  
