#!/bin/bash

# Copyright (C) 2020 The Android Open Source Project
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
# Changes all samples to a new Compose version
#
# Example: To run 
#     ./scripts/upgrade_samples.sh
#
########################################################################

set -e

echo "Version to change Compose to (e.g 1.2.0-alpha06): ";
read compose_version;

echo "Snapshot ID: (Blank for none)";
read snapshot_version;

echo "Upgrading to $compose_version snapshot $snapshot_version"

# Upgrade Dependencies.kt
# for DEPENDENCIES_FILE in `find . -type f -iname "dependencies.kt"` ; do
#     echo $DEPENDENCIES_FILE;
#     COMPOSE_BLOCK=false;
#     while IFS= read -r line; do
#         if [[ $line == *"val version ="* ]] && $COMPOSE_BLOCK = true; then
#             echo "$line" | sed -En 's/".*"/"'$compose_version'"/p'
#         elif [[ $line == *"val snapshot ="* ]] && $COMPOSE_BLOCK = true; then
#             echo "$line" | sed -En 's/".*"/"'$snapshot_version'"/p'
#         else
#             if [[ $line == *"object Compose {"* ]]; then
#                 COMPOSE_BLOCK=true;
#             elif [[ $line == *"}"* ]]; then
#                 COMPOSE_BLOCK=false;
#             fi
#             echo "$line";
#         fi
#     done < $DEPENDENCIES_FILE > "${DEPENDENCIES_FILE}_new"
#     mv "${DEPENDENCIES_FILE}_new" $DEPENDENCIES_FILE
# done

# Upgrade build.gradle
for DEPENDENCIES_FILE in `find . -type f -iname "build.gradle"` ; do
    echo $DEPENDENCIES_FILE;
    
    while IFS= read -r line; do
        if [[ $line == *"ext.compose_version ="* ]]; then
            echo "$line" | sed -En "s/\'.*'/\'$compose_version\'/p"
        elif [[ $line == *"val snapshot ="* ]] && $COMPOSE_BLOCK = true; then
            echo "$line" | sed -En 's/".*"/"'$snapshot_version'"/p'
        else
            echo "$line";
        fi
    done < $DEPENDENCIES_FILE > "${DEPENDENCIES_FILE}_new"
    mv "${DEPENDENCIES_FILE}_new" $DEPENDENCIES_FILE
done

