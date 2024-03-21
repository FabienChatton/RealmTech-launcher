### RealmTech-launcher
Le launcher de RealmTech permet de télécharger RealmTech et de le mettre à jour. Il permet aussi d'avoir les derniers actualité de RealmTech.

## installer le launcher
Les script d'installation permettent de télécharger le launcher le plus simplement possible. Vous pouvez installer le launcher en une commande ou installer manuellement l'installateur.

Installer le launcher en une commande:

- windows powershell:
```sh
Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://github.com/FabienChatton/RealmTech-launcher/releases/download/installateur/realmtech-install.ps1'))
```
- windows bash:
```sh
powershell Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://github.com/FabienChatton/RealmTech-launcher/releases/download/installateur/realmtech-install.ps1'))
```

- linux:
```sh
curl -s "https://raw.githubusercontent.com/FabienChatton/RealmTech-launcher/master/realmtech-install.sh" | bash
```

exécuter l'installateur pour télécharger la dernière version du launcher
