import os
import re

extensions = ['.kt', '.xml', '.json', '.gradle', '.kts']

for root, dirs, files in os.walk('app/src'):
    for file in files:
        if any(file.endswith(ext) for ext in extensions):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            new_content = content
            # Fix any iiys left
            new_content = re.sub(r'Localiiiyiiy', 'Localiiiy', new_content, flags=re.IGNORECASE)
            
            if content != new_content:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Fixed: {path}")
