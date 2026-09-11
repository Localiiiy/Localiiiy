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
            # Fix any iiys
            new_content = re.sub(r'Localiiiyiiy', 'Localiiiy', new_content, flags=re.IGNORECASE)
            # Fix standalone locali
            new_content = re.sub(r'\bLocali\b', 'Localiiiy', new_content)
            new_content = re.sub(r'\blocali\b', 'localiiiy', new_content)
            new_content = re.sub(r'LOCALI\b', 'LOCALIIIY', new_content)
            
            # What if there is LocalLocaliBrandColors
            new_content = re.sub(r'LocalLocali', 'LocalLocaliiiy', new_content)
            
            # LocaliDao -> LocaliiiyDao is covered by \b maybe not? 
            # In camelcase: \b doesn't match between lower and upper case
            new_content = re.sub(r'Locali(?=[A-Z])', 'Localiiiy', new_content)
            new_content = re.sub(r'locali(?=[A-Z])', 'localiiiy', new_content)
            
            if content != new_content:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Fixed: {path}")
