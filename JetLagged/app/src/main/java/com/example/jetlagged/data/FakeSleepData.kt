/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetlagged.data

import com.example.jetlagged.sleep.SleepDayData
import com.example.jetlagged.sleep.SleepGraphData
import com.example.jetlagged.sleep.SleepPeriod
import com.example.jetlagged.sleep.SleepType
import java.time.LocalDateTime

// In the real world, you should get this data from a backend.
val sleepData = SleepGraphData(
    listOf(
        SleepDayData(
            LocalDateTime.now().minusDays(7),
            listOf(
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(7)
                        .withHour(21)
                        .withMinute(8),
                    endTime = LocalDateTime.now()
                        .minusDays(7)
                        .withHour(21)
                        .withMinute(40),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(7)
                        .withHour(21)
                        .withMinute(40),
                    endTime = LocalDateTime.now()
                        .minusDays(7)
                        .withHour(22)
                        .withMinute(20),
                    type = SleepType.Light
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(7)
                        .withHour(22)
                        .withMinute(20),
                    endTime = LocalDateTime.now()
                        .minusDays(7)
                        .withHour(22)
                        .withMinute(50),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(7)
                        .withHour(22)
                        .withMinute(50),
                    endTime = LocalDateTime.now()
                        .minusDays(7)
                        .withHour(23)
                        .withMinute(30),
                    type = SleepType.REM
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(7)
                        .withHour(23)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(1)
                        .withMinute(10),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(1)
                        .withMinute(10),
                    endTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(2)
                        .withMinute(30),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(2)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(4)
                        .withMinute(10),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(4)
                        .withMinute(10),
                    endTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(5)
                        .withMinute(30),
                    type = SleepType.Awake
                )
            ),
            sleepScore = 90
        ),
        SleepDayData(
            LocalDateTime.now().minusDays(6),
            listOf(
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(22)
                        .withMinute(38),
                    endTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(22)
                        .withMinute(50),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(22)
                        .withMinute(50),
                    endTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(23)
                        .withMinute(30),
                    type = SleepType.Light
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(23)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(23)
                        .withMinute(55),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(6)
                        .withHour(23)
                        .withMinute(55),
                    endTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(2)
                        .withMinute(40),
                    type = SleepType.REM
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(2)
                        .withMinute(40),
                    endTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(2)
                        .withMinute(50),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(2)
                        .withMinute(50),
                    endTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(4)
                        .withMinute(12),
                    type = SleepType.Deep
                )
            ),
            sleepScore = 70
        ),
        SleepDayData(
            LocalDateTime.now().minusDays(5),
            listOf(
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(22)
                        .withMinute(8),
                    endTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(22)
                        .withMinute(40),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(22)
                        .withMinute(40),
                    endTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(22)
                        .withMinute(50),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(22)
                        .withMinute(50),
                    endTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(22)
                        .withMinute(55),
                    type = SleepType.REM
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(22)
                        .withMinute(55),
                    endTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(23)
                        .withMinute(30),
                    type = SleepType.Light
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(5)
                        .withHour(23)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(1)
                        .withMinute(10),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(1)
                        .withMinute(10),
                    endTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(2)
                        .withMinute(30),
                    type = SleepType.REM
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(2)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(3)
                        .withMinute(5),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(3)
                        .withMinute(5),
                    endTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(4)
                        .withMinute(50),
                    type = SleepType.Light
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(4)
                        .withMinute(50),
                    endTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(6)
                        .withMinute(30),
                    type = SleepType.REM
                )
            ),
            sleepScore = 60
        ),
        SleepDayData(
            LocalDateTime.now().minusDays(4),
            listOf(
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(20)
                        .withMinute(20),
                    endTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(22)
                        .withMinute(40),
                    type = SleepType.Light
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(22)
                        .withMinute(40),
                    endTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(22)
                        .withMinute(50),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(22)
                        .withMinute(50),
                    endTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(23)
                        .withMinute(55),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(4)
                        .withHour(23)
                        .withMinute(55),
                    endTime = LocalDateTime.now()
                        .minusDays(3)
                        .withHour(1)
                        .withMinute(33),
                    type = SleepType.REM
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(3)
                        .withHour(1)
                        .withMinute(33),
                    endTime = LocalDateTime.now()
                        .minusDays(3)
                        .withHour(2)
                        .withMinute(30),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(3)
                        .withHour(2)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .minusDays(3)
                        .withHour(3)
                        .withMinute(45),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(3)
                        .withHour(3)
                        .withMinute(45),
                    endTime = LocalDateTime.now()
                        .minusDays(3)
                        .withHour(7)
                        .withMinute(15),
                    type = SleepType.Light
                )
            ),
            sleepScore = 90
        ),
        SleepDayData(
            LocalDateTime.now().minusDays(3),
            listOf(
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(3)
                        .withHour(22)
                        .withMinute(50),
                    endTime = LocalDateTime.now()
                        .minusDays(3)
                        .withHour(23)
                        .withMinute(30),
                    type = SleepType.REM
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(3)
                        .withHour(23)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(0)
                        .withMinute(10),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(0)
                        .withMinute(10),
                    endTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(1)
                        .withMinute(10),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(1)
                        .withMinute(10),
                    endTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(2)
                        .withMinute(30),
                    type = SleepType.REM
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(2)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(4)
                        .withMinute(30),
                    type = SleepType.Light
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(4)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(4)
                        .withMinute(45),
                    type = SleepType.REM
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(4)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(4)
                        .withMinute(45),
                    type = SleepType.REM
                )
            ),
            sleepScore = 40
        ),
        SleepDayData(
            LocalDateTime.now().minusDays(2),
            listOf(
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(20)
                        .withMinute(40),
                    endTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(21)
                        .withMinute(40),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(21)
                        .withMinute(40),
                    endTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(22)
                        .withMinute(20),
                    type = SleepType.Light
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(22)
                        .withMinute(20),
                    endTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(22)
                        .withMinute(50),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(22)
                        .withMinute(50),
                    endTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(23)
                        .withMinute(30),
                    type = SleepType.REM
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(2)
                        .withHour(23)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(1)
                        .withMinute(10),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(1)
                        .withMinute(10),
                    endTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(2)
                        .withMinute(30),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(2)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(4)
                        .withMinute(10),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(4)
                        .withMinute(10),
                    endTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(5)
                        .withMinute(30),
                    type = SleepType.Awake
                )
            ),
            sleepScore = 82
        ),
        SleepDayData(
            LocalDateTime.now().minusDays(1),
            listOf(
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(22)
                        .withMinute(8),
                    endTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(22)
                        .withMinute(40),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(22)
                        .withMinute(40),
                    endTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(22)
                        .withMinute(50),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(22)
                        .withMinute(50),
                    endTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(22)
                        .withMinute(55),
                    type = SleepType.REM
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(22)
                        .withMinute(55),
                    endTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(23)
                        .withMinute(30),
                    type = SleepType.REM
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .minusDays(1)
                        .withHour(23)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .withHour(1)
                        .withMinute(10),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .withHour(1)
                        .withMinute(10),
                    endTime = LocalDateTime.now()
                        .withHour(2)
                        .withMinute(30),
                    type = SleepType.Awake
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .withHour(2)
                        .withMinute(30),
                    endTime = LocalDateTime.now()
                        .withHour(3)
                        .withMinute(5),
                    type = SleepType.Deep
                ),
                SleepPeriod(
                    startTime = LocalDateTime.now()
                        .withHour(3)
                        .withMinute(5),
                    endTime = LocalDateTime.now()
                        .withHour(4)
                        .withMinute(50),
                    type = SleepType.Light
                )
            ),
            sleepScore = 70
        ),
    )
)
