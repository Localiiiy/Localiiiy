with open('app/src/main/java/com/example/ui/components/LocaliiiyTopBar.kt', 'r') as f:
    lines = f.readlines()
depth = 0
for i, line in enumerate(lines):
    if '{' in line: depth += line.count('{')
    if '}' in line: depth -= line.count('}')
    if depth < 0:
        print(f"Error on line {i+1}: depth < 0")
print(f"Final depth: {depth}")
