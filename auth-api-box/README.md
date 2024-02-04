# Box OAuth2 Provider Implementation

This library implements support for OAuth2 from box.com.

## Quirks

Some box.com's API with regards to authentication and user profile require Client Secret as part of request parameters. To preserve the original implementation's method signatures separate route to inject Client Secret to components is necessary.

## Required OAuth2 Scopes

None. By default it doesn't ask for any scopes - please remember to add them on your own.

## Required Configurations

None. Default settings provided by box.com shall suffice.

## Using This Library

Jot down your App's Client ID and Client Secret, then use `OmhAuthClientImpl.Builder` to create the `OmhAuthClient` instance. Assuming you put the client ID at `BuildConfig.BOX_CLIENT_ID` and `BuildConfig.BOX_CLIENT_SECRET`, this is how it looks like to create the `OmhAuthClient`:

```kotlin
import com.omh.android.auth.box.presentation.OmhAuthClientImpl

// ... Other code

val client = OmhAuthClientImpl.Builder(BuildConfig.BOX_CLIENT_ID, BuildConfig.BOX_CLIENT_SECRET)
.addScope("root_readonly")
.addScope("root_readwrite")
.build(context)
```

Also you may use multiple `addScope()` calls to add required permissions for the access token along the way.

By default, the library uses `omh://auth.oauth2.box/redirect` as callback redirect URI. If you need to use another URI for redirection, override `com.omh.android.auth.box.oauth2.redirect.scheme`, `com.omh.android.auth.box.oauth2.redirect.host` and `com.omh.android.auth.box.oauth2.redirect.pathPrefix`.

## Limitations

- box.com's OAuth2 implementation does not support [ID token](https://openid.net/specs/openid-connect-core-1_0.html#IDToken) - CANNOT FIX
- box.com's OAuth2 implementation seems does not support multi-factor authentication (MFA). Some hinted towards cookies problem with CustomTabs, but not sure