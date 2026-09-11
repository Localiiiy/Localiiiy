import os
import re
import glob

def process(text):
    # Reel -> Clip
    text = re.sub(r'Reel', 'Clip', text)
    text = re.sub(r'reel', 'clip', text)
    text = re.sub(r'REEL', 'CLIP', text)
    
    # Locali -> Localiiiy
    # We use negative lookahead to avoid Locality, Localization, Localiiiy, LocalContext, Locale etc.
    # We want to catch Locali and locali.
    text = re.sub(r'Locali(?!ty|zation|iiiy|ze|sed|st)', 'Localiiiy', text)
    text = re.sub(r'locali(?!ty|zation|iiiy|ze|sed|st)', 'localiiiy', text)
    text = re.sub(r'LOCALI(?!TY|ZATION|IIIY|ZE|SED|ST)', 'LOCALIIIY', text)
    
    return text

extensions = ['.kt', '.xml', '.json', '.gradle', '.kts']

for root, dirs, files in os.walk('app/src'):
    for file in files:
        if any(file.endswith(ext) for ext in extensions):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
            new_content = process(content)
            if content != new_content:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(new_content)
            
            new_file = process(file)
            if new_file != file:
                new_path = os.path.join(root, new_file)
                os.rename(path, new_path)
                print(f"Renamed: {path} -> {new_path}")
