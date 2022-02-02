#!/usr/bin/env bash
# fail if any commands fails
set -e
# debug log
set -x

sudo apt-get install -y jq
results=$(jq '.[0] | .test_details' ./results.out)
if [[ $results == *"failed"* && $results == *"flaky"* && $results == *"passed"* ]]; then
  failedTests=$(echo "$results" | sed -e 's/.*"\(.*\)test cases failed.*/\1/')
  passedTests=$(($(echo "$results" | sed -e 's/.*,\(.*\)passed.*/\1/') + $(echo "$results" | sed -e 's/.*,\(.*\)flaky.*/\1/')))
  totalTests=$(($failedTests + $passedTests))
  echo $failedTests | envman add --key FAILED_TESTS
  echo $passedTests | envman add --key PASSED_TESTS
  echo $totalTests | envman add --key TOTAL_TESTS
elif [[ $results != *"failed"* && $results == *"flaky"* && $results == *"passed"* ]]; then
  failedTests=0
  passedTests=$(($(echo "$results" | sed -e 's/.*,\(.*\)passed.*/\1/') + $(echo "$results" | sed -e 's/.*"\(.*\)test cases flaky.*/\1/')))
  totalTests=$(($failedTests + $passedTests))
  echo $failedTests | envman add --key FAILED_TESTS
  echo $passedTests | envman add --key PASSED_TESTS
  echo $totalTests | envman add --key TOTAL_TESTS
elif [[ $results == *"failed"* && $results != *"flaky"* && $results == *"passed"* ]]; then
  failedTests=$(echo "$results" | sed -e 's/.*"\(.*\)test cases failed.*/\1/')
  passedTests=$(echo "$results" | sed -e 's/.*,\(.*\)passed.*/\1/')
  totalTests=$(($failedTests + $passedTests))
  echo $failedTests | envman add --key FAILED_TESTS
  echo $passedTests | envman add --key PASSED_TESTS
  echo $totalTests | envman add --key TOTAL_TESTS
elif [[ $results == *"failed"* && $results == *"flaky"* && $results != *"passed"* ]]; then
  failedTests=$(echo "$results" | sed -e 's/.*"\(.*\)test cases failed.*/\1/')
  passedTests=$(echo "$results" | sed -e 's/.*,\(.*\)flaky.*/\1/')
  totalTests=$(($failedTests + $passedTests))
  echo $failedTests | envman add --key FAILED_TESTS
  echo $passedTests | envman add --key PASSED_TESTS
  echo $totalTests | envman add --key TOTAL_TESTS
elif [[ $results != *"failed"* && $results != *"flaky"* && $results == *"passed"* ]]; then
  failedTests=0
  passedTests=$(echo "$results" | sed -e 's/.*"\(.*\)test cases passed.*/\1/')
  totalTests=$(($failedTests + $passedTests))
  echo $passedTests | envman add --key PASSED_TESTS
  echo $failedTests | envman add --key FAILED_TESTS
  echo $totalTests | envman add --key TOTAL_TESTS
elif [[ $results == *"failed"* && $results != *"flaky"* && $results != *"passed"* ]]; then
  failedTests=$(echo "$results" | sed -e 's/.*"\(.*\)test cases failed.*/\1/')
  passedTests=0
  passedTests=$(($flakyTests + $passedTests))
  totalTests=$(($failedTests + $passedTests))
  echo $failedTests | envman add --key FAILED_TESTS
  echo $passedTests | envman add --key PASSED_TESTS
  echo $totalTests | envman add --key TOTAL_TESTS
elif [[ $results != *"failed"* && $results == *"flaky"* && $results != *"passed"* ]]; then
  failedTests=0
  passedTests=$(echo "$results" | sed -e 's/.*"\(.*\)test cases flaky.*/\1/')
  totalTests=$(($failedTests + $passedTests))
  echo $failedTests | envman add --key FAILED_TESTS
  echo $passedTests | envman add --key PASSED_TESTS
  echo $totalTests | envman add --key TOTAL_TESTS
fi
