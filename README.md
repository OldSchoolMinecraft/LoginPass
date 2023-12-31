# Login Pass

## What does it do?

Bypass /login on your server.

Once players link their account, they only have to press one button on [MCLegacy](https://mclegacy.net/auth) to authenticate themselves and play!

## Supported Authentication Plugins
- AuthMe
- xAuth
- OSAS

## Coming Soon™️
- Pre-authorization (with a configurable TTL) so launchers & clients can implement code to automate the login process.

## Example Configuration
```
c2host: your.server.ip.here # IPv4 only
c2port: 12992 # this can be whatever you want really, but this is default
c2key: abcdef1234 # the key given to you when you complete the C2 registration form
holderName: "My Minecraft Server" # your server's name. the EXACT same as the one you provided to MCLegacy on your C2 registration form
holderUUID: # this will uniquely identify your server on our API backend. this will be provided to you when you complete the C2 registration form
```

You should provide an icon with your server. Place it inside <code>plugins/LoginPass/</code> and it MUST be called <code>server-icon.png</code> and be in PNG format. The API will reject and delete any file that does not have a valid PNG header.
