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
# Duplicates libs.versions.toml into each sample from master copy.
#
# Example: To run build over all projects run:
#     ./scripts/duplicate_version_config.sh
#
########################################################################

set -xe

cp scripts/libs.versions.toml Crane/gradle/libs.versions.toml
cp scripts/libs.versions.toml Jetcaster/gradle/libs.versions.toml
cp scripts/libs.versions.toml Jetchat/gradle/libs.versions.toml
cp scripts/libs.versions.toml JetLagged/gradle/libs.versions.toml
cp scripts/libs.versions.toml JetNews/gradle/libs.versions.toml
cp scripts/libs.versions.toml Jetsnack/gradle/libs.versions.toml
cp scripts/libs.versions.toml Jetsurvey/gradle/libs.versions.toml
cp scripts/libs.versions.toml Owl/gradle/libs.versions.toml
cp scripts/libs.versions.toml Reply/gradle/libs.versions.toml

cp scripts/toml-updater-config.gradle Crane/buildscripts/toml-updater-config.gradle
cp scripts/toml-updater-config.gradle Jetcaster/buildscripts/toml-updater-config.gradle
cp scripts/toml-updater-config.gradle Jetchat/buildscripts/toml-updater-config.gradle
cp scripts/toml-updater-config.gradle JetLagged/buildscripts/toml-updater-config.gradle
cp scripts/toml-updater-config.gradle JetNews/buildscripts/toml-updater-config.gradle
cp scripts/toml-updater-config.gradle Jetsnack/buildscripts/toml-updater-config.gradle
cp scripts/toml-updater-config.gradle Jetsurvey/buildscripts/toml-updater-config.gradle
cp scripts/toml-updater-config.gradle Owl/buildscripts/toml-updater-config.gradle
cp scripts/toml-updater-config.gradle Reply/buildscripts/toml-updater-config.gradle

cp scripts/init.gradle.kts Crane/buildscripts/init.gradle.kts
cp scripts/init.gradle.kts Jetcaster/buildscripts/init.gradle.kts
cp scripts/init.gradle.kts Jetchat/buildscripts/init.gradle.kts
cp scripts/init.gradle.kts JetLagged/buildscripts/init.gradle.kts
cp scripts/init.gradle.kts JetNews/buildscripts/init.gradle.kts
cp scripts/init.gradle.kts Jetsnack/buildscripts/init.gradle.kts
cp scripts/init.gradle.kts Jetsurvey/buildscripts/init.gradle.kts
cp scripts/init.gradle.kts Owl/buildscripts/init.gradle.kts
cp scripts/init.gradle.kts Reply/buildscripts/init.gradle.kts