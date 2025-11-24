#!/bin/bash
echo "--- INICIANDO CONFIGURACIÓN AUTOMÁTICA DE KMS ---"

# 1. Intentar crear la clave
KEY_ID=$(awslocal kms create-key --description "Llave Maestra Automatica" --query KeyMetadata.KeyId --output text)

echo "Clave creada con ID: $KEY_ID"

# 2. Crear un ALIAS (Nombre fijo) para esa clave
awslocal kms create-alias --alias-name alias/passman-key --target-key-id $KEY_ID

echo "Alias 'alias/passman-key' asignado correctamente."
echo "--- CONFIGURACIÓN COMPLETA ---"
