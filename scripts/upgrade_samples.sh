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
# Changes all samples to a new Compose version
#
# Example: To run 
#     ./scripts/upgrade_samples.sh
#
########################################################################

set -e

# Check for clean git status
if [[ `git status --porcelain` ]]; then
    read -r -p "You have uncommited git changes. Are you sure you want to continue? [y/N] " response
    if [[ "$response" =~ ^([nN])$ ]]; then
        exit 0;
    fi
fi

echo "Version to change Compose to (e.g 1.2.0-alpha06): ";
read compose_version;

echo "Snapshot ID: (Blank for none)";
read snapshot_version;

if [ -z "$snapshot_version" ]; then
    echo "Changing Compose version to $compose_version"
else
    echo "Changing Compose version to $compose_version Snapshot $snapshot_version"
fi

# Change Dependencies.kt versions
for DEPENDENCIES_FILE in `find . -type f -iname "dependencies.kt"` ; do
    COMPOSE_BLOCK=false;
    MADE_CHANGE=false;
    TEMP_FILENAME="${DEPENDENCIES_FILE}_new";
    while IFS= read -r line; do
        if [[ $line == *"val version ="* ]] && $COMPOSE_BLOCK = true; then
            echo "$line" | sed -En 's/".*"/"'$compose_version'"/p'
            MADE_CHANGE=true;
        elif [[ $line == *"val snapshot ="* ]] && $COMPOSE_BLOCK = true; then
            echo "$line" | sed -En 's/".*"/"'$snapshot_version'"/p'
            MADE_CHANGE=true;
        else
            if [[ $line == *"object Compose {"* ]]; then
                COMPOSE_BLOCK=true;
            elif [[ $line == *"}"* ]]; then
                COMPOSE_BLOCK=false;
            fi
            echo "$line";
        fi
    done < $DEPENDENCIES_FILE > $TEMP_FILENAME
    if $MADE_CHANGE = true; then
        echo $DEPENDENCIES_FILE;
        mv $TEMP_FILENAME $DEPENDENCIES_FILE;
    else
        rm $TEMP_FILENAME;
    fi
    
done

# Change build.gradle versions
for DEPENDENCIES_FILE in `find . -type f -iname "build.gradle"` ; do
    MADE_CHANGE=false;
    TEMP_FILENAME="${DEPENDENCIES_FILE}_new";
    while IFS= read -r line; do
        if [[ $line == *"ext.compose_version ="* ]]; then
            echo "$line" | sed -En "s/\'.*'/\'$compose_version\'/p"
            MADE_CHANGE=true;
        elif [[ $line == *"ext.compose_snapshot_version ="* ]]; then
            echo "$line" | sed -En "s/\'.*'/\'$snapshot_version\'/p"
            MADE_CHANGE=true;
        else
            echo "$line";
        fi
    done < $DEPENDENCIES_FILE > $TEMP_FILENAME
    if $MADE_CHANGE = true; then
        echo $DEPENDENCIES_FILE;
        mv $TEMP_FILENAME $DEPENDENCIES_FILE;
    else
        rm $TEMP_FILENAME;
    fi
done

