SET PGPASSWORD=postgres
SET DB_NAME=dhis2db
SET n=2
setlocal enabledelayedexpansion

"POSTGRES_INSTALL_LOCATION\bin\psql.exe" -c "CREATE USER dhis CREATEDB LOGIN PASSWORD 'dhis';" -U postgres -w postgres

for %%X in (dhis2db\*.*) do (
	"POSTGRES_INSTALL_LOCATION\bin\createdb.exe" -U postgres -w -O dhis !DB_NAME!
	"POSTGRES_INSTALL_LOCATION\bin\psql.exe" -U postgres -w -d !DB_NAME! -f "%%X"
	SET DB_NAME=%DB_NAME%!n!
	SET /a n+=1
)