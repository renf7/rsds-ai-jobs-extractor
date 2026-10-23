# RSDS AI Jobs Extractor

Spring Boot backend for the AI Jobs Executor (`eu.rsdsoftware.raje`). It is an initial
service foundation; job extraction behavior will be added in a later step.

## Stack

- Java 27 (Zulu at `D:\Java\zulu27.28.101-ca-jdk27.0.0-win_x64`)
- Spring Boot 4.1.2
- Gradle 9.8.2
- OAuth 2.0 / OpenID Connect login for Google, Microsoft, Facebook, and Apple
- Optional IMAPS mailbox polling

## Run

On Windows, set `JAVA_HOME` to the Zulu JDK and run:

```powershell
$env:JAVA_HOME = 'D:\Java\zulu27.28.101-ca-jdk27.0.0-win_x64'
.\gradlew.bat bootRun
```

The health endpoint is available at `http://localhost:8080/api/health`.

## SSO setup

Register OAuth applications with each provider and configure the following environment
variables locally or in the deployment secret manager. Never commit client secrets.
For local development, register `http://localhost:8080/login/oauth2/code/{registrationId}`
as the provider callback URL (with the provider name as the registration ID). Override
`OAUTH_REDIRECT_URI` when deploying to use the public backend callback address.

| Provider | Client ID | Client secret |
| --- | --- | --- |
| Google | `GOOGLE_CLIENT_ID` | `GOOGLE_CLIENT_SECRET` |
| Microsoft | `MICROSOFT_CLIENT_ID` | `MICROSOFT_CLIENT_SECRET` |
| Facebook | `FACEBOOK_CLIENT_ID` | `FACEBOOK_CLIENT_SECRET` |
| Apple | `APPLE_CLIENT_ID` | `APPLE_CLIENT_SECRET` |

Set `MICROSOFT_TENANT_ID` to the tenant ID, or leave it as `common` for multi-tenant
Microsoft identity. Set `FRONTEND_URL` to the frontend origin; it defaults to
`http://localhost:5173`.

## Mailboxes

Mailbox polling is disabled by default. Configure one or more IMAPS accounts with
Spring's indexed environment properties, then set `MAILBOXES_ENABLED=true`. Example
for one account:

```powershell
$env:MAILBOXES_ENABLED = 'true'
$env:RAJE_MAILBOXES_ACCOUNTS_0_NAME = 'jobs'
$env:RAJE_MAILBOXES_ACCOUNTS_0_HOST = 'imap.example.com'
$env:RAJE_MAILBOXES_ACCOUNTS_0_PORT = '993'
$env:RAJE_MAILBOXES_ACCOUNTS_0_USERNAME = 'jobs@example.com'
$env:RAJE_MAILBOXES_ACCOUNTS_0_PASSWORD = 'use-an-app-password'
$env:RAJE_MAILBOXES_ACCOUNTS_0_FOLDER = 'INBOX'
```

For additional mailboxes, use the next index (for example,
`RAJE_MAILBOXES_ACCOUNTS_1_HOST`).

Polling opens each configured folder in `READ_ONLY` mode. Messages are not deleted,
marked read, or otherwise modified. Credentials should be supplied through environment
variables or a secret manager, never committed to source control. The current receiver
only verifies connectivity and reports mailbox counts; job extraction is not implemented.

## Tests

```powershell
.\gradlew.bat test
```
