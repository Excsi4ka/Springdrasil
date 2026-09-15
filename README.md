# Springdrasil

Springdrasil is a Kotlin/Spring Boot implementation of the Yggdrasil API used by
Minecraft launchers, servers, and authlib-injector. It provides authentication,
session joining, profile lookup, signed profile properties, and PNG texture
downloads.

## Requirements

- JDK 25 for running the application directly
- Docker and Docker Compose for the included container setup
- PostgreSQL
- An RSA key pair for signing Yggdrasil properties

## Quick start with Docker

1. Copy the example environment file:

   ```powershell
   Copy-Item .env.example .env
   ```

2. Fill in the database, texture URL, and signing-key values in `.env`.

3. Start the application and PostgreSQL:

   ```powershell
   docker compose up --build
   ```

The application listens on port `8080` inside the container. The host port is
controlled by `SPRINGDRASIL_PORT` (for example, `http://localhost:8082`). Flyway
runs the database migrations when the application starts.

## Generate the signing keys

The auth server signs property values with the private key. Minecraft/authlib-
injector verifies those signatures using the public key returned by `GET /`.
The private key must never be shared with clients.

Using OpenSSL:

```powershell
openssl genrsa 4096 > auth-private.pem
openssl rsa -pubout -in auth-private.pem > auth-public.pem
```

The current application parser expects a PKCS#8 private key (`BEGIN PRIVATE
KEY`) and an X.509/SPKI public key (`BEGIN PUBLIC KEY`). If OpenSSL produced a
traditional RSA private key (`BEGIN RSA PRIVATE KEY`), convert it with:

```powershell
openssl pkcs8 -topk8 -nocrypt -in auth-private.pem -out auth-private-pkcs8.pem
```

For a direct local run, load the PEM contents into the environment before
starting Spring Boot:

```powershell
$env:YGGDRASIL_SIGNATURE_PRIVATE_KEY = Get-Content -Raw .\auth-private-pkcs8.pem
$env:YGGDRASIL_SIGNATURE_PUBLIC_KEY = Get-Content -Raw .\auth-public.pem
$env:TEXTURE_BASE_URL = "http://localhost:8080"
.\gradlew.bat bootRun
```

Use a URL reachable by the Minecraft client for `TEXTURE_BASE_URL`; `localhost`
only works when the client is running on the same machine.

## Configuration

The application reads these values from environment variables:

| Variable | Required | Purpose |
| --- | --- | --- |
| `DB_URL` | Yes | JDBC URL for PostgreSQL |
| `DB_USER` | Yes | Database username |
| `DB_PASSWORD` | Yes | Database password |
| `TEXTURE_BASE_URL` | Yes | Public base URL used in texture properties |
| `YGGDRASIL_SIGNATURE_PRIVATE_KEY` | Yes | PEM-encoded PKCS#8 RSA private key |
| `YGGDRASIL_SIGNATURE_PUBLIC_KEY` | Yes | PEM-encoded X.509/SPKI RSA public key |
| `SESSION_TIMEOUT` | No | Session-token lifetime; defaults to `10d` |
| `MAX_TOKENS_PER_USER` | No | Maximum token rotation count; defaults to `5` |

Do not commit `.env` or private-key files. In production, inject the key values
through the deployment platform's secret mechanism while preserving the PEM
newlines.

## API endpoints

The current implementation exposes:

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/` | Returns API metadata, including the signature public key |
| `POST` | `/authserver/authenticate` | Authenticates a user and returns tokens |
| `POST` | `/authserver/refresh` | Refreshes an access token |
| `POST` | `/authserver/validate` | Validates an access token |
| `POST` | `/authserver/invalidate` | Invalidates an access token |
| `POST` | `/authserver/signout` | Signs out a user |
| `POST` | `/sessionserver/session/minecraft/join` | Records a server join |
| `GET` | `/sessionserver/session/minecraft/hasJoined` | Resolves a joining player to a profile |
| `GET` | `/sessionserver/session/minecraft/profile/{uuid}` | Looks up a profile by unhyphenated UUID |
| `GET` | `/textures/{hash}` | Returns the stored PNG for a texture hash |

The profile endpoint follows the authlib-injector behavior for the `unsigned`
query parameter. `unsigned=true` (the default) omits signatures; use
`unsigned=false` to receive signed properties. A missing profile currently
returns `204 No Content`.

## How textures work

When a profile has textures, Springdrasil creates a `textures` property whose
value is Base64-encoded JSON. Each texture URL has this form:

```text
{TEXTURE_BASE_URL}/textures/{sha256-hash}
```

The hash is calculated from the original PNG bytes. The texture endpoint returns
those bytes with `image/png`, so clients can download and cache the image by its
hash.

The current profile response also advertises:

```text
uploadableTextures=skin,cape
```

The upload API itself is not implemented yet, so this property should be
removed or made conditional until upload endpoints exist. Likewise, metadata
currently returns an empty `skinDomains` list; configure that before relying on
domain restrictions in a production authlib-injector setup.

## Typical client flow

1. A launcher calls `/authserver/authenticate` and receives an access token.
2. The Minecraft client joins a server through `/sessionserver/session/minecraft/join`.
3. The Minecraft server calls `/sessionserver/session/minecraft/hasJoined`.
4. Springdrasil returns the profile and signed properties.
5. The client downloads each texture from the URL in the `textures` property.

Configure authlib-injector to use the public base URL of this application as its
Yggdrasil server URL. The base URL must be reachable by both the Minecraft
server and the client.

## Current limitations

- Batch profile lookup at `POST /api/profiles/minecraft` is not implemented.
- Texture upload and texture deletion endpoints are not implemented.
- Before deploying, verify that `/textures/**` is permitted anonymously in the
  Spring Security configuration; texture URLs must be publicly downloadable by
  the client.
