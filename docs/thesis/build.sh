#!/usr/bin/env bash
set -e

root="$(pwd)"
tmpdir="$(mktemp -d)"

cp -r "$root"/* "$tmpdir"/
cd "$tmpdir"
pdflatex main.tex
mv main.pdf "$root"
rm -rf "$tmpdir"
