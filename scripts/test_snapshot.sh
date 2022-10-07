#!/bin/bash

# Copyright (C) 2022 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

########################################################################
#
# Allows testing a snapshot version across all samples
#
# Example: To run build over all projects run:
#     ./scripts/test_snapshot.sh
#
########################################################################
set -xe

if [ -z "$1" ]; then
    read -p "Enter compose version e.g. 1.3.0: " compose_ver
else
    echo "Using compose version: $1"
    compose_ver=$1
fi
if [ -z "$2" ]; then
    read -p "Enter snapshot ID: " snapshot
else 
    echo "Using compose snapshot: $2"
    snapshot=$2
fi
export COMPOSE_SNAPSHOT_ID=$snapshot

# Switch version to SNAPSHOT
cp ./scripts/libs.versions.toml ./scripts/libs.versions.toml.tmp
if [[ "$OSTYPE" == "darwin"* ]]; then
    sed -i '' -e 's/^compose = ".*"/compose = "'$compose_ver'-SNAPSHOT"/g' ./scripts/libs.versions.toml
else
    sed -i -e 's/^compose = ".*"/compose = "'$compose_ver'-SNAPSHOT"/g' ./scripts/libs.versions.toml
fi

# Copy to all samples and verify
./scripts/duplicate_version_config.sh
./scripts/verify_samples.sh
./scripts/gradlew_recursive.sh testDebug --stacktrace

# Undo all changes
mv ./scripts/libs.versions.toml.tmp ./scripts/libs.versions.toml
./scripts/duplicate_version_config.sh
