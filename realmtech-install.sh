#!/usr/bin/env bash
latest=$(curl https://api.github.com/repos/FabienChatton/RealmTech-launcher/releases/latest)
downloadUrl=$(echo "$latest" | grep "browser_download_url" | cut -d ":" -f 2,3 | cut -c3- | rev | cut -c2- | rev)

buildName=$(echo "$downloadUrl" | cut -d "/" -f 9 | rev | cut -c5- | rev)

rootPath="$HOME"/.local/RealmTechData
launcherPath="$rootPath/launcher"

mkdir -p "$launcherPath"

wget "$downloadUrl" -O "$launcherPath"/temp.zip
unzip "$launcherPath"/temp.zip -d "$launcherPath"
mv "$launcherPath"/"$buildName"/* "$launcherPath"
rm "$launcherPath"/temp.zip
rm -rf "${launcherPath:?}"/"$buildName"

read -r -p "Do you want to create a shortcut in your start menu? [y/n]" -n 1
echo
if [[ "$REPLY" =~ ^[Yy]$ ]]; then
  {
    echo [Desktop Entry]
    echo Name=RealmTech-launcher
    echo Exec="$launcherPath"/bin/RealmTech-launcher
    echo Terminal=false
    echo Type=Application
    echo Icon=""
  } > "$HOME"/.local/share/application/RealmTech-launcher.desktop
fi

read -r -p "Do you want to launcher realmtech? [y/n]" -n 1
echo

if [[ "$REPLY" =~ ^[Yy]$ ]]; then
  "$launcherPath"/bin/RealmTech-launcher
fi

