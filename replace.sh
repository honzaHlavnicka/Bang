#!/bin/bash
# 1. Replace cz.honzaa.bang with cz.honzaa.bang
find . -type f -not -path "*/\.git/*" -not -path "*/node_modules/*" -not -path "*/target/*" -not -path "*/build/*" -not -name "*.jar" -exec grep -l "cz\.honza\.bang" {} + | while read -r file; do
    sed -i 's/cz\.honza\.bang/cz.honzaa.bang/g' "$file"
done

# 2. Replace <groupId>cz.honza</groupId> with <groupId>cz.honzaa</groupId>
find . -type f -name "pom.xml" -not -path "*/\.git/*" -not -path "*/node_modules/*" -not -path "*/target/*" -not -path "*/build/*" -exec grep -l "<groupId>cz\.honza</groupId>" {} + | while read -r file; do
    sed -i 's/<groupId>cz\.honza<\/groupId>/<groupId>cz.honzaa<\/groupId>/g' "$file"
done
