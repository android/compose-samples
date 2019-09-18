/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

buildscript {
    val workdir = File("../..").absolutePath
    val externalM2RepoPath = "$workdir/androidx-master-dev/prebuilts/androidx/external"
    repositories {
        maven {
            url = uri(externalM2RepoPath)
        }
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:3.5.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.50-compose-20190806")
    }
}

allprojects {
    val workdir = File("../..").absolutePath
    val externalM2RepoPath = "$workdir/androidx-master-dev/prebuilts/androidx/external"
    val snapshotUrl = "https://ci.android.com/builds/submitted/5843334/androidx_snapshot/latest/ui/repository/"
    repositories {
        maven {
            url = uri(snapshotUrl)
        }
        maven {
            url = uri(externalM2RepoPath)

        }
        google()
        jcenter()
    }
}
