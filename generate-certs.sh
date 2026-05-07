#!/bin/bash

declare -A PARTIES=(
    ["smp"]="conf/certs/smp"
    ["smp"]="dynamic-discovery/certs"
    ["sender"]="conf/certs/sender"
    ["receiver"]="conf/certs/receiver"
)

generate_cert() {
    local party_id=$1
    local cert_dir=$2

    echo "Generating certificates for: $party_id in $cert_dir"

    mkdir -p "$cert_dir"

    local NAME="keystore"
    openssl genpkey -algorithm RSA -out "$cert_dir/$NAME.key" -pkeyopt rsa_keygen_bits:2048
    openssl req -new -x509 -key "$cert_dir/$NAME.key" -out "$cert_dir/$NAME.crt" -days 1825 -subj "/CN=$party_id"
    openssl pkcs12 -export -out "$cert_dir/$NAME.p12" -inkey "$cert_dir/$NAME.key" -in "$cert_dir/$NAME.crt" -name "$party_id" -passout pass:changeit

    echo "Certificates generated for $party_id"
}

if [ $# -eq 0 ]; then
    PARTIES_TO_GENERATE=("smp" "sender" "receiver")
else
    PARTIES_TO_GENERATE=()
    for arg in "$@"; do
        if [[ -v PARTIES[$arg] ]]; then
            PARTIES_TO_GENERATE+=("$arg")
        else
            echo "Warning: Unknown party '$arg' - ignoring"
        fi
    done
fi

if [ ${#PARTIES_TO_GENERATE[@]} -eq 0 ]; then
    echo "No valid parties specified. Nothing to generate."
    exit 1
fi

for party in "${PARTIES_TO_GENERATE[@]}"; do
    generate_cert "$party" "${PARTIES[$party]}"
done

echo "Certificate generation complete!"