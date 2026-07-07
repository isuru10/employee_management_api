#!/bin/bash
# Employee Management API Workspace Initialization and Health Check

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color
BLUE='\033[0;34m'

echo -e "${BLUE}=== Employee Management API: Health Check ===${NC}"

# 1. Check Java Version
echo "Checking Java version..."
if command -v java >/dev/null 2>&1; then
    JAVA_VER=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2 | cut -d '.' -f 1)
    if [[ "$JAVA_VER" == "1" ]]; then
        JAVA_VER=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2 | cut -d '.' -f 2)
    fi
    echo "Found Java version: $JAVA_VER"
    if [ "$JAVA_VER" -lt 21 ]; then
        echo -e "${RED}[WARNING]${NC} Java version is $JAVA_VER. Target modernization version is Java 21."
    else
        echo -e "${GREEN}[PASS]${NC} Java version check passed ($JAVA_VER)."
    fi
else
    echo -e "${RED}[FAIL]${NC} Java is NOT installed or NOT in PATH."
    exit 1
fi

# 2. Check Maven Wrapper
if [ -f "./mvnw" ]; then
    echo -e "${GREEN}[PASS]${NC} Maven Wrapper found."
    if [ ! -x "./mvnw" ]; then
        echo "Making ./mvnw executable..."
        chmod +x ./mvnw
    fi
else
    echo -e "${RED}[FAIL]${NC} Maven Wrapper ./mvnw NOT found."
    exit 1
fi

# 3. Check Directory Layout
REQUIRED_DIRS=(
    "src/main/java/com/ruhuna/employee_management_api/model"
    "src/main/java/com/ruhuna/employee_management_api/repository"
    "src/main/java/com/ruhuna/employee_management_api/dto"
    "src/main/java/com/ruhuna/employee_management_api/controller"
    "src/test/java/com/ruhuna/employee_management_api"
)

ALL_DIRS_OK=true
for dir in "${REQUIRED_DIRS[@]}"; do
    if [ -d "$dir" ]; then
         echo -e "  ${GREEN}[OK]${NC} Found directory: $dir"
    else
         echo -e "  ${RED}[MISSING]${NC} Directory: $dir"
         ALL_DIRS_OK=false
    fi
done

if [ "$ALL_DIRS_OK" = true ]; then
    echo -e "${GREEN}[PASS]${NC} Directory layout verified."
else
    echo -e "${RED}[FAIL]${NC} Directory layout check failed!"
    exit 1
fi

# 4. Clean and Compile Check
echo "Compiling project to verify build state..."
if ./mvnw clean compile > /dev/null 2>&1; then
    echo -e "${GREEN}[PASS]${NC} Project clean compile successful."
else
    echo -e "${RED}[FAIL]${NC} Project clean compile failed!"
    exit 1
fi

# 5. Run Test Suite & CRAP Metrics Check
echo "Running tests and CRAP metrics check..."
if ./mvnw clean verify > /dev/null 2>&1; then
    echo -e "${GREEN}[PASS]${NC} All tests and CRAP metrics check passed."
else
    echo -e "${RED}[FAIL]${NC} Test suite or CRAP metrics check failed!"
    exit 1
fi

# 6. Run Mutation Testing Check
echo "Running mutation testing..."
if ./mvnw pitest:mutationCoverage > /dev/null 2>&1; then
    echo -e "${GREEN}[PASS]${NC} Mutation testing completed successfully."
else
    echo -e "${RED}[FAIL]${NC} Mutation testing failed!"
    exit 1
fi

# 7. Check Git Status (Chain of Small Steps check)
echo "Checking Git status..."
if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    if [[ -n $(git status -s) ]]; then
        echo -e "${RED}[WARNING]${NC} You have uncommitted changes. Consider committing verified steps (Chain of Small Steps) before proceeding."
    else
        echo -e "${GREEN}[PASS]${NC} Git working directory is clean."
    fi
else
    echo -e "${RED}[WARNING]${NC} Not a Git repository. Consider initializing one for the Chain of Small Steps pattern."
fi

echo -e "${BLUE}=== Health Check Complete ===${NC}"
exit 0
