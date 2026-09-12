with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    text = f.read()

start_index = text.find('fun LocaliiiyApp')
if start_index != -1:
    count = 0
    in_func = False
    for i in range(start_index, len(text)):
        if text[i] == '{':
            count += 1
            in_func = True
        elif text[i] == '}':
            count -= 1
        
        if in_func and count == 0:
            print(f"LocaliiiyApp ends at line: {text[:i].count(chr(10)) + 1}")
            break
