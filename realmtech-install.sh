#!/usr/bin/env bash
latest=$(curl https://api.github.com/repos/FabienChatton/RealmTech-launcher/releases/latest) > /dev/null
downloadUrl=$(echo "$latest" | jq -r '.assets[] | select(.name | endswith(".tar")) | .browser_download_url')

buildName=$(echo "$downloadUrl" | cut -d "/" -f 9 | rev | cut -c5- | rev) > /dev/null
rootPath="$HOME"/.local/RealmTechData
launcherPath="$rootPath/launcher"

mkdir -p "$launcherPath"

curl -# -L -o "$launcherPath/temp.tar" "$downloadUrl"
tar -xf temp.tar -C "$launcherPath" --overwrite
rsync -av --remove-source-files "$launcherPath"/"$buildName"/* "$launcherPath"/
rm "$launcherPath"/temp.tar
rm -rf "${launcherPath}"/"$buildName"

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
  } > "$HOME"/.local/share/applications/RealmTech-launcher.desktop
fi

read -r -p "Do you want to launcher realmtech? [y/n]" -n 1
echo

if [[ "$REPLY" =~ ^[Yy]$ ]]; then
  "$launcherPath"/bin/RealmTech-launcher
fi

