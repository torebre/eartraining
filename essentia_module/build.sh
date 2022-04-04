#!/bin/bash

npx rollup --config rollup.config.js
cp dist/essentiaModule.js ../webpitch2/src/main/resources/
