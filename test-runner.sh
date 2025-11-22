#!/usr/bin/env bash

# 테스트 실행 스크립트
# 사용법: ./test-runner.sh <test-pattern>
# 예: ./test-runner.sh '*B_5_S_8'

set -a
source ./local-env/.test-env 2>/dev/null || true
set +a

TEST_PATTERN="${1:-*}"
mkdir -p debug/logs
LOG_FILE="debug/logs/test-output-$(date +%Y%m%d-%H%M%S).log"

echo "================================================"
echo "Running tests: ${TEST_PATTERN}"
echo "Log file: ${LOG_FILE}"
echo "Started at: $(date)"
echo "================================================"
echo ""

# Gradle 테스트 실행
./gradlew test \
  --tests "${TEST_PATTERN}" \
  --rerun-tasks \
  --console=plain 2>&1 | tee "${LOG_FILE}"

EXIT_CODE=${PIPESTATUS[0]}

echo ""
echo "================================================"
echo "Finished at: $(date)"
echo "Exit code: ${EXIT_CODE}"
echo "Log saved to: ${LOG_FILE}"
echo "================================================"
echo ""

# 테스트 결과 요약 출력
echo "📊 Test Results Summary"
echo "================================================"
echo ""

if [ ! -f "${LOG_FILE}" ]; then
    echo "⚠️  Log file not found: ${LOG_FILE}"
    exit ${EXIT_CODE}
fi

TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# 로그 파일에서 테스트 결과 추출
# 패턴 예: "BungaeServiceTest > B-5-S-1: 날짜 투표... PASSED"
while IFS= read -r line; do
    # "TestClass > test name PASSED" 또는 "TestClass > test name FAILED" 패턴 매칭
    # > 문자를 명시적으로 포함하여 Task 라인은 자연스럽게 제외됨
    if [[ $line =~ [^[:space:]]+[[:space:]]+\>[[:space:]]+(.+)[[:space:]]+(PASSED|FAILED)[[:space:]]*$ ]]; then
        test_name="${BASH_REMATCH[1]}"
        test_status="${BASH_REMATCH[2]}"
        TOTAL_TESTS=$((TOTAL_TESTS + 1))

        if [ "$test_status" = "PASSED" ]; then
            echo "  ✅ $test_name"
            PASSED_TESTS=$((PASSED_TESTS + 1))
        else
            echo "  ❌ $test_name"
            FAILED_TESTS=$((FAILED_TESTS + 1))
        fi
    fi
done < "${LOG_FILE}"

echo ""
echo "================================================"
echo "📈 Total: $TOTAL_TESTS tests"
echo "   ✅ Passed: $PASSED_TESTS"
echo "   ❌ Failed: $FAILED_TESTS"
echo "================================================"

if [ ${EXIT_CODE} -eq 0 ]; then
    echo ""
    echo "🎉 All tests PASSED!"
else
    echo ""
    echo "⚠️  Some tests FAILED"
    echo ""
    echo "📄 Detailed report:"
    echo "   open build/reports/tests/test/index.html"
fi

exit ${EXIT_CODE}

