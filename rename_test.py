import os
import re

def process(text):
    # Reel -> Clip
    text = re.sub(r'Reel', 'Clip', text)
    text = re.sub(r'reel', 'clip', text)
    text = re.sub(r'REEL', 'CLIP', text)
    
    # Locali -> Localiiiy
    # We use negative lookahead to avoid Locality, Localization, Localiiiy
    text = re.sub(r'Locali(?!ty|zation|iiiy|ze|sed|st)', 'Localiiiy', text)
    text = re.sub(r'locali(?!ty|zation|iiiy|ze|sed|st)', 'localiiiy', text)
    text = re.sub(r'LOCALI(?!TY|ZATION|IIIY|ZE|SED|ST)', 'LOCALIIIY', text)
    
    return text

for root, dirs, files in os.walk('app/src/main/java'):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r') as f:
                content = f.read()
            new_content = process(content)
            if content != new_content:
                print(f"Would change: {path}")
import shutil

for root, dirs, files in os.walk('app/src/main/java'):
    for file in files:
        if file.endswith('.kt'):
            new_file = process(file)
            if new_file != file:
                print(f"Would rename file: {file} -> {new_file}")
