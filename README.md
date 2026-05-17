# RecuperacionDI - Car Manager App

Proyecto de recuperación de la asignatura Desarrollo de Interfaces.

La aplicación consiste en un sistema de gestión de coches desarrollado con JavaFX, FXML, MySQL y JDBC. Permite registrar usuarios, iniciar sesión, diferenciar entre usuarios normales y administradores, gestionar coches, marcar favoritos, consultar una clasificación general y acceder a una vista detallada de cada coche.

---

# 1. Funcionamiento general de la aplicación

## 1.1. Inicio de la aplicación

Al ejecutar la aplicación, se inicializa la ventana principal mediante la clase `AppShell`. Esta clase se encarga de cargar las distintas vistas FXML y mantener la ventana maximizada, dejando visible la barra de tareas del sistema.

Antes de mostrar la pantalla de login, la aplicación comprueba si existe una sesión persistente guardada en:

```text
src/main/resources/session/session.txt
```

Si existe una sesión válida y el usuario guardado todavía existe en la base de datos, la aplicación inicia sesión automáticamente y carga directamente la pantalla principal.

Si no existe sesión guardada, si el archivo está vacío o si el usuario ya no existe, se muestra la pantalla de inicio de sesión.

Por seguridad, las cuentas con rol `admin` no se guardan como sesión persistente.

---

## 1.2. Registro de usuarios

Desde la pantalla de login, el usuario puede acceder a la pantalla de registro.

El registro solicita:

```text
- Nombre de usuario
- Email
- Contraseña
- Repetición de contraseña
```

Antes de guardar el usuario, se realizan varias comprobaciones:

```text
- Todos los campos deben estar cubiertos.
- El email debe tener un formato básico válido.
- El nombre de usuario no puede estar repetido.
- El email no puede estar repetido.
- La contraseña debe tener una longitud mínima.
- La contraseña debe contener letras y números.
- La contraseña y la repetición deben coincidir.
```

Cuando el registro es correcto, la contraseña no se guarda en texto plano. Se codifica mediante la clase `PasswordUtil`, que genera un hash usando PBKDF2 con salt aleatoria.

Los usuarios registrados desde la interfaz reciben por defecto el rol:

```text
user
```

---

## 1.3. Inicio de sesión

En la pantalla de login, el usuario introduce su nombre de usuario y contraseña.

El proceso de inicio de sesión es el siguiente:

```text
1. Se comprueba que los campos no estén vacíos.
2. Se busca el usuario en la base de datos mediante UserDAO.
3. Si el usuario existe, se comprueba la contraseña con PasswordUtil.
4. Si la contraseña es correcta, se guarda el usuario actual en Session.
5. Se carga la vista principal de la aplicación.
```

La pantalla de login también incluye una casilla:

```text
Mantener sesión iniciada
```

Si esta casilla está marcada y el usuario no es administrador, se guarda el nombre de usuario en el archivo `session.txt`. En el siguiente arranque de la aplicación, se podrá restaurar la sesión automáticamente.

Si la casilla no está marcada, o si el usuario es administrador, no se guarda sesión persistente.

---

## 1.4. Roles de usuario

La aplicación distingue entre dos tipos de usuario:

```text
user
admin
```

El usuario normal puede:

```text
- Ver el listado de coches.
- Filtrar coches por tipo.
- Marcar un coche como favorito.
- Consultar la clasificación.
- Abrir la vista de detalles de cada coche.
```

El administrador puede hacer todo lo anterior y además:

```text
- Crear coches.
- Editar coches.
- Eliminar coches.
- Ver la gestión de usuarios.
- Cambiar roles de usuarios.
- Eliminar usuarios.
```

La aplicación oculta automáticamente los controles de administración cuando el usuario no tiene rol `admin`.

Además, aunque por algún error visual apareciese un botón restringido, los controladores vuelven a comprobar el rol antes de ejecutar acciones importantes.

---

## 1.5. Vista principal

Tras iniciar sesión, se carga la vista principal de la aplicación.

Esta vista contiene:

```text
- Barra de navegación superior.
- Mensaje de bienvenida.
- Panel central dinámico.
- Pie de página.
```

Desde la barra de navegación se puede acceder a:

```text
- Coches
- Clasificación
- Usuarios, solo si el usuario es admin
- Cerrar sesión
```

El contenido central se carga dinámicamente dentro de un `StackPane`. Esto permite cambiar entre vistas sin abrir ventanas nuevas.

---

## 1.6. Vista de coches

La vista de coches muestra todos los coches registrados en la base de datos.

Cada coche aparece como una card con:

```text
- Imagen
- Nombre
- Tipo
- Potencia
- Fecha de matriculación
- Estado de favorito
- Botón para marcar como favorito
```

Si el usuario es administrador, cada card incluye también:

```text
- Botón Editar
- Botón Eliminar
```

En la parte superior existe un filtro por tipo de coche. Los tipos se cargan directamente desde la base de datos, por lo que si se añade un nuevo tipo, aparece automáticamente en el filtro.

La imagen de cada coche se carga desde una ruta guardada en la base de datos, por ejemplo:

```text
images/rayo-mcqueen.png
```

Las imágenes están guardadas dentro de:

```text
src/main/resources/images
```

---

## 1.7. Gestión de coches

La gestión de coches solo está disponible para administradores.

El formulario de administración permite crear o editar coches con los siguientes datos:

```text
- Marca
- Modelo
- Potencia
- Tipo
- Fecha de matriculación
- Ruta de imagen
```

El formulario está dentro de un panel desplegable para que no ocupe demasiado espacio visual.

Cuando el administrador pulsa editar en una card, los datos del coche se cargan en el formulario y el botón cambia a modo actualización.

Cuando se guarda correctamente un coche, la aplicación recarga el listado y actualiza los filtros.

---

## 1.8. Sistema de favoritos

Cada usuario puede marcar un único coche como favorito.

La relación se guarda en la tabla:

```text
favorites
```

Esta tabla relaciona usuarios y coches:

```text
user_id
car_id
```

Cuando un usuario marca un coche como favorito, la aplicación actualiza la base de datos y recarga la vista de coches.

La interfaz destaca visualmente:

```text
- El coche favorito del usuario actual.
- El coche más elegido globalmente.
- El coche que coincide con ambos casos.
```

Esto permite al usuario ver rápidamente cuál es su favorito y cuál es el más popular.

---

## 1.9. Clasificación

La vista de clasificación es accesible tanto para usuarios normales como para administradores.

Muestra todos los coches ordenados por número de votos favoritos.

Cada fila incluye:

```text
- Puesto
- Imagen
- Nombre del coche
- Número de votos
```

Los coches sin votos también aparecen en la clasificación con 0 votos. Para ello se usa una consulta con `LEFT JOIN`, que permite incluir coches aunque no tengan entradas en la tabla `favorites`.

La primera posición se destaca visualmente con un estilo especial.

Además, al pulsar sobre una fila de la clasificación, se abre la vista de detalles del coche seleccionado.

---

## 1.10. Vista de detalles del coche

La vista de detalles se puede abrir desde:

```text
- La imagen de una card en la vista de coches.
- Una fila de la clasificación.
```

Esta vista muestra una pantalla dividida en dos zonas:

```text
- A la izquierda, imagen grande del coche.
- A la derecha, panel lateral con información detallada.
```

El panel lateral muestra:

```text
- Nombre del coche.
- Tipo.
- Potencia.
- Fecha de matriculación.
- Ruta de imagen.
- Número de votos.
- Posición en el ranking.
- Lista de usuarios que lo tienen como favorito.
```

También incluye un botón para volver a la vista de coches.

---

## 1.11. Gestión de usuarios

La vista de usuarios solo está disponible para administradores.

Permite:

```text
- Ver todos los usuarios registrados.
- Filtrar usuarios por email.
- Seleccionar un usuario en la tabla.
- Cambiar su rol.
- Eliminar usuarios.
```

La aplicación impide que el administrador:

```text
- Se elimine a sí mismo.
- Se quite a sí mismo el rol de administrador.
```

Esto evita quedarse accidentalmente sin cuenta administradora.

---

## 1.12. Cierre de sesión

Al pulsar cerrar sesión:

```text
1. Se borra la sesión persistente.
2. Se limpia el usuario actual guardado en Session.
3. Se vuelve a cargar la pantalla de login.
```

De esta forma, aunque el usuario hubiese marcado “Mantener sesión iniciada”, al cerrar sesión se elimina el archivo `session.txt`.

---

# 2. Estructura general del proyecto

La estructura principal del proyecto es:

```text
src/main/java/com/recuperacion/carmanager
│
├── controller
│   ├── CarsController.java
│   ├── CarDetailsController.java
│   ├── LeaderboardController.java
│   ├── LoginController.java
│   ├── MainController.java
│   ├── RegisterController.java
│   └── UserController.java
│
├── dao
│   ├── CarDAO.java
│   ├── Database.java
│   ├── FavoritoDAO.java
│   └── UserDAO.java
│
├── model
│   ├── Car.java
│   ├── Leaderboard.java
│   └── User.java
│
├── utils
│   ├── PasswordUtil.java
│   ├── RememberSessionUtil.java
│   └── Session.java
│
├── AppShell.java
└── MainApp.java
```

Los recursos se encuentran en:

```text
src/main/resources
│
├── css
│   └── styles.css
│
├── database
│   ├── schema.sql
│   └── sample-data.sql
│
├── fxml
│   ├── cars-view.fxml
│   ├── car-details-view.fxml
│   ├── leaderboard-view.fxml
│   ├── login-view.fxml
│   ├── main-view.fxml
│   ├── register-view.fxml
│   └── user-view.fxml
│
├── images
│   └── imágenes de coches
│
└── session
    └── session.txt
```

---

# 3. Clases principales de la aplicación

## 3.1. `MainApp`

`MainApp` es la clase principal de JavaFX.

Su función es iniciar la aplicación.

Al arrancar:

```text
1. Inicializa AppShell con el Stage principal.
2. Comprueba si existe una sesión persistente.
3. Si hay sesión válida, carga la pantalla principal.
4. Si no hay sesión válida, carga el login.
```

Es el punto de entrada de toda la aplicación.

---

## 3.2. `AppShell`

`AppShell` centraliza el cambio entre las pantallas principales.

Permite cargar:

```text
- login-view.fxml
- register-view.fxml
- main-view.fxml
```

También se encarga de:

```text
- Mantener una única ventana principal.
- Aplicar el archivo CSS a cada escena.
- Mantener la ventana maximizada.
- Evitar que al cambiar de vista la ventana se reduzca.
```

Gracias a `AppShell`, los controladores no tienen que crear ventanas nuevas. Simplemente llaman a métodos como:

```java
AppShell.showLoginView();
AppShell.showMainView();
```

---

# 4. Controllers

Los controladores conectan las vistas FXML con la lógica de Java.

Cada controlador se encarga de responder a los botones, cargar datos, actualizar etiquetas y comunicarse con los DAO.

---

## 4.1. `LoginController`

Controla la pantalla de inicio de sesión.

Sus funciones principales son:

```text
- Leer username y password.
- Validar que no estén vacíos.
- Buscar el usuario en la base de datos.
- Comprobar la contraseña usando PasswordUtil.
- Guardar el usuario en Session si el login es correcto.
- Gestionar la casilla “Mantener sesión iniciada”.
- Ir a la pantalla de registro.
```

Cuando el login es correcto, se llama a:

```java
Session.setCurrentUser(user);
AppShell.showMainView();
```

Si la casilla de mantener sesión está marcada y el usuario no es admin, se guarda el username en `session.txt`.

---

## 4.2. `RegisterController`

Controla la pantalla de registro.

Se encarga de:

```text
- Leer los datos del formulario.
- Validar username, email y contraseña.
- Comprobar que username y email no estén repetidos.
- Codificar la contraseña con PasswordUtil.
- Crear un nuevo usuario con rol user.
- Guardarlo en la base de datos.
- Volver al login.
```

El registro no guarda contraseñas en texto plano.

Antes de guardar, se usa:

```java
PasswordUtil.hashPassword(password);
```

---

## 4.3. `MainController`

Controla la pantalla principal.

Sus responsabilidades son:

```text
- Mostrar el mensaje de bienvenida.
- Mostrar u ocultar el botón de Usuarios según el rol.
- Cargar la vista de coches.
- Cargar la vista de usuarios.
- Cargar la vista de clasificación.
- Gestionar el cierre de sesión.
```

La navegación interna se realiza con el método `loadView`, que carga un FXML dentro del `contentPane`.

Esto permite cambiar entre vistas sin cambiar toda la ventana.

---

## 4.4. `CarsController`

Controla la vista de coches.

Es uno de los controladores más completos del proyecto.

Se encarga de:

```text
- Cargar todos los coches.
- Filtrar coches por tipo.
- Crear visualmente las cards.
- Cargar imágenes.
- Marcar favoritos.
- Resaltar favoritos.
- Mostrar botones de edición y borrado si el usuario es admin.
- Crear coches.
- Editar coches.
- Eliminar coches.
- Abrir la vista de detalles al pulsar la imagen.
```

Usa:

```text
CarDAO
FavoritoDAO
Session
```

para conectar la vista con la base de datos y con el usuario actual.

---

## 4.5. `CarDetailsController`

Controla la vista de detalles de un coche.

Recibe un objeto `Car` mediante el método:

```java
setCar(Car car)
```

y carga en pantalla:

```text
- Imagen grande.
- Nombre.
- Tipo.
- Potencia.
- Fecha de matriculación.
- Ruta de imagen.
- Número de votos.
- Posición en ranking.
- Usuarios que lo tienen como favorito.
```

También permite volver a la vista de coches.

Usa `FavoritoDAO` para consultar votos, ranking y usuarios favoritos.

---

## 4.6. `LeaderboardController`

Controla la vista de clasificación.

Se encarga de:

```text
- Cargar la clasificación desde FavoritoDAO.
- Crear una fila visual por cada coche.
- Mostrar puesto, imagen, nombre y votos.
- Destacar el primer puesto.
- Abrir la vista de detalles al pulsar una fila.
```

Usa `CarDAO` para recuperar el objeto `Car` completo antes de abrir los detalles.

---

## 4.7. `UserController`

Controla la vista de gestión de usuarios.

Solo está disponible para administradores.

Permite:

```text
- Cargar todos los usuarios.
- Mostrar usuarios en una TableView.
- Filtrar usuarios por email.
- Seleccionar un usuario.
- Cambiar su rol.
- Eliminar usuarios.
```

También incluye medidas de seguridad para impedir que el administrador se modifique o elimine a sí mismo.

---

# 5. DAOs

Los DAO son las clases que se comunican directamente con MySQL.

Separan el acceso a datos de los controladores, evitando que el código SQL esté mezclado con la interfaz gráfica.

---

## 5.1. `Database`

`Database` centraliza la conexión con MySQL.

Contiene los datos de conexión:

```text
- URL
- Usuario
- Contraseña
```

Su método principal es:

```java
getConnection()
```

Este método devuelve una conexión activa con la base de datos.

Todas las clases DAO usan `Database.getConnection()` para acceder a MySQL.

---

## 5.2. `UserDAO`

Gestiona la tabla `users`.

Incluye métodos para:

```text
- Comprobar si existe un username.
- Comprobar si existe un email.
- Guardar usuarios.
- Buscar usuarios por username.
- Obtener todos los usuarios.
- Filtrar usuarios por email.
- Cambiar roles.
- Eliminar usuarios.
```

Se usa principalmente en:

```text
- LoginController
- RegisterController
- UserController
- RememberSessionUtil
```

---

## 5.3. `CarDAO`

Gestiona la tabla `cars`.

Incluye métodos para:

```text
- Obtener todos los coches.
- Filtrar coches por tipo.
- Obtener todos los tipos de coche.
- Guardar coches.
- Actualizar coches.
- Eliminar coches.
- Buscar coches por id.
```

Se usa en:

```text
- CarsController
- LeaderboardController
```

---

## 5.4. `FavoritoDAO`

Gestiona la tabla `favorites`.

Incluye métodos para:

```text
- Obtener el coche favorito de un usuario.
- Marcar un coche como favorito.
- Obtener el coche más votado.
- Contar votos de un coche.
- Construir la clasificación.
- Obtener la posición de un coche en el ranking.
- Obtener usuarios que tienen un coche como favorito.
```

Se usa en:

```text
- CarsController
- CarDetailsController
- LeaderboardController
```

---

# 6. Modelos

Los modelos representan los datos principales de la aplicación.

No contienen lógica de interfaz. Su función es almacenar información y permitir mover datos entre controladores, DAO y vistas.

---

## 6.1. `User`

Representa un usuario de la aplicación.

Sus campos principales son:

```text
- id
- username
- email
- password
- role
```

El campo `role` determina si el usuario es normal o administrador.

---

## 6.2. `Car`

Representa un coche.

Sus campos principales son:

```text
- id
- brand
- model
- horsePower
- carType
- registrationDate
- imagePath
```

También incluye un método útil:

```java
getFullName()
```

que devuelve la marca y el modelo unidos en un único texto.

---

## 6.3. `Leaderboard`

Representa una entrada de la clasificación.

Sus campos principales son:

```text
- position
- carId
- carName
- imagePath
- votes
```

Se usa para mostrar la vista de clasificación sin necesidad de cargar todos los datos completos del coche.

---

# 7. Utilidades

Las clases de utilidad agrupan funcionalidades comunes que se usan desde varias partes del programa.

---

## 7.1. `Session`

Gestiona la sesión activa en memoria.

Guarda el usuario que ha iniciado sesión.

Incluye métodos como:

```java
setCurrentUser(User user)
getCurrentUser()
isLoggedIn()
isAdmin()
clear()
```

Se usa para saber:

```text
- qué usuario está usando la aplicación
- si hay sesión iniciada
- si el usuario actual es admin
```

---

## 7.2. `PasswordUtil`

Gestiona el tratamiento seguro de contraseñas.

No guarda contraseñas en texto plano.

Sus métodos principales son:

```java
hashPassword(String password)
checkPassword(String password, String storedPassword)
```

`hashPassword` se usa durante el registro.

`checkPassword` se usa durante el login.

El hash se genera usando:

```text
PBKDF2WithHmacSHA256
```

y una salt aleatoria.

---

## 7.3. `RememberSessionUtil`

Gestiona la sesión persistente.

Permite guardar un usuario en:

```text
src/main/resources/session/session.txt
```

Sus métodos principales son:

```java
saveRememberedUser(String username)
loadRememberedUser()
clearRememberedUser()
```

No guarda contraseñas.

Solo guarda el username.

Además, si el usuario guardado ya no existe en la base de datos, borra el archivo y obliga a iniciar sesión de nuevo.

---

# 8. Recursos FXML

Las vistas de la aplicación están separadas en archivos FXML.

## `login-view.fxml`

Pantalla de inicio de sesión.

Incluye:

```text
- Campo de usuario
- Campo de contraseña
- Checkbox de mantener sesión iniciada
- Botón entrar
- Botón crear cuenta
```

---

## `register-view.fxml`

Pantalla de registro.

Incluye:

```text
- Username
- Email
- Contraseña
- Repetir contraseña
- Botón registrar
- Botón volver al login
```

---

## `main-view.fxml`

Vista principal tras iniciar sesión.

Incluye:

```text
- Barra de navegación
- Mensaje de bienvenida
- StackPane central para cargar vistas internas
- Footer
```

---

## `cars-view.fxml`

Vista de coches.

Incluye:

```text
- Filtro por tipo
- Panel de administración desplegable para admin
- TilePane con cards de coches
```

---

## `car-details-view.fxml`

Vista de detalles de un coche.

Incluye:

```text
- Imagen grande
- Panel lateral con datos
- Lista de usuarios que lo tienen como favorito
- Botón para volver a coches
```

---

## `leaderboard-view.fxml`

Vista de clasificación.

Incluye:

```text
- Lista de coches ordenados por votos
- Imagen
- Puesto
- Nombre
- Número de votos
```

---

## `user-view.fxml`

Vista de gestión de usuarios.

Incluye:

```text
- Campo de filtro por email
- Tabla de usuarios
- Selector de rol
- Botón cambiar rol
- Botón eliminar usuario
```

---

# 9. Base de datos

La aplicación usa una base de datos MySQL llamada:

```sql
recuperacion_di
```

Las tablas principales son:

```text
users
cars
favorites
```

---

## 9.1. Tabla `users`

Guarda los usuarios registrados.

Campos principales:

```text
id
username
email
password
role
```

---

## 9.2. Tabla `cars`

Guarda los coches disponibles en la aplicación.

Campos principales:

```text
id
brand
model
horse_power
car_type
registration_date
image_path
```

---

## 9.3. Tabla `favorites`

Relaciona usuarios con coches favoritos.

Campos principales:

```text
id
user_id
car_id
```

El campo `user_id` debe ser único para asegurar que cada usuario solo tenga un coche favorito.

---

# 10. Ejecución del proyecto

## 10.1. Requisitos

Para ejecutar el proyecto se necesita:

```text
- Java
- JavaFX configurado mediante Maven
- MySQL
- IntelliJ IDEA
```

---

## 10.2. Configurar conexión

La conexión se configura en:

```text
src/main/java/com/recuperacion/carmanager/dao/Database.java
```

Si el usuario o contraseña de MySQL son distintos, deben modificarse en esa clase.

---

## 10.3. Ejecutar la aplicación

Desde IntelliJ IDEA se puede ejecutar la clase principal:

```text
MainApp
```


# 12. Funcionalidades implementadas

La aplicación incluye:

```text
- Registro de usuarios.
- Login con contraseña codificada.
- Sesión persistente opcional.
- Control de roles.
- Vista principal con navegación.
- Listado de coches.
- Filtro por tipo.
- Gestión de coches para admin.
- Gestión de usuarios para admin.
- Sistema de favoritos.
- Clasificación por votos.
- Vista de detalles de coche.
- Carga de imágenes desde resources.
- Ventana maximizada.
```

---
# 13. Conclusión

Este proyecto implementa una aplicación de escritorio completa usando JavaFX, FXML, MySQL y JDBC.

La aplicación permite gestionar coches y usuarios, diferencia entre roles, mantiene una sesión de usuario, permite marcar favoritos y consultar una clasificación general. Además, incluye una vista detallada para cada coche y una interfaz visual coherente.
