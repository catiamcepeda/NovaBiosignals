cordova-bioplux
================

Bioplux  plugin for Cordova version 3.0.0+

Installation
------------

Installation below 3.0.0 version should be done manually. Copy the contents of
`manual/<platform>/src` and `manual/www` to their respective locations on your
project. Remember to add plugin specification to `config.xml` and permissions to
`AndroidManifest.xml`.

In `config.xml`...
```
<plugin name="Bluetooth" value="org.apache.cordova.bioplux.BioPluxPlugin" />
```

In `AndroidManifest.xml`...
```
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```

Usage
-----

If you installed the plugin with plugman in 3.0.0 environment, the plugin is
accessible from `window.bioplux`.

If you installed the plugin manually, you need to add the `bioplux.js` script
to your app. Then require the plugin after the `deviceready` event.

```
window.bioplux = cordova.require("cordova/plugin/bioplux");
```

License
-------
This plugin is available under MIT. See LICENSE for details.
