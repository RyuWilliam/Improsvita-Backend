# Skill: git-commit-push

## Descripción
Automatiza de forma segura el flujo completo de Git: inspect → validate → review → stage → commit → push → verify. Prioridad absoluta: seguridad de cambios > integridad historial > no perder trabajo > commit correcto > push correcto.

## Activadores
- "haz commit y push"
- "commit and push"
- "sube los cambios"
- "crea el commit y súbelo"
- "git commit push"
- "commit push"

## Reglas de comportamiento

### 1. Detectar rama actual (nunca cambiarla)
```bash
git branch --show-current
```
- Trabajar **exclusivamente** sobre la rama activa
- No hacer checkout, merge, rebase, ni crear ramas
- Push siempre hacia la rama actual

### 2. Inspeccionar estado antes de cualquier acción
```bash
git status
git diff
git diff --stat
```
- Analizar cambios antes de `git add`
- No asumir que todos los cambios son de la tarea actual
- Distinguir cambios de la tarea vs cambios previos manuales

### 3. Staging selectivo (NUNCA `git add .` sin revisar)
- Revisar cada archivo modificado
- Hacer staging **solo** de archivos relevantes para el commit
- Excluir archivos generados/irrelevantes:
  - IDE: `.idea/`, `.vscode/`, `*.iml`, `*.ipr`, `*.iws`
  - Logs: `*.log`, `logs/`, `*.out`
  - Builds: `build/`, `dist/`, `target/`, `out/`, `bin/`
  - Temp: `*.tmp`, `*.temp`, `.tmp/`, `tmp/`
  - Dependencias: `node_modules/`, `.gradle/`, `vendor/`
  - Config local: `*.local.*`, `local.*`, `*.local`

### 4. Protección de archivos sensibles (DETENER si encuentra)
**Patrones bloqueados:**
```
.env
.env.*
*.pem
*.key
*.p12
*.jks
credentials.*
secrets.*
```
**Detección de contenido sensible (grep en staged):**
- API keys: `sk-`, `pk_`, `api_key`, `apikey`, `access_key`, `secret_key`
- Tokens: `Bearer`, `token`, `jwt`, `eyJ`
- DB creds: `password=`, `jdbc:.*password`, `spring.datasource.password`
- Cloud: `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`, `AZURE_CLIENT_SECRET`, `GCP_SERVICE_ACCOUNT`

Si detecta sensible → **DETENER** y explicar el problema.

### 5. Validaciones pre-commit por tecnología
Detectar stack y ejecutar **solo** comandos aplicables:

**Java/Gradle** (`build.gradle` o `build.gradle.kts`):
```bash
./gradlew test
./gradlew build
```

**Java/Maven** (`pom.xml`):
```bash
./mvnw test
./mvnw verify
```

**Node** (`package.json`):
```bash
npm test
npm run lint
npm run build
```

**Python** (`pyproject.toml` / `requirements.txt` / `setup.py`):
```bash
pytest
```

- Si fallan tests/lint/build → **NO commit automático**
- Analizar si fallo causado por cambios actuales
- Si solución clara y segura → corregir y revalidar
- Si no → detener e informar

### 6. Revisión final pre-commit
```bash
git status
git diff
```
Confirmar que el staging es correcto.

### 7. Mensaje de commit (Conventional Commits)
**Tipos permitidos:** `feat`, `fix`, `refactor`, `test`, `docs`, `build`, `ci`, `perf`, `chore`, `style`

**Formato:** `<tipo>(<scope>): <descripción concreta>`

**Ejemplos buenos:**
- `feat(auth): add refresh token rotation`
- `fix(events): prevent duplicate event registration`
- `refactor(user): extract authentication service`
- `test(events): add integration tests for reservation flow`
- `docs(api): update authentication endpoints documentation`
- `build(docker): optimize backend image size`

**PROHIBIDOS (genéricos):**
- `update`, `changes`, `final changes`, `fix stuff`, `work`, `wip`, `commit`

El mensaje debe describir **realmente** los cambios.

### 8. Commit y verificación
```bash
git add <archivos-relevantes>
git commit -m "<mensaje-conventional>"
git status
git log -1 --oneline
```

### 9. Push seguro
```bash
git push origin <rama-actual>
# Si no hay upstream:
git push -u origin <rama-actual>
```

**NUNCA automático:**
- `git push --force` / `git push -f`
- `git reset --hard`
- `git clean -fd`
- `git checkout -- .` / `git restore .`
- `git rebase`
- `git commit --amend`
- `git branch -D`

Estas requieren **autorización explícita del usuario**.

### 10. Manejo de errores en push
Si falla:
1. Leer y analizar error completo
2. **No** intentar `--force`
3. **No** eliminar cambios locales
4. **No** hacer reset
5. **No** cambiar de rama
6. Explicar causa y solución segura
7. Si non-fast-forward → **DETENER** y solicitar autorización antes de modificar historial

## Flujo completo (autónomo para caso normal)

```
1. git branch --show-current          → rama actual
2. git status && git diff && git diff --stat
3. Revisar cada archivo modificado
4. Filtrar sensibles + generados/irrelevantes
5. git add <solo-relevantes>
6. Detectar stack → ejecutar validaciones (test/lint/build)
7. Si validaciones fallan → analizar → corregir si seguro → revalidar → o detener
8. git status && git diff (staged)
9. Generar mensaje Conventional Commits descriptivo
10. git commit -m "..."
11. git status && git log -1 --oneline
12. git push origin <rama> (o -u si sin upstream)
13. Verificar éxito real
```

## Salida final esperada (éxito)
```
Commit creado y pusheado correctamente.

Commit: <hash> <mensaje>
Branch: <rama>
Remote: origin
Status: clean / cambios restantes
```

## Salida final (fallo)
```
Error en <paso>: <descripción>
Causa: <análisis>
Solución segura: <recomendación>
Requiere acción manual: sí/no
```

## Principio fundamental (jerarquía de prioridades)
```
Seguridad de los cambios
    >
Integridad del historial Git
    >
No perder trabajo del usuario
    >
Commit correcto
    >
Push correcto
```

Conservador con operaciones destructivas, autónomo para flujo normal.

## Idioma
Español para interacción con usuario. Comandos Git en inglés.