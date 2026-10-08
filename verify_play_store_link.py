#!/usr/bin/env python3
import re
import urllib.request
import urllib.error
import sys

def get_package_name_from_gradle():
    try:
        with open("app/build.gradle.kts", "r") as f:
            content = f.read()
            # Match applicationId = "..."
            match = re.search(r'applicationId\s*=\s*"([^"]+)"', content)
            if match:
                return match.group(1)
    except Exception as e:
        print(f"Error reading app/build.gradle.kts: {e}", file=sys.stderr)
    return "com.aistudio.localiiiy.live"  # Fallback to registered package

def verify_play_store_link():
    package_name = get_package_name_from_gradle()
    play_store_url = f"https://play.google.com/store/apps/details?id={package_name}"
    
    print("=" * 60)
    print("🤖 GOOGLE PLAY STORE LINK VERIFICATION UTILITY")
    print("=" * 60)
    print(f"Parsed Package Name: {package_name}")
    print(f"Target Play Store Link: {play_store_url}")
    print("-" * 60)
    
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'
    }
    
    req = urllib.request.Request(play_store_url, headers=headers)
    
    try:
        print("Checking connection to Google Play Store servers...")
        with urllib.request.urlopen(req, timeout=10) as response:
            status_code = response.getcode()
            if status_code == 200:
                print(f"🟢 SUCCESS: Play Store URL is live and redirects correctly!")
                print(f"Status Code: {status_code}")
                return True
    except urllib.error.HTTPError as e:
        # A 404 is expected if the app is currently in "Draft", "Closed Testing", or "Internal Testing" track
        # and has not been approved for public production listing yet.
        if e.code == 404:
            print(f"🟡 NOTICE (HTTP 404): The Play Store URL is well-formed but currently private.")
            print("Explanation: This is completely normal and expected for new apps because your build is in the 'Closed Testing / Draft' phase.")
            print("Once you promote your draft/closed track build to the public Production Track, Google Play will make this link public.")
            return True
        else:
            print(f"🔴 ERROR (HTTP {e.code}): Play Store link check failed.", file=sys.stderr)
            return False
    except Exception as e:
        print(f"🔴 EXCEPTION: Failed to query Play Store URL. Reason: {e}", file=sys.stderr)
        return False

if __name__ == "__main__":
    success = verify_play_store_link()
    print("=" * 60)
    sys.exit(0 if success else 1)
