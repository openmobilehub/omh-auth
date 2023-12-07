# Dropbox OAuth2 Provider Implementation

This library implements support for OAuth2 from Dropbox.

## Required OAuth2 Scopes

By default it asks for `openid` `profile` `email` `account_info.read` scopes in addition to requested scopes.

## Required Configurations

Some configurations required at your App Registration at Azure Portal.

- At **Settings -> OAuth2**, set **Allow public clients (Implicit Grant & PKCE)** to **Allow**
- At **Permissions -> OpenID Scopes -> Connect**, tick **profile** and **email**

## Using This Library

Jot down your App's Client ID, then use `OmhAuthClientImpl.Builder` to create the `OmhAuthClient` instance. Assuming you put the client ID at `BuildConfig.DROPBOX_CLIENT_ID`, this is how it looks like to create the `OmhAuthClient`:

```kotlin
import com.omh.android.auth.dropbox.presentation.OmhAuthClientImpl

// ... Other code

val client = OmhAuthClientImpl.Builder(BuildConfig.DROPBOX_CLIENT_ID)
.addScope("files.content.read")
.addScope("files.content.write")
.build(context)
```

Also you may use multiple `addScope()` calls to add required permissions for the access token along the way.

By default, the library uses `omh://auth.oauth2.dropbox/redirect` as callback redirect URI. If you need to use another URI for redirection, override `com.omh.android.auth.dropbox.oauth2.redirect.scheme`, `com.omh.android.auth.dropbox.oauth2.redirect.host` and `com.omh.android.auth.dropbox.oauth2.redirect.pathPrefix`.

## Limitations/TODO:

- Team/Enterprise Dropbox support
- Fetch account picture