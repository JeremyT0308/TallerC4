# Verificación Redis Cloud

## Estado de esta entrega

No hay variables `REDIS_*` ni credenciales Redis disponibles en el entorno local de esta ejecución. Por seguridad no se intentó adivinar un endpoint ni se imprimieron valores sensibles. La conexión queda preparada y se verificó localmente con `RedisAntCacheTest`, dobles controlados y el fallback en memoria.

## Configuración local segura

Obtener estos valores desde el panel de Redis Cloud y definirlos solo en la sesión local. No escribirlos en `.env`, Git, capturas, logs ni mensajes:

```powershell
$env:REDIS_ENABLED = "true"
$env:REDIS_HOST = "<endpoint-redis-cloud>"
$env:REDIS_PORT = "6379"
$env:REDIS_PASSWORD = "<password-local-no-compartir>"
$env:REDIS_DATABASE = "0"
$env:REDIS_SSL_ENABLED = "true"
$env:REDIS_CONNECT_TIMEOUT_MS = "3000"
$env:REDIS_READ_TIMEOUT_MS = "3000"
$env:ANT_PROVIDER = "simulated"
$env:ANT_SIMULATION_SCENARIO = "success"
```

La contraseña es un marcador: reemplazarla directamente en la terminal, sin pegarla en archivos. Al terminar, limpiar la sesión:

```powershell
Remove-Item Env:REDIS_PASSWORD
Remove-Item Env:REDIS_HOST
```

## Prueba de conectividad y cache

Con las variables definidas, iniciar el backend:

```powershell
cd backend
mvn spring-boot:run
```

En otra terminal, ejecutar consultas ANT con la misma identificación sintética:

```powershell
$live = Invoke-RestMethod "http://localhost:8080/api/v1/ant/points?identification=0000000011&scenario=success"
$fallback = Invoke-RestMethod "http://localhost:8080/api/v1/ant/points?identification=0000000011&scenario=timeout"
$live | Select-Object points,source,stale,retrievedAt | ConvertTo-Json
$fallback | Select-Object points,source,stale,retrievedAt,staleSince,note | ConvertTo-Json
```

Resultado esperado: la primera respuesta tiene `stale=false`; la segunda conserva los mismos puntos, `stale=true`, `retrievedAt` original, `staleSince` y la advertencia de cache. Si Redis no responde, una consulta viva sigue siendo utilizable y el servicio no expone la contraseña.

## Verificación local sin Redis Cloud

Con `REDIS_ENABLED=false` —default— el mismo flujo usa `InMemoryAntCache`. Las pruebas no requieren credenciales:

```powershell
cd backend
mvn -q '-Dtest=RedisAntCacheTest,AntPointsCacheTest,AntPointsServiceTest' test
```

Esta prueba local no equivale a una conexión Redis Cloud; la evidencia de conexión real debe completarse con un endpoint autorizado y registrar únicamente fecha, región/entorno, `PING` exitoso y estado de la consulta, nunca la contraseña.
