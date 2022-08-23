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
# Updates dependencies using Jetchat as the source of truth (then copies Jetchat's 
# output into each sample)
#
# Example: To run build over all projects run:
#     ./scripts/updateDeps.sh
#
########################################################################

set -xe

./JetNews/gradlew -p ./JetNews versionCatalogUpdate 

cp JetNews/gradle/libs.versions.toml scripts/libs.versions.toml
./scripts/duplicate_version_config.sh
