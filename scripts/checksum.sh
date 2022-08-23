#
#  Copyright 2022 Google, Inc.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

#!/bin/bash
SAMPLE=$1
RESULT_FILE=$2

if [ -f $RESULT_FILE ]; then
  rm $RESULT_FILE
fi
touch $RESULT_FILE

checksum_file() {
  echo $(openssl md5 $1 | awk '{print $2}')
}

FILES=()
while read -r -d ''; do
	FILES+=("$REPLY")
done < <(find $SAMPLE -type f \( -name "build.gradle*" -o -name "gradle-wrapper.properties"  -o -name "robolectric.properties" \) -print0)

# Loop through files and append MD5 to result file
for FILE in ${FILES[@]}; do
	echo $(checksum_file $FILE) >> $RESULT_FILE
done
# Now sort the file so that it is idempotent
sort $RESULT_FILE -o $RESULT_FILE
