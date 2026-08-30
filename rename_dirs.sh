#!/bin/bash
find . -type d -path "*/cz/honzaa/bang" | while read -r dir; do
    parent_dir=$(dirname "$dir")
    grandparent_dir=$(dirname "$parent_dir")
    
    # grandparent_dir is .../cz
    # We want to create .../cz/honzaa
    new_parent="$grandparent_dir/honzaa"
    mkdir -p "$new_parent"
    
    # move bang to new_parent
    mv "$dir" "$new_parent/"
    
    # try to remove old parent if empty
    rmdir "$parent_dir" 2>/dev/null || true
done
