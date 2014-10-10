!define AppName "DHIS2"
!define AppVersion "2.2.0"
!define ShortName "DHIS2"
!define Vendor "HISP"


; JDK defines
!define JDK_INSTALLER "jdk-6u24-windows-i586.exe"
!define JDK_VERSION "1.7.21"

;MySQL defines
!define MYSQL_INSTALLER "mysql-5.5.9-win32.msi"
!define MYSQL_VERSION "5.5.9"
!define MYSQL_PORT "3306" 
!define MYSQL_SERVICE_NAME "MYSQL55"
!define MYSQL_SCHEMA_NAME "dhis"
!define MYSQL_USER "root"
!define MYSQL_PASS "root"
!define MYSQL_DUMP_FILE "dump.sql"
!define MYSQL_HOST "localhost"

;Tomcat defines
!define TOMCAT_INSTALLER "apache-tomcat-6.0.32.exe"
!define TOMCAT_VERSION "6.0.21"

;DHIS war and hibernate defines
!define HIBERNATE_FILE "hibernate.properties"
!define WAR "dhis.war"

;Browser (Chrome) defines
!define BROWSER_INSTALLER "ChromeStandaloneSetup.exe"

; Include files
!include "LogicLib.nsh"
!include "nsDialogs.nsh"
!include "TextFunc.nsh"
!include "WordFunc.nsh"
;!include "EnvVarUpdate.nsh"
 
!insertmacro ConfigWrite
!insertmacro ConfigRead


; Needed Variables

VAR JAVAINST            ; To test if JDK should be installed

!insertmacro VersionCompare

;--------------------------------
;Include Modern UI

!include "MUI2.nsh"

;--------------------------------
;General

  ;Name and file
  Name "DHIS All in One Installer"
  OutFile "DHIS2 Installer.exe"

  ;Default installation folder
  InstallDir "$LOCALAPPDATA\DHIS2"

  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\DHIS2" ""

  ;Request application privileges for Windows Vista
  ;RequestExecutionLevel Admin
  
;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_LICENSE "${NSISDIR}\Docs\Modern UI\License.txt"
  !insertmacro MUI_PAGE_COMPONENTS
  Page custom CheckInstalledJRE  
  ; Define headers for the 'Java installation successfully' page
  !define MUI_INSTFILESPAGE_FINISHHEADER_TEXT "Java installation complete"
  !define MUI_PAGE_HEADER_TEXT "Installing JDK"
  !define MUI_PAGE_HEADER_SUBTEXT "Please wait while we install the JDK"
  !define MUI_INSTFILESPAGE_FINISHHEADER_SUBTEXT "JDK installed successfully."
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  !define MUI_INSTFILESPAGE_FINISHHEADER_TEXT "JDK Installation complete"
  !define MUI_PAGE_HEADER_TEXT "Installing"
  !define MUI_PAGE_HEADER_SUBTEXT "Please wait while Tomcat Web Server is being installed."

  !insertmacro MUI_PAGE_FINISH
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "Java Development Kit" jdk
SectionIn 1 RO    ; Full install, cannot be unselected
    SetOutPath "$INSTDIR"
    SetOverwrite ifnewer

    ${if} $JAVAINST == "yes"
DetailPrint "Starting JDK installation"
Goto InstJDK    
    ${else}
        DetailPrint "Skipping JDK installation"
        Goto JavaHomeNo
    ${endif}

InstJDK:    
        File "Tools\${JDK_INSTALLER}"
        ExecWait '"$INSTDIR\${JDK_INSTALLER}" /s /log jdk_logs.txt'
        Delete "$INSTDIR\${JDK_INSTALLER}"

  ReadRegStr $7 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
MessageBox MB_OK "Installed JDK Version : $7"
  ReadRegStr $8 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$7" "JavaHome"
MessageBox MB_OK "Installed JDK Path : $8"


            Goto SetEnvers

SetEnvers:
     
  JavaHomeNo:


SectionEnd

Section "MySQL Windows service" mysql
SectionIn 1 RO    ; Full install, cannot be unselected
    SetOutPath "$INSTDIR"
    SetOverwrite ifnewer

        File "Tools\${MYSQL_INSTALLER}"
ExecWait 'msiexec.exe /i "$INSTDIR\${MYSQL_INSTALLER}" /qn INSTALLDIR="$INSTDIR\MySQL\" /L* "$INSTDIR\MSI-MySQL-Log.txt"'

        Delete "$INSTDIR\${MYSQL_INSTALLER}"

  ReadRegStr $7 HKLM "SOFTWARE\MySQL AB\MySQL Server 5.5" "Version"
MessageBox MB_OK "Installed MySQL Version : $7"
  ReadRegStr $8 HKLM "SOFTWARE\MySQL AB\MySQL Server 5.5" "Location"
MessageBox MB_OK "Installed MySQL Path : $8"


ExecWait '$8bin\MySQLInstanceConfig.exe -i -q "-l$INSTDIR\MSI-MySQL-Log.txt" "-nMySQL Server 5.5" "-p$8" -v${MYSQL_VERSION}"-t$8my-template.ini" "-c$8my.ini" ServerType=SERVER DatabaseType=MIXED ConnectionUsage=DSS Port=${MYSQL_PORT} ServiceName=${MYSQL_SERVICE_NAME} RootPassword=${MYSQL_PASS}'
MessageBox MB_OK "Successfully Configured MySQL Instance"
ExecWait '$8bin\mysqlslap --silent --delimiter=";" --user=${MYSQL_USER} --password=${MYSQL_PASS} --engine=innodb --create="CREATE TABLE a (b int);INSERT INTO a VALUES (23)" --query="SELECT * FROM a" --concurrency=1 --iterations=1 --query="DROP TABLE a"'
MessageBox MB_OK "MySQL Instance Successfully Passed Tests."

ExecWait '$8bin\mysql --silent -u${MYSQL_USER} -p${MYSQL_PASS} -e "CREATE SCHEMA ${MYSQL_SCHEMA_NAME}"'
MessageBox MB_OK "${MYSQL_SCHEMA_NAME} SCHEMA Created Successfully"

        File "dhis\${MYSQL_DUMP_FILE}"
ReadEnvStr $R0 COMSPEC

Exec '"$R0" /C $8bin\mysql --silent --wait --reconnect --user=${MYSQL_USER} --password=${MYSQL_PASS} --host=${MYSQL_HOST} --database=${MYSQL_SCHEMA_NAME} < "$INSTDIR\${MYSQL_DUMP_FILE}"'
        Delete "$INSTDIR\${MYSQL_DUMP_FILE}"
MessageBox MB_OK "${MYSQL_SCHEMA_NAME} SCHEMA Tables Created Successfully"
        
SectionEnd


Section "Tomcat 6 Windows service and ${AppName}" tomcat
SectionIn 1 RO    ; Full install, cannot be unselected
    SetOutPath "$INSTDIR"
    SetOverwrite ifnewer

        File "Tools\${TOMCAT_INSTALLER}"
        ExecWait '"$INSTDIR\${TOMCAT_INSTALLER}" /S /D=$INSTDIR\tomcat6\'
        Delete "$INSTDIR\${TOMCAT_INSTALLER}"

    WriteRegExpandStr HKLM "SYSTEM\CurrentControlSet\Control\Session Manager\Environment" "DHIS2_HOME" $INSTDIR
    ; ... and for this context
    System::Call 'Kernel32::SetEnvironmentVariableA(t, t) i("DHIS2_HOME", "$INSTDIR").r0'

        File "dhis\${HIBERNATE_FILE}"

    SetOutPath "$INSTDIR\tomcat6\webapps\"
    SetOverwrite ifnewer

        File "dhis\${WAR}"

    ExecWait '"$INSTDIR\tomcat6\bin\tomcat6.exe" //IS//Tomcat6 --DisplayName="Apache Tomcat 6 for DHIS" --Install="$INSTDIR\tomcat6\bin\tomcat6.exe"  --Startup=auto --Jvm=auto --StartMode=jvm --StopMode=jvm --StartClass=org.apache.catalina.startup.Bootstrap --StartParams=start --StopClass=org.apache.catalina.startup.Bootstrap --StopParams=stop'

    SetOutPath "$INSTDIR"
    SetOverwrite ifnewer

    File "Tools\${BROWSER_INSTALLER}"
        
ExecWait '"$INSTDIR\${BROWSER_INSTALLER}" /S --system-level'
;--do-not-launch-chrome

        Delete "$INSTDIR\${BROWSER_INSTALLER}"


SectionEnd

;dhis
Section "Installation of ${AppName}" SecAppFiles
SectionIn 1 RO    ; Full install, cannot be unselected
  SetOutPath "$INSTDIR"

  ;ADD YOUR OWN FILES HERE...

  ;Store installation folder
  WriteRegStr HKCU "Software\DHIS" "" $INSTDIR

  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
SectionEnd

Section "Start menu shortcuts" SecCreateShortcut
  SectionIn 1   ; Can be unselected
  CreateDirectory "$SMPROGRAMS\${AppName}"
  CreateShortCut "$SMPROGRAMS\${AppName}\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
; Etc
SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecDummy ${LANG_ENGLISH} "A test section."

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecAppFiles} $(DESC_SecDummy)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  Delete "$INSTDIR\Uninstall.exe"

  RMDir "$INSTDIR"

  DeleteRegKey /ifempty HKCU "Software\DHIS2"

SectionEnd

Function SetupSections
  !insertmacro SelectSection ${jdk}
  !insertmacro SelectSection ${tomcat}  
  !insertmacro SelectSection ${SecAppFiles}
  !insertmacro SelectSection ${SecCreateShortcut}
FunctionEnd

Function CheckInstalledJRE
;  MessageBox MB_OK "Checking Installed JRE Version"
  Push "${JDK_VERSION}"
  Exch $0   ; Get version requested

  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$1" "JavaHome"

${If} $1 != ''
;  MessageBox MB_OK "Detected JDK version is : $1"
;  MessageBox MB_OK "Detected JDK Path is : $2"
  Goto CompareMajorVersion        
${ElseIf} $2 != ''
;  MessageBox MB_OK "Detected JDK Path is : $2"
${Else}
;  MessageBox MB_OK 'No Installed JDK found'
  Goto InstallJDK
${EndIf}

CompareMajorVersion:

  StrCpy $3 $0 1     
  StrCpy $4 $1 1     

${If} $4 = $3
;  MessageBox MB_OK "Need $3 , found $4"
  Goto CompareMinorVersion
${ElseIf} $4 > $3
;  MessageBox MB_OK " $3 is less than $4"
  Goto DontInstallJDK  
${Else}
;  MessageBox MB_OK "$3 more than $4"
  Goto InstallJDK
${EndIf}


CompareMinorVersion:

  StrCpy $5 $0 1 2
  StrCpy $6 $1 1 2   
;  MessageBox MB_OK "Need $5 , found $6" 

${If} $5 = $6
;  MessageBox MB_OK "Need $5 , found $6"
  Goto DontInstallJDK  
${ElseIf} $6 > $5
;  MessageBox MB_OK " $5 is less than $6"
  Goto DontInstallJDK  
${Else}
;  MessageBox MB_OK "$5 more than $6"
  Goto InstallJDK
${EndIf}


InstallJDK:
;  Messagebox MB_OK "Done checking JRE version, reccomend install"  
StrCpy $JAVAINST 'yes'
Goto End

DontInstallJDK:
;  Messagebox MB_OK "Done checking JRE version, reccomend cancel"
StrCpy $JAVAINST 'no'

End:
;  Messagebox MB_OK "Done checking JRE version"
FunctionEnd

Function .onInit
  Call SetupSections 
FunctionEnd
