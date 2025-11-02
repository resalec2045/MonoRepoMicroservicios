# language: es
Característica: Gestión de usuarios (API)
  Para verificar el correcto funcionamiento del microservicio de usuarios
  Como equipo de QA
  Quiero ejecutar pruebas de aceptación con Cucumber + RestAssured

  Escenario: CRUD básico de usuario
    Dado un usuario aleatorio válido
    Cuando creo el usuario
    Entonces puedo consultarlo
    Entonces la respuesta cumple el esquema "schemas/user.json"
    Cuando lo actualizo con un nuevo nombre
    Entonces lo puedo eliminar

    # Ejemplos removidos: la columna 'iter' no se usa en el escenario

  Escenario: Crear usuario con datos inválidos
    Dado un usuario con datos inválidos
    Cuando intento crear el usuario
    Entonces recibo un error de validación

  Escenario: Consultar usuario inexistente
    Dado un identificador de usuario que no existe
    Cuando consulto el usuario
    Entonces recibo un error de no encontrado

  Escenario: Actualizar usuario inexistente
    Dado un identificador de usuario que no existe
    Cuando intento actualizar el usuario
    Entonces recibo un error de no encontrado

  Escenario: Eliminar usuario inexistente
    Dado un identificador de usuario que no existe
    Cuando intento eliminar el usuario
    Entonces recibo un error de no encontrado

  Escenario: Listar usuarios
    Dado que existen usuarios en el sistema
    Cuando consulto la lista de usuarios
    Entonces recibo la lista completa de usuarios
