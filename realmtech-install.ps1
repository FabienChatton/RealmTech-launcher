if($ENV:OS -ne "Windows_NT"){
  Write-Host "You're not on Windows"
  exit(1)
}

$realmtechRoot="$Env:appdata\RealmTechData\"
$realmtechLauncherDirectory="$($realmtechRoot)launcher\"
$latest = Invoke-RestMethod -Uri "https://api.github.com/repos/FabienChatton/RealmTech-launcher/releases/latest"
foreach ($item in $latest.assets){
  if($item.name -like "*.zip"){
    $realmtechOrigine = $item.browser_download_url
    $realmtechName = $item.name
  }
}
$tempZip="$($realmtechLauncherDirectory)temp.zip"

if (!(Test-Path -Path $realmtechRoot)) {
	New-Item -ItemType Directory -Path $realmtechRoot
}
if (!(Test-Path -Path $realmtechLauncherDirectory)) {
	New-Item -ItemType Directory -Path $realmtechLauncherDirectory
}
if (!(Test-Path -Path $realmtechLauncherDirectory)) {
	New-Item -ItemType Directory -Path $realmtechLauncherDirectory
}

function DownloadFile($url, $targetFile)
{
   $uri = New-Object "System.Uri" "$url"
   $request = [System.Net.HttpWebRequest]::Create($uri)
   $request.set_Timeout(15000) #15 second timeout
   $response = $request.GetResponse()
   $totalLength = [System.Math]::Floor($response.get_ContentLength()/1024)
   $responseStream = $response.GetResponseStream()
   $targetStream = New-Object -TypeName System.IO.FileStream -ArgumentList $targetFile, Create
   $buffer = new-object byte[] 1000KB
   $count = $responseStream.Read($buffer,0,$buffer.length)
   $downloadedBytes = $count
   while ($count -gt 0)
   {
       $targetStream.Write($buffer, 0, $count)
       $count = $responseStream.Read($buffer,0,$buffer.length)
       $downloadedBytes = $downloadedBytes + $count
       Write-Progress -activity "Downloading file '$($url.split('/') | Select -Last 1)'" -status "Downloaded ($([System.Math]::Floor($downloadedBytes/1024))K of $($totalLength)K): " -PercentComplete ((([System.Math]::Floor($downloadedBytes/1024)) / $totalLength)  * 100)
   }
   Write-Progress -activity "Finished downloading file '$($url.split('/') | Select -Last 1)'"
   $targetStream.Flush()
   $targetStream.Close()
   $targetStream.Dispose()
   $responseStream.Dispose()
}


echo "downloading RealmTech-launcher $($realmtechName)..."
try {
	DownloadFile $realmtechOrigine $tempZip
} catch {
	echo "can not download RealmTech-launcher"
	echo $_
	exit
}

Expand-Archive $tempZip -DestinationPath $realmtechLauncherDirectory -Force
Remove-Item $tempZip
$folderName = "$($realmtechLauncherDirectory)$($realmtechName)" -replace ".{4}$"
robocopy $folderName $realmtechLauncherDirectory /S /Move /np /nfl
Remove-Item $folderName -Recurse
# create shortcut on desktop
$title    = 'Create shortcut on desktop'
$question = 'Do you want create a shortcut on your desktop'
$choices  = '&Yes', '&No'
$decision = $Host.UI.PromptForChoice($title, $question, $choices, 0)

if ($decision -eq 0) {
	$DesktopPath = [Environment]::GetFolderPath("Desktop")
	$WshShell = New-Object -comObject WScript.Shell
	$Shortcut = $WshShell.CreateShortcut("$DesktopPath\RealmTech-Launcher.lnk")
	$Shortcut.TargetPath = "$($realmtechLauncherDirectory)bin\RealmTech-launcher.bat"
	$Shortcut.Save()
}

# create shortcut in start menu
$title    = 'Create shortcut in start menu'
$question = 'Do you want create a shortcut in your start menu ? This require admin previlege'
$choices  = '&Yes', '&No'
$decision = $Host.UI.PromptForChoice($title, $question, $choices, 0)



if ($decision -eq 0) {
    if (!([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
		$tempFile = Get-ChildItem ([IO.Path]::GetTempFileName()) | Rename-Item -NewName { [IO.Path]::ChangeExtension($_, ".ps1") } -PassThru
		echo "`$WshShell = New-Object -comObject WScript.Shell;" >> $tempFile
		echo "`$Shortcut = `$WshShell.CreateShortcut('C:\ProgramData\Microsoft\Windows\Start Menu\Programs\RealmTech-Launcher.lnk');" >> $tempFile
		echo "`$Shortcut.TargetPath = '$($realmtechLauncherDirectory)bin\RealmTech-launcher.bat';" >> $tempFile
		echo "`$Shortcut.Save();" >> $tempFile
		Start-Process powershell.exe "-NoProfile -ExecutionPolicy Bypass $tempFile" -Verb RunAs;
	} else {
		$WshShell = New-Object -comObject WScript.Shell;
		$Shortcut = $WshShell.CreateShortcut('C:\ProgramData\Microsoft\Windows\Start Menu\Programs\RealmTech-Launcher.lnk');
		$Shortcut.TargetPath = "$($realmtechLauncherDirectory)bin\RealmTech-launcher.bat";
		$Shortcut.Save();
	}
}

$title    = 'Launcher RealmTech'
$question = 'Do you want to play RealmTech ?'
$choices  = '&Yes', '&No'
$decision = $Host.UI.PromptForChoice($title, $question, $choices, 0)
if ($decision -eq 0) {
	Start-Process "$($realmtechLauncherDirectory)bin\RealmTech-launcher.bat"
}
