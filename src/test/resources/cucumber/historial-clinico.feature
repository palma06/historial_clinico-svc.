# language: es
Característica: Servicio Historial Clínico (microservicio historial-clinico del caso caso07)
  Los escenarios validan el contrato REST del microservicio alineado a sus endpoints.

  Escenario: el listado del recurso responde 200
    Dado el servicio "Historial Clínico" está disponible
    Cuando consulto el listado de "expedientes"
    Entonces el listado responde con código 200

  Escenario: ciclo de vida completo del recurso
    Dado un nuevo "expediente" con nombre "hola-cucumber"
    Cuando consulto el "expediente" recién creado
    Entonces el recurso tiene nombre "hola-cucumber" y código 200
    Cuando actualizo el "expediente" con nombre "cucumber-actualizado"
    Entonces el recurso queda con nombre "cucumber-actualizado" y código 200
    Cuando elimino el "expediente"
    Entonces la eliminación responde con código 204
    Y al consultar el "expediente" eliminado responde 404
