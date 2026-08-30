#!/bin/bash

npx rollup --config rollup.config.mjs
cp dist/essentiaModule.js ../webpitch2/src/main/resources/
