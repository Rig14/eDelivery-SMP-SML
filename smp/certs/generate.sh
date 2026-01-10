#!/bin/bash

party_id="smp"

openssl genpkey -algorithm RSA -out "own.key" -pkeyopt rsa_keygen_bits:2048
openssl req -new -x509 -key "own.key" -out "own.crt" -days 365 -subj "/CN=$party_id"
openssl pkcs12 -export -out "own.p12" -inkey "own.key" -in "own.crt" -name "$party_id" -passout pass:changeit